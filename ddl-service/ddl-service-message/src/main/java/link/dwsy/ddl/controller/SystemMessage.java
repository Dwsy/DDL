package link.dwsy.ddl.controller;

import link.dwsy.ddl.annotation.AuthAnnotation;
import link.dwsy.ddl.repository.Meaasge.SystemMessageRepository;
import link.dwsy.ddl.service.impl.SystemMessageServiceImpl;
import link.dwsy.ddl.util.PageData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Author Dwsy
 * @Date 2022/9/2
 */

@RestController
@RequestMapping("/sys")
public class SystemMessage {
    @Resource
    private SystemMessageServiceImpl systemMessageService;
    @Resource
    private SystemMessageRepository systemMessageRepository;

    @GetMapping("/message")
    @AuthAnnotation
    public PageData<link.dwsy.ddl.entity.Message.SystemMessage> pullSysMessage(
            @RequestParam(required = false, defaultValue = "1", name = "page") int page,
            @RequestParam(required = false, defaultValue = "20", name = "size") int size
    ) {
        return systemMessageService.pullSysMessage(page < 0 ? 0 : page-1, Math.min(size, 20));
    }
}
