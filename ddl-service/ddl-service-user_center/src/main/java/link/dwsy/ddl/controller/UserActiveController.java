package link.dwsy.ddl.controller;

import link.dwsy.ddl.XO.Enum.Article.CommentType;
import link.dwsy.ddl.XO.Enum.User.UserActiveType;
import link.dwsy.ddl.XO.VO.UserThumbActiveVO;
import link.dwsy.ddl.annotation.AuthAnnotation;
import link.dwsy.ddl.core.CustomExceptions.CodeException;
import link.dwsy.ddl.core.constant.CustomerErrorCode;
import link.dwsy.ddl.entity.Article.ArticleComment;
import link.dwsy.ddl.entity.Article.ArticleField;
import link.dwsy.ddl.repository.Article.ArticleCommentRepository;
import link.dwsy.ddl.repository.Article.ArticleFieldRepository;
import link.dwsy.ddl.repository.User.UserActiveRepository;
import link.dwsy.ddl.repository.User.UserRepository;
import link.dwsy.ddl.service.Impl.UserActiveServiceImpl;
import link.dwsy.ddl.support.UserSupport;
import link.dwsy.ddl.util.DateUtil;
import link.dwsy.ddl.util.PRHelper;
import link.dwsy.ddl.util.PageData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Author Dwsy
 * @Date 2022/9/5
 */
@RestController
@RequestMapping("/active")
public class UserActiveController {

    @Resource
    private UserActiveRepository userActiveRepository;
    @Resource
    private UserSupport userSupport;

    @Resource
    private UserActiveServiceImpl userActiveService;

    @Resource
    private UserRepository userRepository;

    @Resource
    private ArticleFieldRepository articleFieldRepository;

    @Resource
    private ArticleCommentRepository articleCommentRepository;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @PostMapping("/check")
    @AuthAnnotation(Level = 0)
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
        userActiveService.ActiveLogUseMQ(UserActiveType.Check_In, null);
        redisTemplate.opsForValue().set("checkIn:" + id, "true", DateUtil.getRemainSecondsOneDay(), TimeUnit.SECONDS);
        return "签到成功";
    }

    @GetMapping("/thumb/{uid}")
    public PageData<UserThumbActiveVO> getUserThumbActive(@PathVariable long uid,
                                                          @RequestParam(required = false, defaultValue = "ASC", name = "order") String order,
                                                          @RequestParam(required = false, defaultValue = "createTime", name = "properties") String[] properties,
                                                          @RequestParam(required = false, defaultValue = "1", name = "page") int page,
                                                          @RequestParam(required = false, defaultValue = "10", name = "size") int size,
                                                          @RequestParam(name = "type") int userActiveType) {
        {
            if (!userRepository.existsById(uid)) {
                throw new CodeException(CustomerErrorCode.UserNotExist);
            }
            PageRequest pageRequest = PRHelper.order(order, properties, page, size);

            UserActiveType type = UserActiveType.values()[userActiveType];

            if (type == UserActiveType.UP_Article) {
                Page<ArticleComment> UP_ArticleActive = articleCommentRepository.
                        findByDeletedFalseAndUser_IdAndParentCommentIdAndCommentType(uid, -1, CommentType.up, pageRequest);
                List<Long> list = UP_ArticleActive.stream().map(articleComment ->
                        articleComment.getArticleField().getId()).collect(Collectors.toList());
//                for (ArticleComment articleComment : UP_ArticleActive.getContent()) {
//
//                }

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
            return null;


//        Set<UserActiveType> activeTypes = Set.of
//                (UserActiveType.UP_Article, UserActiveType.UP_Question, UserActiveType.UP_Question_Answer);
//
//
//        Page<UserActive> userActiveS = userActiveRepository
//                .findByDeletedFalseAndUserIdAndUserActiveTypeIn(uid, activeTypes, pageRequest);
//
//        List<UserActive> userActiveContent = userActiveS.getContent();

//        var UP_ArticleActive = userActiveContent.stream()
//                .filter(userActive -> userActive.getUserActiveType() == UserActiveType.UP_Article).map(UserActive::getSourceId).collect(Collectors.toList());
//        var UP_QuestionActive = userActiveContent.stream()todo
//                .filter(userActive -> userActive.getUserActiveType() == UserActiveType.UP_Question).map(UserActive::getSourceId).collect(Collectors.toList());
//        var UP_Question_AnswerActive = userActiveContent.stream()
//                .filter(userActive -> userActive.getUserActiveType() == UserActiveType.UP_Question_Answer).map(UserActive::getSourceId).collect(Collectors.toList());
//        List<UserThumbActiveVO> userThumbActiveVO = new ArrayList<>();
//        List<ArticleField> articleFieldList = articleFieldRepository.findAllById(UP_ArticleActive);
//        for (ArticleField articleField : articleFieldList) {
//            UserThumbActiveVO build = UserThumbActiveVO.builder()
//                    .id(articleField.getId())
//                    .user(articleField.getUser())
//                    .title(articleField.getTitle())
//                    .banner(articleField.getBanner())
//                    .summary(articleField.getSummary())
//                    .createTime(articleField.getCreateTime())
//                    .userActiveType(UserActiveType.UP_Article)
//                    .build();
//            userThumbActiveVO.add(build);
//        }
//        PageData<UserThumbActiveVO> pageData = new PageData<>();
//        pageData.setContent(userThumbActiveVO);
//        pageData.setPageSize(userActiveS.getSize());
//        pageData.setTotalPages(userActiveS.getTotalPages());
//        pageData.setTotalElements(userActiveS.getTotalElements());
//        pageData.setPageNumber(userActiveS.getNumber());

//        for (UserActive userActive : userActiveContent) {
//            switch (userActive.getUserActiveType()) {
//                case UP_Article:
//                    Long sourceId = userActive.getSourceId();
//                    articleFieldRepository.find
//                    userThumbActiveVO.add();
//                    break;
//                case UP_Question:
//
//                    break;
//                case UP_Question_Answer:
//
//                    break;
//            }
//        }
//            return pageData;
        }
    }

}
