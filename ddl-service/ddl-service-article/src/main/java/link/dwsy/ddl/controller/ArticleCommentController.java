package link.dwsy.ddl.controller;

import link.dwsy.ddl.XO.Enum.Article.CommentType;
import link.dwsy.ddl.XO.Enum.User.PointsType;
import link.dwsy.ddl.XO.Enum.User.UserActiveType;
import link.dwsy.ddl.XO.RB.ArticleCommentActionRB;
import link.dwsy.ddl.XO.RB.ArticleCommentRB;
import link.dwsy.ddl.annotation.AuthAnnotation;
import link.dwsy.ddl.annotation.Points;
import link.dwsy.ddl.core.CustomExceptions.CodeException;
import link.dwsy.ddl.core.constant.CustomerErrorCode;
import link.dwsy.ddl.entity.Article.ArticleComment;
import link.dwsy.ddl.entity.Article.ArticleField;
import link.dwsy.ddl.entity.User.User;
import link.dwsy.ddl.repository.Article.ArticleCommentRepository;
import link.dwsy.ddl.repository.Article.ArticleFieldRepository;
import link.dwsy.ddl.service.Impl.UserActiveCommonServiceImpl;
import link.dwsy.ddl.service.RPC.AuditService;
import link.dwsy.ddl.service.impl.ArticleCommentServiceImpl;
import link.dwsy.ddl.service.impl.ArticleFieldServiceImpl;
import link.dwsy.ddl.support.UserSupport;
import link.dwsy.ddl.util.HtmlHelper;
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

    @Resource
    private UserActiveCommonServiceImpl userActiveCommonService;

    @GetMapping("/{id}")
    @Points(TYPE = PointsType.Browse_Article)
    public PageData<ArticleComment> getUCommentListById(
            @RequestParam(required = false, defaultValue = "1", name = "page") int page,
            @RequestParam(required = false, defaultValue = "8", name = "size") int size,
            @RequestParam(required = false, defaultValue = "ASC", name = "order") String order,
            @RequestParam(required = false, defaultValue = "createTime", name = "properties") String[] properties,
            @PathVariable("id") Long aid) {
        if (aid < 1 || size < 1) {
            throw new CodeException(CustomerErrorCode.ParamError);
        }
//        articleFieldService.ActiveLog(UserActiveType.Browse_Article, aid);
        if (page == 1) {
            userActiveCommonService.ActiveLogUseMQ(UserActiveType.Browse_Article, aid);
        }
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
        if (aid < 1 || size < 1) {
            throw new CodeException(CustomerErrorCode.ParamError);
        }
//        articleFieldService.ActiveLog(UserActiveType.Browse_Article, aid);
        PageRequest pageRequest = PRHelper.order(Sort.Direction.ASC, "createTime", page, size);
        return articleCommentService.getChildCommentsByParentId(aid, pid, pageRequest);
    }

    @Resource
    private ArticleFieldRepository articleFieldRepository;
    @Resource
    private UserSupport userSupport;
    @Resource
    private ArticleCommentRepository articleCommentRepository;
    @Resource
    private AuditService auditService;
    @PostMapping()
    public ArticleComment reply(@RequestBody ArticleCommentRB articleCommentRB) {
        long articleFieldId = articleCommentRB.getArticleFieldId();
        if (articleFieldRepository.userIsCancellation(articleFieldId) > 0) {
            throw new CodeException(CustomerErrorCode.ArticleCommentIsClose);
        }
        if (!articleFieldRepository.existsByDeletedFalseAndAllowCommentTrueAndId(articleFieldId)) {
            throw new CodeException(CustomerErrorCode.ArticleCommentIsClose);
        }
        if (auditService.contains(articleCommentRB.getText())) {
            throw new CodeException(CustomerErrorCode.SensitiveWordsExistInTheContent);
        }
        User user = new User();
        user.setId(userSupport.getCurrentUser().getId());
        articleCommentRB.setText(HtmlHelper.filter(articleCommentRB.getText().trim()));
        ArticleField af = new ArticleField();
        af.setId(articleFieldId);
        int commentSerialNumber = 1;
        //评论文章
        CommentType commentType = CommentType.comment;
        if (articleCommentRB.getParentCommentId() == 0) {

            return articleCommentService. replyArticle(articleCommentRB, commentType, articleFieldId, user, af, commentSerialNumber);
        } else {
            long parentCommentId = articleCommentRB.getParentCommentId();
            if (!articleCommentRepository.isFirstComment(parentCommentId)) {
                throw new CodeException(CustomerErrorCode.ArticleCommentNotIsFirst);
            }
            if (!articleCommentRepository.existsByDeletedFalseAndIdAndArticleFieldIdAndCommentType
                    (parentCommentId, articleFieldId, CommentType.comment)) {
                throw new CodeException(CustomerErrorCode.ArticleCommentNotFount);
            }
            ArticleComment lastComment = articleCommentRepository
                    .findFirstByDeletedFalseAndArticleField_IdAndParentCommentIdAndCommentTypeOrderByCommentSerialNumberDesc
                            (articleFieldId, parentCommentId, commentType);
            if (lastComment != null) {
                commentSerialNumber = lastComment.getCommentSerialNumber() + 1;
            }
            //回复评论

            if (articleCommentRB.getReplyUserCommentId() == 0) {
                return articleCommentService.replyArticleComment(articleCommentRB, commentType, articleFieldId, user, af, commentSerialNumber, parentCommentId);
            } else {
                //回复二级评论
                return articleCommentService.replyArticleSecondComment(articleCommentRB, commentType, articleFieldId, user, af, commentSerialNumber, parentCommentId);
            }
        }
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
