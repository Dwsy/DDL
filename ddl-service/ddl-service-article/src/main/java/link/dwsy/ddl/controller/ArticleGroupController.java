package link.dwsy.ddl.controller;

import link.dwsy.ddl.XO.RB.ArticleGroupRB;
import link.dwsy.ddl.XO.VO.fieldVO;
import link.dwsy.ddl.annotation.AuthAnnotation;
import link.dwsy.ddl.annotation.UserActiveLog;
import link.dwsy.ddl.core.CustomExceptions.CodeException;
import link.dwsy.ddl.core.constant.CustomerErrorCode;
import link.dwsy.ddl.entity.Article.ArticleGroup;
import link.dwsy.ddl.service.impl.ArticleGroupServiceImpl;
import link.dwsy.ddl.util.PRHelper;
import link.dwsy.ddl.util.PageData;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
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

    @GetMapping("list")
    @UserActiveLog
    public List<ArticleGroup> getGroupList(
            @RequestParam(required = false, defaultValue = "ASC", name = "order") String order,
            @RequestParam(required = false, defaultValue = "createTime", name = "properties") String[] properties) {

        return articleGroupService.getGroupList(PRHelper.sort(order, properties));
    }

    @GetMapping("article/{id}")
    public PageData<fieldVO> getFieldListByGroupId(
            @PathVariable(name = "id") Long id,
            @RequestParam(required = false, defaultValue = "1", name = "page") int page,
            @RequestParam(required = false, defaultValue = "8", name = "size") int size,
            @RequestParam(required = false, defaultValue = "ASC", name = "order") String order,
            @RequestParam(required = false, defaultValue = "createTime", name = "properties") String[] properties) {
        if (id < 0L || size < 1)
            throw new CodeException(CustomerErrorCode.ParamError);
        PageRequest pageRequest = PRHelper.order(order, properties, page, size);
        return articleGroupService.getFieldListByGroupId(id,pageRequest);
    }

    @PostMapping()
    @AuthAnnotation(Level = 999)
    public boolean addGroup(@RequestBody @Validated ArticleGroupRB articleGroupRB) {
        return articleGroupService.addGroup(articleGroupRB);
    }

    @PutMapping("{id}")
    @AuthAnnotation(Level = 999)
    public boolean updateGroup(@PathVariable("id") Long id, @RequestBody @Validated ArticleGroupRB articleGroupRB) {
        articleGroupService.updateGroup(id, articleGroupRB);
        return true;
    }

    @DeleteMapping("{id}")
    @AuthAnnotation(Level = 999)
    public boolean deleteGroup(@PathVariable(name = "id") Long id) {
        return articleGroupService.deleteGroup(id);
    }
}
