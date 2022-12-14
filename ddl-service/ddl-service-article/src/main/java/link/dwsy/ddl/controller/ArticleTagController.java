package link.dwsy.ddl.controller;

import link.dwsy.ddl.XO.RB.ArticleTagRB;
import link.dwsy.ddl.XO.VO.fieldVO;
import link.dwsy.ddl.annotation.AuthAnnotation;
import link.dwsy.ddl.core.CustomExceptions.CodeException;
import link.dwsy.ddl.core.constant.CustomerErrorCode;
import link.dwsy.ddl.entity.Article.ArticleTag;
import link.dwsy.ddl.repository.Article.ArticleTagRepository;
import link.dwsy.ddl.service.impl.ArticleTagServiceImpl;
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
@RequestMapping("tag")
public class ArticleTagController {
    @Resource
    ArticleTagServiceImpl articleTagService;

    @Resource
    ArticleTagRepository articleTagRepository;

    @GetMapping("list")
    public List<ArticleTag> getTagList(
            @RequestParam(required = false, defaultValue = "ASC,DESC", name = "order") String[] order,
            @RequestParam(required = false, defaultValue = "weight,articleNum", name = "properties") String[] properties) {
        return articleTagService.getTagList(PRHelper.sort(order, properties));
    }

    @GetMapping
    public ArticleTag getTag(@RequestParam(required = false, defaultValue = "0", name = "tagId") long tagId) {
        return articleTagRepository.findById(tagId).orElseThrow(() -> new CodeException(CustomerErrorCode.ArticleTagNotFound));
    }

    @GetMapping("group/list/{id}")
    public List<ArticleTag> getTagListByGroupId(
            @PathVariable(name = "id") Long id,
            @RequestParam(required = false, defaultValue = "ASC", name = "order") String order,
            @RequestParam(required = false, defaultValue = "createTime", name = "properties") String[] properties) {
        return articleTagService.getTagListByGroupId(id,PRHelper.sort(order, properties));
    }

    @GetMapping("article/{id}")
    public PageData<fieldVO> getArticleFieldListByTagId(
            @PathVariable(name = "id") Long id,
            @RequestParam(required = false, defaultValue = "1", name = "page") int page,
            @RequestParam(required = false, defaultValue = "8", name = "size") int size,
            @RequestParam(required = false, defaultValue = "ASC", name = "order") String order,
            @RequestParam(required = false, defaultValue = "createTime", name = "properties") String[] properties
    ) {
        if (id < 0L || size < 1) {
            throw new CodeException(CustomerErrorCode.ParamError);
        }
        PageRequest pageRequest = PRHelper.order(order, properties, page, size);
        return articleTagService.getArticleListById(id, pageRequest);
    }


    @PostMapping()
    @AuthAnnotation(Level = 999)
    public boolean addTag(@RequestBody @Validated ArticleTagRB articleTagRB) {
        return articleTagService.addTag(articleTagRB);
    }

    @PutMapping("{id}")
    @AuthAnnotation(Level = 999)
    public boolean updateTag(@PathVariable("id") Long id, @RequestBody @Validated ArticleTagRB articleTagRB) {
        articleTagService.updateTag(id, articleTagRB);
        return true;
    }

    @DeleteMapping("{id}")
    @AuthAnnotation(Level = 999)
    public boolean deleteTag(@PathVariable(name = "id") Long id) {
        articleTagService.deleteTag(id);
        return true;
    }


}
