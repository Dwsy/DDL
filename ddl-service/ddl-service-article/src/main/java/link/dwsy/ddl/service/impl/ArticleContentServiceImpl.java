package link.dwsy.ddl.service.impl;

import link.dwsy.ddl.XO.Enum.Article.ArticleState;
import link.dwsy.ddl.XO.Enum.Article.CommentType;
import link.dwsy.ddl.XO.VO.UserActionVO;
import link.dwsy.ddl.XO.VO.fieldVO;
import link.dwsy.ddl.core.domain.LoginUserInfo;
import link.dwsy.ddl.entity.Article.ArticleComment;
import link.dwsy.ddl.entity.Article.ArticleField;
import link.dwsy.ddl.repository.Article.*;
import link.dwsy.ddl.repository.User.UserFollowingRepository;
import link.dwsy.ddl.repository.User.UserRepository;
import link.dwsy.ddl.service.ArticleContentService;
import link.dwsy.ddl.support.UserSupport;
import link.dwsy.ddl.util.PageData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;
import java.util.Set;

/**
 * @Author Dwsy
 * @Date 2022/8/25
 */
@Service
public class ArticleContentServiceImpl implements ArticleContentService {
    @Resource
    ArticleTagRepository articleTagRepository;
    @Resource
    ArticleFieldRepository articleFieldRepository;
    @Resource
    ArticleGroupRepository articleGroupRepository;
    @Resource
    ArticleContentRepository articleContentRepository;
    @Resource
    RabbitTemplate rabbitTemplate;
    @Resource
    UserSupport userSupport;

    @Resource
    UserRepository userRepository;

    @Resource
    ArticleCommentRepository articleCommentRepository;

    @Resource
    UserFollowingRepository userFollowingRepository;


//    public PageData<ArticleContent, ArticleContentDTO> getPageList(int page, int size) {
//        PageRequest pageRequest = PageRequest.of(page-1, size);
//        Page<ArticleContent> articleContentPage = articleContentRepository.findAllByDeletedIsFalseOrDeletedIsNull(pageRequest);
//        return new PageData<>(articleContentPage, ArticleContentDTO::convert);
//    }

    public PageData<fieldVO> getPageList(PageRequest pageRequest, ArticleState articleState) {

        Page<fieldVO> fieldVOList = articleFieldRepository.findAllByDeletedIsFalseAndArticleState(articleState, pageRequest);
        PageData<fieldVO> fieldVOPageData = new PageData<>(fieldVOList);
        return fieldVOPageData;
    }

    public PageData<fieldVO> getPageList(PageRequest pageRequest, ArticleState articleState, long articleTagId) {

        Page<fieldVO> fieldVOList = articleFieldRepository.findByDeletedFalseAndArticleStateAndArticleTags_Id
                (articleState, articleTagId, pageRequest);
        return new PageData<>(fieldVOList);
    }

    public ArticleField getArticleById(long id, ArticleState articleState) {
//        LoginUserInfo currentUser = userSupport.getCurrentUser();
        ArticleField af = articleFieldRepository.findByIdAndDeletedIsFalseAndArticleState(id, articleState);
//        if (currentUser != null) {//ssr 没token需要放在前端加载 所有加一个接口
//            Optional<ArticleComment> userAction = articleCommentRepository.findByUserIdAndParentCommentIdAndCommentTypeIn(currentUser.getId(), -1L, Set.of(CommentType.up, CommentType.down));
//            userAction.ifPresent(e-> af.setUserAction(e.getCommentType()));
//        }
        return af;
    }

    //todo 收藏 返回
    public UserActionVO getUserAction(long id) {
        LoginUserInfo currentUser = userSupport.getCurrentUser();
        UserActionVO userActionVO = new UserActionVO();


        if (currentUser != null) {//ssr 没token需要放在前端加载 所有加一个接口
            Optional<ArticleComment> action = articleCommentRepository
                    .findByDeletedFalseAndUser_IdAndParentCommentIdAndCommentTypeInAndArticleField_Id
                            (currentUser.getId(), -1L ,Set.of(CommentType.up, CommentType.down, CommentType.cancel),id);
            userActionVO.setThumb(action.map(ArticleComment::getCommentType).orElse(null));
            userActionVO.setCollect(false);
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

}
