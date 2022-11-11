package link.dwsy.ddl.controller;

import link.dwsy.ddl.XO.RB.TagIdsRB;
import link.dwsy.ddl.annotation.AuthAnnotation;
import link.dwsy.ddl.entity.User.User;
import link.dwsy.ddl.entity.User.UserTag;
import link.dwsy.ddl.repository.User.UserRepository;
import link.dwsy.ddl.repository.User.UserTagRepository;
import link.dwsy.ddl.support.UserSupport;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("tag")
public class UserTagController {

    @Resource
    private UserRepository userRepository;
    @Resource
    private UserSupport userSupport;

    @Resource
    private UserTagRepository tagRepository;


    @PostMapping()
    @AuthAnnotation
    public void updateUserTag(@RequestBody TagIdsRB tagIdsRB) {
        Set<Long> tagIds = tagIdsRB.getTagIds();
        Long userId = userSupport.getCurrentUser().getId();
        User user = userRepository.findUserByIdAndDeletedIsFalse(userId);
        List<UserTag> tagList = tagRepository.findByDeletedFalseAndIdIn(tagIds);
        user.setUserTags(tagList);
        userRepository.save(user);
    }

    @PostMapping("user")
    public List<User> getUserByTagId(
            @RequestBody TagIdsRB tagIdsRB
    ) {
        Set<Long> tagIds = tagIdsRB.getTagIds();
        ArrayList<Long> userId = tagRepository.finduserByTagIdIn(tagIds);
        return userRepository.findAllById(userId);
    }

    @GetMapping("user/{id}")
    public List<UserTag> getUserTagByUserId(@PathVariable long id) {
        User user = userRepository.findUserByIdAndDeletedIsFalse(id);
        return user.getUserTags();
    }
}
