package link.dwsy.ddl.controller;

import link.dwsy.ddl.XO.VO.ArticleDataStatisticsVO;
import link.dwsy.ddl.annotation.AuthAnnotation;
import link.dwsy.ddl.entity.Data.Article.ArticleDailyData;
import link.dwsy.ddl.repository.Article.ArticleFieldRepository;
import link.dwsy.ddl.repository.Article.ArticleGroupRepository;
import link.dwsy.ddl.repository.Data.Article.ArticleDailyDataRepository;
import link.dwsy.ddl.repository.Data.Question.QuestionDailyDataRepository;
import link.dwsy.ddl.repository.Infinity.InfinityClubRepository;
import link.dwsy.ddl.repository.Infinity.InfinityRepository;
import link.dwsy.ddl.repository.Infinity.InfinityTopicRepository;
import link.dwsy.ddl.repository.QA.QaGroupRepository;
import link.dwsy.ddl.repository.QA.QaQuestionFieldRepository;
import link.dwsy.ddl.support.UserSupport;
import link.dwsy.ddl.task.ArticleDailyDataTask;
import link.dwsy.ddl.task.QuestionDailyDataTask;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author Dwsy
 * @Date 2022/12/15
 */
@RestController
@RequestMapping("user/article")
public class UserDataController {
    @Resource
    private ArticleFieldRepository articleFieldRepository;
    @Resource
    private QaGroupRepository qaGroupRepository;
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
    @Resource
    private ArticleGroupRepository articleGroupRepository;
    @Resource(name = "stringRedisTemplate")
    private StringRedisTemplate redisTemplate;
    @Resource
    private InfinityTopicRepository infinityTopicRepository;
    @Resource
    private InfinityClubRepository infinityClubRepository;
    @Resource
    private InfinityRepository infinityRepository;
    @Resource
    private UserSupport userSupport;

    @GetMapping
    @AuthAnnotation
    public List<ArticleDataStatisticsVO> getUserArticleDataStatistics(
            @RequestParam(name = "daysAgo", required = false, defaultValue = "7") int daysAgo) {

        Long userId = userSupport.getCurrentUser().getId();

        List<ArticleDailyData> articleDailyData = articleDailyDataRepository.
                findByUserIdAndDataDateBetween(userId, LocalDate.now().minusDays(daysAgo), LocalDate.now());
        if (articleDailyData.size() == 0) {
            return null;
        }
        Map<LocalDate, List<ArticleDailyData>> collect = articleDailyData.stream().collect(Collectors.groupingBy(ArticleDailyData::getDataDate, Collectors.toList()));
        List<ArticleDataStatisticsVO> dataStatisticsVOList = new ArrayList<>();
        ArticleDataStatisticsVO preHasDataStatisticsVO = null;
        int viewNum = 0, upNum = 0, commentNum = 0, downNum = 0, collectNum = 0;
        for (int i = 0, ago = daysAgo; ago > 0; ago--) {
            LocalDate localDate = LocalDate.now().minusDays(ago);
            if (collect.containsKey(localDate)) {
                List<ArticleDailyData> dailyDataList = collect.get(localDate);
                for (ArticleDailyData dailyData : dailyDataList) {
                    viewNum += dailyData.getViewNum();
                    upNum += dailyData.getUpNum();
                    commentNum += dailyData.getCommentNum();
                    downNum += dailyData.getDownNum();
                    collectNum += dailyData.getCollectNum();
                }
                ArticleDataStatisticsVO statisticsVO = ArticleDataStatisticsVO.builder()
                        .serial(i++)
                        .viewNum(viewNum)
                        .upNum(upNum)
                        .downNum(downNum)
                        .collectNum(collectNum)
                        .commentNum(commentNum)
                        .build();
                dataStatisticsVOList.add(statisticsVO);
                preHasDataStatisticsVO = statisticsVO;
            } else {
                i++;
                if (preHasDataStatisticsVO != null) {
                    dataStatisticsVOList.add(preHasDataStatisticsVO);
                } else {
                    dataStatisticsVOList.add(ArticleDataStatisticsVO.builder()
                            .serial(i)
                            .viewNum(0)
                            .upNum(0)
                            .downNum(0)
                            .collectNum(0)
                            .commentNum(0)
                            .build());
                }
            }
            System.out.println(i);
        }
        return dataStatisticsVOList;
    }
}
