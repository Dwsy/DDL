//package link.dwsy.ddl.demo;
//
//import link.dwsy.ddl.demo.entity.User;
//import link.dwsy.ddl.demo.repository.UserRepository;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import javax.annotation.Resource;
//
///**
// * @Author Dwsy
// * @Date 2022/8/16
// */
//@SpringBootTest
//public class tuserTest {
//    @Resource
//    private UserRepository userRepository;
//
//    @Test
//    public void test() {
//        User u = User.builder()
//                .username("Dwsy")
//                .password("123").build();
////                .area("+86")
////                .phone("12345678912")
////                .email("1@1.1").build();
//        userRepository.save(u);
//        User u1 = User.builder()
//                .username("Dwsy1")
//                .password("123")
//                .area("+86")
//                .phone("12345678912")
//                .email("1@1.1").build();
//        userRepository.save(u1);
//        User u2 = User.builder()
//                .username("Dwsy1")
//                .password("123")
//                .area("+86")
//                .phone("12345678912")
//                .email("1@1.1").build();
//        userRepository.save(u2);
//        userRepository.findAll().forEach(System.out::println);
//    }
////    @Resource
////    private tuserMapper tmp;
////    @Test
////    public void test1(){
////        tmp.selectList(null).forEach(System.out::println);
////        tuser t = new tuser();
////        t.setUsername("dwsy");
////        t.setExtraInfo("oo");
////        t.setPassword("111");
////        tmp.insert(t);
//}
//
//
