package link.dwsy.ddl.controller;

import link.dwsy.ddl.XO.Enum.Article.ArticleState;
import link.dwsy.ddl.XO.Enum.User.CollectionType;
import link.dwsy.ddl.XO.RB.UserCollectionRB;
import link.dwsy.ddl.annotation.AuthAnnotation;
import link.dwsy.ddl.core.CustomExceptions.CodeException;
import link.dwsy.ddl.core.constant.CustomerErrorCode;
import link.dwsy.ddl.entity.Article.ArticleComment;
import link.dwsy.ddl.entity.Article.ArticleField;
import link.dwsy.ddl.entity.QA.QaAnswer;
import link.dwsy.ddl.entity.User.UserCollection;
import link.dwsy.ddl.entity.User.UserCollectionGroup;
import link.dwsy.ddl.repository.Article.ArticleCommentRepository;
import link.dwsy.ddl.repository.Article.ArticleFieldRepository;
import link.dwsy.ddl.repository.QA.QaAnswerRepository;
import link.dwsy.ddl.repository.QA.QaQuestionFieldRepository;
import link.dwsy.ddl.repository.User.UserCollectionGroupRepository;
import link.dwsy.ddl.repository.User.UserCollectionRepository;
import link.dwsy.ddl.repository.User.UserRepository;
import link.dwsy.ddl.support.UserSupport;
import link.dwsy.ddl.util.HtmlHelper;
import link.dwsy.ddl.util.PRHelper;
import link.dwsy.ddl.util.PageData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

/**
 * @Author Dwsy
 * @Date 2022/9/10
 */

@RestController
@RequestMapping("collection")
@Slf4j
public class userCollectionController {
    @Resource
    private UserCollectionGroupRepository userCollectionGroupRepository;

    @Resource
    private UserCollectionRepository userCollectionRepository;

    @Resource
    private ArticleFieldRepository articleFieldRepository;

    @Resource
    private ArticleCommentRepository articleCommentRepository;

    @Resource
    private QaAnswerRepository qaAnswerRepository;

    @Resource
    private QaQuestionFieldRepository qaQuestionFieldRepository;

    @Resource
    private UserRepository userRepository;

    @Resource
    private UserSupport userSupport;


    @GetMapping("goto/link/{id}")
    @AuthAnnotation
    public String getCollectionToLink(@PathVariable long id) {
        Optional<UserCollection> collection = userCollectionRepository.findById(id);
        if (collection.isPresent()) {
            UserCollection userCollection = collection.get();
            CollectionType collectionType = userCollection.getCollectionType();
            Long sourceId = userCollection.getSourceId();
            if (collectionType == CollectionType.Answer) {
                long questionId = qaAnswerRepository.getQuestionIdByAnswerId(sourceId);
                return "/question/" + questionId;//todo page calc location
            }
            return "~~";
        } else {
            throw new CodeException(CustomerErrorCode.UserCollectionNotExist);
        }
    }

    @PostMapping
    @AuthAnnotation
    public String addCollectionToGroup(@Validated @RequestBody UserCollectionRB userCollectionRB) {
        CollectionType collectionType = userCollectionRB.getCollectionType();
        Long gid = userCollectionRB.getGroupId();
        Long sid = userCollectionRB.getSourceId();
        Long uid = userSupport.getCurrentUser().getId();

        UserCollectionGroup userCollectionGroup = userCollectionGroupRepository.findByIdAndUserIdAndDeletedIsFalse(gid, uid);

        if (userCollectionGroup == null) {
            throw new CodeException(CustomerErrorCode.UserCollectionGroupNotExist);
        }


        Optional<UserCollection> uce = userCollectionRepository
                .findByUserIdAndSourceIdAndUserCollectionGroup_IdAndCollectionType(uid, sid, gid, collectionType);
        uce.ifPresent(userCollection -> {
            if (userCollection.getDeleted()) {
                userCollection.setDeleted(false);


                userCollectionRepository.save(userCollection);
            } else {
                throw new CodeException(CustomerErrorCode.UserCollectionAlreadyExist);
            }
        });
        CollectionType type = userCollectionRB.getCollectionType();

        switch (type) {
            // todo
            case Article:
                log.info("user:{}收藏文章:{}", uid, sid);
                articleFieldRepository.collectNumIncrement(sid, 1);
                break;
            case Question:
                log.info("user:{}收藏问题:{}", uid, sid);
                qaQuestionFieldRepository.collectNumIncrement(sid, 1);
                break;
            case Answer:
                log.info("user:{}收藏问题答案:{}", uid, sid);
                break;
//                    case Comment:
//                        break;
        }
        userCollectionGroup.setCollectionNum(userCollectionGroup.getCollectionNum() + 1);
        UserCollectionGroup g = userCollectionGroupRepository.save(userCollectionGroup);
        if (uce.isPresent()) {
            return "收藏成功";
        }
        String sourceTitle = null;
        if (collectionType == CollectionType.Article) {
            ArticleField articleField = articleFieldRepository
                    .findByIdAndDeletedFalseAndArticleState(sid, ArticleState.published)
                    .orElseThrow(() -> new CodeException(CustomerErrorCode.ArticleNotFound));
            sourceTitle = articleField.getTitle();
        }

        if (collectionType == CollectionType.Comment) {
            ArticleComment articleComment = articleCommentRepository.
                    findByDeletedFalseAndId(sid).
                    orElseThrow(() -> new CodeException(CustomerErrorCode.ArticleCommentNotFount));
            String text = articleComment.getText();
            if (text.length() > 50) {
                text = text.substring(0, 50);
            }
            sourceTitle = text;
        }

        if (collectionType == CollectionType.Question) {
            sourceTitle = qaQuestionFieldRepository.findByIdAndDeletedFalse(sid)
                    .orElseThrow(() -> new CodeException(CustomerErrorCode.QuestionNotFound))
                    .getTitle();
        }

        if (collectionType == CollectionType.Answer) {

            QaAnswer qaAnswer = qaAnswerRepository.findByDeletedFalseAndId(sid).orElseThrow(() -> new CodeException(CustomerErrorCode.AnswerNotFound));

            String title = qaQuestionFieldRepository.getTitleByAnswerId(qaAnswer.getId());
            String textHtml = qaAnswer.getTextHtml();
            String pure = HtmlHelper.toPure(textHtml);
            if (pure.length() > 150) {
                sourceTitle = pure.substring(0, 150);
            }

            sourceTitle = String.format(
                    "<div class=\"d-collect-answer-title\">%s</div>" +
                            "<div class=\"d-collect-answer-content\">%s</div>", title, sourceTitle);
        }

        userCollectionRepository.save(UserCollection.builder()
                .userCollectionGroup(g)
                .userId(uid)
                .collectionType(collectionType)
                .sourceId(sid)
                .sourceTitle(sourceTitle)
                .build());

        return "收藏成功";
    }

