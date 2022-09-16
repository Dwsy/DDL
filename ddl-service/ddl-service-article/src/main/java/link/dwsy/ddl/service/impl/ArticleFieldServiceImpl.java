package link.dwsy.ddl.service.impl;

import cn.hutool.core.util.StrUtil;
import link.dwsy.ddl.XO.Enum.UserActiveType;
import link.dwsy.ddl.XO.Message.UserActiveMessage;
import link.dwsy.ddl.XO.RB.ArticleContentRB;
import link.dwsy.ddl.XO.RB.ArticleRecoveryRB;
import link.dwsy.ddl.core.CustomExceptions.CodeException;
import link.dwsy.ddl.core.constant.CustomerErrorCode;
import link.dwsy.ddl.core.domain.LoginUserInfo;
import link.dwsy.ddl.entity.Article.ArticleContent;
import link.dwsy.ddl.entity.Article.ArticleField;
import link.dwsy.ddl.entity.Article.ArticleGroup;
import link.dwsy.ddl.entity.Article.ArticleTag;
import link.dwsy.ddl.mq.ArticleSearchConstants;
import link.dwsy.ddl.mq.UserActiveConstants;
import link.dwsy.ddl.repository.Article.ArticleContentRepository;
import link.dwsy.ddl.repository.Article.ArticleFieldRepository;
import link.dwsy.ddl.repository.Article.ArticleGroupRepository;
import link.dwsy.ddl.repository.Article.ArticleTagRepository;
import link.dwsy.ddl.repository.User.UserRepository;
import link.dwsy.ddl.service.ArticleFieldService;
import link.dwsy.ddl.support.UserSupport;
import link.dwsy.ddl.util.HtmlHelper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;

/**
 * @Author Dwsy
 * @Date 2022/8/27
 */

@Service
public class ArticleFieldServiceImpl implements ArticleFieldService {

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

    public void ActiveLog(UserActiveType userActiveType, Long sourceId) {
        LoginUserInfo currentUser = userSupport.getCurrentUser();
        if (currentUser != null) {
            rabbitTemplate.convertAndSend(UserActiveConstants.QUEUE_DDL_USER_ACTIVE, UserActiveMessage.builder()
                    .userActiveType(userActiveType).userId(userSupport.getCurrentUser().getId())
                    .sourceId(sourceId).ua(userSupport.getUserAgent()).build());

        }
    }

    public Long createArticle(ArticleContentRB articleContentRB) {
        LoginUserInfo currentUser = userSupport.getCurrentUser();

        HashSet<ArticleTag> articleTags;
        if (articleContentRB.getArticleTagIds().isEmpty()) {
            articleTags = new HashSet<>();
        } else {
            articleTags = new HashSet<>(articleTagRepository.findAllById(articleContentRB.getArticleTagIds()));
        }
        ArticleGroup articleGroup = articleGroupRepository
                .findById(articleContentRB.getArticleGroupId())
                .orElseThrow(() -> new CodeException(CustomerErrorCode.GroupNotFound));
        String html = HtmlHelper.toHTML(articleContentRB.getContent());
        String pure = HtmlHelper.toPure(html);
        if (StrUtil.isBlank(articleContentRB.getSummary())) {
            articleContentRB.setSummary(pure.substring(0, 100));
        }

        ArticleContent content = ArticleContent.builder()
                .textPure(pure)
                .textMd(articleContentRB.getContent())
                .textHtml(html)
                .build();

        ArticleField field = ArticleField.builder()
                .user(userRepository.findUserByIdAndDeletedIsFalse(currentUser.getId()))
                .title(articleContentRB.getTitle())
                .summary(articleContentRB.getSummary())
                .banner(articleContentRB.getBanner())
                .articleState(articleContentRB.getArticleState())
                .articleTags(articleTags)
                .articleGroup(articleGroup)
                .articleContent(content).build();


        ArticleField save = articleFieldRepository.save(field);
        articleContentRepository.setArticleFieldId(save.getId(), save.getArticleContent().getId());
        rabbitTemplate.convertAndSend(ArticleSearchConstants.EXCHANGE_DDL_ARTICLE_SEARCH,ArticleSearchConstants.RK_DDL_ARTICLE_SEARCH_CREATE,save.getId());
        rabbitTemplate.convertAndSend("ddl.article.search.update.all", save.getId());
        return save.getId();
    }



