package link.dwsy.ddl.repository.Data.Article;

import link.dwsy.ddl.XO.Projection.DailyData_Id_And_ScoreCount;
import link.dwsy.ddl.entity.Data.Article.ArticleDailyData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;


public interface ArticleDailyDataRepository extends JpaRepository<ArticleDailyData, Long> {
    @Query(nativeQuery = true, value = "select article_field_id as id, sum(score) as scoreCount \n" +
            "from article_daily_data\n" +
            "where date_trunc('day', (data_date)) > date_trunc('day', date(?1))\n" +
            "group by article_field_id\n" +
            "order by sum(score) desc limit ?2")
    List<DailyData_Id_And_ScoreCount> getNDaysRank(LocalDate startDate, int limit);
//    select *
//    from article_daily_data
//    where id in (select article_field_id
//             from article_daily_data
//                     where date_trunc('day', date(data_date)) > date_trunc('day', date('2022-12-3'))
//    group by article_field_id
//    order by sum(score) desc)
//    and jsonb_contains(tag_ids, '[4]')
//    limit 30;

}
