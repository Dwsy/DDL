package link.dwsy.ddl.controller;

import link.dwsy.ddl.XO.CustomER.entity.ArticleCommentCustom;
import link.dwsy.ddl.service.impl.ArticleCommentServiceImpl;
import link.dwsy.ddl.util.PageData;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Author Dwsy
 * @Date 2022/8/26
 */
@RestController
@RequestMapping("comment")
public class ArticleCommentController {
    @Resource
    private ArticleCommentServiceImpl articleCommentService;

    @GetMapping("/{id}")
    public PageData<ArticleCommentCustom> GetCommentById(
            @RequestParam(required = false, defaultValue = "1" , name = "page") int page,
            @RequestParam(required = false, defaultValue = "8", name = "size") int size,
            @PathVariable("id") String aid) {
        return articleCommentService.getByArticleId(Long.parseLong(aid),page,size);
    }
}
