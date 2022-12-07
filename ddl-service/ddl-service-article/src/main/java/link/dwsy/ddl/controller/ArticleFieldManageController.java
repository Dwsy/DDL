package link.dwsy.ddl.controller;

import link.dwsy.ddl.XO.Enum.Article.ArticleState;
import link.dwsy.ddl.XO.RB.ArticleRecoveryRB;
import link.dwsy.ddl.XO.VO.VersionData;
import link.dwsy.ddl.XO.VO.fieldVO;
import link.dwsy.ddl.annotation.AuthAnnotation;
import link.dwsy.ddl.core.CustomExceptions.CodeException;
import link.dwsy.ddl.core.constant.CustomerErrorCode;
import link.dwsy.ddl.core.domain.LoginUserInfo;
import link.dwsy.ddl.entity.Article.ArticleField;
import link.dwsy.ddl.repository.Article.ArticleFieldRepository;
import link.dwsy.ddl.repository.User.UserRepository;
import link.dwsy.ddl.service.Impl.UserActiveCommonServiceImpl;
import link.dwsy.ddl.service.impl.ArticleContentServiceImpl;
import link.dwsy.ddl.service.impl.ArticleFieldServiceImpl;
import link.dwsy.ddl.support.UserSupport;
import link.dwsy.ddl.util.PRHelper;
import link.dwsy.ddl.util.PageData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

/**
 * @Author Dwsy
 * @Date 2022/10/15
 */

@RestController
@RequestMapping("article/manage")
@Slf4j
public class ArticleFieldManageController {
    @Resource
    private ArticleContentServiceImpl articleContentService;

    @Resource
    private ArticleFieldServiceImpl articleFieldService;

    @Resource
    private UserActiveCommonServiceImpl userActiveCommonService;

    @Resource
    private UserRepository userRepository;

    @Resource
    private UserSupport userSupport;
    @Resource
    private ArticleFieldRepository articleFieldRepository;

    @GetMapping("field/list")
    @AuthAnnotation
    public PageData<fieldVO> articleList(
            @RequestParam(required = false, defaultValue = "DESC", name = "order") String order,
            @RequestParam(required = false, defaultValue = "createTime", name = "properties") String[] properties,
            @RequestParam(required = false, defaultValue = "1", name = "page") int page,
            @RequestParam(required = false, defaultValue = "8", name = "size") int size,
            @RequestParam(required = false, defaultValue = "0", name = "tagId") long tagId,
            @RequestParam(required = false, defaultValue = "all", name = "state") ArticleState state
    ) {
        if (size < 1)
            throw new CodeException(CustomerErrorCode.ParamError);
        PageRequest pageRequest = PRHelper.order(order, properties, page, size);
        LoginUserInfo user = userSupport.getCurrentUser();
        Page<fieldVO> articleList;
        if (state == ArticleState.all) {
            articleList = articleFieldRepository.
                    findByUser_IdAndArticleStateNotAndDeleted(user.getId(), ArticleState.draft, false, pageRequest);
        } else {
            articleList = articleFieldRepository.
                    findByUser_IdAndArticleStateAndDeleted(user.getId(), state, false, pageRequest);
        }
        return new PageData<>(articleList);
    }

    @GetMapping("field/num")
    @AuthAnnotation
    public Map<ArticleState, Integer> getArticleCountByState() {
        LoginUserInfo user = userSupport.getCurrentUser();
        HashMap<ArticleState, Integer> countByState = new HashMap<>();
        countByState.put(ArticleState.all, articleFieldRepository.countByDeletedFalseAndUser_IdAndArticleStateNot(user.getId(), ArticleState.draft));
        countByState.put(ArticleState.draft, articleFieldRepository.countByDeletedFalseAndUser_IdAndArticleState(user.getId(), ArticleState.draft));
        countByState.put(ArticleState.published, articleFieldRepository.countByDeletedFalseAndUser_IdAndArticleState(user.getId(), ArticleState.published));
        countByState.put(ArticleState.hide, articleFieldRepository.countByDeletedFalseAndUser_IdAndArticleState(user.getId(), ArticleState.hide));
        countByState.put(ArticleState.auditing, articleFieldRepository.countByDeletedFalseAndUser_IdAndArticleState(user.getId(), ArticleState.auditing));
        countByState.put(ArticleState.rejected, articleFieldRepository.countByDeletedFalseAndUser_IdAndArticleState(user.getId(), ArticleState.rejected));
        return countByState;
    }

