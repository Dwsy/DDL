package link.dwsy.ddl.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import link.dwsy.ddl.XO.Enum.Article.ArticleState;
import link.dwsy.ddl.XO.Enum.Article.CodeHighlightStyle;
import link.dwsy.ddl.XO.Enum.Article.MarkDownTheme;
import link.dwsy.ddl.XO.Enum.Article.MarkDownThemeDark;
import link.dwsy.ddl.XO.Enum.InfinityType;
import link.dwsy.ddl.XO.Message.InfinityMessage;
import link.dwsy.ddl.XO.RB.ArticleContentRB;
import link.dwsy.ddl.XO.RB.ArticleRecoveryRB;
import link.dwsy.ddl.constants.article.ArticleRedisKey;
import link.dwsy.ddl.constants.mq.ArticleSearchMQConstants;
import link.dwsy.ddl.constants.mq.InfinityMQConstants;
import link.dwsy.ddl.constants.task.RedisRecordHashKey;
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
import link.dwsy.ddl.service.Impl.ArticleRedisRecordService;
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
import java.util.Set;
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

    //    @Resource
//    private Gson
    @Resource
    private ArticleRedisRecordService articleRedisRecordService;

//    @Override
//    public void ActiveLog(UserActiveType userActiveType, Long sourceId) {
//        LoginUserInfo currentUser = userSupport.getCurrentUser();
//        if (currentUser != null) {
//            UserActiveMessage userActiveMessage = UserActiveMessage
//                    .builder()
//                    .userActiveType(userActiveType)
//                    .userId(userSupport.getCurrentUser().getId())
//                    .sourceId(sourceId)
//                    .ua(userSupport.getUserAgent()).build();
//            rabbitTemplate.convertAndSend(UserActiveMQConstants.QUEUE_DDL_USER_ACTIVE, userActiveMessage);
//
//        }
//    }

    @Override
    public long createArticle(ArticleContentRB articleContentRB) {
        ArticleState articleState = articleContentRB.getArticleState();
        String title = articleContentRB.getTitle();
        title = title.trim().replaceAll("\n", "");
        Set<ArticleState> allowState = Set.of(ArticleState.draft, ArticleState.published, ArticleState.hide);
        if (!allowState.contains(articleState)) {
            throw new CodeException(CustomerErrorCode.ArticleStateError);
        }
        LoginUserInfo currentUser = userSupport.getCurrentUser();

        ArrayList<ArticleTag> articleTags;
        if (articleContentRB.getArticleTagIds().isEmpty()) {
            articleTags = new ArrayList<>();
        } else {
            articleTags = new ArrayList<>(articleTagRepository.findAllById(articleContentRB.getArticleTagIds().stream().distinct().collect(Collectors.toList())));
        }
        ArticleGroup articleGroup = articleGroupRepository.findById(articleContentRB.getArticleGroupId()).orElseThrow(() -> new CodeException(CustomerErrorCode.GroupNotFound));
        String html = HtmlHelper.toHTML(articleContentRB.getContent());
        String pure = HtmlHelper.toPure(html);
        if (StrUtil.isBlank(articleContentRB.getSummary())) {
            articleContentRB.setSummary(pure.substring(0, Math.min(pure.length(), 200)));
        }

        ArticleContent content = ArticleContent.builder().textPure(pure).textMd(articleContentRB.getContent()).textHtml(html).build();

        ArticleField field = ArticleField.builder().user(userRepository.findUserByIdAndDeletedIsFalse(currentUser.getId())).title(title).summary(articleContentRB.getSummary()).banner(articleContentRB.getBanner()).articleState(articleState).articleTags(articleTags).articleGroup(articleGroup).articleSource(articleContentRB.getArticleSource()).articleSourceUrl(articleContentRB.getArticleSourceUrl()).articleContent(content).build();


        ArticleField save = articleFieldRepository.save(field);
        long articleId = save.getId();
        articleContentRepository.setArticleFieldId(articleId, save.getArticleContent().getId());
        if (articleState == ArticleState.published) {
            rabbitTemplate.convertAndSend(ArticleSearchMQConstants.EXCHANGE_DDL_ARTICLE_SEARCH, ArticleSearchMQConstants.RK_DDL_ARTICLE_SEARCH_CREATE, articleId);
            if (articleContentRB.isSendInfinity()) {
                InfinityMessage infinityMessage = InfinityMessage.builder().infinityType(InfinityType.Article).refId(articleId).build();
                rabbitTemplate.convertAndSend(InfinityMQConstants.QUEUE_DDL_INFINITY_SEND, infinityMessage);
            }
        }
        return articleId;
    }

    @Override
    public long updateArticle(ArticleContentRB articleContentRB) {
        String title = articleContentRB.getTitle();
        title = title.trim().replaceAll("\n", "");
        ArticleState articleState = articleContentRB.getArticleState();
        Set<ArticleState> allowState = Set.of(ArticleState.draft, ArticleState.published, ArticleState.hide);
        if (!allowState.contains(articleState)) {
            throw new CodeException(CustomerErrorCode.ArticleStateError);
        }
        Long uid = userSupport.getCurrentUser().getId();
        if (!articleFieldRepository.existsByDeletedFalseAndIdAndUser_Id(articleContentRB.getArticleId(), uid)) {
            throw new CodeException(CustomerErrorCode.ArticleNotFound);
        }

        ArrayList<ArticleTag> articleTags;
        if (articleContentRB.getArticleTagIds().isEmpty()) {
            articleTags = new ArrayList<>();
        } else {
            articleTags = new ArrayList<>(articleTagRepository.findAllById(articleContentRB.getArticleTagIds().stream().distinct().collect(Collectors.toList())));
        }
        ArticleGroup articleGroup = articleGroupRepository.findById(articleContentRB.getArticleGroupId()).orElseThrow(() -> new CodeException(CustomerErrorCode.GroupNotFound));


        String MdText = articleContentRB.getContent();
        String html = HtmlHelper.toHTML(MdText);
        String pure = HtmlHelper.toPure(html);
        if (StrUtil.isBlank(articleContentRB.getSummary())) {
            articleContentRB.setSummary(pure.substring(0, 150));
        }


        ArticleField field = articleFieldRepository.findByDeletedFalseAndId(articleContentRB.getArticleId());
        Optional<ArticleContent> articleContentOptional = articleContentRepository.findById(articleFieldRepository.getContentIdById(articleContentRB.getArticleId()).longValue());
        if (articleContentOptional.isEmpty()) {
            throw new CodeException(CustomerErrorCode.ArticleNotFound);
        }
        //历史版本保存
        ArticleContent articleContent = articleContentOptional.get();
        int version = field.getVersion() + 1;
        if (articleState == ArticleState.published || articleState == ArticleState.draft) {
            long fieldId = field.getId();
            redisTemplate.opsForList().rightPush(ArticleRedisKey.getHistoryVersionFieldKey(fieldId), JSON.toJSONString(field));
            redisTemplate.opsForList().rightPush(ArticleRedisKey.getHistoryVersionTitleKey(fieldId), title);
            redisTemplate.opsForList().rightPush(ArticleRedisKey.getHistoryVersionContentKey(fieldId), articleContent.getTextMd());
            redisTemplate.opsForList().rightPush(ArticleRedisKey.getHistoryVersionCreateDateKey(fieldId), String.valueOf(System.currentTimeMillis()));
        }

        articleContent.setTextHtml(html);
        articleContent.setTextMd(MdText);
        articleContent.setTextPure(pure);
        field.setVersion(version);
        field.setArticleContent(articleContent);
        field.setTitle(title);
        field.setSummary(articleContentRB.getSummary());
        field.setBanner(articleContentRB.getBanner());
        field.setArticleState(articleState);
        field.setArticleTags(articleTags);
        field.setArticleGroup(articleGroup);
        field.setArticleSource(articleContentRB.getArticleSource());
        field.setArticleSourceUrl(articleContentRB.getArticleSourceUrl());

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
        if (articleContentRB.getArticleState() == ArticleState.published) {
            rabbitTemplate.convertAndSend(ArticleSearchMQConstants.EXCHANGE_DDL_ARTICLE_SEARCH, ArticleSearchMQConstants.RK_DDL_ARTICLE_SEARCH_UPDATE, articleContentRB.getArticleId());
        } else {
            rabbitTemplate.convertAndSend(ArticleSearchMQConstants.EXCHANGE_DDL_ARTICLE_SEARCH, ArticleSearchMQConstants.RK_DDL_ARTICLE_SEARCH_DELETE, articleContentRB.getArticleId());
        }

        return save.getId();
    }

    @Override
    public void logicallyDeleted(Long articleId) {
//        Long uid = userSupport.getCurrentUser().getId();
//        if (!articleFieldRepository.existsByDeletedFalseAndIdAndUser_Id(articleId, uid)) {
//            throw new CodeException(CustomerErrorCode.ArticleNotFound);
//        }
        Long userId = userSupport.getCurrentUser().getId();
        if (!articleFieldRepository.existsByDeletedFalseAndIdAndUser_Id(articleId, userId)) {
            throw new CodeException(CustomerErrorCode.ArticleNotFound);
        }
        rabbitTemplate.convertAndSend(ArticleSearchMQConstants.EXCHANGE_DDL_ARTICLE_SEARCH, ArticleSearchMQConstants.RK_DDL_ARTICLE_SEARCH_DELETE, articleId);
        articleFieldRepository.logicallyDeleted(articleId);
        articleContentRepository.logicallyDeleted(articleId);
    }

    @Override
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
            rabbitTemplate.convertAndSend(ArticleSearchMQConstants.EXCHANGE_DDL_ARTICLE_SEARCH, ArticleSearchMQConstants.RK_DDL_ARTICLE_SEARCH_CREATE, aid);
        }

    }

    @Override
    public void view(Long id) {
        articleRedisRecordService.record(id, RedisRecordHashKey.view, 1);
        articleFieldRepository.viewNumIncrement(id, 1);
    }


}
