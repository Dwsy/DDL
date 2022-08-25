package link.dwsy.ddl.controller;

import link.dwsy.ddl.XO.CustomER.entity.ArticleContentCustom;
//import link.dwsy.ddl.XO.DTO.ArticleContentDTO;
import link.dwsy.ddl.XO.Enum.ArticleState;
import link.dwsy.ddl.annotation.IgnoreResponseAdvice;
import link.dwsy.ddl.core.CustomExceptions.CodeException;
import link.dwsy.ddl.repository.ArticleContentRepository;
import link.dwsy.ddl.repository.ArticleGroupRepository;
import link.dwsy.ddl.repository.ArticleTagRepository;
import link.dwsy.ddl.service.impl.ArticleContentServiceImpl;
import link.dwsy.ddl.util.PageData;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Author Dwsy
 * @Date 2022/8/25
 */

@RestController
@RequestMapping("article")
public class ArticleContentController {
    @Resource
    ArticleTagRepository articleTagRepository;
    @Resource
    ArticleContentRepository articleContentRepository;
    @Resource
    ArticleGroupRepository articleGroupRepository;

    @Resource
    ArticleContentServiceImpl articleContentService;

    @GetMapping("list")
    public PageData<ArticleContentCustom> ArticleList(
            @RequestParam(required = false, defaultValue = "1" , name = "page") int page,
            @RequestParam(required = false, defaultValue = "8", name = "size") int size) {
        if (size < 1) {
            throw new CodeException(10, "size > 0 ");
        }
        return articleContentService.getPageList(page, size, ArticleState.open);
    }

    @GetMapping("/{id}")
//    @IgnoreResponseAdvice
    public ArticleContentCustom GetArticleById(
            @PathVariable("id") String id) {
        if (id == null) {
            throw new CodeException(404, "页面没有找到");
        }
        return articleContentService.getArticleById(Long.parseLong(id), ArticleState.open);
    }
}
