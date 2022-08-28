package link.dwsy.ddl.service.impl;

import link.dwsy.ddl.XO.Enum.Article.ArticleState;
import link.dwsy.ddl.XO.VO.fieldVO;
import link.dwsy.ddl.entity.Article.ArticleGroup;
import link.dwsy.ddl.repository.Article.ArticleFieldRepository;
import link.dwsy.ddl.repository.Article.ArticleGroupRepository;
import link.dwsy.ddl.service.ArticleGroupService;
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
public class ArticleGroupServiceImpl implements ArticleGroupService {

    @Resource
    ArticleGroupRepository articleGroupRepository;
    @Resource
    ArticleFieldRepository articleFieldRepository;

    public PageData<fieldVO> getFieldListByGroupId(Long gid, int page, int size) {
        Page<fieldVO> fieldVO = articleFieldRepository
                .findAllByDeletedIsFalseAndArticleGroupIdAndArticleState
                        (gid, ArticleState.open, PageRequest.of(page-1, size));
        return new PageData<>(fieldVO);
    }

    public List<ArticleGroup> getGroupList() {
        return articleGroupRepository.findAllByDeletedIsFalse();
    }
}
