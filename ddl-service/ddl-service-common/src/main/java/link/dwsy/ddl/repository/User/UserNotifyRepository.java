package link.dwsy.ddl.repository.User;

import link.dwsy.ddl.XO.Enum.Message.NotifyState;
import link.dwsy.ddl.XO.Enum.Message.NotifyType;
import link.dwsy.ddl.entity.User.UserNotify;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Collection;

/**
 * @Author Dwsy
 * @Date 2022/9/12
 */

public interface UserNotifyRepository extends JpaRepository<UserNotify, Long> {
    boolean existsByDeletedFalseAndFromUserIdAndToUserIdAndCommentIdAndNotifyType
            (long fromUserId, long toUserId, long commentId, NotifyType notifyType);

    boolean existsByDeletedFalseAndFromUserIdAndToUserIdAndCommentIdAndNotifyTypeAndArticleId
            (long fromUserId, long toUserId, long commentId, NotifyType notifyType, long articleId);

    boolean existsByDeletedFalseAndFromUserIdAndToUserIdAndAnswerIdAndNotifyTypeAndQuestionId
            (long fromUserId, long toUserId, long answerId, NotifyType notifyType, long questionId);


    Page<UserNotify> findByDeletedFalseAndToUserIdAndNotifyType
            (long toUserId, NotifyType notifyType, Pageable pageable);

    Page<UserNotify> findByDeletedFalseAndToUserIdAndNotifyTypeIn
            (long toUserId, Collection<NotifyType> notifyTypes, Pageable pageable);

    int countByDeletedFalseAndToUserIdAndNotifyStateIn
            (long toUserId, Collection<NotifyState> notifyStates);

    int countByDeletedFalseAndToUserIdAndNotifyState(long toUserId, NotifyState notifyState);

    int countByDeletedFalseAndToUserIdAndNotifyStateAndNotifyTypeIn
            (long toUserId, NotifyState notifyState, Collection<NotifyType> notifyTypes);

    int countByDeletedFalseAndToUserIdAndNotifyStateAndNotifyType
            (long toUserId, NotifyState notifyState, NotifyType notifyType);

    UserNotify findByDeletedFalseAndQuestionIdAndFromUserIdAndToUserId(long questionId, long fromUserId, long toUserId);

    boolean existsByDeletedFalseAndQuestionIdAndCancelFalseAndFromUserIdAndToUserId(long questionId, long fromUserId, long toUserId);

    boolean existsByDeletedFalseAndQuestionIdAndCancelFalseAndFromUserIdAndToUserIdAndNotifyType(long questionId, long fromUserId, long toUserId, NotifyType notifyType);
    UserNotify findByDeletedFalseAndQuestionIdAndCancelFalseAndFromUserIdAndToUserIdAndNotifyType(long questionId, long fromUserId, long toUserId, NotifyType notifyType);


    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "update user_notify set cancel = ?1 where id = ?2")
    int SetCancelNotifyById(boolean cancel, long id);
}
