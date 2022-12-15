package link.dwsy.ddl.controller;

import link.dwsy.ddl.XO.Enum.InfinityType;
import link.dwsy.ddl.XO.Enum.User.UserActiveType;
import link.dwsy.ddl.XO.RB.InfinityRB;
import link.dwsy.ddl.annotation.AuthAnnotation;
import link.dwsy.ddl.constants.OtherConstants;
import link.dwsy.ddl.constants.task.RedisInfinityRecordHashKey;
import link.dwsy.ddl.core.CustomExceptions.CodeException;
import link.dwsy.ddl.core.constant.CustomerErrorCode;
import link.dwsy.ddl.core.domain.LoginUserInfo;
import link.dwsy.ddl.entity.Infinity.Infinity;
import link.dwsy.ddl.entity.Infinity.InfinityClub;
import link.dwsy.ddl.entity.Infinity.InfinityTopic;
import link.dwsy.ddl.entity.User.User;
import link.dwsy.ddl.repository.Infinity.InfinityClubRepository;
import link.dwsy.ddl.repository.Infinity.InfinityRepository;
import link.dwsy.ddl.repository.Infinity.InfinityTopicRepository;
import link.dwsy.ddl.repository.User.UserRepository;
import link.dwsy.ddl.service.Impl.Infinity.InfinityClubRedisRecordService;
import link.dwsy.ddl.service.Impl.Infinity.InfinityRedisRecordService;
import link.dwsy.ddl.service.Impl.Infinity.InfinityTopicRedisRecordService;
import link.dwsy.ddl.service.Impl.UserActiveCommonServiceImpl;
import link.dwsy.ddl.service.Impl.UserStateService;
import link.dwsy.ddl.service.InfinityCommentService;
import link.dwsy.ddl.support.UserSupport;
import link.dwsy.ddl.util.PRHelper;
import link.dwsy.ddl.util.PageData;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author Dwsy
 * @Date 2022/11/23
 */
@RestController
@RequestMapping("infinity")
@Slf4j
public class InfinityController {
    @Resource
    private UserSupport userSupport;

    @Resource
    private InfinityRepository infinityRepository;

    @Resource
    private InfinityTopicRepository infinityTopicRepository;

    @Resource
    private InfinityClubRepository infinityClubRepository;

    @Resource
    private UserRepository userRepository;

    @Resource
    private UserStateService userStateService;
    @Resource
    private InfinityRedisRecordService infinityRedisRecordService;

    @Resource
    private InfinityCommentService infinityCommentService;
    @Resource
    private InfinityClubRedisRecordService infinityClubRedisRecordService;
    @Resource
    private InfinityTopicRedisRecordService infinityTopicRedisRecordService;
    @Resource
    private UserActiveCommonServiceImpl userActiveCommonService;

    @GetMapping("list")
    public PageData<Infinity> getInfinityPageList(
            @RequestParam(required = false, defaultValue = "DESC", name = "order") String order,
            @RequestParam(required = false, defaultValue = "createTime", name = "properties") String[] properties,
            @RequestParam(required = false, defaultValue = "1", name = "page") int page,
            @RequestParam(required = false, defaultValue = "8", name = "size") int size,
            @RequestParam(required = false, defaultValue = "0", name = "clubId") long clubId,
            @RequestParam(required = false, defaultValue = "0", name = "topicId") long topicId) {
        if (size < 1)
            throw new CodeException(CustomerErrorCode.ParamError);
        PageRequest pageRequest = PRHelper.order(order, properties, page, size);
        Page<Infinity> infinityPage = getInfinityPage(clubId, topicId, pageRequest);
//        for (Infinity infinity : infinityPage) {
//            userStateService.cancellationUserHandel(infinity.getUser());
//        }
        List<Infinity> content = infinityPage.getContent();
        List<Infinity> infinities = content.stream().filter(infinity -> !infinity.getUser().getDeleted()).collect(Collectors.toList());
        return new PageData<>(infinityPage, infinities);
    }

