package link.dwsy.ddl;


import cn.hutool.extra.mail.MailUtil;

public class mailTest {
    public static void main(String[] args) {
        MailUtil.send("", "测试", "邮件来自Hutool测试", false);
    }
}
