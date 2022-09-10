package link.dwsy.ddl.controller;

import link.dwsy.ddl.XO.Enum.Article.ArticleState;
import link.dwsy.ddl.XO.Enum.CollectionType;
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
import link.dwsy.ddl.repository.QA.QaFieldRepository;
import link.dwsy.ddl.repository.User.UserCollectionGroupRepository;
import link.dwsy.ddl.repository.User.UserCollectionRepository;
import link.dwsy.ddl.repository.User.UserRepository;
import link.dwsy.ddl.support.UserSupport;
import link.dwsy.ddl.util.HtmlHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * @Author Dwsy
 * @Date 2022/9/10
 */

@RestController
@RequestMapping("collection")
@Slf4j
public class userCollectionController {
    @Resource
    UserCollectionGroupRepository userCollectionGroupRepository;

    @Resource
    UserCollectionRepository userCollectionRepository;

    @Resource
    ArticleFieldRepository articleFieldRepository;

    @Resource
    ArticleCommentRepository articleCommentRepository;

    @Resource
    QaAnswerRepository qaAnswerRepository;

    @Resource
    QaFieldRepository qaFieldRepository;

    @Resource
    UserRepository userRepository;

    @Resource
    UserSupport userSupport;

    
    @PostMapping
    public String addCollectionToGroup(@RequestBody UserCollectionRB userCollectionRB) {
        CollectionType collectionType = userCollectionRB.getCollectionType();
        Long gid = userCollectionRB.getGroupId();
        Long sid = userCollectionRB.getSourceId();
        Long uid = userSupport.getCurrentUser().getId();
        Optional<UserCollection> uce = userCollectionRepository
                .findByDeletedFalseAndUserIdAndSourceIdAndUserCollectionGroup_IdAndCollectionType(uid, sid, gid, collectionType);
        uce.ifPresent(userCollection -> {
            if (userCollection.isDeleted()) {
                userCollection.setDeleted(false);
                userCollectionRepository.save(userCollection);
            } else {
                throw new CodeException(CustomerErrorCode.UserCollectionAlreadyExist);
            }
        });
        UserCollectionGroup userCollectionGroup = userCollectionGroupRepository.findByIdAndUserIdAndDeletedIsFalse(gid, uid);

        if (userCollectionGroup == null) {
            throw new CodeException(CustomerErrorCode.UserCollectionGroupNotExist);
        }

        userCollectionGroup.setCollectionNum(userCollectionGroup.getCollectionNum() + 1);
        UserCollectionGroup g = userCollectionGroupRepository.save(userCollectionGroup);
        String sourceTitle=null;
        if (collectionType == CollectionType.Article) {
            ArticleField articleField = articleFieldRepository
                    .findByIdAndDeletedFalseAndArticleState(sid, ArticleState.open)
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
            sourceTitle = qaFieldRepository.findByIdAndDeletedFalse(sid)
                    .orElseThrow(() -> new CodeException(CustomerErrorCode.QuestionNotFound))
                    .getTitle();
        }

        if (collectionType == CollectionType.Answer) {
            QaAnswer qaAnswer = qaAnswerRepository.findByDeletedFalseAndId(sid).orElseThrow(() -> new CodeException(CustomerErrorCode.AnswerNotFound));
            String textHtml = qaAnswer.getTextHtml();
            String pure = HtmlHelper.toPure(textHtml);
            if (pure.length() > 50) {
                sourceTitle = pure.substring(0, 50);
            }
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

    @DeleteMapping()
    @AuthAnnotation
    private String deleteCollection(@RequestBody UserCollectionRB userCollectionRB) {
        Long uid = userSupport.getCurrentUser().getId();
        Long sourceId = userCollectionRB.getSourceId();
        CollectionType collectionType = userCollectionRB.getCollectionType();
        Long groupId = userCollectionRB.getGroupId();

        UserCollection userCollection = userCollectionRepository
                .findByDeletedFalseAndUserIdAndSourceIdAndUserCollectionGroup_IdAndCollectionType(uid, sourceId, groupId,collectionType)
                .orElseThrow(() -> new CodeException(CustomerErrorCode.UserCollectionNotExist));

        UserCollectionGroup userCollectionGroup = userCollection.getUserCollectionGroup();
        userCollectionGroup.setCollectionNum(userCollectionGroup.getCollectionNum() - 1);
        userCollection.setDeleted(true);

        userCollectionGroupRepository.save(userCollectionGroup);
        userCollectionRepository.save(userCollection);

        return "删除成功";
    }



}
