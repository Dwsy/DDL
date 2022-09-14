package link.dwsy.ddl.controller;

import link.dwsy.ddl.XO.Enum.Article.CommentType;
import link.dwsy.ddl.XO.RB.ArticleCommentActionRB;
import link.dwsy.ddl.XO.RB.ArticleCommentRB;
import link.dwsy.ddl.annotation.AuthAnnotation;
import link.dwsy.ddl.core.CustomExceptions.CodeException;
import link.dwsy.ddl.core.constant.CustomerErrorCode;
import link.dwsy.ddl.entity.Article.ArticleComment;
import link.dwsy.ddl.service.impl.ArticleCommentServiceImpl;
import link.dwsy.ddl.util.PRHelper;
import link.dwsy.ddl.util.PageData;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
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
    public PageData<ArticleComment> getUCommentById(
            @RequestParam(required = false, defaultValue = "1", name = "page") int page,
            @RequestParam(required = false, defaultValue = "8", name = "size") int size,
            @RequestParam(required = false, defaultValue = "ASC", name = "order") String order,
            @RequestParam(required = false, defaultValue = "createTime", name = "properties") String[] properties,
            @PathVariable("id") Long aid) {
        if (aid < 1 || size < 1)
            throw new CodeException(CustomerErrorCode.ParamError);
        PageRequest pageRequest = PRHelper.order(order, properties, page, size);
        return articleCommentService.getByArticleId(aid, pageRequest);
    }

    @PostMapping()
    public boolean reply(@RequestBody ArticleCommentRB articleCommentRB) {
        articleCommentService.reply(articleCommentRB, CommentType.comment);
        return true;
    }

    @AuthAnnotation(Level = 999)
    @DeleteMapping("/{id}")//todo time
//    logically delete a comment
    public boolean delete(@PathVariable("id") Long id) {
        return articleCommentService.logicallyDelete(id);
    }

    @AuthAnnotation(Level = 999)
    @PostMapping("/{id}")
    public boolean recovery(@PathVariable("id") Long id) {
        return articleCommentService.logicallyRecovery(id);
    }

    @PostMapping("/action")
    @AuthAnnotation
    public CommentType action(@Validated @RequestBody ArticleCommentActionRB commentActionRB) {
        CommentType action = articleCommentService.action(commentActionRB);
        return action;
    }
}
