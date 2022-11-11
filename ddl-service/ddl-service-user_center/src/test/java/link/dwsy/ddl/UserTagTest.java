package link.dwsy.ddl;

import link.dwsy.ddl.entity.QA.QaTag;
import link.dwsy.ddl.entity.User.User;
import link.dwsy.ddl.entity.User.UserTag;
import link.dwsy.ddl.repository.QA.QaQuestionTagRepository;
import link.dwsy.ddl.repository.User.UserRepository;
import link.dwsy.ddl.repository.User.UserTagRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author Dwsy
 * @Date 2022/11/10
 */
@SpringBootTest
public class UserTagTest {
    @Resource
    UserRepository userRepository;

    @Resource
    UserTagRepository userTagRepository;

    @Resource
    QaQuestionTagRepository qaQuestionTagRepository;

    @Test
    public void T() {
        List<UserTag> userTags = userRepository.findUserByIdAndDeletedIsFalse(3L).getUserTags();
        for (UserTag userTag : userTags) {
            System.out.println(userTag.getName());
        }
        System.out.println(userTags);
    }

    @Test
    public void Add() {
        User user = userRepository.findUserByIdAndDeletedIsFalse(3L);
        List<UserTag> all = userTagRepository.findAll();
        user.setUserTags(all);
        userRepository.save(user);
        T();
    }

    @Test
    public void copy() {
        List<QaTag> all = qaQuestionTagRepository.findAll();
        for (QaTag qaTag : all) {
            UserTag build = UserTag.builder()
                    .name(qaTag.getName())
                    .useNum(qaTag.getQuestionNum())
                    .weight(qaTag.getWeight())
                    .indexPageDisplay(qaTag.isIndexPageDisplay())
                    .tagInfo(qaTag.getTagInfo())
                    .qaGroup(qaTag.getQaGroup())
                    .build();

            userTagRepository.save(build);
        }
    }
}
