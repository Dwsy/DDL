package link.dwsy.ddl.controller;

//import link.dwsy.ddl.XO.DTO.ArticleContentDTO;

import link.dwsy.ddl.XO.Enum.Article.ArticleState;
import link.dwsy.ddl.XO.Enum.UserActiveType;
import link.dwsy.ddl.XO.RB.ArticleContentRB;
import link.dwsy.ddl.XO.RB.ArticleRecoveryRB;
import link.dwsy.ddl.XO.VO.fieldVO;
import link.dwsy.ddl.annotation.AuthAnnotation;
import link.dwsy.ddl.core.CustomExceptions.CodeException;
import link.dwsy.ddl.core.constant.CustomerErrorCode;
import link.dwsy.ddl.entity.Article.ArticleField;
import link.dwsy.ddl.service.Impl.UserActiveServiceImpl;
import link.dwsy.ddl.service.impl.ArticleContentServiceImpl;
import link.dwsy.ddl.service.impl.ArticleFieldServiceImpl;
import link.dwsy.ddl.util.PRHelper;
import link.dwsy.ddl.util.PageData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.Size;
import java.util.Optional;

/**
 * @Author Dwsy
 * @Date 2022/8/25
 */

@RestController
@RequestMapping("article")
@Slf4j
public class ArticleFieldController {

    @Resource
    ArticleContentServiceImpl articleContentService;

    @Resource
    ArticleFieldServiceImpl articleFieldService;

    @Resource
    UserActiveServiceImpl userActiveService;


    @GetMapping("field/list")
    public PageData<fieldVO> articleList(
            @RequestParam(required = false, defaultValue = "ASC", name = "order") String order,
            @RequestParam(required = false, defaultValue = "createTime", name = "properties") String[] properties,
            @RequestParam(required = false, defaultValue = "1", name = "page") int page,
            @RequestParam(required = false, defaultValue = "8", name = "size") int size) {
        if (size < 1)
            throw new CodeException(CustomerErrorCode.ParamError);
        PageRequest pageRequest = PRHelper.order(order, properties, page, size);
        return articleContentService.getPageList(pageRequest, ArticleState.open);
    }

    @GetMapping("field/{id}")
    public ArticleField getArticleById(@PathVariable("id") Long id) {
        if (id < 0L)
            throw new CodeException(CustomerErrorCode.ParamError);
        articleFieldService.ActiveLog(UserActiveType.Browse_Article, id);
        return articleContentService.getArticleById(id, ArticleState.open);
    }

    /**
     * @param id   id
     * @param type 0 html 1 md 2 pure
     * @return String
     */
    @GetMapping(value = "content/{id}", produces = "application/json")
//    @IgnoreResponseAdvice
    public String getArticleContent(
            @Size
            @PathVariable(name = "id") Long id,
            @RequestParam(required = false, defaultValue = "0", name = "type") int type) {
        if (id < 0L)
            throw new CodeException(CustomerErrorCode.ParamError);

        if (type < 0 || type > 2)
            throw new CodeException(CustomerErrorCode.ParamError);
        Optional<String> ret = Optional.ofNullable(articleContentService.getContent(id, type));
        if (ret.isPresent()) {
            return ret.get();
        } else {
            throw new CodeException(CustomerErrorCode.ArticleNotFound);
        }
    }

    @PostMapping
    @AuthAnnotation
    public Long createArticle(@RequestBody @Validated ArticleContentRB articleContentRB) {
        Long articleId = articleFieldService.createArticle(articleContentRB);
        return articleId;
    }
    @PutMapping
    @AuthAnnotation
    public Long updateArticle(@RequestBody @Validated ArticleContentRB articleContentRB) {
        if (articleContentRB.getArticleId() == null||articleContentRB.getArticleId()<0) {
            throw new CodeException(CustomerErrorCode.ArticleNotFound);
        }
        Long articleId = articleFieldService.updateArticle(articleContentRB);
        return articleId;
    }
    @DeleteMapping("{articleId}")
    @AuthAnnotation
    public boolean deleteArticle(@PathVariable Long articleId) {
        if (articleId == null||articleId<0) {
            throw new CodeException(CustomerErrorCode.ArticleNotFound);
        }

        try {
            articleFieldService.logicallyDeleted(articleId);
        } catch (Exception e) {
            log.info("删除文章{}失败",articleId);
            return false;
        }
        return true;
    }

    @PostMapping("recovery")
    @AuthAnnotation
    public void recoveryArticle(@RequestBody @Validated ArticleRecoveryRB articleRecoveryRB) {
        articleFieldService.logicallyRecovery(articleRecoveryRB);
    }


    @PostMapping("action")
    @AuthAnnotation
    public void actionArticle(@RequestBody @Validated ArticleRecoveryRB articleRecoveryRB) {
//        articleFieldService.actionArticle(articleRecoveryRB);
    }


    @GetMapping(value = "test")
//    @IgnoreResponseAdvice
    public String g() {

        return "乱码二分之一";
    }

}
