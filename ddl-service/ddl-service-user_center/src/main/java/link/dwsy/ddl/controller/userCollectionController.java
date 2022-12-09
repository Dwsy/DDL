package link.dwsy.ddl.controller;

import link.dwsy.ddl.XO.Enum.User.CollectionType;
import link.dwsy.ddl.XO.RB.UserCollectionRB;
import link.dwsy.ddl.annotation.AuthAnnotation;
import link.dwsy.ddl.core.CustomExceptions.CodeException;
import link.dwsy.ddl.core.constant.CustomerErrorCode;
import link.dwsy.ddl.entity.User.UserCollection;
import link.dwsy.ddl.repository.Article.ArticleCommentRepository;
import link.dwsy.ddl.repository.Article.ArticleFieldRepository;
import link.dwsy.ddl.repository.QA.QaAnswerRepository;
import link.dwsy.ddl.repository.QA.QaQuestionFieldRepository;
import link.dwsy.ddl.repository.User.UserCollectionGroupRepository;
import link.dwsy.ddl.repository.User.UserCollectionRepository;
import link.dwsy.ddl.repository.User.UserRepository;
import link.dwsy.ddl.service.Impl.UserCollectionService;
import link.dwsy.ddl.support.UserSupport;
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
    private UserCollectionService userCollectionService;

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
        return userCollectionService.addCollectionToGroup(userCollectionRB, collectionType, gid, sid, uid);
    }

    @DeleteMapping
    @AuthAnnotation
    public String cancelCollection(@RequestBody @Validated UserCollectionRB userCollectionRB) {
        return userCollectionService.cancelCollection(userCollectionRB);
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
            @RequestParam(required = false, defaultValue = "", name = "collectionType") CollectionType collectionType,
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
