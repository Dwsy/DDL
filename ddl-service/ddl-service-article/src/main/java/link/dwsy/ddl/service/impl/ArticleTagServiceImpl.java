package link.dwsy.ddl.service.impl;

import link.dwsy.ddl.XO.Enum.Article.ArticleState;
import link.dwsy.ddl.XO.RB.ArticleTagRB;
import link.dwsy.ddl.XO.VO.fieldVO;
import link.dwsy.ddl.core.CustomExceptions.CodeException;
import link.dwsy.ddl.core.constant.CustomerErrorCode;
import link.dwsy.ddl.entity.Article.ArticleTag;
import link.dwsy.ddl.repository.Article.ArticleFieldRepository;
import link.dwsy.ddl.repository.Article.ArticleTagRepository;
import link.dwsy.ddl.service.ArticleTagService;
import link.dwsy.ddl.util.PageData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;
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
    public List<ArticleTag> getTagList(Sort sort) {

        return articleTagRepository.findByDeletedFalseAndArticleGroup_IdNotNullAndIndexPageDisplayIsTrue(sort);
    }

    public PageData<fieldVO> getArticleListById(Long id, PageRequest pageRequest) {
        Collection<Long> ids = articleTagRepository.findArticleContentIdListById(id);

        Page<fieldVO> fieldVOList = articleFieldRepository
                .findAllByIdInAndDeletedIsFalseAndArticleState
                        (ids, ArticleState.open, pageRequest);
        return new PageData<>(fieldVOList);


    }

    public List<ArticleTag> getTagListByGroupId(Long id, Sort sort) {
        return articleTagRepository.findByDeletedFalseAndArticleGroupId(id, sort);
    }



    public boolean addTag(ArticleTagRB articleTagRB) {

        if (articleTagRepository.existsByName(articleTagRB.getName())) {
            throw new CodeException(CustomerErrorCode.ArticleTagAlreadyExists);
        }
        ArticleTag articleTag = ArticleTag.builder()
                .name(articleTagRB.getName()).tagInfo(articleTagRB.getTagInfo()).build();
        articleTagRepository.save(articleTag);
        return true;
    }


    public void updateTag(Long id,ArticleTagRB articleTagRB) {
        articleTagRepository.findById(id).orElseThrow(() -> new CodeException(CustomerErrorCode.ArticleTagNotFound));
        ArticleTag.ArticleTagBuilder articleTagBuilder = ArticleTag.builder().name(articleTagRB.getName()).tagInfo(articleTagRB.getTagInfo());
        articleTagRepository.save(articleTagBuilder.build());
    }


    public boolean deleteTag(Long id) {
        int i = articleTagRepository.logicallyDeleteById(id);
        return i > 0;
    }
}
