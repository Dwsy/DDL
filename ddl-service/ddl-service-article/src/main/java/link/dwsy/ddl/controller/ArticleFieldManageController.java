package link.dwsy.ddl.controller;

import link.dwsy.ddl.XO.Enum.Article.ArticleState;
import link.dwsy.ddl.XO.VO.fieldVO;
import link.dwsy.ddl.annotation.AuthAnnotation;
import link.dwsy.ddl.core.CustomExceptions.CodeException;
import link.dwsy.ddl.core.constant.CustomerErrorCode;
import link.dwsy.ddl.core.domain.LoginUserInfo;
import link.dwsy.ddl.repository.Article.ArticleFieldRepository;
import link.dwsy.ddl.repository.User.UserRepository;
import link.dwsy.ddl.service.Impl.UserActiveServiceImpl;
import link.dwsy.ddl.service.impl.ArticleContentServiceImpl;
import link.dwsy.ddl.service.impl.ArticleFieldServiceImpl;
import link.dwsy.ddl.support.UserSupport;
import link.dwsy.ddl.util.PRHelper;
import link.dwsy.ddl.util.PageData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author Dwsy
 * @Date 2022/10/15
 */

@RestController
@RequestMapping("article/manage")
@Slf4j
public class ArticleFieldManageController {
    @Resource
    private ArticleContentServiceImpl articleContentService;

    @Resource
    private ArticleFieldServiceImpl articleFieldService;

    @Resource
    private UserActiveServiceImpl userActiveService;

    @Resource
    private UserRepository userRepository;

    @Resource
    private UserSupport userSupport;
    @Resource
    private ArticleFieldRepository articleFieldRepository;

    @GetMapping("field/list")
    @AuthAnnotation
    public PageData<fieldVO> articleList(
            @RequestParam(required = false, defaultValue = "DESC", name = "order") String order,
            @RequestParam(required = false, defaultValue = "createTime", name = "properties") String[] properties,
            @RequestParam(required = false, defaultValue = "1", name = "page") int page,
            @RequestParam(required = false, defaultValue = "8", name = "size") int size,
            @RequestParam(required = false, defaultValue = "0", name = "tagId") long tagId,
            @RequestParam(required = false, defaultValue = "all", name = "state") ArticleState state
    ) {
        if (size < 1)
            throw new CodeException(CustomerErrorCode.ParamError);
        PageRequest pageRequest = PRHelper.order(order, properties, page, size);
        LoginUserInfo user = userSupport.getCurrentUser();
        Page<fieldVO> articleList;
        if (state == ArticleState.all) {
            articleList = articleFieldRepository.
                    findByUser_IdAndArticleStateNotAndDeleted(user.getId(), ArticleState.draft, false, pageRequest);
        } else {
            articleList = articleFieldRepository.
                    findByUser_IdAndArticleStateAndDeleted(user.getId(), state, false, pageRequest);
        }
        return new PageData<>(articleList);
    }

    @GetMapping("field/num")
    @AuthAnnotation
    public Map<ArticleState, Integer> getArticleCountByState() {
        LoginUserInfo user = userSupport.getCurrentUser();
        HashMap<ArticleState, Integer> countByState = new HashMap<>();
        countByState.put(ArticleState.all, articleFieldRepository.countByDeletedFalseAndUser_IdAndArticleStateNot(user.getId(), ArticleState.draft));
        countByState.put(ArticleState.draft, articleFieldRepository.countByDeletedFalseAndUser_IdAndArticleState(user.getId(), ArticleState.draft));
        countByState.put(ArticleState.published, articleFieldRepository.countByDeletedFalseAndUser_IdAndArticleState(user.getId(), ArticleState.published));
        countByState.put(ArticleState.hide, articleFieldRepository.countByDeletedFalseAndUser_IdAndArticleState(user.getId(), ArticleState.hide));
        countByState.put(ArticleState.auditing, articleFieldRepository.countByDeletedFalseAndUser_IdAndArticleState(user.getId(), ArticleState.auditing));
        countByState.put(ArticleState.rejected, articleFieldRepository.countByDeletedFalseAndUser_IdAndArticleState(user.getId(), ArticleState.rejected));
        return countByState;
    }
}
