package link.dwsy.ddl.service;

import link.dwsy.ddl.XO.Enum.Article.CommentType;
import link.dwsy.ddl.XO.RB.ArticleCommentActionRB;
import link.dwsy.ddl.XO.RB.ArticleCommentRB;
import link.dwsy.ddl.entity.Article.ArticleComment;
import link.dwsy.ddl.util.PageData;
import org.springframework.data.domain.PageRequest;

public interface ArticleCommentService {
    PageData<ArticleComment> getByArticleId(long aid, PageRequest pageRequest);

    PageData<ArticleComment> getChildCommentsByParentId(Long aid, Long pid, PageRequest pageRequest);

//    ArticleComment reply(ArticleCommentRB articleCommentRB, CommentType commentType);

    boolean logicallyDelete(long articleId, long commentId);

    boolean logicallyRecovery(Long id);

    CommentType action(ArticleCommentActionRB commentActionRB);
}
