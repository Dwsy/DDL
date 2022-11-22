package link.dwsy.ddl.repository.Infinity;

import link.dwsy.ddl.entity.Infinity.Infinity;
import link.dwsy.ddl.entity.Infinity.InfinityClub;
import link.dwsy.ddl.entity.Infinity.InfinityTopic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author Dwsy
 * @Date 2022/11/23
 */

public interface InfinityRepository extends JpaRepository<Infinity, Long> {
    Infinity findByDeletedFalseAndId(long id);

    Page<Infinity> findByDeletedFalseAndInfinityTopic(InfinityTopic infinityTopic, Pageable pageable);

    Page<Infinity> findByDeletedFalseAndInfinityClub(InfinityClub infinityClub, Pageable pageable);

    Page<Infinity> findByDeletedFalseAndUser_Id(long id, Pageable pageable);


    @Modifying
    @Transactional
    @Query(nativeQuery = true,
            value = "update infinity set up_num=up_num+?2 where id=?1")
    void upNumIncrement(long id, int num);

    @Modifying
    @Transactional
    @Query(nativeQuery = true,
            value = "update infinity set collect_num=collect_num+?2 where id=?1")
    void collectNumIncrement(long id, int num);
}
