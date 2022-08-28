package link.dwsy.ddl.service.impl;

import link.dwsy.ddl.entity.Article.ArticleComment;
import link.dwsy.ddl.entity.QA.QaAnswer;
import link.dwsy.ddl.repository.Article.ArticleCommentRepository;
import link.dwsy.ddl.repository.QA.QaAnswerRepository;
import link.dwsy.ddl.service.QaAnswerService;
import link.dwsy.ddl.util.PageData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author Dwsy
 * @Date 2022/8/26
 */

@Service
public class QaAnswerServiceServiceImpl implements QaAnswerService {
    @Resource
    private ArticleCommentRepository articleCommentRepository;

    @Resource
    private QaAnswerRepository qaAnswerRepository;

    public PageData<QaAnswer> getByQuestionId(long qid, int page, int size) {
//        , Sort.by(Sort.Direction.ASC,"id")
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<QaAnswer> QaAnswerData = qaAnswerRepository
                .findAllByDeletedIsFalseAndQuestionFieldIdAndParentAnswerId(qid, 1L, pageRequest);
        for (QaAnswer qaAnswer : QaAnswerData) {
            long pid = qaAnswer.getId();
            qaAnswer.setChildQaAnswers(qaAnswerRepository
                    .findAllByDeletedIsFalseAndQuestionFieldIdAndParentAnswerId(qid, pid));
        }
        return new PageData<>(QaAnswerData);
    }
}

