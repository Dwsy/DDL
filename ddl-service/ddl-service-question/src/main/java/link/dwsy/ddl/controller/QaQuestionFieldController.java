package link.dwsy.ddl.controller;

import link.dwsy.ddl.XO.Enum.QA.QuestionState;
import link.dwsy.ddl.XO.Enum.User.UserActiveType;
import link.dwsy.ddl.core.CustomExceptions.CodeException;
import link.dwsy.ddl.core.constant.CustomerErrorCode;
import link.dwsy.ddl.entity.QA.QaQuestionField;
import link.dwsy.ddl.service.Impl.UserActiveServiceImpl;
import link.dwsy.ddl.service.impl.QaQuestionFieldServiceImpl;
import link.dwsy.ddl.service.impl.QuestionContentServiceImpl;
import link.dwsy.ddl.util.PRHelper;
import link.dwsy.ddl.util.PageData;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author Dwsy
 * @Date 2022/8/25
 */

@RestController
@RequestMapping("question")
public class QaQuestionFieldController {



    @Resource
    QaQuestionFieldServiceImpl qaQuestionFieldService;

    @Resource
    QuestionContentServiceImpl questionContentService;

    @Resource
    UserActiveServiceImpl userActiveService;


    @GetMapping("field/list")
    public PageData<QaQuestionField> QuestionList(
            @RequestParam(required = false, defaultValue = "1", name = "page") int page,
            @RequestParam(required = false, defaultValue = "8", name = "size") int size,
            @RequestParam(required = false, defaultValue = "ASC", name = "order") String order,
            @RequestParam(required = false, defaultValue = "createTime", name = "properties") String[] properties,
            @RequestParam(required = false, defaultValue = "ask", name = "status") Set<String> statusStr
            ) {
        if (size < 1)
            throw new CodeException(CustomerErrorCode.ParamError);

        Set<QuestionState> questionStates = new HashSet<>();

        QaQuestionFieldController qaQuestionFieldController = new QaQuestionFieldController();

        PageRequest pageRequest = PRHelper.order(order, properties, page, size);

        statusStr.forEach(status -> questionStates.add(QuestionState.valueOf(status.toUpperCase())));
        return qaQuestionFieldService.getPageList(questionStates,pageRequest);
    }

    @GetMapping("field/{id}")
    public QaQuestionField GetQuestionById(@PathVariable("id") Long id) {
        if (id < 1L)
            throw new CodeException(CustomerErrorCode.ParamError);
        userActiveService.ActiveLog(UserActiveType.Browse_QA, id);
        return qaQuestionFieldService.getQuestionById(id);
    }

    /**
     * @param id   id
     * @param type 0 html 1 md 2 pure
     * @return String
     */
    @GetMapping(value = "content/{id}",produces="application/json")
//    @IgnoreResponseAdvice
    public String GetArticleContent(
            @Size
            @PathVariable(name = "id") Long id,
            @RequestParam(required = false, defaultValue = "0", name = "type") int type) {
        if (id < 0L)
            throw new CodeException(CustomerErrorCode.ParamError);

        if (type < 0 || type > 2)
            throw new CodeException(CustomerErrorCode.ParamError);

        return questionContentService.getContent(id, type);
    }


}
