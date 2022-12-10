package link.dwsy.ddl.controller;

import link.dwsy.ddl.XO.Projection.DailyData_Id_And_ScoreCount;
import link.dwsy.ddl.XO.VO.ArticleFieldRankVO;
import link.dwsy.ddl.XO.VO.QuestionFieldRankVO;
import link.dwsy.ddl.entity.Article.ArticleField;
import link.dwsy.ddl.entity.QA.QaQuestionField;
import link.dwsy.ddl.repository.Article.ArticleFieldRepository;
import link.dwsy.ddl.repository.Data.Article.ArticleDailyDataRepository;
import link.dwsy.ddl.repository.Data.Question.QuestionDailyDataRepository;
import link.dwsy.ddl.repository.QA.QaQuestionFieldRepository;
import link.dwsy.ddl.task.ArticleDailyDataTask;
import link.dwsy.ddl.task.QuestionDailyDataTask;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rank")
public class Rank {

    @Resource
    ArticleFieldRepository articleFieldRepository;
    @Resource
    private ArticleDailyDataTask articleDailyDataTask;
    @Resource
    private QuestionDailyDataTask questionDailyDataTask;
    @Resource
    private ArticleDailyDataRepository articleDailyDataRepository;
    @Resource
    private QuestionDailyDataRepository questionDailyDataRepository;
    @Resource
    private QaQuestionFieldRepository qaQuestionFieldRepository;


    @GetMapping("article")
    public List<ArticleFieldRankVO> getArticleRankList(@RequestParam(name = "daysAgo", required = false, defaultValue = "7") int daysAgo,
                                                       @RequestParam(name = "size", required = false, defaultValue = "30") int size) {
        List<DailyData_Id_And_ScoreCount> daysRankList = articleDailyDataRepository.getNDaysRank(LocalDate.now().minusDays(daysAgo), size);

        List<Long> articleIdList = daysRankList.stream().map(DailyData_Id_And_ScoreCount::getId).collect(Collectors.toList());

        Map<Long, Integer> idScoreMap = daysRankList.stream().collect(Collectors.toMap(DailyData_Id_And_ScoreCount::getId, DailyData_Id_And_ScoreCount::getScoreCount));
//        System.out.println("--------------");
//        System.out.println("(id," + StringUtils.join(articleIdList, ",") + ")");
        List<ArticleField> articleFieldList = articleFieldRepository.findByDeletedFalseAndIdIn(articleIdList).stream().sorted((o1, o2) -> {
            int score1 = idScoreMap.get(o1.getId());
            int score2 = idScoreMap.get(o2.getId());
            return score2 - score1;
        }).collect(Collectors.toList());
        return articleFieldList.stream().map(articleField -> new ArticleFieldRankVO(articleField, idScoreMap.get(articleField.getId()))).collect(Collectors.toList());
    }

    @GetMapping("question")
    public List<QuestionFieldRankVO> getQuestionRankList(@RequestParam(name = "daysAgo", required = false, defaultValue = "7") int daysAgo,
                                                         @RequestParam(name = "size", required = false, defaultValue = "30") int size) {
        List<DailyData_Id_And_ScoreCount> daysRankList = questionDailyDataRepository.getNDaysRank(LocalDate.now().minusDays(daysAgo), size);

        List<Long> questionIdlist = daysRankList.stream().map(DailyData_Id_And_ScoreCount::getId).collect(Collectors.toList());

        Map<Long, Integer> idScoreMap = daysRankList.stream().collect(Collectors.toMap(DailyData_Id_And_ScoreCount::getId, DailyData_Id_And_ScoreCount::getScoreCount));
        List<QaQuestionField> questionFields = qaQuestionFieldRepository.findAllByDeletedFalseAndIdIn(questionIdlist).stream().sorted((o1, o2) -> {
            int score1 = idScoreMap.get(o1.getId());
            int score2 = idScoreMap.get(o2.getId());
            return score2 - score1;
        }).collect(Collectors.toList());
        return questionFields.stream().map(questionField -> new QuestionFieldRankVO(questionField, idScoreMap.get(questionField.getId()))).collect(Collectors.toList());
    }

    @GetMapping("a")
    public String test() {
        articleDailyDataTask.record();
        return "o~~0";
    }

    @GetMapping("q")
    public String test1() {
        questionDailyDataTask.record();
        return "o~~0";
    }
}
