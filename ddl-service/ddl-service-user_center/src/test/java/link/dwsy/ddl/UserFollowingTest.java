package link.dwsy.ddl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import link.dwsy.ddl.core.CustomExceptions.CodeException;
import link.dwsy.ddl.core.constant.CustomerErrorCode;
import link.dwsy.ddl.entity.User.User;
import link.dwsy.ddl.entity.User.UserFollowing;
import link.dwsy.ddl.repository.User.UserFollowingRepository;
import link.dwsy.ddl.repository.User.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author Dwsy
 * @Date 2022/9/2
 */

@SpringBootTest
public class UserFollowingTest {

    @Resource
    private UserFollowingRepository userFollowingRepository;
    @Resource
    private UserRepository userRepository;

    @Test
    public void test() {
        FollowUser(3, 4);
        FollowUser(3, 5);
        FollowUser(3, 6);
        FollowUser(4, 3);
        FollowUser(5, 3);
        FollowUser(6, 3);
    }

    @Test
    public void test2() throws JsonProcessingException {
        List<User> following = getUserFollowing(3);
        System.out.println(new ObjectMapper().writeValueAsString(following));
        List<User> follower = getUserFollower(3);
        System.out.println(new ObjectMapper().writeValueAsString(follower));
    }

    public void FollowUser(long uid, long fid) {
//token 校验不需要判断null了
        User followUser = userRepository.findUserByIdAndDeletedIsFalse(fid);
        if (followUser == null) {
            throw new CodeException(CustomerErrorCode.UserNotExist);
        }
        UserFollowing userFollowing = UserFollowing.builder().userId(uid).followingUserId(fid).build();
        userFollowingRepository.save(userFollowing);
    }

    //    获取用户关注列表
    public List<User> getUserFollowing(long uid) {
        PageRequest of = PageRequest.of(0, 10);
        Page<BigInteger> followingUserIdList = userFollowingRepository.findFollowingUserIdList(uid, of);
        List<Long> longList = followingUserIdList.getContent().stream().map(BigInteger::longValue).collect(Collectors.toList());
//        followingUserIdList.getContent().
        return userRepository.findAllById(longList);
    }

    //    获取粉丝列表
    public List<User> getUserFollower(long uid) {
        PageRequest of = PageRequest.of(0, 10);
        Page<BigInteger> followerUserIdList = userFollowingRepository.findFollowerUserIdList(uid, of);
        List<Long> longList = followerUserIdList.getContent().stream().map(BigInteger::longValue).collect(Collectors.toList());
        return userRepository.findAllById(longList);
    }
}
