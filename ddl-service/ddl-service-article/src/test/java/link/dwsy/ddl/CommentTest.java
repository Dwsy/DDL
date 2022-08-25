package link.dwsy.ddl;

import link.dwsy.ddl.entity.ArticleComment;
import link.dwsy.ddl.entity.ArticleContent;
import link.dwsy.ddl.entity.User;
import link.dwsy.ddl.repository.ArticleCommentRepository;
import link.dwsy.ddl.repository.ArticleContentRepository;
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
    @Test
    public void reply() {
        Long aid = 73L;
        Optional<User> user = userRepository.findById(18L);
        Optional<ArticleContent> content = articleContentRepository.findById(aid);
        ArticleComment comment = ArticleComment.builder()
                .user(user.orElse(null))
                .commentType(CommentType.comment)
                .text("评论测试")
                .articleContent(content.orElse(null)).build();

        articleCommentRepository.save(comment);
    }

    @Test
    public void replyComment() {
        Long aid = 73L;
        Optional<User> user = userRepository.findById(18L);
        Optional<ArticleContent> content = articleContentRepository.findById(aid);
        ArticleComment comment = ArticleComment.builder()
                .user(user.orElse(null))
                .commentType(CommentType.comment)
                .text("子评论测试2")
                .articleContent(content.orElse(null))
//                .parentComment(articleCommentRepository.findById(75L).orElse(null))
                .parentCommentId(78L)
                .build();
        articleCommentRepository.save(comment);

        articleCommentRepository.findAll().forEach(System.out::println);
    }
}
