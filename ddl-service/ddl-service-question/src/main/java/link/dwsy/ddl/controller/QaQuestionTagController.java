package link.dwsy.ddl.controller;

import link.dwsy.ddl.XO.Enum.QA.QuestionState;
import link.dwsy.ddl.core.CustomExceptions.CodeException;
import link.dwsy.ddl.core.constant.CustomerErrorCode;
import link.dwsy.ddl.entity.QA.QaQuestionField;
import link.dwsy.ddl.entity.QA.QaTag;
import link.dwsy.ddl.service.impl.QaQuestionTagServiceImpl;
import link.dwsy.ddl.util.PRHelper;
import link.dwsy.ddl.util.PageData;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author Dwsy
 * @Date 2022/8/25
 */
@RestController
@RequestMapping("tag")
public class QaQuestionTagController {
    @Resource
    QaQuestionTagServiceImpl qaQuestionTagService;

    @GetMapping("list")
    public List<QaTag> GetTagList(
            @RequestParam(required = false, defaultValue = "ASC", name = "order") String order,
            @RequestParam(required = false, defaultValue = "createTime", name = "properties") String[] properties) {

        return qaQuestionTagService.getTagList(PRHelper.sort(order, properties));
    }

    @GetMapping("question/{id}")
    public PageData<QaQuestionField> GetArticleFieldList(
            @PathVariable(name = "id") Long id,
            @RequestParam(required = false, defaultValue = "1", name = "page") int page,
            @RequestParam(required = false, defaultValue = "8", name = "size") int size,
            @RequestParam(required = false, defaultValue = "ASC", name = "order") String order,
            @RequestParam(required = false, defaultValue = "createTime", name = "properties") String[] properties,
            @RequestParam(required = false, defaultValue = "ask", name = "status") Set<String> statusStr) {
        if (id < 1L || size < 1)
            throw new CodeException(CustomerErrorCode.ParamError);
        Set<QuestionState> questionStates = statusStr.stream().map(String::toUpperCase).map(QuestionState::valueOf).collect(Collectors.toSet());
        PageRequest pageRequest = PRHelper.order(order, properties, page, size);

        return qaQuestionTagService.getQuestionListById(id,questionStates,pageRequest);
    }
}


