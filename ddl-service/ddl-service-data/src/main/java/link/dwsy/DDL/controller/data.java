package link.dwsy.DDL.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/data")
public class data {

    @GetMapping("rank/article")
    public String getArticleRankList() {
        return "data/rank/article";
    }
}
