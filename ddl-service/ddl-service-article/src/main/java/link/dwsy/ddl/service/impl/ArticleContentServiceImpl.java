package link.dwsy.ddl.service.impl;

import link.dwsy.ddl.XO.Enum.Article.ArticleState;
import link.dwsy.ddl.XO.VO.fieldVO;
import link.dwsy.ddl.entity.Article.ArticleField;
import link.dwsy.ddl.repository.Article.ArticleContentRepository;
import link.dwsy.ddl.repository.Article.ArticleFieldRepository;
import link.dwsy.ddl.repository.Article.ArticleGroupRepository;
import link.dwsy.ddl.repository.Article.ArticleTagRepository;
import link.dwsy.ddl.repository.User.UserRepository;
import link.dwsy.ddl.service.ArticleContentService;
import link.dwsy.ddl.support.UserSupport;
import link.dwsy.ddl.util.PageData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
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
    ArticleFieldRepository articleFieldRepository;
    @Resource
    ArticleGroupRepository articleGroupRepository;
    @Resource
    ArticleContentRepository articleContentRepository;
    @Resource
    RabbitTemplate rabbitTemplate;
    @Resource
    UserSupport userSupport;

    @Resource
    UserRepository userRepository;


//    public PageData<ArticleContent, ArticleContentDTO> getPageList(int page, int size) {
//        PageRequest pageRequest = PageRequest.of(page-1, size);
//        Page<ArticleContent> articleContentPage = articleContentRepository.findAllByDeletedIsFalseOrDeletedIsNull(pageRequest);
//        return new PageData<>(articleContentPage, ArticleContentDTO::convert);
//    }

    public PageData<fieldVO> getPageList(PageRequest pageRequest, ArticleState articleState) {

        Page<fieldVO> fieldVOList = articleFieldRepository.findAllByDeletedIsFalseAndArticleState(articleState, pageRequest);
        PageData<fieldVO> fieldVOPageData = new PageData<>(fieldVOList);
        return fieldVOPageData;
    }
    public PageData<fieldVO> getPageList(PageRequest pageRequest, ArticleState articleState, long articleTagId) {

        Page<fieldVO> fieldVOList = articleFieldRepository.findByDeletedFalseAndArticleStateAndArticleTags_Id
                (articleState, articleTagId,pageRequest);
        return new PageData<>(fieldVOList);
    }

    public ArticleField getArticleById(long id, ArticleState articleState) {
        ArticleField af = articleFieldRepository.findByIdAndDeletedIsFalseAndArticleState(id, articleState);
        return af;
    }




    public String getContent(long id, int type) {
        if (type == 0) {
            return articleContentRepository.getHtmlTextById(id);
        }
        if (type == 1) {
            return articleContentRepository.getPureTextById(id);
        }
        if (type == 2) {
            return articleContentRepository.getMdTextById(id);
        }
        return null;
    }

}
