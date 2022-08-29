package link.dwsy.ddl.controller;

import link.dwsy.ddl.XO.VO.fieldVO;
import link.dwsy.ddl.core.CustomExceptions.CodeException;
import link.dwsy.ddl.core.constant.CustomerErrorCode;
import link.dwsy.ddl.entity.Article.ArticleTag;
import link.dwsy.ddl.service.impl.ArticleTagServiceImpl;
import link.dwsy.ddl.util.PRHelper;
import link.dwsy.ddl.util.PageData;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
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
    public List<ArticleTag> GetTagList(
            @RequestParam(required = false, defaultValue = "ASC", name = "order") String order,
            @RequestParam(required = false, defaultValue = "createTime", name = "properties") String[] properties) {

        return articleTagService.getTagList(PRHelper.sort(order, properties));
    }

    @GetMapping("article/{id}")
    public PageData<fieldVO> GetArticleFieldList(
            @PathVariable(name = "id") Long id,
            @RequestParam(required = false, defaultValue = "1", name = "page") int page,
            @RequestParam(required = false, defaultValue = "8", name = "size") int size,
            @RequestParam(required = false, defaultValue = "ASC", name = "order") String order,
            @RequestParam(required = false, defaultValue = "createTime", name = "properties") String[] properties
    ) {
        if (id < 0L || size < 1)
            throw new CodeException(CustomerErrorCode.ParamError);
        PageRequest pageRequest = PRHelper.order(order, properties, page, size);
        return articleTagService.getArticleListById(id, pageRequest);
    }


}
