package link.dwsy.ddl.controller;

import link.dwsy.ddl.XO.VO.fieldVO;
import link.dwsy.ddl.core.CustomExceptions.CodeException;
import link.dwsy.ddl.core.constant.CustomerErrorCode;
import link.dwsy.ddl.entity.ArticleTag;
import link.dwsy.ddl.service.impl.ArticleTagServiceImpl;
import link.dwsy.ddl.util.PageData;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @Author Dwsy
 * @Date 2022/8/25
 */
@RestController
@RequestMapping("tag")
public class ArticleTagController {
    @Resource
    ArticleTagServiceImpl articleTagService;

    @GetMapping("list")
    public List<ArticleTag> GetTagList() {

        return articleTagService.getTagList();
    }

    @GetMapping("article/{id}")
    public PageData<fieldVO> GetArticleFieldList(
            @PathVariable(name = "id") Long id,
            @RequestParam(required = false, defaultValue = "1", name = "page") int page,
            @RequestParam(required = false, defaultValue = "8", name = "size") int size) {
        if (id < 0L || size < 1)
            throw new CodeException(CustomerErrorCode.ParamError);

        return articleTagService.getArticleListById(id, page < 1 ? 1 : page, size > 20 ? 20 : size);
    }


}
