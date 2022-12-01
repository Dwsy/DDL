package link.dwsy.ddl.controller;

import link.dwsy.ddl.XO.Enum.InfinityType;
import link.dwsy.ddl.XO.RB.ReplyInfinityRB;
import link.dwsy.ddl.annotation.AuthAnnotation;
import link.dwsy.ddl.core.CustomExceptions.CodeException;
import link.dwsy.ddl.core.constant.CustomerErrorCode;
import link.dwsy.ddl.core.domain.LoginUserInfo;
import link.dwsy.ddl.entity.Infinity.Infinity;
import link.dwsy.ddl.entity.User.User;
import link.dwsy.ddl.repository.Infinity.InfinityClubRepository;
import link.dwsy.ddl.repository.Infinity.InfinityRepository;
import link.dwsy.ddl.repository.Infinity.InfinityTopicRepository;
import link.dwsy.ddl.repository.User.UserRepository;
import link.dwsy.ddl.support.UserSupport;
import link.dwsy.ddl.util.PRHelper;
import link.dwsy.ddl.util.PageData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author Dwsy
 * @Date 2022/11/23
 */
@RestController
@RequestMapping("infinity/comment")
@Slf4j
public class InfinityCommentController {

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


    @GetMapping("{id}")
    public Map<String, Object> getInfinityChildCommentPageList(
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
        PageRequest replyPageRequest = PRHelper.order("ASC", new String[]{"createTime"}, 1, 8);
        Page<Infinity> childComments = infinityRepository
                .findByDeletedFalseAndParentTweetIdAndTypeAndReplyUserTweetId
                        (id, InfinityType.TweetCommentOrReply, null, pageRequest);
        List<Infinity> childCommentsContent = childComments.getContent();
        HashMap<Long, List<Infinity>> commentReplyMap = new HashMap<>();
        childCommentsContent.forEach(childComment -> {
            childComment.noRetCreateUser();
            childComment.setImgUrlList();
            if (currentUser != null) {
                if (infinityRepository.existsByDeletedFalseAndUser_IdAndParentTweetIdAndType(currentUser.getId(), childComment.getId(), InfinityType.upTweet)) {
                    childComment.setUp(true);
                }
            }
            List<Infinity> commentReplyList = infinityRepository.findByDeletedFalseAndParentTweetIdAndTypeAndReplyUserTweetId
                    (id, InfinityType.TweetCommentOrReply, childComment.getId(), replyPageRequest).getContent();
            if (commentReplyList.size() != 0) {
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
                });
                commentReplyMap.put(childComment.getId(), commentReplyList);
            }
        });
        HashMap<String, Object> map = new HashMap<>();
        map.put("childComments", new PageData<>(childComments));
        map.put("commentReplyMap", commentReplyMap);
        return map;
    }

    @GetMapping("reply/{id}")
    public PageData<Infinity> getInfinityChildCommentReplyPageList(
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
        Page<Infinity> commentReplyPage = infinityRepository
                .findByDeletedFalseAndTypeAndReplyUserTweetId(InfinityType.TweetCommentOrReply, id, pageRequest);
        List<Infinity> commentReplyPageContent = commentReplyPage.getContent();
        commentReplyPageContent.forEach(reply -> {
            reply.noRetCreateUser();
            reply.setImgUrlList();
            if (currentUser != null) {
                if (infinityRepository.existsByDeletedFalseAndUser_IdAndParentTweetIdAndType(currentUser.getId(), reply.getId(), InfinityType.upTweet)) {
                    reply.setUp(true);
                }
            }
        });
        return new PageData<>(commentReplyPage);
    }


    @PostMapping()
    @AuthAnnotation
    public Infinity replyInfinity(@Validated @RequestBody ReplyInfinityRB infinityRB) {
        Long userId = userSupport.getCurrentUser().getId();
        long replyId = infinityRB.getReplyId();
        String content = infinityRB.getContent();
        Long replyUserTweetId = infinityRB.getReplyUserTweetId();
        if (replyUserTweetId == null) {
            boolean exists = infinityRepository.existsByDeletedFalseAndIdAndType(replyId, InfinityType.Tweet);
            if (!exists) {
                throw new CodeException(CustomerErrorCode.INFINITY_NOT_EXIST);
            }
            Infinity infinity = Infinity.builder()
                    .type(InfinityType.TweetCommentOrReply)
                    .content(content)
                    .ua(userSupport.getUserAgent())
                    .user((User) new User().setId(userId))
                    .parentTweetId(replyId).build();

            return infinityRepository.save(infinity);
        } else {
            boolean exists = infinityRepository.existsByDeletedFalseAndIdAndType(replyUserTweetId, InfinityType.TweetCommentOrReply);
            if (!exists) {
                throw new CodeException(CustomerErrorCode.INFINITY_NOT_EXIST);
            }
            Infinity infinity = Infinity.builder()
                    .type(InfinityType.TweetCommentOrReply)
                    .content(content)
                    .user((User) new User().setId(userId))
                    .parentTweetId(replyId)
                    .parentUserId(infinityRB.getReplyUserId())
                    .replyUserTweetId(replyUserTweetId)
                    .ua(userSupport.getUserAgent())
                    .build();
            Long refId = infinityRB.getRefId();
            if (refId != null) {
                infinity.setRefId(refId);
            }
            //回复时间线二级评论关联上一级评论id
            infinity.setImgUrlByList(infinityRB.getImgUrlList());
            return infinityRepository.save(infinity);
            //todo reply@name:
        }
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
