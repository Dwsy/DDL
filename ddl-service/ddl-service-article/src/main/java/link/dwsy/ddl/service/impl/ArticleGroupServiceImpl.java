package link.dwsy.ddl.service.impl;

import link.dwsy.ddl.XO.Enum.Article.ArticleState;
import link.dwsy.ddl.XO.RB.ArticleGroupRB;
import link.dwsy.ddl.XO.VO.fieldVO;
import link.dwsy.ddl.core.CustomExceptions.CodeException;
import link.dwsy.ddl.core.constant.CustomerErrorCode;
import link.dwsy.ddl.entity.Article.ArticleGroup;
import link.dwsy.ddl.repository.Article.ArticleFieldRepository;
import link.dwsy.ddl.repository.Article.ArticleGroupRepository;
import link.dwsy.ddl.service.ArticleGroupService;
import link.dwsy.ddl.util.PageData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author Dwsy
 * @Date 2022/8/27
 */

@Service
public class ArticleGroupServiceImpl implements ArticleGroupService {

    @Resource
    ArticleGroupRepository articleGroupRepository;
    @Resource
    ArticleFieldRepository articleFieldRepository;

    public PageData<fieldVO> getFieldListByGroupId(Long gid,PageRequest pageRequest) {
        Page<fieldVO> fieldVO = articleFieldRepository
                .findAllByDeletedIsFalseAndArticleGroupIdAndArticleState
                        (gid, ArticleState.published, pageRequest);
        return new PageData<>(fieldVO);
    }

    public List<ArticleGroup> getGroupList(Sort sort) {
        return articleGroupRepository.findAllByDeletedIsFalse(sort);
    }

    public boolean addGroup(ArticleGroupRB articleGroupRB) {
        if (articleGroupRepository.existsByName(articleGroupRB.getName())) {
            throw new CodeException(CustomerErrorCode.ArticleGroupAlreadyExists);
        }
        ArticleGroup articleGroup = ArticleGroup.builder()
                .name(articleGroupRB.getName()).info(articleGroupRB.getInfo()).build();
        articleGroupRepository.save(articleGroup);
        return true;
    }

    public boolean deleteGroup(Long id) {
        int i = articleGroupRepository.logicallyDeleteById(id);
        return i > 0;


    }

    public void updateGroup(Long id, ArticleGroupRB articleGroupRB) {
        ArticleGroup articleGroup = articleGroupRepository.findById(id).orElseThrow(() -> new CodeException(CustomerErrorCode.ArticleGroupNotFound));

        articleGroup.setName(articleGroupRB.getName());
        articleGroup.setInfo(articleGroupRB.getInfo());

        articleGroupRepository.save(articleGroup);
    }
}
