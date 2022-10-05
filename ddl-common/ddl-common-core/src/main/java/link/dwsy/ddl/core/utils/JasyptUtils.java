package link.dwsy.ddl.core.utils;

import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEByteEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;


public class JasyptUtils {

    @Value("${jasypt.encryptor.password}")
    private String password;

    /**
     * Jasypt生成加密结果
     *
     * @param password 配置文件中设定的加密密码 jasypt.encryptor.password
     * @param value    待加密值
     */
    public static String encryptPwd(String password, String value) {
        PooledPBEStringEncryptor encryptOr = new PooledPBEStringEncryptor();
        encryptOr.setConfig(cryptOr(password));
        return encryptOr.encrypt(value);
    }

    /**
     * 解密
     *
     * @param password 配置文件中设定的加密密码 jasypt.encryptor.password
     * @param value    待解密密文
     */
    public static String decyptPwd(String password, String value) {
        PooledPBEStringEncryptor encryptOr = new PooledPBEStringEncryptor();
        encryptOr.setConfig(cryptOr(password));
        return encryptOr.decrypt(value);
    }

    /**
     * @param password salt
     */
    public static SimpleStringPBEConfig cryptOr(String password) {
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(password);
        config.setAlgorithm(StandardPBEByteEncryptor.DEFAULT_ALGORITHM);
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setProviderName(null);
//        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setStringOutputType("base64");
        return config;
    }

    public static void main(String[] args) {
        // 加密
        // 盐值替换成自己熟悉的口令，此口令为解密密钥，需要妥善保管。
        // 盐值也需要在第三步写入配置文件
        Map<String, String> env = System.getenv();
        String salt = env.get("DDL_SALT");
//        System.out.println(salt);
        System.out.println(encryptPwd(salt, "rkpO69kR_pMDNUjlppXxq-mTLjhitwQ20YFPOHWf"));
        System.out.println(encryptPwd(salt, "Gv03_Qz9RBT3J2mqGxygRnx0FyNZyuFCHv6ZJMB5"));
        System.out.println(encryptPwd(salt, "dams-demo"));
        System.out.println(encryptPwd(salt, "qiniu.dwsy.link"));

        System.out.println(decyptPwd("17df5675c2ee9097f8f604167fa89f7d", "FLQUNaYejX5rQWBKXiH3Pg=="));
//        String account = "root";
//        BasicTextEncryptor encryptor = new BasicTextEncryptor();
//        //秘钥
//        //encryptor.setPassword(System.getProperty("jasypt.encryptor.password"));
//        encryptor.setPassword(salt);
//        //密码进行加密
//        String newAccount = encryptor.encrypt(account);
//        System.out.println("加密后账号：" + newAccount);
    }

//    public static void main(String[] args) {
//        String account = "root";
//        String password = "123456";
//        BasicTextEncryptor encryptor = new BasicTextEncryptor();
//        //秘钥
//        //encryptor.setPassword(System.getProperty("jasypt.encryptor.password"));
//        encryptor.setPassword("eug83f3gG");
//        //密码进行加密
//        String newAccount = encryptor.encrypt(account);
//        String newPassword = encryptor.encrypt(password);
//        System.out.println("加密后账号：" + newAccount);
//        System.out.println("加密后密码：" + newPassword);
//    }

}