package link.dwsy.ddl.repository.User;

import link.dwsy.ddl.entity.User.UserFollowing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigInteger;

/**
 * @Author Dwsy
 * @Date 2022/9/2
 */

public interface UserFollowingRepository extends JpaRepository<UserFollowing, Long> {
//    @Query("select u from UserFollowing u where u.deleted = false and u.userId = ?1")
//    Page<UserFollowing> findFollowingUserIdList(Long userId, Pageable pageable);



    @Query(value = "select following_user_id from user_following where deleted is false and user_id=?1",
            countQuery = "select count(following_user_id) from user_following where deleted is false and user_id=?1",
            nativeQuery = true)
        //    获取用户关注列表
    Page<BigInteger> findFollowingUserIdList(long uid, Pageable pageable);

    @Query(value = "select user_id from user_following where deleted is false and following_user_id=?1",
            countQuery = "select count(user_id) from user_following where deleted is false and following_user_id=?1",
            nativeQuery = true)
        //    获取粉丝列表
    Page<BigInteger> findFollowerUserIdList(long uid,
                                            Pageable pageable);

    UserFollowing findByUserIdAndFollowingUserIdAndDeletedIsFalse(Long userId, Long followingUserId);

    UserFollowing findByUserIdAndFollowingUserId(Long userId, Long followingUserId);

    boolean existsByUserIdAndFollowingUserIdAndDeletedIsFalse(Long userId, Long followingUserId);




}
