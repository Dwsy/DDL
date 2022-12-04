package link.dwsy.ddl.controller;

import link.dwsy.ddl.XO.Enum.User.PointsType;
import link.dwsy.ddl.annotation.AuthAnnotation;
import link.dwsy.ddl.annotation.Points;
import link.dwsy.ddl.annotation.UserActiveLog;
import link.dwsy.ddl.entity.Article.ArticleComment;
import link.dwsy.ddl.repository.Article.ArticleCommentRepository;
import link.dwsy.ddl.service.RPC.AuditService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author Dwsy
 * @Date 2022/8/24
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @Resource
    private ArticleCommentRepository articleCommentRepository;


    @Resource
    private AuditService auditService;

    @PostMapping("audit")
    public String test(@RequestParam(name = "text") String text) {
        return auditService.contains(text) ? "包含敏感词" : "不包含敏感词";
    }

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

    @GetMapping("v")
    @Points(TYPE = PointsType.Publish_Article)
    public void aVoid() {
        System.out.println("void");
    }

    @GetMapping("b/{id}")
    @UserActiveLog
    public String aBoolean(@PathVariable String id) {
        System.out.println(id);
        return id;
    }

}
