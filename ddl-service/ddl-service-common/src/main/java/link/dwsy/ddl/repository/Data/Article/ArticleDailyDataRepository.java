package link.dwsy.ddl.repository.Data.Article;

import link.dwsy.ddl.entity.Data.Article.ArticleDailyData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;


public interface ArticleDailyDataRepository extends JpaRepository<ArticleDailyData, Long> {

    @Query(value = "select a from ArticleDailyData as a where a.date<= ?1 group by a.articleFieldId order by sum(a.score) desc")
    List<ArticleDailyData> getNDaysRank(LocalDate startDate );
}
