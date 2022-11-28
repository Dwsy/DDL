package link.dwsy.ddl.repository;

import link.dwsy.ddl.XO.Enum.OauthType;
import link.dwsy.ddl.entity.OauthUserRef;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Author Dwsy
 * @Date 2022/8/24
 */
public interface OauthUserRefRepository extends JpaRepository<OauthUserRef,Long> {
    OauthUserRef findByDeletedFalseAndOauthIdAndOauthNodeIdAndOauthType(long oauthId, String oauthNodeId, OauthType oauthType);

}
