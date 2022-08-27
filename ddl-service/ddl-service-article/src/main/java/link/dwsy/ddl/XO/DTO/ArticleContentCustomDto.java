package link.dwsy.ddl.XO.DTO;

import link.dwsy.ddl.XO.Enum.ArticleState;
import link.dwsy.ddl.XO.Enum.CommentType;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * @Author Dwsy
 * @Date 2022/8/26
 */
@Data
public class ArticleContentCustomDto implements Serializable {
    private final long id;
    private final boolean deleted;
    private final Date createTime;
    private final Date lastModifiedTime;
    private final UserCustomDto user;
    private final String title;
    private final String textHtml;
    private final String summary;
    private final ArticleState articleState;
    private final boolean allowComment;
    private final long viewNum;
    private final long collectNum;
    private final String banner;
    private final Set<ArticleTagCustomDto> articleTags;
    private final long articleGroupId;
    private final boolean articleGroupDeleted;
    private final Date articleGroupCreateTime;
    private final Date articleGroupLastModifiedTime;
    private final String articleGroupName;
    private final String articleGroupInfo;
    private final Set<ArticleCommentCustomDto> articleComments;

    @Data
    public static class UserCustomDto implements Serializable {
        private final long id;
        private final boolean deleted;
        private final Date createTime;
        private final Date lastModifiedTime;
        private final String username;
        private final int level;
    }

    @Data
    public static class ArticleTagCustomDto implements Serializable {
        private final long id;
        private final boolean deleted;
        private final Date createTime;
        private final Date lastModifiedTime;
        private final String name;
        private final Long articleNum;
        private final String tagInfo;
    }

    @Data
    public static class ArticleCommentCustomDto implements Serializable {
        private final long id;
        private final boolean deleted;
        private final Date createTime;
        private final Date lastModifiedTime;
        private final String text;
        private final long thumb;
        private final long parentCommentId;
        private final CommentType commentType;
        private final String ua;
    }
}
