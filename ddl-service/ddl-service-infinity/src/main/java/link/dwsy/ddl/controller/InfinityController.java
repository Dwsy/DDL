package link.dwsy.ddl.controller;

import link.dwsy.ddl.XO.Enum.InfinityType;
import link.dwsy.ddl.XO.RB.InfinityRB;
import link.dwsy.ddl.annotation.AuthAnnotation;
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
import link.dwsy.ddl.support.UserSupport;
import link.dwsy.ddl.util.PRHelper;
import link.dwsy.ddl.util.PageData;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

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
        return new PageData<>(infinityPage);
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
                        (id, InfinityType.TweetReply, null, pageRequest);
        List<Infinity> childCommentsContent = childComments.getContent();
        childCommentsContent.forEach(childComment -> {
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
            infinityPage = infinityRepository.findByDeletedFalseAndType(InfinityType.Tweet, pageRequest);
        }
        LoginUserInfo currentUser = userSupport.getCurrentUser();
        PageRequest replyPageRequest = PRHelper.order("DESC", new String[]{"createTime"}, 1, 8);
        infinityPage.forEach(infinity -> {
            infinity.setImgUrlList();
            infinity.noRetCreateUser();
            long id = infinity.getId();
            Page<Infinity> childComments = infinityRepository
                    .findByDeletedFalseAndParentTweetIdAndType
                            (id, InfinityType.TweetReply, replyPageRequest);
            List<Infinity> childCommentsContent = childComments.getContent();
            if (currentUser != null) {
                Long currentUserId = currentUser.getId();
                if (infinityRepository.existsByDeletedFalseAndUser_IdAndParentTweetIdAndType(currentUserId, id, InfinityType.upTweet)) {
                    infinity.setUp(true);
                }
                //--
                childCommentsContent.forEach(childComment -> {
                    childComment.noRetCreateUser();
                    childComment.setImgUrlList();
                    if (infinityRepository.existsByDeletedFalseAndUser_IdAndParentTweetIdAndType(currentUserId, childComment.getId(), InfinityType.upTweet)) {
                        childComment.setUp(true);
                    }
                });
            }
            infinity.setChildComments(childCommentsContent);
            infinity.setChildCommentTotalPages(childComments.getTotalPages());
            infinity.setChildCommentNum(childComments.getTotalElements());
        });
        return infinityPage;
    }


    @PostMapping
    @AuthAnnotation
    public Infinity sendInfinity(@Validated @RequestBody InfinityRB infinityRB) {
        Long userId = userSupport.getCurrentUser().getId();
        Long infinityClubId = infinityRB.getInfinityClubId();
        List<Long> infinityTopicIds = infinityRB.getInfinityTopicIds();
        InfinityClub infinityClub;
        Infinity infinity = new Infinity();
        if (infinityClubId != null) {
            infinityClub = infinityClubRepository.findById(infinityClubId)
                    .orElseThrow(() -> new CodeException(CustomerErrorCode.INFINITY_CLUB_NOT_EXIST));
            infinity.setInfinityClub(infinityClub);
        }
        if (infinityTopicIds != null) {
//            infinityTopic = infinityTopicRepository.findById(infinityTopicId)
//                    .orElseThrow(() -> new CodeException(CustomerErrorCode.INFINITY_TOPIC_NOT_EXIST));
//            infinity.setInfinityTopic(infinityTopic);
            List<InfinityTopic> infinityTopics = infinityTopicRepository.findByDeletedFalseAndIdIn(infinityTopicIds);
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
        }//todo other
        return infinityRepository.save(infinity);
    }

    @PutMapping("{id}")
    @AuthAnnotation
    public void updateInfinity(@Validated @RequestBody InfinityRB infinityRB, @PathVariable long id) {
        InfinityType infinityType = infinityRB.getType();
        if (!Set.of(InfinityType.Tweet, InfinityType.TweetReply).contains(infinityType)) {
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
        LoginUserInfo currentUser = userSupport.getCurrentUser();
        PageRequest replyPageRequest = PRHelper.order("DESC", new String[]{"createTime"}, 1, 8);

        infinity.setImgUrlList();
        infinity.noRetCreateUser();
//        Page<Infinity> childComments = infinityRepository
//                .findByDeletedFalseAndParentTweetIdAndType
//                        (id, InfinityType.TweetReply, replyPageRequest);
        Page<Infinity> childComments = infinityRepository
                .findByDeletedFalseAndParentTweetIdAndTypeAndReplyUserTweetId
                        (id, InfinityType.TweetReply, 0L, replyPageRequest);
        List<Infinity> childCommentsContent = childComments.getContent();
        if (currentUser != null) {
            Long currentUserId = currentUser.getId();
            if (infinityRepository.existsByDeletedFalseAndUser_IdAndParentTweetIdAndType(currentUserId, id, InfinityType.upTweet)) {
                infinity.setUp(true);
            }
        }
        HashMap<Long, List<Infinity>> commentReplyMap = new HashMap<>();
        childCommentsContent.forEach(childComment -> {
            childComment.noRetCreateUser();
            childComment.setImgUrlList();
            if (currentUser != null) {
                Long currentUserId = currentUser.getId();
                if (infinityRepository.existsByDeletedFalseAndUser_IdAndParentTweetIdAndType(currentUserId, childComment.getId(), InfinityType.upTweet)) {
                    childComment.setUp(true);
                }
            }
            replyPageRequest.withSort(Sort.by(Sort.Direction.ASC, "createTime"));
            Page<Infinity> childCommentPage = infinityRepository.findByDeletedFalseAndParentTweetIdAndTypeAndReplyUserTweetId
                    (id, InfinityType.TweetReply, childComment.getId(), replyPageRequest);
            List<Infinity> commentReplyList = childCommentPage.getContent();
            childComment.setChildCommentNum(childCommentPage.getTotalElements());
            if (commentReplyList.size()!=0) {
                commentReplyList.forEach(commentReply -> {
                    commentReply.noRetCreateUser();
                    commentReply.setImgUrlList();
                    if (currentUser != null) {
                        Long currentUserId = currentUser.getId();
                        if (infinityRepository.existsByDeletedFalseAndUser_IdAndParentTweetIdAndType(currentUserId, commentReply.getId(), InfinityType.upTweet)) {
                            commentReply.setUp(true);
                        }
                    }
                    String replyUserNickname = userRepository.findUserNicknameById(commentReply.getParentUserId());
                    if (replyUserNickname != null) {
                        commentReply.setReplyUserName(replyUserNickname);
                    } else {
                        childComment.setReplyUserName("已注销");
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

//    @PostMapping("reply")
//    @AuthAnnotation
//    public Infinity replyInfinity(@Validated @RequestBody ReplyInfinityRB infinityRB) {
//        Long userId = userSupport.getCurrentUser().getId();
//        long replyId = infinityRB.getReplyId();
//        String content = infinityRB.getContent();
//        Long replyUserTweetId = infinityRB.getReplyUserTweetId();
//        if (replyUserTweetId == null) {
//            boolean exists = infinityRepository.existsByDeletedFalseAndIdAndType(replyId, InfinityType.Tweet);
//            if (!exists) {
//                throw new CodeException(CustomerErrorCode.INFINITY_NOT_EXIST);
//            }
//            Infinity infinity = Infinity.builder()
//                    .type(InfinityType.TweetReply)
//                    .content(content)
//                    .ua(userSupport.getUserAgent())
//                    .user((User) new User().setId(userId))
//                    .parentTweetId(replyId).build();
//
//            return infinityRepository.save(infinity);
//        } else {
//            boolean exists = infinityRepository.existsByDeletedFalseAndIdAndType(replyUserTweetId, InfinityType.TweetReply);
//            if (!exists) {
//                throw new CodeException(CustomerErrorCode.INFINITY_NOT_EXIST);
//            }
//            Infinity infinity = Infinity.builder()
//                    .type(InfinityType.TweetReply)
//                    .content(content)
//                    .user((User) new User().setId(userId))
//                    .parentTweetId(replyId)
//                    .parentUserId(infinityRB.getReplyUserId())
//                    .replyUserTweetId(replyUserTweetId)
//                    .build();
//            return infinityRepository.save(infinity);
//            //todo reply@name:
//        }
//    }

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
                infinityRepository.save(upInfinity);
                infinityRepository.upNumIncrement(id, 1);
                return "点赞成功";
            }
            return "已点赞";
        } else {
            if (userAction != null) {
                infinityRepository.delete(userAction);
                infinityRepository.upNumIncrement(id, -1);
                return "取消点赞成功";
            }
            return "未点赞";
        }
    }
}
