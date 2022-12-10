package link.dwsy.ddl.service;


import link.dwsy.ddl.XO.VO.VersionData;

import java.util.Map;

public interface QuestionContentService {
    String getContent(long id, int type);

    String getContentByVersion(Long id, int version);

    Map<String, VersionData> getHistoryVersionTitle(long questionId);

    boolean logicallyDeleteQuestionById(long id);

    boolean logicallyRecoveryQuestionById(long id);
}
