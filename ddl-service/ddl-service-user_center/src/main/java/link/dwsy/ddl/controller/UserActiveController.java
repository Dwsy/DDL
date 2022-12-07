package link.dwsy.ddl.controller;

import link.dwsy.ddl.XO.Enum.User.UserActiveType;
import link.dwsy.ddl.XO.VO.UserThumbActiveVO;
import link.dwsy.ddl.annotation.AuthAnnotation;
import link.dwsy.ddl.core.CustomExceptions.CodeException;
import link.dwsy.ddl.core.constant.CustomerErrorCode;
import link.dwsy.ddl.repository.Article.ArticleFieldRepository;
import link.dwsy.ddl.repository.User.UserActiveRepository;
import link.dwsy.ddl.repository.User.UserRepository;
import link.dwsy.ddl.service.Impl.UserActiveServiceImpl;
import link.dwsy.ddl.support.UserSupport;
import link.dwsy.ddl.util.PRHelper;
import link.dwsy.ddl.util.PageData;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Author Dwsy
 * @Date 2022/9/5
 */
@RestController
@RequestMapping("/active")
public class UserActiveController {

    @Resource
    private UserActiveRepository userActiveRepository;
    @Resource
    private UserSupport userSupport;

    @Resource
    private UserActiveServiceImpl userActiveService;

    @Resource
    private UserRepository userRepository;

    @Resource
    private ArticleFieldRepository articleFieldRepository;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @PostMapping("/check")
    @AuthAnnotation(Level = 0)
    public String checkIn() {
        return userActiveService.checkIn();
    }

    @GetMapping("/thumb/{uid}")
    public PageData<UserThumbActiveVO> getUserThumbActive(
            @PathVariable long uid,
            @RequestParam(required = false, defaultValue = "ASC", name = "order") String order,
            @RequestParam(required = false, defaultValue = "createTime", name = "properties") String[] properties,
            @RequestParam(required = false, defaultValue = "1", name = "page") int page,
            @RequestParam(required = false, defaultValue = "10", name = "size") int size,
            @RequestParam(required = true, name = "type") UserActiveType type) {
        {
            if (!userRepository.existsByDeletedFalseAndId(uid)) {
                throw new CodeException(CustomerErrorCode.UserNotExist);
            }
            PageRequest pageRequest = PRHelper.order(order, properties, page, size);

//            UserActiveType type = UserActiveType.values()[userActiveType];

            return userActiveService.getUserThumbActive(uid, type, pageRequest);
        }
    }


}
