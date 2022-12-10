package link.dwsy.ddl.service.Impl;

import link.dwsy.ddl.XO.Enum.Article.CommentType;
import link.dwsy.ddl.XO.Enum.User.UserActiveType;
import link.dwsy.ddl.XO.VO.UserThumbActiveVO;
import link.dwsy.ddl.core.CustomExceptions.CodeException;
import link.dwsy.ddl.entity.Article.ArticleComment;
import link.dwsy.ddl.entity.Article.ArticleField;
import link.dwsy.ddl.entity.Infinity.Infinity;
import link.dwsy.ddl.entity.QA.QaAnswer;
import link.dwsy.ddl.entity.QA.QaQuestionField;
import link.dwsy.ddl.entity.User.UserActive;
import link.dwsy.ddl.repository.Article.ArticleCommentRepository;
import link.dwsy.ddl.repository.Article.ArticleFieldRepository;
import link.dwsy.ddl.repository.Infinity.InfinityRepository;
import link.dwsy.ddl.repository.QA.QaAnswerRepository;
import link.dwsy.ddl.repository.QA.QaQuestionFieldRepository;
import link.dwsy.ddl.repository.User.UserActiveRepository;
import link.dwsy.ddl.repository.User.UserRepository;
import link.dwsy.ddl.service.UserActiveService;
import link.dwsy.ddl.support.UserSupport;
import link.dwsy.ddl.util.DateUtil;
import link.dwsy.ddl.util.HtmlHelper;
import link.dwsy.ddl.util.PageData;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Author Dwsy
 * @Date 2022/12/7
 */

@Service
public class UserActiveServiceImpl implements UserActiveService {
    @Resource
    private UserActiveRepository userActiveRepository;
    @Resource
    private UserSupport userSupport;

    @Resource
    private UserActiveCommonServiceImpl userActiveCommonService;

    @Resource
    private UserRepository userRepository;

    @Resource
    private InfinityRepository infinityRepository;

    @Resource
    private ArticleFieldRepository articleFieldRepository;

    @Resource
    private ArticleCommentRepository articleCommentRepository;

    @Resource
    private QaQuestionFieldRepository qaQuestionFieldRepository;

    @Resource
    private QaAnswerRepository qaAnswerRepository;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public String checkIn() {
        Date tomorrowZero = DateUtil.getTomorrowZero();
        Date zero = DateUtil.getZero();
        Long id = userSupport.getCurrentUser().getId();
        if (redisTemplate.opsForValue().get("checkIn:" + id) != null) {
            throw new CodeException("今日已签到");

        }
        if (userActiveRepository.existsByUserIdIsAndUserActiveTypeAndCreateTimeBetween
                (id, UserActiveType.Check_In, zero, tomorrowZero)) {
            throw new CodeException("今日已签到");
        }
        userActiveCommonService.ActiveLogUseMQ(UserActiveType.Check_In, null);
        redisTemplate.opsForValue().set("checkIn:" + id, "true", DateUtil.getRemainSecondsOneDay(), TimeUnit.SECONDS);
        return "签到成功";
    }

    @Override
    @Nullable
    public PageData<UserThumbActiveVO> getUserThumbActive(long uid, UserActiveType type, PageRequest pageRequest) {
        switch (type) {
            case Thumb_Tweet:
                return getUserThumbTweetById(uid, pageRequest);
            case UP_Article:
                return getUserUPArticleById(uid, pageRequest);
            case UP_Question:
                return getUserUPQuestionById(uid, pageRequest);
            case UP_Question_Answer:
                return getUserUPQuestionAnswerById(uid, pageRequest);
            default:
                return null;
        }
    }

    private PageData<UserThumbActiveVO> getUserUPQuestionAnswerById(long uid, PageRequest pageRequest) {
        Page<UserActive> userActivePage = userActiveRepository.findByDeletedFalseAndUserIdAndUserActiveType(uid, UserActiveType.UP_Question_Answer, pageRequest);
        List<Long> sourceIdList = userActivePage.getContent().stream().map(UserActive::getSourceId).collect(Collectors.toList());
        Map<Long, Date> createTimeMap = userActivePage.getContent().stream().collect(Collectors.toMap(UserActive::getSourceId, UserActive::getCreateTime));
        List<QaAnswer> qaAnswerList = qaAnswerRepository.findByDeletedFalseAndIdIn(sourceIdList);
        ArrayList<UserThumbActiveVO> activeVOArrayList = new ArrayList<>();
        for (QaAnswer qaAnswer : qaAnswerList) {
            UserThumbActiveVO build = UserThumbActiveVO.builder()
                    .userActiveType(UserActiveType.Answer_Question)
                    .user(qaAnswer.getUser())
                    .id(qaAnswer.getId())
                    .title(qaQuestionFieldRepository.getTitleByAnswerId(qaAnswer.getId()))
                    .summary(HtmlHelper.toPure(qaAnswer.getTextHtml()))
                    .createTime(createTimeMap.get(qaAnswer.getId())).build();
            activeVOArrayList.add(build);
        }
        return new PageData<>(userActivePage, activeVOArrayList);
    }

