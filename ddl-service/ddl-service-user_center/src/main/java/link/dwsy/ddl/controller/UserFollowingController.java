package link.dwsy.ddl.controller;


import link.dwsy.ddl.XO.VO.FollowUserVO;
import link.dwsy.ddl.annotation.AuthAnnotation;
import link.dwsy.ddl.annotation.RequestSingleParam;
import link.dwsy.ddl.core.CustomExceptions.CodeException;
import link.dwsy.ddl.core.constant.CustomerErrorCode;
import link.dwsy.ddl.entity.User.User;
import link.dwsy.ddl.entity.User.UserFollowing;
import link.dwsy.ddl.repository.User.UserFollowingRepository;
import link.dwsy.ddl.repository.User.UserRepository;
import link.dwsy.ddl.support.UserSupport;
import link.dwsy.ddl.util.PRHelper;
import link.dwsy.ddl.util.PageData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/follow")
public class UserFollowingController {

    @Resource
    private   UserFollowingRepository userFollowingRepository;

    @Resource
    private   UserRepository userRepository;

    @Resource
    private   UserSupport userSupport;

    @GetMapping("follower/list")
    @AuthAnnotation
    public PageData<User> getUserFollower(
            @RequestParam(required = false, defaultValue = "ASC", name = "order") String order,
            @RequestParam(required = false, defaultValue = "create_time", name = "properties") String[] properties,
            @RequestParam(required = false, defaultValue = "1", name = "page") int page,
            @RequestParam(required = false, defaultValue = "10", name = "size") int size
    ) {

        Long id = userSupport.getCurrentUser().getId();
        PageRequest pageRequest = PRHelper.order(order, properties, page, size);
        Page<BigInteger> followerUserIdList = userFollowingRepository.findFollowerUserIdList(id, pageRequest);
        List<Long> longList = followerUserIdList.getContent().stream().map(BigInteger::longValue).collect(Collectors.toList());
        List<User> userList = userRepository.findAllById(longList);
        for (User user : userList) {
            FollowUserVO followUserVO = new FollowUserVO(user);

            boolean b = userFollowingRepository.existsByUserIdAndFollowingUserIdAndDeletedIsFalse(id, user.getId());
            followUserVO.setMutual(b);
        }
        Page<User> users = new PageImpl<>(userList, pageRequest, followerUserIdList.getTotalElements());

        return new PageData<>(users);
    }

    @GetMapping("following/list")
    @AuthAnnotation
    public PageData<User> getUserFollowing(
            @RequestParam(required = false, defaultValue = "ASC", name = "order") String order,
            @RequestParam(required = false, defaultValue = "create_time", name = "properties") String[] properties,
            @RequestParam(required = false, defaultValue = "1", name = "page") int page,
            @RequestParam(required = false, defaultValue = "10", name = "size") int size
    ) {
        Long id = userSupport.getCurrentUser().getId();
        PageRequest pageRequest = PRHelper.order(order, properties, page, size);
        Page<BigInteger> followerUserIdList = userFollowingRepository.findFollowingUserIdList(id, pageRequest);
        List<Long> longList = followerUserIdList.getContent().stream().map(BigInteger::longValue).collect(Collectors.toList());
        List<User> userList = userRepository.findAllById(longList);
        for (User user : userList) {
            FollowUserVO followUserVO = new FollowUserVO(user);
            // 互关
            boolean b = userFollowingRepository.existsByUserIdAndFollowingUserIdAndDeletedIsFalse(user.getId(), id);
            followUserVO.setMutual(b);
        }
        Page<User> users = new PageImpl<>(userList, pageRequest, followerUserIdList.getTotalElements());


        return new PageData<>(users);
    }


    @PostMapping("following")
    @AuthAnnotation
    public String followUser(@RequestSingleParam(value = "fid") String fid) {
        Long uid = userSupport.getCurrentUser().getId();
        if (Long.valueOf(fid).equals(uid)) {
            throw new CodeException(CustomerErrorCode.FOLLOW_SELF);
        }

        User followUser = userRepository.findUserByIdAndDeletedIsFalse(Long.valueOf(fid));

        if (followUser == null) {
            throw new CodeException(CustomerErrorCode.UserNotExist);
        }

        UserFollowing exist = userFollowingRepository.findByUserIdAndFollowingUserId(uid, Long.valueOf(fid));
        if (exist != null) {
            if (exist.getDeleted()) {
                exist.setDeleted(false);
                userFollowingRepository.save(exist);
                return "关注成功";
            } else {
                throw new CodeException(CustomerErrorCode.UserAlreadyFollowed);
            }
        }
        UserFollowing userFollowing = UserFollowing.builder().userId(uid).followingUserId(Long.valueOf(fid)).build();
        userFollowingRepository.save(userFollowing);
        return "关注成功";
    }

    @PostMapping("unfollow")
    @AuthAnnotation
    public Object unfollowUser(@RequestSingleParam(value = "fid") String fid) {
        Long uid = userSupport.getCurrentUser().getId();
        User followUser = userRepository.findUserByIdAndDeletedIsFalse(Long.valueOf(fid));
        if (followUser == null) {
            throw new CodeException(CustomerErrorCode.UserNotExist);
        }
        UserFollowing exist = userFollowingRepository.findByUserIdAndFollowingUserId(uid, Long.valueOf(fid));
        if (exist != null) {
            if (!exist.getDeleted()) {
                exist.setDeleted(true);
                userFollowingRepository.save(exist);
                return "取消关注成功";
            }else {
                throw new CodeException(CustomerErrorCode.UserNotFollowed);
            }
        } else {
            throw new CodeException(CustomerErrorCode.UserNotFollowed);
        }
    }

}
