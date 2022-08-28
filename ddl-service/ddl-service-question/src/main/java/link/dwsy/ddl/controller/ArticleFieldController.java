//package link.dwsy.ddl.controller;
//
////import link.dwsy.ddl.XO.DTO.ArticleContentDTO;
//
//import link.dwsy.ddl.XO.Enum.Article.ArticleState;
//import link.dwsy.ddl.XO.VO.fieldVO;
//import link.dwsy.ddl.core.CustomExceptions.CodeException;
//import link.dwsy.ddl.core.constant.CustomerErrorCode;
//import link.dwsy.ddl.entity.Article.ArticleField;
//import link.dwsy.ddl.repository.Article.ArticleContentRepository;
//import link.dwsy.ddl.repository.Article.ArticleGroupRepository;
//import link.dwsy.ddl.repository.Article.ArticleTagRepository;
//import link.dwsy.ddl.service.impl.ArticleContentServiceImpl;
//import link.dwsy.ddl.service.impl.ArticleFieldServiceImpl;
//import link.dwsy.ddl.util.PageData;
//import org.springframework.web.bind.annotation.*;
//
//import javax.annotation.Resource;
//import javax.validation.constraints.Size;
//
///**
// * @Author Dwsy
// * @Date 2022/8/25
// */
//
//@RestController
//@RequestMapping("article")
//public class ArticleFieldController {
//    @Resource
//    ArticleTagRepository articleTagRepository;
//    @Resource
//    ArticleContentRepository articleContentRepository;
//    @Resource
//    ArticleGroupRepository articleGroupRepository;
//
//    @Resource
//    ArticleContentServiceImpl articleContentService;
//
//    @Resource
//    ArticleFieldServiceImpl articleFieldService;
//
//    @GetMapping("field/list")
//    public PageData<fieldVO> ArticleList(
//            @RequestParam(required = false, defaultValue = "1", name = "page") int page,
//            @RequestParam(required = false, defaultValue = "8", name = "size") int size) {
//        if (size < 1)
//            throw new CodeException(CustomerErrorCode.ParamError);
//
//        return articleContentService.getPageList(page < 1 ? 1 : page, size > 20 ? 20 : size, ArticleState.open);
//    }
//
//    @GetMapping("field/{id}")
//    public ArticleField GetArticleById(@PathVariable("id") Long id) {
//        if (id < 0L)
//            throw new CodeException(CustomerErrorCode.ParamError);
//
//        return articleContentService.getArticleById(id, ArticleState.open);
//    }
//
//    /**
//     * @param id   id
//     * @param type 0 html 1 md 2 pure
//     * @return String
//     */
//    @GetMapping(value = "content/{id}",produces="application/json")
////    @IgnoreResponseAdvice
//    public String GetArticleContent(
//            @Size
//            @PathVariable(name = "id") Long id,
//            @RequestParam(required = false, defaultValue = "0", name = "type") int type) {
//        if (id < 0L)
//            throw new CodeException(CustomerErrorCode.ParamError);
//
//        if (type < 0 || type > 2)
//            throw new CodeException(CustomerErrorCode.ParamError);
//
//        return articleContentService.getContent(id, type);
//    }
//
//    @GetMapping(value = "test")
////    @IgnoreResponseAdvice
//    public String g() {
//        return "乱码二分之一";
//    }
//
//}