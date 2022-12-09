package link.dwsy.ddl.service.impl;

import com.alibaba.fastjson2.JSON;
import link.dwsy.ddl.XO.Enum.Article.ArticleState;
import link.dwsy.ddl.XO.Enum.Article.CommentType;
import link.dwsy.ddl.XO.Enum.User.CollectionType;
import link.dwsy.ddl.XO.VO.UserActionVO;
import link.dwsy.ddl.XO.VO.VersionData;
import link.dwsy.ddl.XO.VO.fieldVO;
import link.dwsy.ddl.constants.article.ArticleRedisKey;
import link.dwsy.ddl.core.CustomExceptions.CodeException;
import link.dwsy.ddl.core.constant.CustomerErrorCode;
import link.dwsy.ddl.core.domain.LoginUserInfo;
import link.dwsy.ddl.entity.Article.ArticleComment;
import link.dwsy.ddl.entity.Article.ArticleField;
import link.dwsy.ddl.repository.Article.ArticleCommentRepository;
import link.dwsy.ddl.repository.Article.ArticleContentRepository;
import link.dwsy.ddl.repository.Article.ArticleFieldRepository;
import link.dwsy.ddl.repository.User.UserCollectionRepository;
import link.dwsy.ddl.repository.User.UserFollowingRepository;
import link.dwsy.ddl.repository.User.UserRepository;
import link.dwsy.ddl.service.ArticleContentService;
import link.dwsy.ddl.service.Impl.UserStateService;
import link.dwsy.ddl.support.UserSupport;
import link.dwsy.ddl.util.PageData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @Author Dwsy
 * @Date 2022/8/25
 */
@Service
@Slf4j
public class ArticleContentServiceImpl implements ArticleContentService {
    @Resource
    private ArticleFieldRepository articleFieldRepository;
    ;
    @Resource
    private ArticleContentRepository articleContentRepository;

    @Resource(name = "stringRedisTemplate")
    private StringRedisTemplate redisTemplate;

    @Resource
    private UserSupport userSupport;

    @Resource
    private UserRepository userRepository;

    @Resource
    private ArticleCommentRepository articleCommentRepository;

    @Resource
    private UserFollowingRepository userFollowingRepository;

    @Resource
    private UserCollectionRepository userCollectionRepository;

    @Resource
    private UserStateService userStateService;



    public PageData<fieldVO> getPageList(PageRequest pageRequest, ArticleState articleState) {
        //todo数据填充
        Page<fieldVO> fieldVOList = articleFieldRepository.findAllByDeletedIsFalseAndArticleState(articleState, pageRequest);
        for (fieldVO fieldVO : fieldVOList.getContent()) {
            userStateService.cancellationUserHandel(fieldVO.getUser());
        }
        return new PageData<>(fieldVOList);
    }


    public PageData<fieldVO> getPageList(PageRequest pageRequest, ArticleState articleState, long articleTagId) {

        Page<fieldVO> fieldVOList = articleFieldRepository.findByDeletedFalseAndArticleStateAndArticleTags_Id
                (articleState, articleTagId, pageRequest);
        for (fieldVO fieldVO : fieldVOList.getContent()) {
            userStateService.cancellationUserHandel(fieldVO.getUser());
        }
        return new PageData<>(fieldVOList);
    }

    public PageData<fieldVO> getArticleListByUserId(PageRequest pageRequest, ArticleState articleState, long userId) {
        if (userStateService.isCancellationUserHandel(userId)) {
            throw new CodeException(CustomerErrorCode.UserNotExist);
        }
        Page<fieldVO> fieldVOList = articleFieldRepository.findByDeletedFalseAndArticleStateAndUser_Id
                (articleState, userId, pageRequest);
        return new PageData<>(fieldVOList);
    }

    public ArticleField getArticleById(long id, ArticleState articleState) {
//        LoginUserInfo currentUser = userSupport.getCurrentUser();
        if (articleFieldRepository.userIsCancellation(id)>0) {
            throw new CodeException(CustomerErrorCode.ArticleNotFound);
        }
        //        if (currentUser != null) {//ssr 没token需要放在前端加载 所有加一个接口
//            Optional<ArticleComment> userAction = articleCommentRepository.findByUserIdAndParentCommentIdAndCommentTypeIn(currentUser.getId(), -1L, Set.of(CommentType.up, CommentType.down));
//            userAction.ifPresent(e-> af.setUserAction(e.getCommentType()));
//        }
        return articleFieldRepository.findByIdAndDeletedIsFalseAndArticleState(id, articleState);
    }

