package link.dwsy.ddl.entity.Infinity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import link.dwsy.ddl.XO.Enum.InfinityType;
import link.dwsy.ddl.entity.Article.ArticleComment;
import link.dwsy.ddl.entity.BaseEntity;
import link.dwsy.ddl.entity.User.User;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;

/**
 * @Author Dwsy
 * @Date 2022/11/23
 */
@Entity
@Table(name = "infinity")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(value = {"deleted", "handler", "hibernateLazyInitializer", "lastModifiedTime"})
public class Infinity extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String content;

    private InfinityType type;

    private long upNum;

    private long collectNum;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private long refId;

    @ManyToOne
    private InfinityTopic infinityTopic;

    @ManyToOne
    private InfinityClub infinityClub;

    @Builder.Default
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JsonInclude(JsonInclude.Include.USE_DEFAULTS)
    private Long parentUserId = 0L;//二级评论父评论的用户ID

    @Builder.Default // 0 为一级评论 -1 up or down other reply null Tweet
    @JsonInclude(JsonInclude.Include.USE_DEFAULTS)
    private Long parentCommentId = null;

    @Builder.Default
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JsonInclude(JsonInclude.Include.USE_DEFAULTS)
    private Long replyUserCommentId = 0L;//回复二级评论的Id


    private int replySerialNumber;

    @Transient
    private User parentUser;

    @Transient
    private Collection<ArticleComment> childComments;

    @Transient
    private long childCommentNum;

    @Transient
    private long childCommentTotalPages;

    @JsonIgnore
    private String imgUrl1;
    @JsonIgnore
    private String imgUrl2;
    @JsonIgnore
    private String imgUrl3;
    @JsonIgnore
    private String imgUrl4;
    @JsonIgnore
    private String imgUrl5;
    @JsonIgnore
    private String imgUrl6;
    @JsonIgnore
    private String imgUrl7;
    @JsonIgnore
    private String imgUrl8;
    @JsonIgnore
    private String imgUrl9;

    @Transient
    private List<String> imgUrlList;

    public List<String> getImgUrlList() {
        return imgUrlList;
    }

    public void setImgUrlList() {
        this.imgUrlList = List.of(imgUrl1, imgUrl2, imgUrl3, imgUrl4, imgUrl5, imgUrl6, imgUrl7, imgUrl8, imgUrl9);
    }

    public void setImgUrlByList(List<String> imgUrlList) {
        this.imgUrlList = imgUrlList;
        int size = imgUrlList.size();
        switch (size) {
            case 9:
                this.imgUrl9 = imgUrlList.get(8);
            case 8:
                this.imgUrl8 = imgUrlList.get(7);
            case 7:
                this.imgUrl7 = imgUrlList.get(6);
            case 6:
                this.imgUrl6 = imgUrlList.get(5);
            case 5:
                this.imgUrl5 = imgUrlList.get(4);
            case 4:
                this.imgUrl4 = imgUrlList.get(3);
            case 3:
                this.imgUrl3 = imgUrlList.get(2);
            case 2:
                this.imgUrl2 = imgUrlList.get(1);
            case 1:
                this.imgUrl1 = imgUrlList.get(0);
        }
//        if (size == 1) {
//            this.imgUrl1 = imgUrlList.get(0);
//        }
//        if (size == 2) {
//            this.imgUrl1 = imgUrlList.get(0);
//            this.imgUrl2 = imgUrlList.get(1);
//        }
//        if (size == 3) {
//            this.imgUrl1 = imgUrlList.get(0);
//            this.imgUrl2 = imgUrlList.get(1);
//            this.imgUrl3 = imgUrlList.get(2);
//        }
//        if (size == 4) {
//            this.imgUrl1 = imgUrlList.get(0);
//            this.imgUrl2 = imgUrlList.get(1);
//            this.imgUrl3 = imgUrlList.get(2);
//            this.imgUrl4 = imgUrlList.get(3);
//        }
//        if (size == 5) {
//            this.imgUrl1 = imgUrlList.get(0);
//            this.imgUrl2 = imgUrlList.get(1);
//            this.imgUrl3 = imgUrlList.get(2);
//            this.imgUrl4 = imgUrlList.get(3);
//            this.imgUrl5 = imgUrlList.get(4);
//        }
//        if (size == 6) {
//            this.imgUrl1 = imgUrlList.get(0);
//            this.imgUrl2 = imgUrlList.get(1);
//            this.imgUrl3 = imgUrlList.get(2);
//            this.imgUrl4 = imgUrlList.get(3);
//            this.imgUrl5 = imgUrlList.get(4);
//            this.imgUrl6 = imgUrlList.get(5);
//        }
//        if (size == 7) {
//            this.imgUrl1 = imgUrlList.get(0);
//            this.imgUrl2 = imgUrlList.get(1);
//            this.imgUrl3 = imgUrlList.get(2);
//            this.imgUrl4 = imgUrlList.get(3);
//            this.imgUrl5 = imgUrlList.get(4);
//            this.imgUrl6 = imgUrlList.get(5);
//            this.imgUrl7 = imgUrlList.get(6);
//        }
//        if (size == 8) {
//            this.imgUrl1 = imgUrlList.get(0);
//            this.imgUrl2 = imgUrlList.get(1);
//            this.imgUrl3 = imgUrlList.get(2);
//            this.imgUrl4 = imgUrlList.get(3);
//            this.imgUrl5 = imgUrlList.get(4);
//            this.imgUrl6 = imgUrlList.get(5);
//            this.imgUrl7 = imgUrlList.get(6);
//            this.imgUrl8 = imgUrlList.get(7);
//        }
//        if (size == 9) {
//            this.imgUrl1 = imgUrlList.get(0);
//            this.imgUrl2 = imgUrlList.get(1);
//            this.imgUrl3 = imgUrlList.get(2);
//            this.imgUrl4 = imgUrlList.get(3);
//            this.imgUrl5 = imgUrlList.get(4);
//            this.imgUrl6 = imgUrlList.get(5);
//            this.imgUrl7 = imgUrlList.get(6);
//            this.imgUrl8 = imgUrlList.get(7);
//            this.imgUrl9 = imgUrlList.get(8);
//        }
    }
}
