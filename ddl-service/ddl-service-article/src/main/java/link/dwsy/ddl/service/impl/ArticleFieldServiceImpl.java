package link.dwsy.ddl.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import link.dwsy.ddl.XO.Enum.Article.ArticleState;
import link.dwsy.ddl.XO.Enum.User.UserActiveType;
import link.dwsy.ddl.XO.Message.UserActiveMessage;
import link.dwsy.ddl.XO.RB.ArticleContentRB;
import link.dwsy.ddl.XO.RB.ArticleRecoveryRB;
import link.dwsy.ddl.constants.article.ArticleRedisKey;
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
        ArticleState articleState = articleContentRB.getArticleState();
        Set<ArticleState> allowState = Set.of(ArticleState.draft, ArticleState.published, ArticleState.hide);
        if (!allowState.contains(articleState)) {
            throw new CodeException(CustomerErrorCode.ArticleStateError);
        }
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
                .articleState(articleState)
                .articleTags(articleTags)
                .articleGroup(articleGroup)
                .articleContent(content).build();


        ArticleField save = articleFieldRepository.save(field);
        articleContentRepository.setArticleFieldId(save.getId(), save.getArticleContent().getId());

        if (articleState == ArticleState.published) {
            rabbitTemplate.convertAndSend(ArticleSearchConstants.EXCHANGE_DDL_ARTICLE_SEARCH, ArticleSearchConstants.RK_DDL_ARTICLE_SEARCH_CREATE, save.getId());
        }
        return save.getId();
    }


    public long updateArticle(ArticleContentRB articleContentRB) {
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
            articleTags = new ArrayList<>(articleTagRepository.findAllById(
                    articleContentRB.getArticleTagIds().stream().distinct().collect(Collectors.toList())
            ));
        }
        ArticleGroup articleGroup = articleGroupRepository
                .findById(articleContentRB.getArticleGroupId())
                .orElseThrow(() -> new CodeException(CustomerErrorCode.GroupNotFound));


        String MdText = articleContentRB.getContent();
        String html = HtmlHelper.toHTML(MdText);
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
        //历史版本保存
        int version = field.getVersion() + 1;
        if (articleState == ArticleState.published || articleState == ArticleState.draft) {
            redisTemplate.opsForList().rightPush(ArticleRedisKey.ArticleHistoryVersionFieldKey + field.getId(), JSON.toJSONString(field));
            redisTemplate.opsForList().rightPush(ArticleRedisKey.ArticleHistoryVersionContentKey + field.getId(), MdText);
        }
        ArticleContent articleContent = articleContentOptional.get();
        articleContent.setTextHtml(html);
        articleContent.setTextMd(MdText);
        articleContent.setTextPure(pure);
        field.setVersion(version);
        field.setArticleContent(articleContent);
        field.setTitle(articleContentRB.getTitle());
        field.setSummary(articleContentRB.getSummary());
        field.setBanner(articleContentRB.getBanner());
        field.setArticleState(articleState);
        field.setArticleTags(articleTags);
        field.setArticleGroup(articleGroup);
        field.setArticleSource(articleContentRB.getArticleSource());
        field.setArticleSourceUrl(articleContentRB.getArticleSourceUrl());


        ArticleField save = articleFieldRepository.save(field);
        if (articleContentRB.getArticleState() == ArticleState.published) {
            rabbitTemplate.convertAndSend(ArticleSearchConstants.EXCHANGE_DDL_ARTICLE_SEARCH,
                    ArticleSearchConstants.RK_DDL_ARTICLE_SEARCH_UPDATE, save.getId());
        } else {
            //todo 隐藏搜索项
        }

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