    public ArticleField getArticleById(long id, Collection<ArticleState> articleStates) {
        if (articleFieldRepository.userIsCancellation(id)>0) {
            throw new CodeException(CustomerErrorCode.ArticleNotFound);
        }

        return articleFieldRepository.findByIdAndDeletedIsFalseAndArticleStateIn(id, articleStates);
    }

    public ArticleField getArticleFieldByIdAndVersion(Long id, Integer version) {
        String fieldJsonStr = redisTemplate.opsForList().index(ArticleRedisKey.ArticleHistoryVersionFieldKey + id, version);
        if (fieldJsonStr == null) {
            throw new CodeException(CustomerErrorCode.ArticleVersionNotFound);
        }
        return JSON.parseObject(fieldJsonStr, ArticleField.class);
    }

    public String getArticleContentByIdAndVersion(Long id, Integer version) {
        String contentStr = redisTemplate.opsForList().index(ArticleRedisKey.ArticleHistoryVersionContentKey + id, version);
        if (contentStr == null) {
            throw new CodeException(CustomerErrorCode.ArticleVersionNotFound);
        }
        return contentStr;
    }

    public UserActionVO getUserAction(long id) {
        LoginUserInfo currentUser = userSupport.getCurrentUser();
        UserActionVO userActionVO = new UserActionVO();


        if (currentUser != null) {//ssr 没token需要放在前端加载 所有加一个接口 todo 迁移
            Optional<ArticleComment> action = articleCommentRepository
                    .findByDeletedFalseAndUser_IdAndParentCommentIdAndCommentTypeInAndArticleField_Id
                            (currentUser.getId(), -1L,
                                    Set.of(CommentType.up, CommentType.down, CommentType.cancel), id);

            userActionVO.setThumb(action.map(ArticleComment::getCommentType).orElse(null));


            userActionVO.setCollect(userCollectionRepository
                    .existsByUserIdAndSourceIdAndCollectionTypeAndDeletedFalse
                            (currentUser.getId(), id, CollectionType.Article));

            Long followUserId = articleFieldRepository.findUserIdById(id);
            if (followUserId != null) {
                userActionVO.setFollow(userFollowingRepository
                        .existsByUserIdAndFollowingUserIdAndDeletedIsFalse(currentUser.getId(), followUserId));
            }
            return userActionVO;
        }
        return null;
    }


    public String getContent(long id, int type) {
        if (articleFieldRepository.userIsCancellation(id)>0) {
            throw new CodeException(CustomerErrorCode.ArticleNotFound);
        }
        //todo 权限校验 use 投影
        if (type == 0) {
            return articleContentRepository.getHtmlTextById(id);
        }
        if (type == 1) {
            return articleContentRepository.getPureTextById(id);
        }
        if (type == 2) {
            return articleContentRepository.getMdTextById(id);
        }
        return null;
    }

    public Map<String, VersionData> getHistoryVersionTitle(long articleId) {
        Long userId = userSupport.getCurrentUser().getId();
        Long articleOwnerUserId = articleFieldRepository.findUserIdById(articleId);
        if (!userId.equals(articleOwnerUserId)) {
            throw new CodeException(CustomerErrorCode.ArticleGroupNotBelongToUser);
        }

        List<String> titleList = redisTemplate.opsForList().range(ArticleRedisKey.ArticleHistoryVersionTitleKey + articleId, 0, -1);
        List<String> dateList = redisTemplate.opsForList().range(ArticleRedisKey.ArticleHistoryVersionCreateDateKey + articleId, 0, -1);
        if (titleList == null || dateList == null) {
            throw new CodeException(CustomerErrorCode.ArticleVersionNotFound);
        }
        HashMap<String, VersionData> versionMap = new HashMap<>();
        if (titleList.size() == dateList.size()) {
            for (int i = 0; i < titleList.size(); i++) {
                versionMap.put(String.valueOf(i), new VersionData(titleList.get(i), dateList.get(i)));
            }
        } else {
            log.info("titleList.size() != dateList.size(),userId:{}articleId:{}", userId, articleId);
            throw new CodeException(CustomerErrorCode.ArticleVersionNotFound);
        }
        return versionMap;
    }
}
