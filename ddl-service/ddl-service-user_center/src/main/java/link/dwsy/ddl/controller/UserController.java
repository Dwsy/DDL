package link.dwsy.ddl.controller;


import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.extra.mail.MailUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import link.dwsy.ddl.XO.RB.UserModifyEmailRB;
import link.dwsy.ddl.XO.VO.UserSettingVO;
import link.dwsy.ddl.annotation.AuthAnnotation;
import link.dwsy.ddl.core.CustomExceptions.CodeException;
import link.dwsy.ddl.core.constant.CustomerErrorCode;
import link.dwsy.ddl.entity.User.User;
import link.dwsy.ddl.repository.User.UserRepository;
import link.dwsy.ddl.support.UserSupport;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Objects;

@RestController
@RequestMapping("user")
public class UserController {

    @Resource
    private  UserSupport userSupport;

    @Resource
    private   UserRepository userRepository;

    @Resource
    private RedisTemplate<String, String> redisTemplate;


    @GetMapping("setting")
    @AuthAnnotation
    @JsonIgnore
    public UserSettingVO getUserSetting() {
        User user = userRepository.findById(userSupport.getCurrentUser().getId())
                .orElseThrow(() -> new CodeException(CustomerErrorCode.UserNotExist));
        UserSettingVO ret = new UserSettingVO();
        ret.setPhone(user.getPhone());
        ret.setEmail(user.getEmail());
        return ret;
    }

    @PostMapping("modify/pwd")
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

    @PostMapping("modify/email")
    @AuthAnnotation
    public int modifyEmail(
            @RequestBody UserModifyEmailRB modifyEmailRB
    ) {
        String code = modifyEmailRB.getCode();
        String email = modifyEmailRB.getEmail();
        boolean captcha = modifyEmailRB.isCaptcha();
        if (captcha) {
            Long id = userSupport.getCurrentUser().getId();
            User user = userRepository.findUserByIdAndDeletedIsFalse(id);
            String e = user.getEmail();
            if (StrUtil.equals(e, email)) {
                throw new CodeException(CustomerErrorCode.EmailEqualsOld);
            }
            if (userRepository.existsByDeletedFalseAndEmail(email)) {
                throw new CodeException(CustomerErrorCode.EmailExists);
            }
            if (StrUtil.isBlank(e)) {
                user.setEmail(email);
                userRepository.save(user);
                return 0;
            }
            int CAPTCHA = RandomUtil.randomInt(10000, 99999);
            System.out.println("captcha" + CAPTCHA);
            //todo 双重验证 旧邮箱发送验证码 后发送链接到新邮箱进行激活
            MailUtil.send(e, "修改邮箱验证",
                    StrFormatter.format(
                            "<h3>验证码:<em style=\"color:red\">{}</em> 有效期15分钟 请误告诉他人</h3>", CAPTCHA), true);
            redisTemplate.opsForValue().set(StrFormatter.format("user:modify:email:id:{}", id), String.valueOf(CAPTCHA));
            return 1;
        } else {
            Long id = userSupport.getCurrentUser().getId();
            User user = userRepository.findUserByIdAndDeletedIsFalse(id);
            String captcha1 = redisTemplate.opsForValue().get(StrFormatter.format("user:modify:email:id:{}", id));
            if (StrUtil.isBlank(captcha1)) {
                throw new CodeException(CustomerErrorCode.ParamError);
            }
            if (captcha1.equals(code)) {
                user.setEmail(email);
                userRepository.save(user);
                redisTemplate.delete(StrFormatter.format("user:modify:email:id:{}", id));
                return 2;
            } else {
                throw new CodeException(CustomerErrorCode.CaptchaWrong);
            }
        }
    }

}

