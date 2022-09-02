package link.dwsy.ddl.service.impl;

import link.dwsy.ddl.entity.Message.SystemMessage;
import link.dwsy.ddl.repository.Meaasge.SystemMessageRepository;
import link.dwsy.ddl.support.UserSupport;
import link.dwsy.ddl.util.PageData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author Dwsy
 * @Date 2022/9/2
 */

@Service
public class SystemMessageServiceImpl {
    @Resource
    private SystemMessageRepository systemMessageRepository;
    @Resource
    private UserSupport userSupport;
    public PageData<SystemMessage> pullSysMessage(int page , int size) {
        Long id = userSupport.getCurrentUser().getId();
        Page<SystemMessage> systemMessages = systemMessageRepository.findAllByToUserIdAndDeletedIsFalse(id, PageRequest.of(page, size));
        return new PageData<>(systemMessages);
    }
}
