package link.dwsy.ddl;

import link.dwsy.ddl.entity.ArticleComment;
import link.dwsy.ddl.entity.ArticleContent;
import link.dwsy.ddl.entity.ArticleField;
import link.dwsy.ddl.entity.User;
import link.dwsy.ddl.repository.ArticleCommentRepository;
import link.dwsy.ddl.repository.ArticleContentRepository;
import link.dwsy.ddl.repository.ArticleFieldRepository;
import link.dwsy.ddl.repository.UserRepository;
import link.dwsy.ddl.XO.Enum.CommentType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * @Author Dwsy
 * @Date 2022/8/24
 */

@SpringBootTest
public class CommentTest {


    @Resource
    ArticleCommentRepository articleCommentRepository;
    @Resource
    ArticleContentRepository articleContentRepository;
    @Resource
    UserRepository userRepository;

    @Resource
    ArticleFieldRepository articleFieldRepository;
    @Test
    public void reply() {
//        这样莫名其妙可以减少select
        Long aid = 73L;
        User user = new User();
        user.setId(3L);
        ArticleField a = new ArticleField();
        a.setId(9L);
//        articleFieldRepository.findAllByDeletedIsFalseAndArticleState()
        ArticleComment comment = ArticleComment.builder()
                .user(user)
                .commentType(CommentType.comment)
                .text("评论测试").articleField(a).commentType(CommentType.comment).ua("user-agent").build();
        articleCommentRepository.save(comment);
    }

    @Test
    public void replyComment() {
        User user = new User();
        user.setId(3L);
        ArticleField a = new ArticleField();
        a.setId(9L);
        ArticleComment comment = ArticleComment.builder()
                .user(user)
                .commentType(CommentType.comment).parentCommentId(2L)
                .text("评论测试").articleField(a).commentType(CommentType.comment).ua("user-agent").build();
        articleCommentRepository.save(comment);
    }

    @Test
    public void ttt() {
        replyComment();
        replyComment();
        replyComment();
        replyComment();

    }
}
