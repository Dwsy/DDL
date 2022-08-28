package link.dwsy.ddl.service.impl;

import link.dwsy.ddl.XO.Enum.Article.ArticleState;
import link.dwsy.ddl.XO.VO.fieldVO;
import link.dwsy.ddl.entity.Article.ArticleTag;
import link.dwsy.ddl.repository.Article.ArticleFieldRepository;
import link.dwsy.ddl.repository.Article.ArticleTagRepository;
import link.dwsy.ddl.service.ArticleTagService;
import link.dwsy.ddl.util.PageData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author Dwsy
 * @Date 2022/8/27
 */

@Service
public class ArticleTagServiceImpl implements ArticleTagService {

    @Resource
    ArticleTagRepository articleTagRepository;

    @Resource
    ArticleFieldRepository articleFieldRepository;
    public List<ArticleTag> getTagList() {

        return articleTagRepository.findAllByDeletedIsFalse();

    }

    public PageData<fieldVO> getArticleListById(Long id, int page, int size) {
        long[] ids = articleTagRepository.findArticleContentIdListById(1L);

        Page<fieldVO> fieldVOList = articleFieldRepository
                .findAllByIdInAndDeletedIsFalseAndArticleState
                        (ids, ArticleState.open, PageRequest.of(page-1, size));
        return new PageData<>(fieldVOList);


    }
}
