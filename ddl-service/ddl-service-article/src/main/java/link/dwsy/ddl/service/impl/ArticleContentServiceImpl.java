package link.dwsy.ddl.service.impl;

import link.dwsy.ddl.XO.CustomER.entity.ArticleContentCustom;
import link.dwsy.ddl.XO.CustomER.repository.ArticleContentRepositoryCustom;
import link.dwsy.ddl.XO.Enum.ArticleState;
import link.dwsy.ddl.repository.ArticleGroupRepository;
import link.dwsy.ddl.repository.ArticleTagRepository;
import link.dwsy.ddl.service.ArticleContentService;
import link.dwsy.ddl.util.PageData;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author Dwsy
 * @Date 2022/8/25
 */
@Service
public class ArticleContentServiceImpl implements ArticleContentService {
    @Resource
    ArticleTagRepository articleTagRepository;
    @Resource
    ArticleContentRepositoryCustom articleContentRepositoryCustom;
    @Resource
    ArticleGroupRepository articleGroupRepository;

//    public PageData<ArticleContent, ArticleContentDTO> getPageList(int page, int size) {
//        PageRequest pageRequest = PageRequest.of(page-1, size);
//        Page<ArticleContent> articleContentPage = articleContentRepository.findAllByDeletedIsFalseOrDeletedIsNull(pageRequest);
//        return new PageData<>(articleContentPage, ArticleContentDTO::convert);
//    }

    public PageData<ArticleContentCustom> getPageList(int page, int size, ArticleState articleState) {
        PageRequest pageRequest = PageRequest.of(page-1, size);
        PageData<ArticleContentCustom> articleContentCustomPageData = new PageData<>(articleContentRepositoryCustom.findAllByDeletedIsFalseAndArticleState(articleState, pageRequest));
        return articleContentCustomPageData;
    }

    public ArticleContentCustom getArticleById(long id, ArticleState articleState) {
        ArticleContentCustom Article = articleContentRepositoryCustom.findByIdAndArticleState(id, articleState);
        return Article;
    }
}
