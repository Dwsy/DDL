package link.dwsy.ddl.controller;

import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import link.dwsy.ddl.annotation.IgnoreResponseAdvice;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author Dwsy
 * @Date 2022/9/3
 */

@RestController
@RequestMapping("/check")
public class ContentChecking {


    @Resource
    private SensitiveWordBs sensitiveWordBs;


    /**
     * 是否包含敏感词
     *
     * @param text 文本
     * @return 结果
     */
    @PostMapping("/contains")
    @IgnoreResponseAdvice
    public boolean contains(@RequestParam("text")  String text) {

        return sensitiveWordBs.contains(text);
    }

    /**
     * 获取所有的敏感词
     * @param text 文本
     * @return 结果
     */
    @PostMapping("/findAll")
    public List<String>  findAll(@RequestParam("text") String text) {

        return sensitiveWordBs.findAll(text);
    }

    /**
     * 获取替换后的结果
     *
     * @param text 文本
     * @return 结果
     */
    @PostMapping("/replace")
    public String replace(@RequestParam("text") String text) {

        return sensitiveWordBs.replace(text);
    }



}
