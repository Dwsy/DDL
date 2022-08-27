package link.dwsy.ddl.controller;

import link.dwsy.ddl.entity.ArticleComment;
import link.dwsy.ddl.repository.ArticleCommentRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import link.dwsy.ddl.annotation.AuthAnnotation;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author Dwsy
 * @Date 2022/8/24
 */
@RestController
@RequestMapping("/test")
public class liveTestController {

    @Resource
    ArticleCommentRepository articleCommentRepository;
    @GetMapping("l1")
    @AuthAnnotation
    public String t1() {
        return "1";
    }

    @GetMapping("l5")
    @AuthAnnotation(Level = 5)
    public String t5() {
        return "5";
    }

    @GetMapping("l3")
    @AuthAnnotation(Level = 3)
    public List<ArticleComment> t3() {
        return articleCommentRepository.findAll();
    }
}