    //todo
//    @GetMapping("childComments/{tweetId}")
    @GetMapping("childComments/{id}")
    public PageData<Infinity> getInfinityChildCommentPageList(
            @RequestParam(required = false, defaultValue = "DESC", name = "order") String order,
            @RequestParam(required = false, defaultValue = "createTime", name = "properties") String[] properties,
            @RequestParam(required = false, defaultValue = "1", name = "page") int page,
            @RequestParam(required = false, defaultValue = "8", name = "size") int size,
            @PathVariable long id
    ) {
        if (size < 1)
            throw new CodeException(CustomerErrorCode.ParamError);
        LoginUserInfo currentUser = userSupport.getCurrentUser();
        PageRequest pageRequest = PRHelper.order(order, properties, page, size);
//        Page<Infinity> childComments = infinityRepository
//                .findByDeletedFalseAndParentTweetIdAndType
//                        (id, InfinityType.TweetReply, pageRequest);
        Page<Infinity> childComments = infinityRepository
                .findByDeletedFalseAndParentTweetIdAndTypeAndReplyUserTweetId
                        (id, InfinityType.TweetCommentOrReply, null, pageRequest);
        if (childComments == null) {
            return null;
        }
        List<Infinity> childCommentsContent = childComments.getContent();
        childCommentsContent.forEach(childComment -> {
            userStateService.cancellationUserHandel(childComment.getUser());

            childComment.noRetCreateUser();
            childComment.setImgUrlList();
            if (currentUser != null) {
                if (infinityRepository.existsByDeletedFalseAndUser_IdAndParentTweetIdAndType(currentUser.getId(), childComment.getId(), InfinityType.upTweet)) {
                    childComment.setUp(true);
                }
            }
        });
        return new PageData<>(childComments);
    }

    @NotNull
    private Page<Infinity> getInfinityPage(long clubId, long topicId, PageRequest pageRequest) {
        Page<Infinity> infinityPage;
        if (clubId != 0) {
            infinityPage = infinityRepository.findByDeletedFalseAndInfinityClub_IdAndType(clubId, InfinityType.Tweet, pageRequest);
        } else if (topicId != 0) {
//            todo
            infinityPage = infinityRepository.findByDeletedFalseAndInfinityTopics_IdInAndType(Set.of(topicId), InfinityType.Tweet, pageRequest);

        } else {
            infinityPage = infinityRepository.findByDeletedFalseAndTypeIn(List.of(InfinityType.Tweet, InfinityType.Article, InfinityType.Tweet, InfinityType.Question), pageRequest);
        }
        LoginUserInfo currentUser = userSupport.getCurrentUser();
        PageRequest replyPageRequest = PRHelper.order("DESC", new String[]{"createTime"}, 1, 8);
        infinityPage.forEach(infinity -> {
            userStateService.cancellationUserHandel(infinity.getUser());
            infinity.setImgUrlList();
            infinity.noRetCreateUser();
            long id = infinity.getId();
            Page<Infinity> childComments = infinityRepository
                    .findByDeletedFalseAndParentTweetIdAndType
                            (id, InfinityType.TweetCommentOrReply, replyPageRequest);
            List<Infinity> childCommentsContent = childComments.getContent();
            if (currentUser != null) {
                Long currentUserId = currentUser.getId();
                if (infinityRepository.existsByDeletedFalseAndUser_IdAndParentTweetIdAndType(currentUserId, id, InfinityType.upTweet)) {
                    infinity.setUp(true);
                }
                //--
            }
            childCommentsContent.forEach(childComment -> {
                childComment.noRetCreateUser();
                childComment.setImgUrlList();
                userStateService.cancellationUserHandel(childComment.getUser());
                if (currentUser != null) {
                    if (infinityRepository.existsByDeletedFalseAndUser_IdAndParentTweetIdAndType(currentUser.getId(), childComment.getId(), InfinityType.upTweet)) {
                        childComment.setUp(true);
                    }
                }
            });
            infinity.setChildComments(childCommentsContent);
            infinity.setChildCommentTotalPages(childComments.getTotalPages());
            infinity.setChildCommentNum(childComments.getTotalElements());
        });
        return infinityPage;
    }

