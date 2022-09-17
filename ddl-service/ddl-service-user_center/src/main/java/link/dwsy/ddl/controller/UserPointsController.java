package link.dwsy.ddl.controller;


import link.dwsy.ddl.annotation.AuthAnnotation;
import link.dwsy.ddl.entity.User.UserPoints;
import link.dwsy.ddl.repository.User.UserPointsRepository;
import link.dwsy.ddl.support.UserSupport;
import link.dwsy.ddl.util.PRHelper;
import link.dwsy.ddl.util.PageData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/points")
public class UserPointsController {

    @Resource
    UserPointsRepository userPointsRepository;

    @Resource
    UserSupport userSupport;


    @GetMapping
    @AuthAnnotation
    public int getPoints() {
        Long id = userSupport.getCurrentUser().getId();
        if (id != null) {
            return userPointsRepository.getSumPointsByUid(id);
        }
        return 0;
    }

    @GetMapping("details")
    @AuthAnnotation
    public PageData<UserPoints> getDetails(@RequestParam(required = false, defaultValue = "1", name = "page") int page,
                                           @RequestParam(required = false, defaultValue = "8", name = "size") int size) {
        Long id = userSupport.getCurrentUser().getId();
        if (id == null) {
            return null;
        }

        Page<UserPoints> pointsList = userPointsRepository.findByUserId(id, PRHelper.order(Sort.Direction.DESC, new String[]{"createTime"}, page, size));

        return new PageData<>(pointsList);
    }

}
