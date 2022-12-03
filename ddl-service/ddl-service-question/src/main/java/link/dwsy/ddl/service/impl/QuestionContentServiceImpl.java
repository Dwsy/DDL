package link.dwsy.ddl.service.impl;

import link.dwsy.ddl.XO.VO.VersionData;
import link.dwsy.ddl.constants.mq.QuestionSearchMQConstants;
import link.dwsy.ddl.constants.question.QuestionRedisKey;
import link.dwsy.ddl.core.CustomExceptions.CodeException;
import link.dwsy.ddl.core.constant.CustomerErrorCode;
import link.dwsy.ddl.repository.QA.QaContentRepository;
import link.dwsy.ddl.repository.QA.QaQuestionFieldRepository;
import link.dwsy.ddl.service.QuestionContentService;
import link.dwsy.ddl.support.UserSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author Dwsy
 * @Date 2022/8/25
 */
@Service
@Slf4j
public class QuestionContentServiceImpl implements QuestionContentService {

    @Resource
    private QaQuestionFieldRepository qaQuestionFieldRepository;

    @Resource
    private QaContentRepository qaContentRepository;

    @Resource(name = "stringRedisTemplate")
    private StringRedisTemplate redisTemplate;

    @Resource
    private UserSupport userSupport;

    @Resource
    private RabbitTemplate rabbitTemplate;


    public String getContent(long id, int type) {
        if (type == 0) {
            return qaContentRepository.getHtmlTextById(id);
        }
        if (type == 1) {
            return qaContentRepository.getPureTextById(id);
        }
        if (type == 2) {
            return qaContentRepository.getMdTextById(id);
        }
        return null;
    }

    public String getContentByVersion(Long id, int version) {
        String contentStr = redisTemplate.opsForList().index(QuestionRedisKey.QuestionHistoryVersionContentKey + id, version);
        if (contentStr == null) {
            throw new CodeException(CustomerErrorCode.QuestionVersionNotFound);
        }
        return contentStr;
    }

    public Map<String, VersionData> getHistoryVersionTitle(long questionId) {
        Long userId = userSupport.getCurrentUser().getId();
        Long questionOwnerUserId = qaQuestionFieldRepository.getUserIdByQuestionFieldId(questionId);
        if (!userId.equals(questionOwnerUserId)) {
            throw new CodeException(CustomerErrorCode.QuestionNotFound);
        }
        List<String> titleList = redisTemplate.opsForList().range(QuestionRedisKey.QuestionHistoryVersionTitleKey + questionId, 0, -1);
        List<String> dateList = redisTemplate.opsForList().range(QuestionRedisKey.QuestionHistoryVersionCreateDateKey + questionId, 0, -1);
        if (titleList == null || dateList == null) {
            throw new CodeException(CustomerErrorCode.QuestionNotFound);
        }
        HashMap<String, VersionData> versionMap = new HashMap<>();
        if (titleList.size() == dateList.size()) {
            for (int i = 0; i < titleList.size(); i++) {
                versionMap.put(String.valueOf(i), new VersionData(titleList.get(i), dateList.get(i)));
            }
        } else {
            log.info("titleList.size() != dateList.size(),userId:{}question:{}", userId, questionId);
            throw new CodeException(CustomerErrorCode.QuestionVersionNotFound);
        }
        return versionMap;
    }

    public boolean logicallyDeleteQuestionById(long id) {
        Long userId = userSupport.getCurrentUser().getId();
        Long questionOwnerUserId = qaQuestionFieldRepository.getUserIdByQuestionFieldId(id);
        if (!userId.equals(questionOwnerUserId)) {
            throw new CodeException(CustomerErrorCode.QuestionNotFound);
        }
        qaQuestionFieldRepository.updateDeleted(id, true);
        qaContentRepository.updateDeleted(id, true);
        rabbitTemplate.convertAndSend(QuestionSearchMQConstants.EXCHANGE_DDL_QUESTION_SEARCH, QuestionSearchMQConstants.RK_DDL_QUESTION_SEARCH_DELETE,id);
        return true;
    }
    public boolean logicallyRecoveryQuestionById(long id) {
        Long userId = userSupport.getCurrentUser().getId();
        Long questionOwnerUserId = qaQuestionFieldRepository.getUserIdByQuestionFieldId(id);
        if (!userId.equals(questionOwnerUserId)) {
            throw new CodeException(CustomerErrorCode.QuestionNotFound);
        }
        qaQuestionFieldRepository.updateDeleted(id, false);
        qaContentRepository.updateDeleted(id, false);
        rabbitTemplate.convertAndSend(QuestionSearchMQConstants.EXCHANGE_DDL_QUESTION_SEARCH, QuestionSearchMQConstants.QUEUE_DDL_QUESTION_SEARCH_CREATE,id);
        return true;
    }
}
