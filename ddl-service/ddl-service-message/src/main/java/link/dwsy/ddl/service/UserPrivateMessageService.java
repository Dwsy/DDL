package link.dwsy.ddl.service;

import link.dwsy.ddl.XO.RB.SendPrivateMessageRB;

/**
 * @Author Dwsy
 * @Date 2022/9/2
 */

public interface UserPrivateMessageService {
    /**
     * 发送私信
     * @param sendPrivateMessageRB
     * @return
     */
    boolean sendPrivateMessage(SendPrivateMessageRB sendPrivateMessageRB) throws Exception;
}
