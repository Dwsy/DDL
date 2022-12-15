package link.dwsy.ddl.controller;

import link.dwsy.ddl.XO.Projection.DailyData_Id_And_ScoreCount;
import link.dwsy.ddl.XO.Projection.DailyData_Id_And_ScoreCountObj;
import link.dwsy.ddl.XO.VO.ArticleFieldRankVO;
import link.dwsy.ddl.XO.VO.InfinityClubRankVO;
import link.dwsy.ddl.XO.VO.InfinityTopicRankVO;
import link.dwsy.ddl.XO.VO.QuestionFieldRankVO;
import link.dwsy.ddl.constants.task.RedisInfinityRecordHashKey;
import link.dwsy.ddl.constants.task.RedisRecordKey;
import link.dwsy.ddl.core.CustomExceptions.CodeException;
import link.dwsy.ddl.core.constant.CustomerErrorCode;
import link.dwsy.ddl.entity.Article.ArticleField;
import link.dwsy.ddl.entity.Infinity.InfinityClub;
import link.dwsy.ddl.entity.Infinity.InfinityTopic;
import link.dwsy.ddl.entity.QA.QaQuestionField;
import link.dwsy.ddl.repository.Article.ArticleFieldRepository;
import link.dwsy.ddl.repository.Article.ArticleGroupRepository;
import link.dwsy.ddl.repository.Data.Article.ArticleDailyDataRepository;
import link.dwsy.ddl.repository.Data.Question.QuestionDailyDataRepository;
import link.dwsy.ddl.repository.Infinity.InfinityClubRepository;
import link.dwsy.ddl.repository.Infinity.InfinityRepository;
import link.dwsy.ddl.repository.Infinity.InfinityTopicRepository;
import link.dwsy.ddl.repository.QA.QaGroupRepository;
import link.dwsy.ddl.repository.QA.QaQuestionFieldRepository;
import link.dwsy.ddl.task.ArticleDailyDataTask;
import link.dwsy.ddl.task.QuestionDailyDataTask;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rank")
public class RankController {

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

    @GetMapping("article")
    public List<ArticleFieldRankVO> getArticleRankList(
            @RequestParam(name = "daysAgo", required = false, defaultValue = "1") int daysAgo,
            @RequestParam(name = "size", required = false, defaultValue = "30") int size,
            @RequestParam(name = "groupId", required = false) Long groupId) {
        List<DailyData_Id_And_ScoreCount> daysRankList;
        if (daysAgo == 1) {
            daysRankList = dailyDataIdAndScoreCounts(RedisRecordKey.RedisArticleRecordToDayKey, size);
        } else {
            daysAgo += 1;
            if (groupId != null) {
                if (!articleGroupRepository.existsById(groupId)) {
                    throw new CodeException(CustomerErrorCode.ArticleGroupNotFound);
                }
                daysRankList = articleDailyDataRepository.getNDaysRankByGroup(LocalDate.now().minusDays(daysAgo), groupId, 10);
            } else {
                daysRankList = articleDailyDataRepository.getNDaysRank(LocalDate.now().minusDays(daysAgo), size);
            }

        }

        List<Long> articleIdList = daysRankList.stream().map(DailyData_Id_And_ScoreCount::getId).collect(Collectors.toList());

        Map<Long, Integer> idScoreMap = daysRankList.stream().collect(Collectors.toMap(DailyData_Id_And_ScoreCount::getId, DailyData_Id_And_ScoreCount::getScoreCount));

        List<ArticleField> articleFieldList = articleFieldRepository.findByDeletedFalseAndIdIn(articleIdList).stream().sorted((o1, o2) -> {
            int score1 = idScoreMap.get(o1.getId());
            int score2 = idScoreMap.get(o2.getId());
            return score2 - score1;
        }).collect(Collectors.toList());
        return articleFieldList.stream().map(articleField -> new ArticleFieldRankVO(articleField, idScoreMap.get(articleField.getId()))).collect(Collectors.toList());
    }