    @PostMapping
    @AuthAnnotation
    public Infinity sendInfinity(@Validated @RequestBody InfinityRB infinityRB) {
        //todo 注销禁止评论
        Long userId = userSupport.getCurrentUser().getId();
        Long infinityClubId = infinityRB.getInfinityClubId();
        List<Long> infinityTopicIds = infinityRB.getInfinityTopicIds();
        InfinityClub infinityClub;
        Infinity infinity = new Infinity();
        if (infinityClubId != null) {
            infinityClub = infinityClubRepository.findById(infinityClubId)
                    .orElseThrow(() -> new CodeException(CustomerErrorCode.INFINITY_CLUB_NOT_EXIST));
            infinity.setInfinityClub(infinityClub);
            infinityClubRedisRecordService.record(infinityClubId, RedisInfinityRecordHashKey.quote, 1);
        }
        if (infinityTopicIds != null) {
//            infinityTopic = infinityTopicRepository.findById(infinityTopicId)
//                    .orElseThrow(() -> new CodeException(CustomerErrorCode.INFINITY_TOPIC_NOT_EXIST));
//            infinity.setInfinityTopic(infinityTopic);
            List<InfinityTopic> infinityTopics = infinityTopicRepository.findByDeletedFalseAndIdIn(infinityTopicIds);
            infinityTopics.forEach(infinityTopic -> {
                infinityTopicRedisRecordService.record(infinityTopic.getId(), RedisInfinityRecordHashKey.quote, 1);
            });
            infinityClubRedisRecordService.record(infinityClubId, RedisInfinityRecordHashKey.quote, 1);
            if (infinityTopics.size() != infinityTopicIds.size()) {
                throw new CodeException(CustomerErrorCode.INFINITY_TOPIC_NOT_EXIST);
            }
            infinity.setInfinityTopics(infinityTopics);
        }
        infinity.setUser((User) new User().setId(userId));
        infinity.setContent(infinityRB.getContent());
        infinity.setUa(userSupport.getUserAgent());
        InfinityType type = infinityRB.getType();
        if (type == InfinityType.Tweet) {
            infinity.setType(InfinityType.Tweet);
            infinity.setImgUrlByList(infinityRB.getImgUrlList());
        }//todo OtherConstants
        return infinityRepository.save(infinity);
    }

    @PutMapping("{id}")
    @AuthAnnotation
    public void updateInfinity(@Validated @RequestBody InfinityRB infinityRB, @PathVariable long id) {
        InfinityType infinityType = infinityRB.getType();
        if (!Set.of(InfinityType.Tweet, InfinityType.TweetCommentOrReply).contains(infinityType)) {
            throw new CodeException(CustomerErrorCode.ParamError);
        }
        Long userId = userSupport.getCurrentUser().getId();
        Long infinityClubId = infinityRB.getInfinityClubId();
        List<Long> infinityTopicIds = infinityRB.getInfinityTopicIds();
        InfinityClub infinityClub;
        InfinityTopic infinityTopic;
        Infinity infinity = infinityRepository.findByDeletedFalseAndUser_IdAndIdAndType(userId, id, InfinityType.Tweet);
        if (infinity == null) {
            throw new CodeException(CustomerErrorCode.INFINITY_NOT_EXIST);
        }
        if (infinityClubId != null) {
            infinityClub = infinityClubRepository.findById(infinityClubId)
                    .orElseThrow(() -> new CodeException(CustomerErrorCode.INFINITY_CLUB_NOT_EXIST));
            infinity.setInfinityClub(infinityClub);
        }
        if (infinityTopicIds != null) {
            List<InfinityTopic> infinityTopics = infinityTopicRepository.findByDeletedFalseAndIdIn(infinityTopicIds);
            if (infinityTopics.size() != infinityTopicIds.size()) {
                throw new CodeException(CustomerErrorCode.INFINITY_TOPIC_NOT_EXIST);
            }
            infinity.setInfinityTopics(infinityTopics);
        }
        infinity.setImgUrlByList(infinityRB.getImgUrlList());
        infinity.setContent(infinityRB.getContent());
        infinityRepository.save(infinity);
    }

