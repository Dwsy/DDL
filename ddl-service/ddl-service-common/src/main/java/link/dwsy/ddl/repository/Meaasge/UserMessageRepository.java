package link.dwsy.ddl.repository.Meaasge;

import link.dwsy.ddl.XO.Enum.Message.MessageState;
import link.dwsy.ddl.entity.Message.UserMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @Author Dwsy
 * @Date 2022/9/1
 */

public interface UserMessageRepository extends JpaRepository<UserMessage, Long> {
    Page<UserMessage> findByConversationIdAndIdGreaterThanAndDeletedFalseAndStatusIn
            (String conversationId, long id, Set<MessageState> status, Pageable pageable);


    Page<UserMessage> findByConversationIdAndIdLessThanAndDeletedFalseAndStatusIn
            (String conversationId, long id, Collection<MessageState> statuses, Pageable pageable);

//    select *
//    from user_message
//    where id in (select max(id) as id
//    from user_message
//    where to_user_id = 3
//    or form_user_id = 3
//    group by conversation_id)
    @Query(nativeQuery = true,value = "select * from user_message where status in (0,1) and id in " +
            "(select max(id) as id from user_message where to_user_id = ?1 or form_user_id = ?1 group by conversation_id) order by create_time desc ")
    List<UserMessage> getPrivateMessageList(long uid);


    @Query(nativeQuery = true,
            value = "select * from user_message where status in (0,1) and id in " +
            "(select max(id) as id from user_message where to_user_id = ?1 or form_user_id = ?1 group by conversation_id)" +
                    " order by create_time desc ",countQuery = "select count(*) from user_message where status in (0,1) and id in (select max(id) as id from user_message where to_user_id = ?1 or form_user_id = ?1 group by conversation_id)")
    Page<UserMessage> getPrivateMessageListPage(long uid, Pageable pageable);

    boolean existsByDeletedFalseAndConversationId(String conversationId);

    int countByToUserIdAndStatus(long toUserId, MessageState status);

    int countByToUserIdAndFormUserIdAndStatusAndDeletedFalse(Long uid, long chatUserId, MessageState unread);


//    List<UserMessage> findDistinctByToUserIdOrFormUserIdAndDeletedIsFalseAndOrderByConversationId(long toUserId, long formUserId);

//    Page<UserMessage> findByConversationIdAndDeletedFalseAndStatusIn(String conversationId, Collection<MessageState> statuses, int page, int size);


}