    @GetMapping("field/{id}")
    @AuthAnnotation
    public ArticleField getArticleById(@PathVariable("id") Long id,
                                       @RequestParam(required = false, defaultValue = "-1", name = "version") int version) {
        if (id < 0L)
            throw new CodeException(CustomerErrorCode.ParamError);
        Long userId = userSupport.getCurrentUser().getId();
        Long articleOwnerUserId = articleFieldRepository.findUserIdById(id);
        if (!userId.equals(articleOwnerUserId)) {
            throw new CodeException(CustomerErrorCode.ArticleNotFound);
        }
        if (version > -1) {
            return articleContentService.getArticleFieldByIdAndVersion(id, version);
        }
        ArticleField article = articleContentService.getArticleById(id, Set.of(ArticleState.published, ArticleState.hide, ArticleState.draft));
        if (article == null)
            throw new CodeException(CustomerErrorCode.ArticleNotFound);
        return article;
    }

    /**
     * @param id   id
     * @param type 0 html 1 md 2 pure
     * @return String
     */
    @GetMapping(value = "content/{id}", produces = "application/json")
    @AuthAnnotation
    public String getArticleContent(
            @PathVariable(name = "id") Long id,
            @RequestParam(required = false, defaultValue = "0", name = "type") int type,
            @RequestParam(required = false, defaultValue = "-1", name = "version") int version) {
        if (id < 0L)
            throw new CodeException(CustomerErrorCode.ParamError);
        if (type < 0 || type > 2)
            throw new CodeException(CustomerErrorCode.ParamError);
        Long userId = userSupport.getCurrentUser().getId();
        Long articleOwnerUserId = articleFieldRepository.findUserIdById(id);
        if (!userId.equals(articleOwnerUserId)) {
            throw new CodeException(CustomerErrorCode.ArticleGroupNotBelongToUser);
        }
        if (version > -1) {
            return articleContentService.getArticleContentByIdAndVersion(id, version);
        }
        Optional<String> ret = Optional.ofNullable(articleContentService.getContent(id, type));
        if (ret.isPresent()) {
            return ret.get();
        } else {
            throw new CodeException(CustomerErrorCode.ArticleNotFound);
        }
    }

    @GetMapping("historyVersion/{id}")
    @AuthAnnotation
    public Map<String, VersionData> getHistoryVersion(@PathVariable long id) {

        return articleContentService.getHistoryVersionTitle(id);
    }


//    @PostMapping("saveVersion/{id}")
//    @AuthAnnotation
//public void saveVersion(@PathVariable long id, @RequestBody Map<String, String> map) {
//        String title = map.get("title");
//        String content = map.get("content");
//        if (title == null || content == null)
//            throw new CodeException(CustomerErrorCode.ParamError);
//        articleContentService.saveVersion(id, title, content);
//    }

    @DeleteMapping("{articleId}")
    @AuthAnnotation
    public boolean deleteArticle(@PathVariable Long articleId) {
        if (articleId == null || articleId < 0) {
            throw new CodeException(CustomerErrorCode.ArticleNotFound);
        }

        try {
            articleFieldService.logicallyDeleted(articleId);
        } catch (Exception e) {
            log.info("删除文章{}失败", articleId);
            return false;
        }
        return true;
    }

    @PostMapping("recovery")
    @AuthAnnotation
    public void recoveryArticle(@RequestBody @Validated ArticleRecoveryRB articleRecoveryRB) {
        articleFieldService.logicallyRecovery(articleRecoveryRB);
    }

}
