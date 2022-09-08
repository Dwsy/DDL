package link.dwsy.ddl.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author Dwsy
 * @Date 2022/9/3
 */

@RestController
@RequestMapping("/check")
public class ContentChecking {

    @GetMapping("/test")
    public String test() {
        return "test";
    }



}
