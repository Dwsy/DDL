package link.dwsy.ddl.repository.Meaasge;

import link.dwsy.ddl.entity.Message.SystemMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Author Dwsy
 * @Date 2022/9/2
 */

public interface SystemMessageRepository extends JpaRepository<SystemMessage, Long> {

    Page<SystemMessage> findAllByToUserIdAndDeletedIsFalse(long toUserId, Pageable pageable);
//    void pullSysMessage(Long id);
}
