package link.dwsy.ddl.service.impl;

import link.dwsy.ddl.XO.Enum.Article.CommentType;
import link.dwsy.ddl.XO.RB.ArticleCommentRB;
import link.dwsy.ddl.core.CustomExceptions.CodeException;
import link.dwsy.ddl.core.constant.CustomerErrorCode;
import link.dwsy.ddl.entity.Article.ArticleComment;
import link.dwsy.ddl.entity.Article.ArticleField;
import link.dwsy.ddl.entity.User.User;
import link.dwsy.ddl.repository.Article.ArticleCommentRepository;
import link.dwsy.ddl.repository.Article.ArticleFieldRepository;
import link.dwsy.ddl.support.UserSupport;
import link.dwsy.ddl.util.PageData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author Dwsy
 * @Date 2022/8/26
 */

@Service
public class ArticleCommentServiceImpl {
    @Resource
    private ArticleCommentRepository articleCommentRepository;
    @Resource
    private UserSupport userSupport;
    @Resource
    private ArticleFieldRepository articleFieldRepository;

    public PageData<ArticleComment> getByArticleId(long aid, PageRequest pageRequest) {
        Page<ArticleComment> parentComment = articleCommentRepository.
                findAllByDeletedIsFalseAndArticleFieldIdAndParentCommentId(aid,0L, pageRequest);
        for (ArticleComment ArticleComment : parentComment) {
            long pid = ArticleComment.getId();
            ArticleComment.setChildComments(articleCommentRepository.findAllByDeletedIsFalseAndArticleFieldIdAndParentCommentId(aid, pid));
        }
        return new PageData<>(parentComment);
    }

    public void reply(ArticleCommentRB articleCommentRB,CommentType commentType) {


        if (!articleFieldRepository.existsByDeletedFalseAndAllowCommentTrueAndId(articleCommentRB.getArticleFieldId())) {
            throw new CodeException(CustomerErrorCode.ArticleCommentIsClose);
        }
        User user = new User();
        user.setId(userSupport.getCurrentUser().getId());

        ArticleField af = new ArticleField();
        af.setId(9L);

        if (articleCommentRB.getParentCommentId() == 0) {
            ArticleComment articleComment = ArticleComment.builder()
                    .parentCommentId(0)
                    .parentUserId(0)
                    .commentType(commentType)
                    .user(user)
                    .articleField(af)
                    .ua(userSupport.getUserAgent())
                    .build();
            articleCommentRepository.save(articleComment);
        } else {
            if (!articleCommentRepository.isFirstAnswer(articleCommentRB.getParentCommentId())) {
                throw new CodeException(CustomerErrorCode.ArticleCommentNotIsFirst);
            }
            if (articleCommentRepository.existsByDeletedFalseAndIdAndArticleFieldId
                    (articleCommentRB.getParentCommentId(), articleCommentRB.getArticleFieldId())) {
                throw new CodeException(CustomerErrorCode.ArticleCommentNotFount);
            }
            ArticleComment articleComment = ArticleComment.builder()
                    .user(user)
                    .articleField(af)
                    .text(articleCommentRB.getText())
                    .parentCommentId(articleCommentRB.getParentCommentId())
                    .parentUserId(articleCommentRB.getParentUserId())
                    .commentType(commentType)
                    .ua(userSupport.getUserAgent())
                    .build();
            articleCommentRepository.save(articleComment);
        }
    }

    public boolean logicallyDelete(Long id) {
        Long uid = userSupport.getCurrentUser().getId();
        int i = articleCommentRepository.logicallyDelete(uid, id);
        if (i > 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean logicallyRecovery(Long id) {
        Long uid = userSupport.getCurrentUser().getId();
        int i = articleCommentRepository.logicallyRecovery(uid, id);
        if (i > 0) {
            return true;
        } else {
            return false;
        }
    }
}

