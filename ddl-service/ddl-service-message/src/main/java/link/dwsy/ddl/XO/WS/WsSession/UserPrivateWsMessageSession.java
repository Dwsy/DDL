package link.dwsy.ddl.XO.WS.WsSession;

import lombok.Builder;
import lombok.Data;

import javax.websocket.Session;
import java.util.Set;

/**
 * @Author Dwsy
 * @Date 2022/9/28
 */

@Data
@Builder
public class UserPrivateWsMessageSession {
    private Set<Session> BigIdUserSessionSet;
    private Set<Session> SmallIdUserSessionSet;
    private long BigId;
    private long SmallId;
    private boolean HasSubChannel=false;
}
