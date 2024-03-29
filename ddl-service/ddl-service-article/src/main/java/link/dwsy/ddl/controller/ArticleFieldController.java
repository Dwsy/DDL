package link.dwsy.ddl.controller;

//import link.dwsy.ddl.XO.DTO.ArticleContentDTO;

import link.dwsy.ddl.XO.Enum.Article.ArticleState;
import link.dwsy.ddl.XO.RB.ArticleContentRB;
import link.dwsy.ddl.XO.RB.ArticleRecoveryRB;
import link.dwsy.ddl.XO.VO.UserActionVO;
import link.dwsy.ddl.XO.VO.fieldVO;
import link.dwsy.ddl.annotation.AuthAnnotation;
import link.dwsy.ddl.core.CustomExceptions.CodeException;
import link.dwsy.ddl.core.constant.CustomerErrorCode;
import link.dwsy.ddl.entity.Article.ArticleField;
import link.dwsy.ddl.repository.Article.ArticleFieldRepository;
import link.dwsy.ddl.repository.User.UserRepository;
import link.dwsy.ddl.service.Impl.UserActiveCommonServiceImpl;
import link.dwsy.ddl.service.impl.ArticleContentServiceImpl;
import link.dwsy.ddl.service.impl.ArticleFieldServiceImpl;
import link.dwsy.ddl.util.PRHelper;
import link.dwsy.ddl.util.PageData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
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
    private ArticleContentServiceImpl articleContentService;

    @Resource
    private ArticleFieldServiceImpl articleFieldService;

    @Resource
    private ArticleFieldRepository articleFieldRepository;

    @Resource
    private UserActiveCommonServiceImpl userActiveCommonService;

    @Resource
    private UserRepository userRepository;


    @GetMapping("field/list")
    public PageData<fieldVO> articleList(
            @RequestParam(required = false, defaultValue = "DESC", name = "order") String order,
            @RequestParam(required = false, defaultValue = "createTime", name = "properties") String[] properties,
            @RequestParam(required = false, defaultValue = "1", name = "page") int page,
            @RequestParam(required = false, defaultValue = "8", name = "size") int size,
            @RequestParam(required = false, defaultValue = "0", name = "tagId") long tagId) {
        if (size < 1)
            throw new CodeException(CustomerErrorCode.ParamError);
        PageRequest pageRequest = PRHelper.order(order, properties, page, size);
        if (tagId == 0) {
            return articleContentService.getPageList(pageRequest, ArticleState.published);
        } else {
            return articleContentService.getPageList(pageRequest, ArticleState.published, tagId);
        }
    }

    @GetMapping("field/list/{uid}")
    public PageData<fieldVO> getArticleListByUserId(
            @RequestParam(required = false, defaultValue = "ASC", name = "order") String order,
            @RequestParam(required = false, defaultValue = "createTime", name = "properties") String[] properties,
            @RequestParam(required = false, defaultValue = "1", name = "page") int page,
            @RequestParam(required = false, defaultValue = "8", name = "size") int size,
//            @RequestParam(required = false, defaultValue = "0", name = "tagId") long tagId,
            @PathVariable long uid) {
        if (size < 1)
            throw new CodeException(CustomerErrorCode.ParamError);
        if (!userRepository.existsById(uid)) {
            throw new CodeException(CustomerErrorCode.UserNotExist);
        }
        PageRequest pageRequest = PRHelper.order(order, properties, page, size);
        return articleContentService.getArticleListByUserId(pageRequest, ArticleState.published, uid);
//        if (tagId == 0) {
//            return articleContentService.getArticleListByUserId(pageRequest, ArticleState.published);
//        } else {
//            return articleContentService.getArticleListByUserId(pageRequest, ArticleState.published, tagId);
//        }
    }

    @GetMapping("field/{id}")
    public ArticleField getArticleById(@PathVariable("id") Long id) {
        if (id < 0L)
            throw new CodeException(CustomerErrorCode.ParamError);
        ArticleField article = articleContentService.getArticleById(id, ArticleState.published);
        if (article == null)
            throw new CodeException(CustomerErrorCode.ArticleNotFound);
        articleFieldService.view(id);
        return article;
    }


    @GetMapping("field/action/{id}")
    @AuthAnnotation()
    public UserActionVO getUserAction(@PathVariable Long id) {

        return articleContentService.getUserAction(id);
    }

    /**
     * @param id   id
     * @param type 0 html 1 md 2 pure
     * @return String
     */
    @GetMapping(value = "content/{id}", produces = "application/json")
//    @IgnoreResponseAdvice
    public String getArticleContent(
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
    public String createArticle(@RequestBody @Validated ArticleContentRB articleContentRB) {
        return String.valueOf(articleFieldService.createArticle(articleContentRB));
    }

    @PutMapping
    @AuthAnnotation
    public String updateArticle(@RequestBody @Validated ArticleContentRB articleContentRB) {
        if (articleContentRB.getArticleId() == null || articleContentRB.getArticleId() < 0) {
            throw new CodeException(CustomerErrorCode.ArticleNotFound);
        }
        return String.valueOf(articleFieldService.updateArticle(articleContentRB));
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
