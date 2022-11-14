package link.dwsy.ddl.service.impl;

import link.dwsy.ddl.constants.question.QuestionRedisKey;
import link.dwsy.ddl.core.CustomExceptions.CodeException;
import link.dwsy.ddl.core.constant.CustomerErrorCode;
import link.dwsy.ddl.repository.Article.ArticleFieldRepository;
import link.dwsy.ddl.repository.QA.QaContentRepository;
import link.dwsy.ddl.service.QuestionContentService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author Dwsy
 * @Date 2022/8/25
 */
@Service
public class QuestionContentServiceImpl implements QuestionContentService {

    @Resource
    private  ArticleFieldRepository articleFieldRepository;

    @Resource
    private  QaContentRepository qaContentRepository;

    @Resource(name = "stringRedisTemplate")
    private StringRedisTemplate redisTemplate;



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
}
