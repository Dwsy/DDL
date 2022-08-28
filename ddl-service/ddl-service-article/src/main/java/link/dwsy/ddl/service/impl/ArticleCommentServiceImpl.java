package link.dwsy.ddl.service.impl;

import link.dwsy.ddl.entity.Article.ArticleComment;
import link.dwsy.ddl.repository.Article.ArticleCommentRepository;
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

    public PageData<ArticleComment> getByArticleId(long aid, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<ArticleComment> parentComment = articleCommentRepository.
                findAllByDeletedIsFalseAndArticleFieldIdAndParentCommentId(aid,0L, pageRequest);
//              findAllByDeletedIsFalseAndArticleContentIdAndParentCommentId(aid,0L, pageRequest);
        for (ArticleComment ArticleComment : parentComment) {
            long pid = ArticleComment.getId();
            ArticleComment.setChildComments(articleCommentRepository.findAllByDeletedIsFalseAndArticleFieldIdAndParentCommentId(aid, pid));
        }
        return new PageData<>(parentComment);
//        parentComment.stream().map(ArticleCommentCustom::getParentCommentId).map(pid -> articleCommentRepositoryCustom.findAllByDeletedIsFalseAndArticleContentIdAndParentCommentId(aid, pid)).forEach();

    }
}

