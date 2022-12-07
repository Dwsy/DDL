package link.dwsy.ddl.controller;

import link.dwsy.ddl.XO.Enum.QA.QuestionState;
import link.dwsy.ddl.XO.VO.VersionData;
import link.dwsy.ddl.annotation.AuthAnnotation;
import link.dwsy.ddl.core.CustomExceptions.CodeException;
import link.dwsy.ddl.core.constant.CustomerErrorCode;
import link.dwsy.ddl.core.domain.LoginUserInfo;
import link.dwsy.ddl.entity.QA.QaQuestionField;
import link.dwsy.ddl.repository.QA.QaQuestionFieldRepository;
import link.dwsy.ddl.service.Impl.UserActiveCommonServiceImpl;
import link.dwsy.ddl.service.impl.QaQuestionFieldServiceImpl;
import link.dwsy.ddl.service.impl.QuestionContentServiceImpl;
import link.dwsy.ddl.support.UserSupport;
import link.dwsy.ddl.util.PRHelper;
import link.dwsy.ddl.util.PageData;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @Author Dwsy
 * @Date 2022/8/25
 */

@RestController
@RequestMapping("question/manage")
public class QaQuestionFieldManageController {


    @Resource
    private QaQuestionFieldServiceImpl qaQuestionFieldService;

    @Resource
    private QuestionContentServiceImpl questionContentService;

    @Resource
    private UserActiveCommonServiceImpl userActiveCommonService;

    @Resource
    private QaQuestionFieldRepository qaQuestionFieldRepository;

    @Resource
    private UserSupport userSupport;

    @GetMapping("field/list")
    @AuthAnnotation
    public PageData<QaQuestionField> QuestionList(
            @RequestParam(required = false, defaultValue = "1", name = "page") int page,
            @RequestParam(required = false, defaultValue = "8", name = "size") int size,
            @RequestParam(required = false, defaultValue = "ASC", name = "order") String order,
            @RequestParam(required = false, defaultValue = "createTime", name = "properties") String[] properties,
            @RequestParam(required = false, defaultValue = "ask", name = "state") String state
    ) {
        if (size < 1)
            throw new CodeException(CustomerErrorCode.ParamError);


        PageRequest pageRequest = PRHelper.order(order, properties, page, size);

        Long userId = userSupport.getCurrentUser().getId();
        if (state.equals("all")) {
            return new PageData<>(qaQuestionFieldRepository
                    .findByDeletedFalseAndUserIdAndQuestionStateNot(userId, QuestionState.DRAFT, pageRequest));
        }
        QuestionState questionState = QuestionState.valueOf(state.toUpperCase());

        return qaQuestionFieldService.getPageListManage(userId, questionState, pageRequest);

    }


    @GetMapping("field/num")
    @AuthAnnotation
    public Map<QuestionState, Integer> getQuestionCountByState() {
        LoginUserInfo user = userSupport.getCurrentUser();
        HashMap<QuestionState, Integer> countByState = new HashMap<>();
        countByState.put(QuestionState.ASK, qaQuestionFieldRepository.countByDeletedFalseAndUser_IdAndQuestionState(user.getId(), QuestionState.ASK));
        countByState.put(QuestionState.DRAFT, qaQuestionFieldRepository.countByDeletedFalseAndUser_IdAndQuestionState(user.getId(), QuestionState.DRAFT));
        countByState.put(QuestionState.HAVE_ANSWER, qaQuestionFieldRepository.countByDeletedFalseAndUser_IdAndQuestionState(user.getId(), QuestionState.HAVE_ANSWER));
        countByState.put(QuestionState.RESOLVED, qaQuestionFieldRepository.countByDeletedFalseAndUser_IdAndQuestionState(user.getId(), QuestionState.RESOLVED));
        countByState.put(QuestionState.UNRESOLVED, qaQuestionFieldRepository.countByDeletedFalseAndUser_IdAndQuestionState(user.getId(), QuestionState.UNRESOLVED));
        countByState.put(QuestionState.HIDE, qaQuestionFieldRepository.countByDeletedFalseAndUser_IdAndQuestionState(user.getId(), QuestionState.HIDE));
        countByState.put(QuestionState.AUDITING, qaQuestionFieldRepository.countByDeletedFalseAndUser_IdAndQuestionState(user.getId(), QuestionState.AUDITING));
        countByState.put(QuestionState.REJECTED, qaQuestionFieldRepository.countByDeletedFalseAndUser_IdAndQuestionState(user.getId(), QuestionState.REJECTED));
        return countByState;
    }

    @GetMapping("field/{id}")
    @AuthAnnotation
    public QaQuestionField GetQuestionById(
            @PathVariable("id") Long id,
            @RequestParam(required = false, defaultValue = "false", name = "getQuestionComment") boolean getQuestionComment,
            @RequestParam(required = false, defaultValue = "-1", name = "version") int version
    ) {
        if (id < 1L)
            throw new CodeException(CustomerErrorCode.ParamError);
        Long userId = userSupport.getCurrentUser().getId();
        Long questionOwnerUserId = qaQuestionFieldRepository.getUserIdByQuestionId(id);
        if (!userId.equals(questionOwnerUserId)) {
            throw new CodeException(CustomerErrorCode.QuestionNotFound);
        }
        if (version > -1) {
            return qaQuestionFieldService.getQuestionByIdAndVersion(id, version);
        }
        QaQuestionField question = qaQuestionFieldService.getQuestionById(id, getQuestionComment);
        if (question == null) {
            throw new CodeException(CustomerErrorCode.QuestionNotFound);
        }
        qaQuestionFieldService.view(id);
        return question;
    }

//    getUserAction

    /**
     * @param id   id
     * @param type 0 html 1 md 2 pure
     * @return String
     */
    @GetMapping(value = "content/{id}", produces = "application/json")
    @AuthAnnotation
//    @IgnoreResponseAdvice
    public String GetQuestionContent(
            @PathVariable(name = "id") Long id,
            @RequestParam(required = false, defaultValue = "0", name = "type") int type,
            @RequestParam(required = false, defaultValue = "-1", name = "version") int version) {
        if (id < 0L)
            throw new CodeException(CustomerErrorCode.ParamError);
        if (type < 0 || type > 2)
            throw new CodeException(CustomerErrorCode.ParamError);
        Long userId = userSupport.getCurrentUser().getId();
        Long questionOwnerUserId = qaQuestionFieldRepository.getUserIdByQuestionFieldId(id);
        if (!userId.equals(questionOwnerUserId)) {
            throw new CodeException(CustomerErrorCode.QuestionNotFound);
        }

        if (version > -1) {
            return questionContentService.getContentByVersion(id, version);
        }
        Optional<String> ret = Optional.ofNullable(questionContentService.getContent(id, type));
        if (ret.isPresent()) {
            return ret.get();
        } else {
            throw new CodeException(CustomerErrorCode.ArticleNotFound);
        }

    }

    @GetMapping("historyVersion/{id}")
    @AuthAnnotation
    public Map<String, VersionData> getHistoryVersion(@PathVariable long id) {

        return questionContentService.getHistoryVersionTitle(id);
    }

    @DeleteMapping("{id}")
    @AuthAnnotation
    public boolean logicallyDeleteQuestionById(@PathVariable long id) {
        return questionContentService.logicallyDeleteQuestionById(id);
    }

    @PostMapping("recovery/{id}")
    @AuthAnnotation
    public boolean logicallyRecoveryQuestionById(@PathVariable long id) {
        return questionContentService.logicallyRecoveryQuestionById(id);
    }
}
