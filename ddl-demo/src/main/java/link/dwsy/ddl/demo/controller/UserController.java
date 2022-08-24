package link.dwsy.ddl.demo.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

import link.dwsy.ddl.demo.entity.User;
import link.dwsy.ddl.demo.repository.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * @Author Dwsy
 * @Date 2022/8/24
 */
@Api(tags = "TEST 用户")
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserRepository userRepository;


    @GetMapping("/list")
    @ApiOperation(value = "getList")
    public List<User> getList(){
        return userRepository.findAll();
    }

    @GetMapping("/id")
    @ApiImplicitParam(name = "id",value  = "用户id",required = true)
    @ApiOperation(value = "getListById")
    public User getListById(@RequestParam(value = "id") Long id) {

        return userRepository.findById(id).get();
    }
}