    @NotNull
    private List<DailyData_Id_And_ScoreCount> dailyDataIdAndScoreCounts(String redisArticleRecordToDayKey, int size) {
        List<DailyData_Id_And_ScoreCount> redisRankList = new ArrayList<>();
        Set<ZSetOperations.TypedTuple<String>> typedTuples =
                redisTemplate.opsForZSet().reverseRangeWithScores(redisArticleRecordToDayKey, 0, size - 1);
        if (typedTuples != null) {
            typedTuples.forEach(p -> {
                String value = p.getValue();
                Double score = p.getScore();
                if (value != null && score != null) {
                    redisRankList.add(new DailyData_Id_And_ScoreCountObj(Long.parseLong(value), score.intValue()));
                }
            });
        }
        return redisRankList;
    }

    @GetMapping("question")
    public List<QuestionFieldRankVO> getQuestionRankList(
            @RequestParam(name = "daysAgo", required = false, defaultValue = "1") int daysAgo,
            @RequestParam(name = "size", required = false, defaultValue = "30") int size,
            @RequestParam(name = "groupId", required = false) Long groupId) {
        List<DailyData_Id_And_ScoreCount> daysRankList;
        if (daysAgo == 1) {
            daysRankList = dailyDataIdAndScoreCounts(RedisRecordKey.RedisQuestionRecordToDayKey, size);
        } else {
            daysAgo += 1;

            if (groupId != null) {
                if (!qaGroupRepository.existsById(groupId)) {
                    throw new CodeException(CustomerErrorCode.GroupNotFound);
                }
                daysRankList = questionDailyDataRepository.getNDaysRankByGroup(LocalDate.now().minusDays(daysAgo), groupId, 10);
            } else {
                daysRankList = questionDailyDataRepository.getNDaysRank(LocalDate.now().minusDays(daysAgo), size);
            }
        }

        List<Long> questionIdlist = daysRankList.stream().map(DailyData_Id_And_ScoreCount::getId).collect(Collectors.toList());

        Map<Long, Integer> idScoreMap = daysRankList.stream().collect(Collectors.toMap(DailyData_Id_And_ScoreCount::getId, DailyData_Id_And_ScoreCount::getScoreCount));
        List<QaQuestionField> questionFields = qaQuestionFieldRepository.findAllByDeletedFalseAndIdIn(questionIdlist).stream().sorted((o1, o2) -> {
            int score1 = idScoreMap.get(o1.getId());
            int score2 = idScoreMap.get(o2.getId());
            return score2 - score1;
        }).collect(Collectors.toList());
        return questionFields.stream().map(questionField -> new QuestionFieldRankVO(questionField, idScoreMap.get(questionField.getId()))).collect(Collectors.toList());
    }

