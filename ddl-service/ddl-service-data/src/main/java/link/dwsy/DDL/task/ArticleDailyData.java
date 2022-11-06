package link.dwsy.DDL.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@Slf4j
public class ArticleDailyData {

    @Scheduled(cron = "*/15 * * * * ?")
    public void execute() {
        log.info("thread id:{},FixedPrintTask execute times", Thread.currentThread().getId());
    }
}
