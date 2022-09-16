package link.dwsy.ddl.controller;


import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.extra.mail.MailUtil;
import link.dwsy.ddl.annotation.AuthAnnotation;
import link.dwsy.ddl.core.CustomExceptions.CodeException;
import link.dwsy.ddl.core.constant.CustomerErrorCode;
import link.dwsy.ddl.entity.User.User;
import link.dwsy.ddl.repository.User.UserRepository;
import link.dwsy.ddl.support.UserSupport;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Objects;

@RestController
@RequestMapping("modify")
public class UserController {

    @Resource
    UserSupport userSupport;

    @Resource
    UserRepository userRepository;

    @Resource
    RedisTemplate<String, String> redisTemplate;


    @PostMapping("pwd")
    @AuthAnnotation
    public boolean ChangePassword(@RequestParam("old") String old, @RequestParam("new") String newpwd) {

        if (old.equals(newpwd)) {
            throw new CodeException(CustomerErrorCode.OldPWDEqualsNewPWD);
        }

        Long id = userSupport.getCurrentUser().getId();

        User user = userRepository.findUserByIdAndDeletedIsFalse(id);

        if (Objects.equals(SecureUtil.md5(old + user.getSalt()), user.getPassword())) {
            user.setPassword(SecureUtil.md5(newpwd + user.getSalt()));
            userRepository.save(user);
            return true;
        } else {
            throw new CodeException(CustomerErrorCode.OldPWDWrong);

        }

    }

    @PostMapping
    @AuthAnnotation
    public boolean changeEmail(
            @RequestParam("captcha") boolean captcha,
            @RequestParam("email") String email) {
        if (captcha) {
            Long id = userSupport.getCurrentUser().getId();
            User user = userRepository.findUserByIdAndDeletedIsFalse(id);
            String e = user.getEmail();
            if (StrUtil.isBlank(e)) {
                user.setEmail(email);
                userRepository.save(user);
                return true;
            }
            int CAPTCHA = RandomUtil.randomInt(4);
            MailUtil.send(e, "修改邮箱验证", StrFormatter.format("<h3>验证码:<em style=\"color:red\">{}</em> 有效期15分钟 请误告诉他人</h3>", CAPTCHA), true);
            redisTemplate.opsForValue().set(StrFormatter.format("user:change:email:id:{}", id), String.valueOf(CAPTCHA));
            return true;
        } else {
            Long id = userSupport.getCurrentUser().getId();
            User user = userRepository.findUserByIdAndDeletedIsFalse(id);
            String captcha1 = redisTemplate.opsForValue().get(StrFormatter.format("user:change:email:id:{}", id));
            if (StrUtil.isBlank(captcha1)) {
                throw new CodeException(CustomerErrorCode.ParamError);
            }
            if (captcha1.equals(email)) {
                user.setEmail(email);
                userRepository.save(user);
                return true;
            } else {
                throw new CodeException(CustomerErrorCode.CaptchaWrong);
            }
        }
    }

}

