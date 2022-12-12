package link.dwsy.ddl.XO.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import link.dwsy.ddl.XO.Enum.ArticleSource;
import link.dwsy.ddl.entity.Article.ArticleField;
import link.dwsy.ddl.entity.Article.ArticleGroup;
import link.dwsy.ddl.entity.Article.ArticleTag;
import link.dwsy.ddl.entity.User.User;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @Author Dwsy
 * @Date 2022/12/10
 */
@Data
public class ArticleFieldRankVO {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private long id;
    private Date createTime;
    int upNum ;
    int downNum ;

    private User user;
    private String title;
    private String summary;

    private int commentNum ;

    private int viewNum ;

    private int collectNum ;

    private String banner;

    private ArticleSource articleSource;

    private String articleSourceUrl;

    private List<ArticleTag> articleTags;

    private ArticleGroup articleGroup;

    private int scoreCount;


    public ArticleFieldRankVO(ArticleField articleField, int scoreCount) {
        this.id = articleField.getId();
        this.createTime = articleField.getCreateTime();
        this.upNum = articleField.getUpNum();
        this.downNum = articleField.getDownNum();
        this.user = articleField.getUser();
        this.title = articleField.getTitle();
        this.summary = articleField.getSummary();
        this.commentNum = articleField.getCommentNum();
        this.viewNum = articleField.getViewNum();
        this.collectNum = articleField.getCollectNum();
        this.banner = articleField.getBanner();
        this.articleSource = articleField.getArticleSource();
        this.articleSourceUrl = articleField.getArticleSourceUrl();
        this.articleTags = articleField.getArticleTags();
        this.articleGroup = articleField.getArticleGroup();
        this.scoreCount = scoreCount;
    }
}
