package link.dwsy.ddl.service;

import link.dwsy.ddl.XO.RB.SendPrivateMessageRB;
import link.dwsy.ddl.entity.Message.UserMessage;
import link.dwsy.ddl.util.PageData;
import org.springframework.data.domain.PageRequest;

/**
 * @Author Dwsy
 * @Date 2022/9/2
 */

public interface UserPrivateMessageService {
    /*
      发送私信

      @param sendPrivateMessageRB
      @return
     */
    ;
    boolean sendPrivateMessage(SendPrivateMessageRB sendPrivateMessageRB) throws Exception;

    PageData<UserMessage> pullMessageByLatestId(long latestId, long toUserId, PageRequest pr);


    PageData<UserMessage> pullHistoryMessage(long latestId, long toUserId, int page, int size);

    boolean readMessage(long id);

    int getUnreadMsgCount(Long uid, long chatUserId);
}
