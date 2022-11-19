package link.dwsy.ddl.service.impl;

import link.dwsy.ddl.XO.VO.VersionData;
import link.dwsy.ddl.constants.question.QuestionRedisKey;
import link.dwsy.ddl.core.CustomExceptions.CodeException;
import link.dwsy.ddl.core.constant.CustomerErrorCode;
import link.dwsy.ddl.repository.QA.QaContentRepository;
import link.dwsy.ddl.repository.QA.QaQuestionFieldRepository;
import link.dwsy.ddl.service.QuestionContentService;
import link.dwsy.ddl.support.UserSupport;
import lombok.extern.slf4j.Slf4j;
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
    private  QaQuestionFieldRepository qaQuestionFieldRepository;

    @Resource
    private  QaContentRepository qaContentRepository;

    @Resource(name = "stringRedisTemplate")
    private StringRedisTemplate redisTemplate;

    @Resource
    private UserSupport userSupport;



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

    public String getContentAndVersion(Long id, int version) {
        String contentStr = redisTemplate.opsForList().index(QuestionRedisKey.QuestionHistoryVersionContentKey + id, version);
        if (contentStr == null) {
            throw new CodeException(CustomerErrorCode.QuestionVersionNotFound);
        }
        return contentStr;
    }

    public Map<String, VersionData> getHistoryVersionTitle(long question) {
        Long userId = userSupport.getCurrentUser().getId();
        Long questionOwnerUserId = qaQuestionFieldRepository.getUserIdByQuestionContentId(question);
        if (!userId.equals(questionOwnerUserId)) {
            throw new CodeException(CustomerErrorCode.QuestionNotFound);
        }
        List<String> titleList = redisTemplate.opsForList().range(QuestionRedisKey.QuestionHistoryVersionTitleKey + question, 0, -1);
        List<String> dateList = redisTemplate.opsForList().range(QuestionRedisKey.QuestionHistoryVersionCreateDateKey + question, 0, -1);
        if (titleList == null || dateList == null) {
            throw new CodeException(CustomerErrorCode.QuestionNotFound);
        }
        HashMap<String, VersionData> versionMap = new HashMap<>();
        if (titleList.size() == dateList.size()) {
            for (int i = 0; i < titleList.size(); i++) {
                versionMap.put(String.valueOf(i), new VersionData(titleList.get(i), dateList.get(i)));
            }
        } else {
            log.info("titleList.size() != dateList.size(),userId:{}question:{}", userId, question);
            throw new CodeException(CustomerErrorCode.QuestionVersionNotFound);
        }
        return versionMap;
    }
}