    public Long updateArticle(ArticleContentRB articleContentRB) {
        Long uid = userSupport.getCurrentUser().getId();
        if (!articleFieldRepository.existsByDeletedFalseAndIdAndUser_Id(articleContentRB.getArticleId(), uid)) {
            throw new CodeException(CustomerErrorCode.ArticleNotFound);
        }

        HashSet<ArticleTag> articleTags = null;
        if (articleContentRB.getArticleTagIds().isEmpty()) {
            articleTags = new HashSet<>();
        } else {
            articleTags = new HashSet<>(articleTagRepository.findAllById(articleContentRB.getArticleTagIds()));
        }
        ArticleGroup articleGroup = articleGroupRepository
                .findById(articleContentRB.getArticleGroupId())
                .orElseThrow(() -> new CodeException(CustomerErrorCode.GroupNotFound));


        String html = HtmlHelper.toHTML(articleContentRB.getContent());
        String pure = HtmlHelper.toPure(html);
        if (StrUtil.isBlank(articleContentRB.getSummary())) {
            articleContentRB.setSummary(pure.substring(0, 100));
        }


        ArticleContent content = ArticleContent.builder()
                .textPure(pure)
                .textMd(articleContentRB.getContent())
                .textHtml(html)//todo server to html and pure
                .build();

        ArticleField field = ArticleField.builder()
                .title(articleContentRB.getTitle())
                .summary(articleContentRB.getSummary())
                .banner(articleContentRB.getBanner())
                .articleState(articleContentRB.getArticleState())
                .articleTags(articleTags)
                .articleGroup(articleGroup)
                .articleContent(content).build();

        ArticleField save = articleFieldRepository.save(field);
        rabbitTemplate.convertAndSend(ArticleSearchConstants.EXCHANGE_DDL_ARTICLE_SEARCH,ArticleSearchConstants.RK_DDL_ARTICLE_SEARCH_UPDATE, save.getId());
        return save.getId();
    }

    public void logicallyDeleted(Long articleId) {
//        Long uid = userSupport.getCurrentUser().getId();
//        if (!articleFieldRepository.existsByDeletedFalseAndIdAndUser_Id(articleId, uid)) {
//            throw new CodeException(CustomerErrorCode.ArticleNotFound);
//        }
        rabbitTemplate.convertAndSend(ArticleSearchConstants.EXCHANGE_DDL_ARTICLE_SEARCH,ArticleSearchConstants.RK_DDL_ARTICLE_SEARCH_DELETE,articleId);
        articleFieldRepository.logicallyDeleted(articleId);
        articleContentRepository.logicallyDeleted(articleId);
    }

    public void logicallyRecovery(ArticleRecoveryRB articleRecoveryRB) {
        Long uid = userSupport.getCurrentUser().getId();
        List<Long> aids = articleRecoveryRB.getAids();
        int count = articleFieldRepository.countByDeletedIsFalseAndIdInAndUser_Id(aids, uid);
        if (count != aids.size()) {
            throw new CodeException(CustomerErrorCode.ArticleNotFound);
        }

        for (Long aid : aids) {
            articleFieldRepository.logicallyRecovery(aid);
            articleContentRepository.logicallyRecovery(aid);
            rabbitTemplate.convertAndSend(ArticleSearchConstants.EXCHANGE_DDL_ARTICLE_SEARCH,ArticleSearchConstants.RK_DDL_ARTICLE_SEARCH_CREATE,aid);
        }

    }
}
