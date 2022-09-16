package link.dwsy.ddl.controller;

import link.dwsy.ddl.XO.Enum.QA.QuestionState;
import link.dwsy.ddl.core.CustomExceptions.CodeException;
import link.dwsy.ddl.core.constant.CustomerErrorCode;
import link.dwsy.ddl.entity.QA.QaGroup;
import link.dwsy.ddl.entity.QA.QaQuestionField;
import link.dwsy.ddl.service.impl.QaQuestionGroupServiceImpl;
import link.dwsy.ddl.util.PRHelper;
import link.dwsy.ddl.util.PageData;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Author Dwsy
 * @Date 2022/8/25
 */

@RestController
@RequestMapping("group")
public class QaQuestionGroupController {

    @Resource
    QaQuestionGroupServiceImpl qaQuestionGroupService;

    @GetMapping("list")
    public List<QaGroup> GetGroupList(
            @RequestParam(required = false, defaultValue = "ASC", name = "order") String order,
            @RequestParam(required = false, defaultValue = "createTime", name = "properties") String[] properties) {
        return qaQuestionGroupService.getGroupList(PRHelper.sort(order, properties));
    }

    @GetMapping("question/{id}")
    public PageData<QaQuestionField> GetFieldListByGroupId(
            @PathVariable(name = "id") Long id,
            @RequestParam(required = false, defaultValue = "1", name = "page") int page,
            @RequestParam(required = false, defaultValue = "8", name = "size") int size,
            @RequestParam(required = false, defaultValue = "ASC", name = "order") String order,
            @RequestParam(required = false, defaultValue = "createTime",name = "properties") String[] properties,
            @RequestParam(required = false, defaultValue = "ask", name = "status") Set<String> statusStr)
    {
        if (id < 1L || size < 1)
            throw new CodeException(CustomerErrorCode.ParamError);
        Set<QuestionState> questionStates = new HashSet<>();
        statusStr.forEach(status -> questionStates.add(QuestionState.valueOf(status.toUpperCase())));
        PageRequest pageRequest = PRHelper.order(order, properties, page, size);
        return qaQuestionGroupService.getFieldListByGroupId(id,questionStates,pageRequest);
    }
}
