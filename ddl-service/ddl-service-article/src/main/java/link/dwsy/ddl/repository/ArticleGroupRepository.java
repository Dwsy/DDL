package link.dwsy.ddl.repository;
import link.dwsy.ddl.entity.ArticleContent;
import link.dwsy.ddl.entity.ArticleGroup;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ArticleGroupRepository extends JpaRepository<ArticleGroup,Long> {
}
