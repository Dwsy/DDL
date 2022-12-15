package link.dwsy.ddl.repository.Data.Article;

import link.dwsy.ddl.XO.Projection.DailyData_Id_And_ScoreCount;
import link.dwsy.ddl.entity.Data.Article.ArticleDailyData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;


public interface ArticleDailyDataRepository extends JpaRepository<ArticleDailyData, Long> {
    List<ArticleDailyData> findByUserIdAndDataDateBetween(long userId, LocalDate dataDateStart, LocalDate dataDateEnd);
    @Query(nativeQuery = true, value = "select article_field_id as id, sum(score) as scoreCount \n" +
            "from article_daily_data\n" +
            "where date_trunc('day', (data_date)) > date_trunc('day', date(?1))\n" +
            "group by article_field_id\n" +
            "order by sum(score) desc limit ?2")
    List<DailyData_Id_And_ScoreCount> getNDaysRank(LocalDate startDate, int limit);

    @Query(nativeQuery = true, value = "select article_field_id as id, sum(score) as scoreCount \n" +
            "from article_daily_data\n" +
            "where date_trunc('day', (data_date)) > date_trunc('day', date(?1)) and group_id=?2\n" +
            "group by article_field_id\n" +
            "order by sum(score) desc limit ?3")
    List<DailyData_Id_And_ScoreCount> getNDaysRankByGroup(LocalDate startDate, long groupId, int limit);

    @Query(nativeQuery = true, value = "select article_field_id as id, sum(score) as scoreCount \n" +
            "from article_daily_data\n" +
            "where date_trunc('day', (data_date)) > date_trunc('day', date(?1)) and jsonb_contains(tag_ids, ?2)\n" +
            "group by article_field_id\n" +
            "order by sum(score) desc limit ?3")
    List<DailyData_Id_And_ScoreCount> getNDaysRankByTag(LocalDate startDate, String tagId, int limit);

    @Query(nativeQuery = true, value = "select article_field_id as id, sum(score) as scoreCount \n" +
            "from article_daily_data\n" +
            "where  date_trunc('day', date(?2)) >= date_trunc('day', (data_date)) >= date_trunc('day', date(?1)) "+
            "group by article_field_id\n" +
            "order by sum(score) desc limit ?3")
    List<DailyData_Id_And_ScoreCount> getBetweenDateRank(LocalDate startDate,LocalDate endDate, int limit);
}
