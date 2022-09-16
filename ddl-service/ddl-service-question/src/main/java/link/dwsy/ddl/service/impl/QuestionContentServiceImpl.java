package link.dwsy.ddl.service.impl;

import link.dwsy.ddl.repository.Article.ArticleFieldRepository;
import link.dwsy.ddl.repository.QA.QaContentRepository;
import link.dwsy.ddl.service.QuestionContentService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author Dwsy
 * @Date 2022/8/25
 */
@Service
public class QuestionContentServiceImpl implements QuestionContentService {

    @Resource
    ArticleFieldRepository articleFieldRepository;

    @Resource
    QaContentRepository qaContentRepository;



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
}
