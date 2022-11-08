package link.dwsy.ddl.service.impl;

import cn.hutool.core.util.StrUtil;
import link.dwsy.ddl.XO.Enum.Article.CodeHighlightStyle;
import link.dwsy.ddl.XO.Enum.Article.MarkDownTheme;
import link.dwsy.ddl.XO.Enum.Article.MarkDownThemeDark;
import link.dwsy.ddl.XO.Enum.User.UserActiveType;
import link.dwsy.ddl.XO.Message.UserActiveMessage;
import link.dwsy.ddl.XO.RB.ArticleContentRB;
import link.dwsy.ddl.XO.RB.ArticleRecoveryRB;
import link.dwsy.ddl.constants.mq.ArticleSearchConstants;
import link.dwsy.ddl.constants.mq.UserActiveConstants;
import link.dwsy.ddl.constants.task.RedisRecordKey;
import link.dwsy.ddl.core.CustomExceptions.CodeException;
import link.dwsy.ddl.core.constant.CustomerErrorCode;
import link.dwsy.ddl.core.domain.LoginUserInfo;
import link.dwsy.ddl.entity.Article.ArticleContent;
import link.dwsy.ddl.entity.Article.ArticleField;
import link.dwsy.ddl.entity.Article.ArticleGroup;
import link.dwsy.ddl.entity.Article.ArticleTag;
import link.dwsy.ddl.repository.Article.ArticleContentRepository;
import link.dwsy.ddl.repository.Article.ArticleFieldRepository;
import link.dwsy.ddl.repository.Article.ArticleGroupRepository;
import link.dwsy.ddl.repository.Article.ArticleTagRepository;
import link.dwsy.ddl.repository.User.UserRepository;
import link.dwsy.ddl.service.ArticleFieldService;
import link.dwsy.ddl.support.UserSupport;
import link.dwsy.ddl.util.HtmlHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Author Dwsy
 * @Date 2022/8/27
 */

@Service
@Slf4j
public class ArticleFieldServiceImpl implements ArticleFieldService {

    @Resource
    private ArticleTagRepository articleTagRepository;
    @Resource
    private ArticleFieldRepository articleFieldRepository;
    @Resource
    private ArticleGroupRepository articleGroupRepository;
    @Resource
    private ArticleContentRepository articleContentRepository;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private UserSupport userSupport;

    @Resource(name = "stringRedisTemplate")
    private StringRedisTemplate redisTemplate;
    @Resource
    private UserRepository userRepository;

    public void ActiveLog(UserActiveType userActiveType, Long sourceId) {
        LoginUserInfo currentUser = userSupport.getCurrentUser();
        if (currentUser != null) {
            UserActiveMessage userActiveMessage = UserActiveMessage.builder()
                    .userActiveType(userActiveType).userId(userSupport.getCurrentUser().getId())
                    .sourceId(sourceId).ua(userSupport.getUserAgent()).build();
            log.info(userActiveMessage.toString());
            rabbitTemplate.convertAndSend(UserActiveConstants.QUEUE_DDL_USER_ACTIVE, userActiveMessage);

        }
    }

