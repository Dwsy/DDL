package link.dwsy.ddl.repository.Meaasge;

import link.dwsy.ddl.XO.Enum.Message.MessageState;
import link.dwsy.ddl.entity.Message.UserMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Set;

/**
 * @Author Dwsy
 * @Date 2022/9/1
 */

public interface UserMessageRepository extends JpaRepository<UserMessage,Long> {
    Page<UserMessage> findByConversationIdAndIdGreaterThanAndDeletedFalseAndStatusIn
            (String conversationId, long id, Set<MessageState> status, Pageable pageable);

    Page<UserMessage> findByConversationIdAndIdLessThanAndDeletedFalseAndStatusIn
            (String conversationId, long id, Collection<MessageState> statuses, Pageable pageable);



}
