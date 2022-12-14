package link.dwsy.ddl.entity.Infinity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import link.dwsy.ddl.XO.Enum.InfinityType;
import link.dwsy.ddl.entity.BaseEntity;
import link.dwsy.ddl.entity.User.User;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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

    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private InfinityType type;

    private long upNum;

    private long collectNum;

    @Builder.Default
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long refId = null;

    private String ua;

    @ManyToMany(
            fetch = FetchType.EAGER, cascade = {CascadeType.MERGE})
    @JoinTable(name = "infinity_topic__ref",
            joinColumns = {@JoinColumn(name = "infinity_id")},
            inverseJoinColumns = {@JoinColumn(name = "topic_id")})
    private List<InfinityTopic> infinityTopics;

    @OneToOne
    private InfinityClub infinityClub;

    @Builder.Default //null 为动态
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long parentTweetId = null;

    @Builder.Default
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long parentUserId = null;//二级评论父评论的用户ID

    @Builder.Default
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long replyUserTweetId = null;//回复二级评论的Id

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Transient
    private String replyUserName;

    private long viewNum;

    private int replySerialNumber;

    @Transient
    private boolean up;


    @Transient
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Collection<Infinity> childComments;

    @Transient
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<Long,List<Infinity>> childCommentReplyMap;

    @Transient
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private long childCommentNum;

    @Transient
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int childCommentTotalPages;

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
        ArrayList<String> urlList = new ArrayList<>();
        if (imgUrl1 != null) {
            urlList.add(imgUrl1);
        }
        if (imgUrl2 != null) {
            urlList.add(imgUrl2);
        }
        if (imgUrl3 != null) {
            urlList.add(imgUrl3);
        }
        if (imgUrl4 != null) {
            urlList.add(imgUrl4);
        }
        if (imgUrl5 != null) {
            urlList.add(imgUrl5);
        }
        if (imgUrl6 != null) {
            urlList.add(imgUrl6);
        }
        if (imgUrl7 != null) {
            urlList.add(imgUrl7);
        }
        if (imgUrl8 != null) {
            urlList.add(imgUrl8);
        }
        if (imgUrl9 != null) {
            urlList.add(imgUrl9);
        }
        this.imgUrlList = urlList;
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
    }

    public void noRetCreateUser() {
        if (this.infinityTopics != null) {
            for (InfinityTopic infinityTopic : this.infinityTopics) {
                infinityTopic.setCreateUser(null);
            }
        }
        if (this.infinityClub != null) {
            this.infinityClub.setCreateUser(null);
        }
    }

}