    public long createArticle(ArticleContentRB articleContentRB) {
        LoginUserInfo currentUser = userSupport.getCurrentUser();

        ArrayList<ArticleTag> articleTags;
        if (articleContentRB.getArticleTagIds().isEmpty()) {
            articleTags = new ArrayList<>();
        } else {
            articleTags = new ArrayList<>(articleTagRepository.findAllById(
                    articleContentRB.getArticleTagIds().stream().distinct().collect(Collectors.toList())
            ));
        }
        ArticleGroup articleGroup = articleGroupRepository
                .findById(articleContentRB.getArticleGroupId())
                .orElseThrow(() -> new CodeException(CustomerErrorCode.GroupNotFound));
        String html = HtmlHelper.toHTML(articleContentRB.getContent());
        String pure = HtmlHelper.toPure(html);
        if (StrUtil.isBlank(articleContentRB.getSummary())) {
            articleContentRB.setSummary(pure.substring(0, 200));
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
        if (articleContentRB.getMarkDownTheme() == null) {
            field.setMarkDownTheme(MarkDownTheme.cyanosis);
        } else {
            field.setMarkDownTheme(articleContentRB.getMarkDownTheme());
        }
        if (articleContentRB.getMarkDownThemeDark() == null) {
            field.setMarkDownThemeDark(MarkDownThemeDark.geekBlackDark);
        } else {
            field.setMarkDownThemeDark(articleContentRB.getMarkDownThemeDark());
        }
        if (articleContentRB.getCodeHighlightStyle() == null) {
            field.setCodeHighlightStyle(CodeHighlightStyle.xcode);
        } else {
            field.setCodeHighlightStyle(articleContentRB.getCodeHighlightStyle());
        }

        ArticleField save = articleFieldRepository.save(field);
        articleContentRepository.setArticleFieldId(save.getId(), save.getArticleContent().getId());

        rabbitTemplate.convertAndSend(ArticleSearchConstants.EXCHANGE_DDL_ARTICLE_SEARCH, ArticleSearchConstants.RK_DDL_ARTICLE_SEARCH_CREATE, save.getId());
//        rabbitTemplate.convertAndSend("ddl.article.search.update.all", save.getId());
        return save.getId();
    }


    public long updateArticle(ArticleContentRB articleContentRB) {
        Long uid = userSupport.getCurrentUser().getId();
        if (!articleFieldRepository.existsByDeletedFalseAndIdAndUser_Id(articleContentRB.getArticleId(), uid)) {
            throw new CodeException(CustomerErrorCode.ArticleNotFound);
        }

        ArrayList<ArticleTag> articleTags = null;
        if (articleContentRB.getArticleTagIds().isEmpty()) {
            articleTags = new ArrayList<>();
        } else {
            articleTags = new ArrayList<>(articleTagRepository.findAllById(
                    articleContentRB.getArticleTagIds().stream().distinct().collect(Collectors.toList())
            ));
        }
        ArticleGroup articleGroup = articleGroupRepository
                .findById(articleContentRB.getArticleGroupId())
                .orElseThrow(() -> new CodeException(CustomerErrorCode.GroupNotFound));


        String html = HtmlHelper.toHTML(articleContentRB.getContent());
        String pure = HtmlHelper.toPure(html);
        if (StrUtil.isBlank(articleContentRB.getSummary())) {
            articleContentRB.setSummary(pure.substring(0, 150));
        }


        ArticleField field = articleFieldRepository.findByDeletedFalseAndId(articleContentRB.getArticleId());
        Optional<ArticleContent> articleContentOptional = articleContentRepository.findById
                (articleFieldRepository.getContentIdById(articleContentRB.getArticleId()).longValue());
        if (articleContentOptional.isEmpty()) {
            throw new CodeException(CustomerErrorCode.ArticleNotFound);
        }
        ArticleContent articleContent = articleContentOptional.get();
        articleContent.setTextHtml(html);
        articleContent.setTextMd(articleContentRB.getContent());
        articleContent.setTextPure(pure);
        field.setTitle(articleContentRB.getTitle());
        field.setSummary(articleContentRB.getSummary());
        field.setBanner(articleContentRB.getBanner());
        field.setArticleState(articleContentRB.getArticleState());
        field.setArticleTags(articleTags);
        field.setArticleGroup(articleGroup);
        field.setArticleSource(articleContentRB.getArticleSource());
        field.setArticleSourceUrl(articleContentRB.getArticleSourceUrl());
        field.setArticleContent(articleContent);
        if (articleContentRB.getMarkDownTheme() == null) {
            field.setMarkDownTheme(MarkDownTheme.cyanosis);
        } else {
            field.setMarkDownTheme(articleContentRB.getMarkDownTheme());
        }
        if (articleContentRB.getMarkDownThemeDark() == null) {
            field.setMarkDownThemeDark(MarkDownThemeDark.geekBlackDark);
        } else {
            field.setMarkDownThemeDark(articleContentRB.getMarkDownThemeDark());
        }
        if (articleContentRB.getCodeHighlightStyle() == null) {
            field.setCodeHighlightStyle(CodeHighlightStyle.xcode);
        } else {
            field.setCodeHighlightStyle(articleContentRB.getCodeHighlightStyle());
        }
        ArticleField save = articleFieldRepository.save(field);
        rabbitTemplate.convertAndSend(ArticleSearchConstants.EXCHANGE_DDL_ARTICLE_SEARCH,
                ArticleSearchConstants.RK_DDL_ARTICLE_SEARCH_UPDATE, save.getId());
        return save.getId();
    }

    public void logicallyDeleted(Long articleId) {
//        Long uid = userSupport.getCurrentUser().getId();
//        if (!articleFieldRepository.existsByDeletedFalseAndIdAndUser_Id(articleId, uid)) {
//            throw new CodeException(CustomerErrorCode.ArticleNotFound);
//        }
        rabbitTemplate.convertAndSend(ArticleSearchConstants.EXCHANGE_DDL_ARTICLE_SEARCH,
                ArticleSearchConstants.RK_DDL_ARTICLE_SEARCH_DELETE, articleId);
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
            rabbitTemplate.convertAndSend(ArticleSearchConstants.EXCHANGE_DDL_ARTICLE_SEARCH,
                    ArticleSearchConstants.RK_DDL_ARTICLE_SEARCH_CREATE, aid);
        }

    }

    public void view(Long id) {
        redisTemplate.opsForHash().increment(RedisRecordKey.RedisArticleRecordKey, id.toString(), 1);
        String num = (String) redisTemplate.opsForHash().get(RedisRecordKey.RedisArticleRecordKey, id.toString());
        if (num != null && (Integer.parseInt(num)) % 10 == 0) {
            log.info("RK_DDL_ARTICLE_SEARCH_UPDATE_SCORE:{}", id);
            rabbitTemplate.convertAndSend(ArticleSearchConstants.EXCHANGE_DDL_ARTICLE_SEARCH, ArticleSearchConstants.RK_DDL_ARTICLE_SEARCH_UPDATE_SCORE, id);
        }
        articleFieldRepository.viewNumIncrement(id, 1);
    }


}
