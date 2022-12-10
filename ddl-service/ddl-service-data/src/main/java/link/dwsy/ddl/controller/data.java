package link.dwsy.ddl.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequestMapping("/rank")
public class data {

    @GetMapping("article")
    public String getArticleRankList(@RequestParam("daysAgo")  int daysAgo){
        LocalDate currentDate = LocalDate.now();
        LocalDate minusDays = currentDate.minusDays(daysAgo);

        return "data/rank/article";
    }
}
