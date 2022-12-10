package link.dwsy.ddl.XO.VO;

import link.dwsy.ddl.XO.Enum.QA.QuestionState;
import link.dwsy.ddl.entity.QA.QaGroup;
import link.dwsy.ddl.entity.QA.QaQuestionField;
import link.dwsy.ddl.entity.QA.QaTag;
import link.dwsy.ddl.entity.User.User;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @Author Dwsy
 * @Date 2022/12/10
 */
@Data
public class QuestionFieldRankVO {

    private long id;
    private Date createTime;
    private  User user;
    private String title;
    private QuestionState questionState = QuestionState.ASK;

    int answerNum = 0;

    int viewNum = 0;

    int collectNum = 0;

    int upNum = 0;

    int downNum = 0;

    private String summary;


    private List<QaTag> questionTags;

    private QaGroup qaGroup;

    private int scoreCount;




    public QuestionFieldRankVO(QaQuestionField questionField, int scoreCount) {
        this.id = questionField.getId();
        this.createTime = questionField.getCreateTime();
        this.upNum = questionField.getUpNum();
        this.downNum = questionField.getDownNum();
        this.user = questionField.getUser();
        this.title = questionField.getTitle();
        this.summary = questionField.getSummary();
        this.answerNum = questionField.getAnswerNum();
        this.viewNum = questionField.getViewNum();
        this.collectNum = questionField.getCollectNum();
        this.questionState = questionField.getQuestionState();
        this.questionTags = questionField.getQuestionTags();
        this.qaGroup = questionField.getQaGroup();
        this.scoreCount = scoreCount;
    }
}
