package link.dwsy.ddl.service;

import link.dwsy.ddl.XO.RB.ArticleGroupRB;
import link.dwsy.ddl.XO.VO.fieldVO;
import link.dwsy.ddl.entity.Article.ArticleGroup;
import link.dwsy.ddl.util.PageData;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface ArticleGroupService {
    PageData<fieldVO> getFieldListByGroupId(Long gid, PageRequest pageRequest);

    List<ArticleGroup> getGroupList(Sort sort);

    boolean addGroup(ArticleGroupRB articleGroupRB);

    boolean deleteGroup(Long id);

    void updateGroup(Long id, ArticleGroupRB articleGroupRB);
}
