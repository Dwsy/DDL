package link.dwsy.ddl.service.Impl;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import link.dwsy.ddl.XO.Enum.Article.CommentType;
import link.dwsy.ddl.XO.Enum.User.UserActiveType;
import link.dwsy.ddl.XO.VO.UserHistoryActiveVO;
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
import java.util.*;
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
        String userAgent = userSupport.getUserAgent();
        Date tomorrowZero = DateUtil.getTomorrowZero();
        Date zero = DateUtil.getZero();
        Long userId = userSupport.getCurrentUser().getId();

        if (!StrUtil.isEmpty(redisTemplate.opsForValue().get("checkIn:" + userId))) {
            throw new CodeException("今日已签到");
        }
        if (userActiveRepository.existsByUserIdIsAndUserActiveTypeAndCreateTimeBetween
                (userId, UserActiveType.Check_In, zero, tomorrowZero)) {
            throw new CodeException("今日已签到");
        }
        userActiveRepository.save(UserActive.builder()
                .userActiveType(UserActiveType.Check_In)
                .userId(userId)
                .ipv4(userSupport.getIpv4())
                .sourceId(null)
                .ua(userAgent).build());
        redisTemplate.opsForValue().set("checkIn:" + userId, "true", DateUtil.getRemainSecondsOneDay(), TimeUnit.SECONDS);
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

    private PageData<UserThumbActiveVO> getUserUPArticleById(long uid, PageRequest pageRequest) {
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

    public PageData<UserHistoryActiveVO> getUserHistoryRecords(String type, PageRequest pageRequest) {
        Long userId = userSupport.getCurrentUser().getId();
        Page<UserActive> historyPage = null;
        ArrayList<UserHistoryActiveVO> historyActiveVOS = new ArrayList<>();
        if (StrUtil.equals(type, "all")) {
            List<UserActiveType> browseTypes = List.of(UserActiveType.Browse_Article, UserActiveType.Browse_QA, UserActiveType.Browse_Infinity);
            historyPage = userActiveRepository.findByDeletedFalseAndUserIdAndUserActiveTypeIn(userId, browseTypes, pageRequest);
            if (ArrayUtil.isEmpty(historyPage.getTotalElements())) {
                return null;
            }
            Map<UserActiveType, List<Long>> userHistoryByActiveTypeGroupListMap =
                    historyPage.getContent().stream().collect
                            (Collectors.groupingBy(UserActive::getUserActiveType, Collectors.mapping(UserActive::getSourceId, Collectors.toList())));
            Map<Long, Date> userHistoryCreateTimeMap =
                    historyPage.getContent().stream().collect(Collectors.toMap(UserActive::getSourceId, UserActive::getCreateTime));
            List<Long> browseArticleIds = userHistoryByActiveTypeGroupListMap.get(UserActiveType.Browse_Article);
            List<Long> browseQAIds = userHistoryByActiveTypeGroupListMap.get(UserActiveType.Browse_QA);
            List<Long> browseInfinityIds = userHistoryByActiveTypeGroupListMap.get(UserActiveType.Browse_Infinity);
            if (!ArrayUtil.isEmpty(browseArticleIds)) {
                List<ArticleField> articleFields = articleFieldRepository.findAllById(browseArticleIds);
                articleFields.forEach(articleField -> {
                    UserHistoryActiveVO build;
                    if (articleField.getDeleted()) {
                        build = UserHistoryActiveVO.builder()
                                .id(articleField.getId())
                                .user(articleField.getUser())
                                .title("内容已失效或被删除")
                                .banner(null)
                                .summary("内容已失效或被删除")
                                .createTime(userHistoryCreateTimeMap.get(articleField.getId()))
                                .userActiveType(UserActiveType.Browse_Article)
                                .build();
                    } else {
                        build = UserHistoryActiveVO.builder()
                                .id(articleField.getId())
                                .user(articleField.getUser())
                                .title(articleField.getTitle())
                                .banner(articleField.getBanner())
                                .summary(articleField.getSummary())
                                .createTime(userHistoryCreateTimeMap.get(articleField.getId()))
                                .userActiveType(UserActiveType.Browse_Article)
                                .build();
                    }
                    historyActiveVOS.add(build);
                });
            }
            if (!ArrayUtil.isEmpty(browseQAIds)) {
                List<QaQuestionField> questionFields = qaQuestionFieldRepository.findAllById(browseQAIds);
                questionFields.forEach(questionField -> {
                    UserHistoryActiveVO build;
                    if (questionField.getDeleted()) {
                        build = UserHistoryActiveVO.builder()
                                .id(questionField.getId())
                                .user(questionField.getUser())
                                .title("内容已失效或被删除")
                                .summary("内容已失效或被删除")
                                .createTime(userHistoryCreateTimeMap.get(questionField.getId()))
                                .userActiveType(UserActiveType.Browse_QA)
                                .build();
                    } else {
                        build = UserHistoryActiveVO.builder()
                                .id(questionField.getId())
                                .user(questionField.getUser())
                                .title(questionField.getTitle())
                                .summary(questionField.getSummary())
                                .createTime(userHistoryCreateTimeMap.get(questionField.getId()))
                                .userActiveType(UserActiveType.Browse_QA)
                                .build();
                    }
                    historyActiveVOS.add(build);
                });
            }
            if (!ArrayUtil.isEmpty(browseInfinityIds)) {
                List<Infinity> infinities = infinityRepository.findAllById(browseInfinityIds);
                infinities.forEach(infinity -> {
                    UserHistoryActiveVO build;
                    if (infinity.getDeleted()) {
                        build = UserHistoryActiveVO.builder()
                                .id(infinity.getId())
                                .user(infinity.getUser())
                                .title("内容已失效或被删除")
                                .summary("内容已失效或被删除")
                                .createTime(userHistoryCreateTimeMap.get(infinity.getId()))
                                .userActiveType(UserActiveType.Browse_Infinity)
                                .build();
                    } else {
                        build = UserHistoryActiveVO.builder()
                                .id(infinity.getId())
                                .user(infinity.getUser())
                                .summary(infinity.getContent())
                                .createTime(userHistoryCreateTimeMap.get(infinity.getId()))
                                .userActiveType(UserActiveType.Browse_Infinity)
                                .build();
                        Optional.ofNullable(infinity.getImgUrl1()).ifPresent(build::setBanner);
                    }
                    historyActiveVOS.add(build);
                });
            }
            List<UserHistoryActiveVO> sortUserHistoryActiveVOS = historyActiveVOS.stream()
                    .sorted(Comparator.comparing(UserHistoryActiveVO::getCreateTime).reversed()).collect(Collectors.toList());
            return new PageData<>(historyPage, sortUserHistoryActiveVOS);
        }
        if (StrUtil.equals(type, "article")) {
            historyPage = userActiveRepository.findByDeletedFalseAndUserIdAndUserActiveType(userId, UserActiveType.Browse_Article, pageRequest);
            if (historyPage.getTotalElements() == 0) {
                return null;
            }
            List<Long> browseArticleIds = historyPage.stream().map(UserActive::getSourceId).collect(Collectors.toList());
            List<ArticleField> articleFields = articleFieldRepository.findAllById(browseArticleIds);
            Map<Long, Date> userHistoryCreateTimeMap =
                    historyPage.getContent().stream().collect(Collectors.toMap(UserActive::getSourceId, UserActive::getCreateTime));
            articleFields.forEach(articleField -> {
                UserHistoryActiveVO build;
                if (articleField.getDeleted()) {
                    build = UserHistoryActiveVO.builder()
                            .id(articleField.getId())
                            .user(articleField.getUser())
                            .title("内容已失效或被删除")
                            .banner(null)
                            .summary("内容已失效或被删除")
                            .createTime(userHistoryCreateTimeMap.get(articleField.getId()))
                            .userActiveType(UserActiveType.Browse_Article)
                            .build();
                } else {
                    build = UserHistoryActiveVO.builder()
                            .id(articleField.getId())
                            .user(articleField.getUser())
                            .title(articleField.getTitle())
                            .banner(articleField.getBanner())
                            .summary(articleField.getSummary())
                            .createTime(userHistoryCreateTimeMap.get(articleField.getId()))
                            .userActiveType(UserActiveType.Browse_Article)
                            .build();
                }
                historyActiveVOS.add(build);
            });
        }
        if (StrUtil.equals(type, "question")) {
            historyPage = userActiveRepository.findByDeletedFalseAndUserIdAndUserActiveType(userId, UserActiveType.Browse_QA, pageRequest);
            if (historyPage.getTotalElements() == 0) {
                return null;
            }
            List<Long> browseQAIds = historyPage.stream().map(UserActive::getSourceId).collect(Collectors.toList());
            List<QaQuestionField> questionFields = qaQuestionFieldRepository.findAllById(browseQAIds);
            Map<Long, Date> userHistoryCreateTimeMap =
                    historyPage.getContent().stream().collect(Collectors.toMap(UserActive::getSourceId, UserActive::getCreateTime));
            questionFields.forEach(questionField -> {
                UserHistoryActiveVO build;
                if (questionField.getDeleted()) {
                    build = UserHistoryActiveVO.builder()
                            .id(questionField.getId())
                            .user(questionField.getUser())
                            .title("内容已失效或被删除")
                            .summary("内容已失效或被删除")
                            .createTime(userHistoryCreateTimeMap.get(questionField.getId()))
                            .userActiveType(UserActiveType.Browse_QA)
                            .build();
                } else {
                    build = UserHistoryActiveVO.builder()
                            .id(questionField.getId())
                            .user(questionField.getUser())
                            .title(questionField.getTitle())
                            .summary(questionField.getSummary())
                            .createTime(userHistoryCreateTimeMap.get(questionField.getId()))
                            .userActiveType(UserActiveType.Browse_QA)
                            .build();
                }
                historyActiveVOS.add(build);
            });
        }
        if (StrUtil.equals(type, "infinity")) {
            historyPage = userActiveRepository.findByDeletedFalseAndUserIdAndUserActiveType(userId, UserActiveType.Browse_Infinity, pageRequest);
            if (historyPage.getTotalElements() == 0) {
                return null;
            }
            List<Long> browseInfinityIds = historyPage.stream().map(UserActive::getSourceId).collect(Collectors.toList());
            List<Infinity> infinities = infinityRepository.findAllById(browseInfinityIds);
            Map<Long, Date> userHistoryCreateTimeMap =
                    historyPage.getContent().stream().collect(Collectors.toMap(UserActive::getSourceId, UserActive::getCreateTime));
            infinities.forEach(infinity -> {
                UserHistoryActiveVO build;
                if (infinity.getDeleted()) {
                    build = UserHistoryActiveVO.builder()
                            .id(infinity.getId())
                            .user(infinity.getUser())
                            .title("内容已失效或被删除")
                            .summary("内容已失效或被删除")
                            .createTime(userHistoryCreateTimeMap.get(infinity.getId()))
                            .userActiveType(UserActiveType.Browse_Infinity)
                            .build();
                } else {
                    build = UserHistoryActiveVO.builder()
                            .id(infinity.getId())
                            .user(infinity.getUser())
                            .summary(infinity.getContent())
                            .createTime(userHistoryCreateTimeMap.get(infinity.getId()))
                            .userActiveType(UserActiveType.Browse_Infinity)
                            .build();
                    Optional.ofNullable(infinity.getImgUrl1()).ifPresent(build::setBanner);
                }
                historyActiveVOS.add(build);
            });
        }
        if (historyPage == null) {
            return null;
        } else {
            return new PageData<>(historyPage, historyActiveVOS.stream()
                    .sorted(Comparator.comparing(UserHistoryActiveVO::getCreateTime).reversed()).collect(Collectors.toList()));
        }


    }
}
