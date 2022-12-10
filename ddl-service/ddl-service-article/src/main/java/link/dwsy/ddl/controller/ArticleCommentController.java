package link.dwsy.ddl.controller;

import link.dwsy.ddl.XO.Enum.Article.CommentType;
import link.dwsy.ddl.XO.Enum.User.UserActiveType;
import link.dwsy.ddl.XO.RB.ArticleCommentActionRB;
import link.dwsy.ddl.XO.RB.ArticleCommentRB;
import link.dwsy.ddl.annotation.AuthAnnotation;
import link.dwsy.ddl.core.CustomExceptions.CodeException;
import link.dwsy.ddl.core.constant.CustomerErrorCode;
import link.dwsy.ddl.entity.Article.ArticleComment;
import link.dwsy.ddl.service.impl.ArticleCommentServiceImpl;
import link.dwsy.ddl.service.impl.ArticleFieldServiceImpl;
import link.dwsy.ddl.util.PRHelper;
import link.dwsy.ddl.util.PageData;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
    private  ArticleFieldServiceImpl articleFieldService;
    @Resource
    private ArticleCommentServiceImpl articleCommentService;

    @GetMapping("/{id}")
    public PageData<ArticleComment> getUCommentListById(
            @RequestParam(required = false, defaultValue = "1", name = "page") int page,
            @RequestParam(required = false, defaultValue = "8", name = "size") int size,
            @RequestParam(required = false, defaultValue = "ASC", name = "order") String order,
            @RequestParam(required = false, defaultValue = "createTime", name = "properties") String[] properties,
            @PathVariable("id") Long aid) {
        if (aid < 1 || size < 1)
            throw new CodeException(CustomerErrorCode.ParamError);
        articleFieldService.ActiveLog(UserActiveType.Browse_Article, aid);
        PageRequest pageRequest = PRHelper.order(order, properties, page, size);
        return articleCommentService.getByArticleId(aid, pageRequest);
    }

    @GetMapping("/child/{aid}-{pid}")
    public PageData<ArticleComment> getChildCommentsById(
            @RequestParam(required = false, defaultValue = "1", name = "page") int page,
            @RequestParam(required = false, defaultValue = "8", name = "size") int size,
//            @RequestParam(required = false, defaultValue = "ASC", name = "order") String order,
//            @RequestParam(required = false, defaultValue = "createTime", name = "properties") String[] properties,
            @PathVariable("aid") Long aid,
            @PathVariable("pid") Long pid
    ) {
        if (aid < 1 || size < 1)
            throw new CodeException(CustomerErrorCode.ParamError);
//        articleFieldService.ActiveLog(UserActiveType.Browse_Article, aid);
        PageRequest pageRequest = PRHelper.order(Sort.Direction.ASC, "createTime", page, size);
        return articleCommentService.getChildCommentsByParentId(aid, pid, pageRequest);
    }

    @PostMapping()
    public String reply(@RequestBody ArticleCommentRB articleCommentRB) {
        return String.valueOf(articleCommentService.reply(articleCommentRB, CommentType.comment));
    }

    @AuthAnnotation(Level = 0)
    @DeleteMapping("/{articleId}/{commentId}")//todo time
//    logically delete a comment
    public boolean delete(@PathVariable long articleId, @PathVariable long commentId) {
        return articleCommentService.logicallyDelete(articleId,commentId);
    }

    @AuthAnnotation(Level = 999)
    @PostMapping("/{id}")
    public boolean recovery(@PathVariable("id") Long id) {
        return articleCommentService.logicallyRecovery(id);
    }

    @PostMapping("/action")
    @AuthAnnotation
    public int action(@Validated @RequestBody ArticleCommentActionRB commentActionRB) {
        CommentType action = articleCommentService.action(commentActionRB);
        return action.ordinal();
    }
}
