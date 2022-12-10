package link.dwsy.ddl.repository.Data.Question;

import link.dwsy.ddl.entity.Data.Question.QuestionDailyData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;


public interface QuestionDailyDataRepository extends JpaRepository<QuestionDailyData, Long> {

    @Query(value = "select q from QuestionDailyData as q where q.date<= ?1 group by q.questionFieldId order by sum(q.score) desc")
    List<QuestionDailyData> getNDaysRank(LocalDate startDate );
}
