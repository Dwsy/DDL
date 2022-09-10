package link.dwsy.ddl;

import link.dwsy.ddl.XO.Enum.Article.ArticleState;
import link.dwsy.ddl.XO.Enum.CollectionType;
import link.dwsy.ddl.core.CustomExceptions.CodeException;
import link.dwsy.ddl.core.constant.CustomerErrorCode;
import link.dwsy.ddl.entity.Article.ArticleComment;
import link.dwsy.ddl.entity.Article.ArticleField;
import link.dwsy.ddl.entity.QA.QaAnswer;
import link.dwsy.ddl.entity.User.User;
import link.dwsy.ddl.entity.User.UserCollection;
import link.dwsy.ddl.entity.User.UserCollectionGroup;
import link.dwsy.ddl.repository.Article.ArticleCommentRepository;
import link.dwsy.ddl.repository.Article.ArticleFieldRepository;
import link.dwsy.ddl.repository.QA.QaAnswerRepository;
import link.dwsy.ddl.repository.QA.QaQuestionFieldRepository;
import link.dwsy.ddl.repository.User.UserCollectionGroupRepository;
import link.dwsy.ddl.repository.User.UserCollectionRepository;
import link.dwsy.ddl.repository.User.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * @Author Dwsy
 * @Date 2022/9/9
 */

@SpringBootTest
public class CollectionTest {

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
    QaQuestionFieldRepository qaQuestionFieldRepository;

    @Resource
    UserRepository userRepository;

    @Test
    public void T() {
//        createDefaultGroup();
//        createDefaultGroup();

//        addCollectionToGroup(3, 1, 9, CollectionType.Article);
//        addCollectionToGroup(3, 1, 1, CollectionType.Answer);
        addCollectionToGroup(3, 1, 1, CollectionType.Comment);
        addCollectionToGroup(3, 1, 1, CollectionType.Question);
    }

    public void addCollectionToGroup(long uid, long gid, long sid, CollectionType collectionType) {
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
            sourceTitle = qaQuestionFieldRepository.findByIdAndDeletedFalse(sid)
                    .orElseThrow(() -> new CodeException(CustomerErrorCode.QuestionNotFound))
                    .getTitle();
        }

        if (collectionType == CollectionType.Answer) {
            QaAnswer qaAnswer = qaAnswerRepository.findByDeletedFalseAndId(sid).orElseThrow(() -> new CodeException(CustomerErrorCode.AnswerNotFound));
            String textHtml = qaAnswer.getTextHtml();
            if (textHtml.length() > 50) {
                textHtml = textHtml.substring(0, 50);
            }
            sourceTitle = textHtml;
        }
        userCollectionRepository.save(UserCollection.builder()
                .userCollectionGroup(g)
                .userId(uid)
                .collectionType(collectionType)
                .sourceId(sid)
                .sourceTitle(sourceTitle)
                .build());
    }

    public void createDefaultGroup() {
        List<User> userList = userRepository.findAll();
        userList.forEach(user -> {
            System.out.println(user.getNickname());
            UserCollectionGroup group = UserCollectionGroup.builder()
                    .userId(user.getId())
                    .groupName("默认收藏夹")
                    .collectionNum(0)
                    .groupOrder(0).build();
            if (!userCollectionGroupRepository.existsByUserIdAndGroupName(user.getId(), "默认收藏夹")) {
                userCollectionGroupRepository.save(userCollectionGroupRepository.save(group));

            } else {
                System.out.println("已存在");
            }
        });
    }

    public void createGroup(long uid, String groupName) {
        if (userCollectionGroupRepository.existsByUserIdAndGroupName(uid, groupName)) {
            throw new CodeException(CustomerErrorCode.ArticleGroupAlreadyExists);
        }
        UserCollectionGroup group = UserCollectionGroup.builder()
                .userId(uid)
                .groupName(groupName)
                .collectionNum(0)
                .groupOrder(0).build();
        userCollectionGroupRepository.save(group);
    }

    public void deleteGroup(long uid, long gid) {
        Optional<UserCollectionGroup> byId = userCollectionGroupRepository.findById(gid);
        if (byId.isPresent()) {
            UserCollectionGroup userCollectionGroup = byId.get();
            if (userCollectionGroup.getUserId() != uid) {
                throw new CodeException(CustomerErrorCode.ArticleGroupNotBelongToUser);
            }
            if (userCollectionGroup.getCollectionNum() != 0) {
                throw new CodeException(CustomerErrorCode.ArticleGroupNotEmpty);
            }
            userCollectionGroupRepository.deleteById(gid);
        } else {
            throw new CodeException(CustomerErrorCode.ArticleGroupNotFound);
        }
    }

    public void updateGroup(long uid, long gid, String groupName, int order) {
        Optional<UserCollectionGroup> byId = userCollectionGroupRepository.findById(gid);
        if (byId.isPresent()) {
            UserCollectionGroup userCollectionGroup = byId.get();
            if (userCollectionGroup.getUserId() != uid) {
                throw new CodeException(CustomerErrorCode.ArticleGroupNotBelongToUser);
            }
            if (userCollectionGroup.getGroupName().equals(groupName)) {
                throw new CodeException(CustomerErrorCode.ArticleGroupAlreadyExists);
            }
            userCollectionGroup.setGroupName(groupName);
            userCollectionGroup.setGroupOrder(order);
            userCollectionGroupRepository.save(userCollectionGroup);
        } else {
            throw new CodeException(CustomerErrorCode.ArticleGroupNotFound);
        }
    }

}