    @GetMapping("{id}")
    public Infinity getInfinity(@PathVariable long id) {
        Infinity infinity = infinityRepository.findByDeletedFalseAndId(id);
        if (infinity == null) {
            throw new CodeException(CustomerErrorCode.INFINITY_NOT_EXIST);
        }
        userActiveCommonService.ActiveLogUseMQ(UserActiveType.Browse_Infinity, id);
        infinityRepository.viewNumIncrement(id, 1);
        infinityRedisRecordService.record(id, RedisInfinityRecordHashKey.view, 1, infinity);
//        infinityRedisRecordService.record(id, RedisInfinityRecordHashKey.reply, 1, null);

//
//        if (infinity.getInfinityClub()!=null) {
//            infinityTopicRedisRecordService.record(infinity.getInfinityClub().getId(), RedisInfinityRecordHashKey.view, 1);
//        }
//        if (infinity.getInfinityTopics()!=null) {
//            infinity.getInfinityTopics().forEach(infinityTopic -> {
//                infinityTopicRedisRecordService.record(infinityTopic.getId(), RedisInfinityRecordHashKey.view, 1);
//            });
//        }

        userStateService.cancellationUserHandel(infinity.getUser());
        LoginUserInfo currentUser = userSupport.getCurrentUser();
        PageRequest replyPageRequest = PRHelper.order("DESC", new String[]{"createTime"}, 1, 8);

        infinity.setImgUrlList();
        infinity.noRetCreateUser();
//        Page<Infinity> childComments = infinityRepository
//                .findByDeletedFalseAndParentTweetIdAndType
//                        (id, InfinityType.TweetReply, replyPageRequest);
        Page<Infinity> childComments = infinityRepository
                .findByDeletedFalseAndParentTweetIdAndTypeAndReplyUserTweetId
                        (id, InfinityType.TweetCommentOrReply, null, replyPageRequest);
        List<Infinity> childCommentsContent = childComments.getContent();
        if (currentUser != null) {
            Long currentUserId = currentUser.getId();
            if (infinityRepository.existsByDeletedFalseAndUser_IdAndParentTweetIdAndType(currentUserId, id, InfinityType.upTweet)) {
                infinity.setUp(true);
            }
        }
        HashMap<Long, List<Infinity>> commentReplyMap = new HashMap<>();
        childCommentsContent.forEach(childComment -> {
            userStateService.cancellationUserHandel(childComment.getUser());
            childComment.noRetCreateUser();
            childComment.setImgUrlList();
            if (currentUser != null) {
                Long currentUserId = currentUser.getId();
                if (infinityRepository.existsByDeletedFalseAndUser_IdAndParentTweetIdAndType(currentUserId, childComment.getId(), InfinityType.upTweet)) {
                    childComment.setUp(true);
                }
            }
//            replyPageRequest.withSort(Sort.by(Sort.Direction.ASC, "createTime"));
            Page<Infinity> childCommentPage = infinityRepository.findByDeletedFalseAndParentTweetIdAndTypeAndReplyUserTweetId
                    (id, InfinityType.TweetCommentOrReply, childComment.getId(), PRHelper.order("ASC", new String[]{"createTime"}, 1, 8));
            List<Infinity> commentReplyList = childCommentPage.getContent();
            childComment.setChildCommentNum(childCommentPage.getTotalElements());
            if (commentReplyList.size() != 0) {
                commentReplyList.forEach(commentReply -> {
                    userStateService.cancellationUserHandel(commentReply.getUser());
                    commentReply.noRetCreateUser();
                    commentReply.setImgUrlList();
                    if (currentUser != null) {
                        Long currentUserId = currentUser.getId();
                        if (infinityRepository.existsByDeletedFalseAndUser_IdAndParentTweetIdAndType(currentUserId, commentReply.getId(), InfinityType.upTweet)) {
                            commentReply.setUp(true);
                        }
                    }
                    String replyUserNickname = userRepository.getUserNicknameById(commentReply.getParentUserId());
                    if (replyUserNickname != null) {
                        commentReply.setReplyUserName(replyUserNickname);
                    } else {
                        childComment.setReplyUserName(OtherConstants.Cancellation_User_Name);
                    }
                    commentReplyMap.put(childComment.getId(), commentReplyList);
                });

            }
        });
        infinity.setChildCommentReplyMap(commentReplyMap);
        infinity.setChildComments(childCommentsContent);
        infinity.setChildCommentNum(childComments.getTotalElements());
        infinity.setChildCommentTotalPages(childComments.getTotalPages());
        infinity.setCollectNum(childComments.getTotalElements());

        return infinity;
    }

