package link.dwsy.ddl.controller;

import cn.hutool.core.util.StrUtil;
import link.dwsy.ddl.XO.RB.UserCollectionGroupRB;
import link.dwsy.ddl.annotation.AuthAnnotation;
import link.dwsy.ddl.core.CustomExceptions.CodeException;
import link.dwsy.ddl.core.constant.CustomerErrorCode;
import link.dwsy.ddl.entity.User.UserCollectionGroup;
import link.dwsy.ddl.repository.Article.ArticleCommentRepository;
import link.dwsy.ddl.repository.Article.ArticleFieldRepository;
import link.dwsy.ddl.repository.QA.QaAnswerRepository;
import link.dwsy.ddl.repository.QA.QaQuestionFieldRepository;
import link.dwsy.ddl.repository.User.UserCollectionGroupRepository;
import link.dwsy.ddl.repository.User.UserCollectionRepository;
import link.dwsy.ddl.repository.User.UserRepository;
import link.dwsy.ddl.support.UserSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * @Author Dwsy
 * @Date 2022/9/10
 */

@RestController
@RequestMapping("collection/group")
@Slf4j
public class UserCollectionGroupController {
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

    @Resource
    UserSupport userSupport;

    // 创建默认分组

    @PostMapping("create_default")
    @AuthAnnotation
//    todo 注册用户创建默认分组
    public void createDefaultGroup() {
        Long uid = userSupport.getCurrentUser().getId();

        UserCollectionGroup group = UserCollectionGroup.builder()
                .userId(uid)
                .groupName("默认收藏夹")
                .collectionNum(0)
                .introduction("文章简介")
                .groupOrder(0).build();
        if (!userCollectionGroupRepository.existsByUserIdAndGroupName(uid, "默认收藏夹")) {
            userCollectionGroupRepository.save(userCollectionGroupRepository.save(group));
        } else {
            log.info("user{}:已存在默认分组", uid);
        }
    }

    @GetMapping("list")
    @AuthAnnotation
    public List<UserCollectionGroup> getGroupList() {
        Long uid = userSupport.getCurrentUser().getId();
        //         不做分页 限制最大数

        return userCollectionGroupRepository.findByUserIdAndDeletedFalse(uid, Sort.by("collectionNum").descending());
    }

    @GetMapping("{id}")
    @AuthAnnotation
    public UserCollectionGroup getGroupList(@PathVariable long id) {
        Long uid = userSupport.getCurrentUser().getId();
        return userCollectionGroupRepository.findByDeletedFalseAndUserIdAndId(uid, id).orElse(null);
    }


    @PostMapping("create")
    @AuthAnnotation
    public String createGroup(@RequestBody UserCollectionGroupRB userCollectionGroupRB) {
        String groupName = userCollectionGroupRB.getGroupName();
        if (StrUtil.isBlank(groupName)) {
            throw new CodeException(CustomerErrorCode.BodyError);
        }
        Long uid = userSupport.getCurrentUser().getId();
        if (userCollectionGroupRepository.existsByUserIdAndGroupName(uid, groupName)) {
            log.info("user{}:已存在分组{}", uid, groupName);
            throw new CodeException(CustomerErrorCode.ArticleGroupAlreadyExists);
        }
        UserCollectionGroup group = UserCollectionGroup.builder()
                .userId(uid)
                .groupName(groupName)
                .collectionNum(0)
                .introduction("文章简介")
                .groupOrder(0).build();
        userCollectionGroupRepository.save(group);
        return "创建成功";
    }

    @DeleteMapping
    @AuthAnnotation
    public String deleteGroup(@RequestBody UserCollectionGroupRB userCollectionGroupRB) {
        Long gid = userCollectionGroupRB.getGroupId();
        if (gid == null || gid < 1) {
            throw new CodeException(CustomerErrorCode.BodyError);
        }
        var uid = userSupport.getCurrentUser().getId();
        Optional<UserCollectionGroup> byId = userCollectionGroupRepository.findById(gid);
        if (byId.isPresent()) {
            UserCollectionGroup userCollectionGroup = byId.get();
            if (!userCollectionGroup.getUserId().equals(uid)) {
                throw new CodeException(CustomerErrorCode.ArticleGroupNotBelongToUser);
            }
            if (userCollectionGroup.getCollectionNum() != 0) {
                throw new CodeException(CustomerErrorCode.ArticleGroupNotEmpty);
            }
            userCollectionGroupRepository.deleteById(gid);
            return "删除成功";
        }
        throw new CodeException(CustomerErrorCode.ArticleGroupNotFound);

    }

    @PutMapping
    @AuthAnnotation
    public String updateGroup(@RequestBody UserCollectionGroupRB userCollectionGroupRB) {
        Long gid = userCollectionGroupRB.getGroupId();
        String groupName = userCollectionGroupRB.getGroupName();
        if (gid == null || gid < 1 || StrUtil.isBlank(groupName)) {
            throw new CodeException(CustomerErrorCode.BodyError);
        }
        var uid = userSupport.getCurrentUser().getId();
        Optional<UserCollectionGroup> byId = userCollectionGroupRepository.findById(gid);
        if (byId.isPresent()) {
            UserCollectionGroup userCollectionGroup = byId.get();
            if (!userCollectionGroup.getUserId().equals(uid)) {
                throw new CodeException(CustomerErrorCode.ArticleGroupNotBelongToUser);
            }
            userCollectionGroup.setGroupName(groupName);
            if (userCollectionGroupRB.getOrder() > 0) {
                userCollectionGroup.setGroupOrder(userCollectionGroupRB.getOrder());
            }
            userCollectionGroupRepository.save(userCollectionGroup);
            return "修改成功";
        }
        throw new CodeException(CustomerErrorCode.ArticleGroupNotFound);
    }
}