    private PageData<UserThumbActiveVO> getUserUPQuestionById(long uid, PageRequest pageRequest) {
        Page<UserActive> userActivePage = userActiveRepository.findByDeletedFalseAndUserIdAndUserActiveType(uid, UserActiveType.UP_Question, pageRequest);
        List<Long> sourceIdList = userActivePage.getContent().stream().map(UserActive::getSourceId).collect(Collectors.toList());
        List<QaQuestionField> questionFieldList = qaQuestionFieldRepository.findAllByDeletedFalseAndIdIn(sourceIdList);
        ArrayList<UserThumbActiveVO> activeVOArrayList = new ArrayList<>();
        Map<Long, Date> createTimeMap = userActivePage.getContent().stream().collect(Collectors.toMap(UserActive::getSourceId, UserActive::getCreateTime));

        for (QaQuestionField questionField : questionFieldList) {
            UserThumbActiveVO build = UserThumbActiveVO.builder()
                    .userActiveType(UserActiveType.UP_Question)
                    .user(questionField.getUser())
                    .id(questionField.getId())
                    .title(questionField.getTitle())
                    .summary(questionField.getSummary())
                    .createTime(createTimeMap.get(questionField.getId())).build();
            activeVOArrayList.add(build);
        }
        return new PageData<>(userActivePage, activeVOArrayList);
    }

    private PageData<UserThumbActiveVO> getUserThumbTweetById(long uid, PageRequest pageRequest) {
        Page<UserActive> userActivePage = userActiveRepository.findByDeletedFalseAndUserIdAndUserActiveType(uid, UserActiveType.Thumb_Tweet, pageRequest);
        List<Long> sourceIdList = userActivePage.getContent().stream().map(UserActive::getSourceId).collect(Collectors.toList());
        List<Infinity> infinityList = infinityRepository.findByDeletedFalseAndIdIn(sourceIdList);
        ArrayList<UserThumbActiveVO> activeVOArrayList = new ArrayList<>();
        Map<Long, Date> createTimeMap = userActivePage.getContent().stream().collect(Collectors.toMap(UserActive::getSourceId, UserActive::getCreateTime));
        for (Infinity infinity : infinityList) {
            UserThumbActiveVO build = UserThumbActiveVO.builder()
                    .userActiveType(UserActiveType.Thumb_Tweet)
                    .user(infinity.getUser())
                    .id(infinity.getId())
                    .summary(infinity.getContent())
                    .createTime(createTimeMap.get(infinity.getId())).build();
            if (infinity.getImgUrl1() != null) {
                build.setBanner(infinity.getImgUrl1());
            }
            activeVOArrayList.add(build);
        }
        return new PageData<>(userActivePage, activeVOArrayList);
    }

    private PageData<UserThumbActiveVO> getUserUPArticleById(long uid, PageRequest pageRequest){
        Page<UserActive> userActivePage = userActiveRepository.findByDeletedFalseAndUserIdAndUserActiveType(uid, UserActiveType.UP_Article, pageRequest);
        List<Long> sourceIdList = userActivePage.getContent().stream().map(UserActive::getSourceId).collect(Collectors.toList());
        ArrayList<UserThumbActiveVO> activeVOArrayList = new ArrayList<>();
        List<ArticleField> articleFieldList = articleFieldRepository.findByDeletedFalseAndIdIn(sourceIdList);
        Map<Long, Date> createTimeMap = userActivePage.getContent().stream().collect(Collectors.toMap(UserActive::getSourceId, UserActive::getCreateTime));
        for (ArticleField articleField : articleFieldList) {
            UserThumbActiveVO build = UserThumbActiveVO.builder()
                    .userActiveType(UserActiveType.UP_Article)
                    .user(articleField.getUser())
                    .id(articleField.getId())
                    .title(articleField.getTitle())
                    .summary(articleField.getSummary())
                    .banner(articleField.getBanner())
                    .createTime(createTimeMap.get(articleField.getId())).build();
            activeVOArrayList.add(build);
        }
        return new PageData<>(userActivePage, activeVOArrayList);
    }
    private PageData<UserThumbActiveVO> getUserUPArticleByIdold(long uid, PageRequest pageRequest) {
        Page<ArticleComment> UP_ArticleActive = articleCommentRepository.
                findByDeletedFalseAndUser_IdAndParentCommentIdAndCommentType(uid, -1, CommentType.up, pageRequest);
        List<Long> list = UP_ArticleActive.stream().map(articleComment ->
                articleComment.getArticleField().getId()).collect(Collectors.toList());
        List<ArticleField> articleFieldList = articleFieldRepository.findAllById(list);
        ArrayList<UserThumbActiveVO> UserThumbActiveVOList = new ArrayList<>();
        for (ArticleField articleField : articleFieldList) {
            UserThumbActiveVO build = UserThumbActiveVO.builder()
                    .id(articleField.getId())
                    .user(articleField.getUser())
                    .title(articleField.getTitle())
                    .banner(articleField.getBanner())
                    .summary(articleField.getSummary())
                    .createTime(articleField.getCreateTime())
                    .userActiveType(UserActiveType.UP_Article)
                    .build();
            UserThumbActiveVOList.add(build);
        }

        PageData<UserThumbActiveVO> pageData = new PageData<>();
        pageData.setContent(UserThumbActiveVOList);
        pageData.setPageSize(UP_ArticleActive.getSize());
        pageData.setTotalPages(UP_ArticleActive.getTotalPages());
        pageData.setTotalElements(UP_ArticleActive.getTotalElements());
        pageData.setPageNumber(UP_ArticleActive.getNumber());
        return pageData;
    }

}
