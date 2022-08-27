package link.dwsy.ddl.controller;

import link.dwsy.ddl.core.CustomExceptions.CodeException;
import link.dwsy.ddl.core.constant.CustomerErrorCode;
import link.dwsy.ddl.entity.ArticleComment;
import link.dwsy.ddl.service.impl.ArticleCommentServiceImpl;
import link.dwsy.ddl.util.PageData;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.*;

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
    public PageData<ArticleComment> GetCommentById(
            @RequestParam(required = false, defaultValue = "1", name = "page") int page,
            @RequestParam(required = false, defaultValue = "8", name = "size") int size,
            @PathVariable("id") Long aid) {
        if (aid < 1 || size < 1)
            throw new CodeException(CustomerErrorCode.ParamError);

        return articleCommentService.getByArticleId(aid, page < 1 ? 1 : page, size > 20 ? 20 : size);
    }
}
