package link.dwsy.ddl.service.impl;

import link.dwsy.ddl.repository.ArticleFieldRepository;
import link.dwsy.ddl.service.ArticleFieldService;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author Dwsy
 * @Date 2022/8/27
 */

@Service
public class ArticleFieldServiceImpl implements ArticleFieldService {

    @Resource
    ArticleFieldRepository articleFieldRepository;

}