    @DeleteMapping
    @AuthAnnotation
    public String cancelCollection(@RequestBody @Validated UserCollectionRB userCollectionRB) {
        Long uid = userSupport.getCurrentUser().getId();
        Long sourceId = userCollectionRB.getSourceId();
        CollectionType collectionType = userCollectionRB.getCollectionType();
        Long groupId = userCollectionRB.getGroupId();

        UserCollection userCollection = userCollectionRepository
                .findByDeletedFalseAndUserIdAndSourceIdAndUserCollectionGroup_IdAndCollectionType(uid, sourceId, groupId, collectionType)
                .orElseThrow(() -> new CodeException(CustomerErrorCode.UserCollectionNotExist));

        UserCollectionGroup userCollectionGroup = userCollection.getUserCollectionGroup();
        userCollectionGroup.setCollectionNum(userCollectionGroup.getCollectionNum() - 1);
        userCollection.setDeleted(true);

        CollectionType type = userCollection.getCollectionType();
        //todo 收藏多次1
        switch (type) {
            case Article:
                articleFieldRepository.collectNumIncrement(sourceId, -1);
                break;
            case Question:
                qaQuestionFieldRepository.collectNumIncrement(sourceId, -1);
                break;
        }


        userCollectionGroupRepository.save(userCollectionGroup);
        userCollectionRepository.save(userCollection);

        return "删除成功";
    }

    @GetMapping("state")
    @AuthAnnotation
    public Set<String> getCollectionState(@RequestParam(name = "sourceId") long sourceId,
                                          @RequestParam(name = "type") CollectionType collectionType) {
        Long uid = userSupport.getCurrentUser().getId();
        List<UserCollection> collectionList = userCollectionRepository
                .findByDeletedFalseAndUserIdAndSourceIdAndCollectionType(uid, sourceId, collectionType);
        Set<String> ret = new HashSet<>();
        for (UserCollection userCollection : collectionList) {
            ret.add(String.valueOf(userCollection.getUserCollectionGroup().getId()));
        }
        return ret;
    }

    @GetMapping("list/{groupId}")
    public PageData<UserCollection> getCollectionListByGroupId(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "DESC", name = "order") String order,
            @RequestParam(required = false, defaultValue = "createTime", name = "properties") String[] properties,
            @RequestParam(required = false, defaultValue = "", name = "CollectionType") CollectionType collectionType,
            @PathVariable long groupId) {
//        Long uid = userSupport.getCurrentUser().getId();
        //todo 权限设置
        if (!userCollectionGroupRepository.existsById(groupId)) {
            throw new CodeException(CustomerErrorCode.UserCollectionGroupNotExist);
        }
        Set<CollectionType> collectionTypeSet = new HashSet<>();
        if (collectionType != null) {
            collectionTypeSet.add(collectionType);
        } else {
            collectionTypeSet = EnumSet.allOf(CollectionType.class);
        }

        PageRequest pageRequest = PRHelper.order(order, properties, page, size);
        Page<UserCollection> userCollections = userCollectionRepository
                .findByDeletedFalseAndCollectionTypeInAndUserCollectionGroup_Id(collectionTypeSet, groupId, pageRequest);
        if (collectionType == CollectionType.Answer) {
            for (UserCollection collection : userCollections) {
                long questionId = qaAnswerRepository.getQuestionIdByAnswerId(collection.getSourceId());
                collection.setLink("/question/" + questionId);
            }
        }
        return new PageData<>(userCollections);
    }

}
