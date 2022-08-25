package link.dwsy.ddl.service.impl;

import link.dwsy.ddl.XO.CustomER.entity.ArticleCommentCustom;
import link.dwsy.ddl.XO.CustomER.repository.ArticleCommentRepositoryCustom;
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
    private ArticleCommentRepositoryCustom articleCommentRepositoryCustom;

    public PageData<ArticleCommentCustom> getByArticleId(long aid, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<ArticleCommentCustom> parentComment = articleCommentRepositoryCustom.
                findAllByDeletedIsFalseAndArticleContentIdAndParentCommentId(aid,0L, pageRequest);
        for (ArticleCommentCustom articleCommentCustom : parentComment) {
            long pid = articleCommentCustom.getId();
            articleCommentCustom.setChildComments(articleCommentRepositoryCustom.findAllByDeletedIsFalseAndArticleContentIdAndParentCommentId(aid, pid));
        }
        return new PageData<>(parentComment);
//        parentComment.stream().map(ArticleCommentCustom::getParentCommentId).map(pid -> articleCommentRepositoryCustom.findAllByDeletedIsFalseAndArticleContentIdAndParentCommentId(aid, pid)).forEach();

    }
}

