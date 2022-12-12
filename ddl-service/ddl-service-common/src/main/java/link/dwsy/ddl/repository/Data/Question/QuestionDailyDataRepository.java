package link.dwsy.ddl.repository.Data.Question;

import link.dwsy.ddl.XO.Projection.DailyData_Id_And_ScoreCount;
import link.dwsy.ddl.entity.Data.Question.QuestionDailyData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;


public interface QuestionDailyDataRepository extends JpaRepository<QuestionDailyData, Long> {

    @Query(nativeQuery = true, value = "select question_field_id as id, sum(score) as scoreCount \n" +
            "from question_daily_data\n" +
            "where date_trunc('day', (data_date)) > date_trunc('day', date(?1)) and group_id=?2 \n" +
            "group by question_field_id\n" +
            "order by sum(score) desc limit ?3")
    List<DailyData_Id_And_ScoreCount> getNDaysRankByGroup(LocalDate startDate, Long groupId, int limit);

    @Query(nativeQuery = true, value = "select question_field_id as id, sum(score) as scoreCount \n" +
            "from question_daily_data\n" +
            "where date_trunc('day', (data_date)) > date_trunc('day', date(?1))\n" +
            "group by question_field_id\n" +
            "order by sum(score) desc limit ?2")
    List<DailyData_Id_And_ScoreCount> getNDaysRank(LocalDate minusDays, int i);
}
