package link.dwsy.ddl.service;

import link.dwsy.ddl.XO.Enum.User.UserActiveType;
import link.dwsy.ddl.XO.RB.ArticleContentRB;
import link.dwsy.ddl.XO.RB.ArticleRecoveryRB;

public interface ArticleFieldService {
    void ActiveLog(UserActiveType userActiveType, Long sourceId);

    long createArticle(ArticleContentRB articleContentRB);

    long updateArticle(ArticleContentRB articleContentRB);

    void logicallyDeleted(Long articleId);

    void logicallyRecovery(ArticleRecoveryRB articleRecoveryRB);

    void view(Long id);
}
