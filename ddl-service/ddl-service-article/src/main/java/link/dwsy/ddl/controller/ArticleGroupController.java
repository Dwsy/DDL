package link.dwsy.ddl.controller;

import link.dwsy.ddl.XO.VO.fieldVO;
import link.dwsy.ddl.core.CustomExceptions.CodeException;
import link.dwsy.ddl.core.constant.CustomerErrorCode;
import link.dwsy.ddl.entity.Article.ArticleGroup;
import link.dwsy.ddl.service.impl.ArticleGroupServiceImpl;
import link.dwsy.ddl.util.PageData;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author Dwsy
 * @Date 2022/8/25
 */

@RestController
@RequestMapping("group")
public class ArticleGroupController {

    @Resource
    ArticleGroupServiceImpl articleGroupService;

    @GetMapping()
    public List<ArticleGroup> GetGroupList() {
        return articleGroupService.getGroupList();
    }

    @GetMapping("article/{id}")
    public PageData<fieldVO> GetFieldListByGroupId(
            @PathVariable(name = "id") Long id,
            @RequestParam(required = false, defaultValue = "1", name = "page") int page,
            @RequestParam(required = false, defaultValue = "8", name = "size") int size) {
        if (id < 0L || size < 1)
            throw new CodeException(CustomerErrorCode.ParamError);
        return articleGroupService.getFieldListByGroupId(id,page < 1 ? 1 : page, size > 20 ? 20 : size);
    }
}