    @GetMapping("infinity/topic")
    public List<InfinityTopicRankVO> infinityTopicRank(
            @RequestParam(name = "daysAgo", required = false, defaultValue = "1") int daysAgo,
            @RequestParam(name = "size", required = false, defaultValue = "15") int size) {
        List<DailyData_Id_And_ScoreCount> daysRankList;
        if (daysAgo == 1) {
            daysRankList = dailyDataIdAndScoreCounts(RedisRecordKey.RedisInfinityTopicRecordToDayKey, size);
        } else {
            return null;
        }
        List<Long> infinityTopicIds = daysRankList.stream().map(DailyData_Id_And_ScoreCount::getId).collect(Collectors.toList());

        Map<Long, Integer> idScoreMap = daysRankList.stream().collect(Collectors.toMap(DailyData_Id_And_ScoreCount::getId, DailyData_Id_And_ScoreCount::getScoreCount));
        List<InfinityTopic> infinityTopics = infinityTopicRepository.findByDeletedFalseAndIdIn(infinityTopicIds).stream().sorted((o1, o2) -> {
            int score1 = idScoreMap.get(o1.getId());
            int score2 = idScoreMap.get(o2.getId());
            return score2 - score1;
        }).collect(Collectors.toList());
        List<InfinityTopicRankVO> topicRankVOS = infinityTopics.stream().map(infinityTopic -> new InfinityTopicRankVO(infinityTopic, idScoreMap.get(infinityTopic.getId())))
                .collect(Collectors.toList());
        topicRankVOS.forEach(i -> {//今日+以前
            Map<Object, Object> dataMap = redisTemplate.opsForHash().entries(RedisRecordKey.RedisInfinityTopicRecordKey + i.getId());
            if (!dataMap.isEmpty()) {
                int view = getMapValue(dataMap, RedisInfinityRecordHashKey.view);
                int quote = getMapValue(dataMap, RedisInfinityRecordHashKey.quote);
                int share = getMapValue(dataMap, RedisInfinityRecordHashKey.share);
                int reply = getMapValue(dataMap, RedisInfinityRecordHashKey.reply);
                int follow = getMapValue(dataMap, RedisInfinityRecordHashKey.follow);
                i.setViewNum(view + i.getViewNum());
                i.setInfinityNum(quote + i.getInfinityNum());
            }
        });
        return topicRankVOS;
//        .map(i->{
//            Map<Object, Object> dataMap = redisTemplate.opsForHash().entries(RedisRecordKey.RedisInfinityTopicRecordKey + i.getId());
//            if (!dataMap.isEmpty()) {
//                int view = getMapValue(dataMap, RedisInfinityRecordHashKey.view);
//                int quote = getMapValue(dataMap, RedisInfinityRecordHashKey.quote);
//                int share = getMapValue(dataMap, RedisInfinityRecordHashKey.share);
//                int reply = getMapValue(dataMap, RedisInfinityRecordHashKey.reply);
//                int follow = getMapValue(dataMap, RedisInfinityRecordHashKey.follow);
//                i.setViewNum(view+i.getViewNum());
//                i.s(view+i.getViewNum());
//            }
//            return i;
//        })
//        return infinityTopics.stream().map(infinityTopic -> new InfinityTopicRankVO(infinityTopic, idScoreMap.get(infinityTopic.getId()))).collect(Collectors.toList());
    }

    @GetMapping("infinity/Club")
    public List<InfinityClubRankVO> infinityClubRank(
            @RequestParam(name = "daysAgo", required = false, defaultValue = "1") int daysAgo,
            @RequestParam(name = "size", required = false, defaultValue = "15") int size) {
        List<DailyData_Id_And_ScoreCount> daysRankList;
        if (daysAgo == 1) {
            daysRankList = dailyDataIdAndScoreCounts(RedisRecordKey.RedisInfinityClubRecordToDayKey, size);
        } else {
            return null;
        }
        List<Long> infinityTopicIds = daysRankList.stream().map(DailyData_Id_And_ScoreCount::getId).collect(Collectors.toList());

        Map<Long, Integer> idScoreMap = daysRankList.stream().collect(Collectors.toMap(DailyData_Id_And_ScoreCount::getId, DailyData_Id_And_ScoreCount::getScoreCount));
        List<InfinityClub> infinityTopics = infinityClubRepository.findByDeletedFalseAndIdIn(infinityTopicIds).stream().sorted((o1, o2) -> {
            int score1 = idScoreMap.get(o1.getId());
            int score2 = idScoreMap.get(o2.getId());
            return score2 - score1;
        }).collect(Collectors.toList());
        return infinityTopics.stream().map(infinityTopic -> new InfinityClubRankVO(infinityTopic, idScoreMap.get(infinityTopic.getId()))).collect(Collectors.toList());
    }

    @GetMapping("q")
    public String test1() {
        questionDailyDataTask.record();
        return "o~~0";
    }

    private int getMapValue(Map<Object, Object> map, RedisInfinityRecordHashKey key) {
        if (map.get(key.toString()) == null) {
            return 0;
        }
        return Integer.parseInt((String) map.get(key.toString()));
    }
}