    @GetMapping("user/{id}")
    public PageData<Infinity> getInfinityPageDataByUserId(
            @RequestParam(required = false, defaultValue = "DESC", name = "order") String order,
            @RequestParam(required = false, defaultValue = "createTime", name = "properties") String[] properties,
            @RequestParam(required = false, defaultValue = "1", name = "page") int page,
            @RequestParam(required = false, defaultValue = "10", name = "size") int size,
            @PathVariable long id) {
        User user = userRepository.findUserByIdAndDeletedIsFalse(id);
        if (user == null) {
            throw new CodeException(CustomerErrorCode.UserNotExist);
        }
        PageRequest pageRequest = PRHelper.order(order, properties, page, size);

        Page<Infinity> infinityPage = infinityRepository.findByDeletedFalseAndUser_IdAndTypeNot(id, InfinityType.upTweet, pageRequest);
        infinityPage.getContent().forEach(infinity -> {
            infinity.setImgUrlList();
            infinity.noRetCreateUser();
            if (infinity.getType() == InfinityType.TweetCommentOrReply) {
                Long replyUserId = infinity.getParentUserId();
                if (replyUserId != null) {
                    infinity.setReplyUserName(userRepository.getUserNicknameById(replyUserId));
                }
            }
        });
        return new PageData<>(infinityPage);

    }

    @PostMapping("action/up/{id}")
    @AuthAnnotation
    public String upTweet(@PathVariable long id,
                          @RequestParam(value = "up", required = false, defaultValue = "true") boolean up) {

        Long userId = userSupport.getCurrentUser().getId();
        if (!infinityRepository.existsByDeletedFalseAndIdAndTypeNot(id, InfinityType.upTweet)) {
            throw new CodeException(CustomerErrorCode.INFINITY_NOT_EXIST);
        }
        Infinity userAction = infinityRepository.findByDeletedFalseAndUser_IdAndParentTweetIdAndType(userId, id, InfinityType.upTweet);

        if (up) {
            if (userAction == null) {
                Infinity upInfinity = Infinity.builder()
                        .type(InfinityType.upTweet)
                        .user((User) new User().setId(userId))
                        .ua(userSupport.getUserAgent())
                        .parentTweetId(id).build();
                infinityRedisRecordService.record(id, RedisInfinityRecordHashKey.up, 1, null);
                Infinity save = infinityRepository.save(upInfinity);
                infinityRepository.upNumIncrement(id, 1);
                infinityCommentService.sendActionMqMessage(userId, save, UserActiveType.Thumb_Tweet, id, false);
                return "点赞成功";
            }
            return "已点赞";
        } else {
            if (userAction != null) {
                infinityRepository.delete(userAction);
                infinityRepository.upNumIncrement(id, -1);
                infinityRedisRecordService.record(id, RedisInfinityRecordHashKey.up, -1, null);
                infinityCommentService.sendActionMqMessage(userId, userAction, UserActiveType.Thumb_Tweet, id, true);
                return "取消点赞成功";
            }
            return "未点赞";
        }
    }
}
