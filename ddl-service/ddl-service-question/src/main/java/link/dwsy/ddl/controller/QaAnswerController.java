package link.dwsy.ddl.controller;

import link.dwsy.ddl.XO.Enum.QA.AnswerType;
import link.dwsy.ddl.XO.RB.QaAnswerRB;
import link.dwsy.ddl.annotation.AuthAnnotation;
import link.dwsy.ddl.core.CustomExceptions.CodeException;
import link.dwsy.ddl.core.constant.CustomerErrorCode;
import link.dwsy.ddl.entity.QA.QaAnswer;
import link.dwsy.ddl.service.impl.QaAnswerServiceServiceImpl;
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
@RequestMapping("answer")
public class QaAnswerController {
    @Resource
    private QaAnswerServiceServiceImpl qaAnswerServiceService;

    @GetMapping("/{id}")
    public PageData<QaAnswer> getAnswerPageById(
            @RequestParam(required = false, defaultValue = "1", name = "page") int page,
            @RequestParam(required = false, defaultValue = "8", name = "size") int size,
            @RequestParam(required = false, defaultValue = "ASC", name = "order") String order,
            @RequestParam(required = false, defaultValue = "createTime", name = "properties") String[] properties,
            @PathVariable("id") Long qid) {
        if (qid < 1 || size < 1)
            throw new CodeException(CustomerErrorCode.ParamError);
//        articleFieldService.ActiveLog(UserActiveType.Browse_Article, aid);
        PageRequest pageRequest = PRHelper.order(order, properties, page, size);
        return qaAnswerServiceService.getByQuestionId(qid, pageRequest);
    }

    @GetMapping("/child/{qid}-{pid}")
    public PageData<QaAnswer> getChildAnswerPageById(
            @RequestParam(required = false, defaultValue = "1", name = "page") int page,
            @RequestParam(required = false, defaultValue = "8", name = "size") int size,
//            @RequestParam(required = false, defaultValue = "ASC", name = "order") String order,
//            @RequestParam(required = false, defaultValue = "createTime", name = "properties") String[] properties,
            @PathVariable("qid") Long qid,
            @PathVariable("pid") Long pid
    ) {
        if (qid < 1 || size < 1)
            throw new CodeException(CustomerErrorCode.ParamError);
//        articleFieldService.ActiveLog(UserActiveType.Browse_Article, aid);
        PageRequest pageRequest = PRHelper.order(Sort.Direction.ASC, "createTime", page, size);
        return qaAnswerServiceService.getChildAnswerPageByParentId(qid, pid, pageRequest);
    }

    @PostMapping()
    public String answer(@Validated @RequestBody QaAnswerRB qaAnswerRB) {
        return String.valueOf(qaAnswerServiceService.answer(qaAnswerRB, qaAnswerRB.getAnswerType()));
    }

    @AuthAnnotation(Level = 0)
    @DeleteMapping("/{questionId}/{answerId}")//todo time
//    logically delete a comment
    public boolean delete(@PathVariable long questionId, @PathVariable long answerId) {
        return qaAnswerServiceService.logicallyDelete(questionId, answerId);
    }

    @PostMapping("/action")
    @AuthAnnotation
    public int action(@Validated @RequestBody QuestionAnswerOrCommentActionRB actionRB) {
        AnswerType action = qaAnswerServiceService.action(actionRB);
        return action.ordinal();
    }
}
