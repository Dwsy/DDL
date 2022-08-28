package link.dwsy.ddl.repository;

import link.dwsy.ddl.entity.Article.ArticleComment;
import link.dwsy.ddl.entity.Article.ArticleField;
import link.dwsy.ddl.entity.User.User;
import link.dwsy.ddl.repository.Article.ArticleCommentRepository;
import link.dwsy.ddl.repository.Article.ArticleContentRepository;
import link.dwsy.ddl.repository.Article.ArticleFieldRepository;
import link.dwsy.ddl.repository.User.UserRepository;
import link.dwsy.ddl.XO.Enum.Article.CommentType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

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
//逻辑判断参照qa修改
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
