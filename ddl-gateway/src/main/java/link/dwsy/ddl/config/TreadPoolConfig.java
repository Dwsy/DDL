package link.dwsy.ddl.config;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.java.Log;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author Dwsy
 * @Date 2022/12/4
 */
@Configuration
@Log
public class TreadPoolConfig {
    int cpuNums = Runtime.getRuntime().availableProcessors();


    @Bean(value = "ThreadPool")
    public ExecutorService buildHttpApiThreadPool() {
        int corePoolSize = 5;
        log.info("TreadPoolConfig创建线程数:" + corePoolSize + ",最大线程数:" + cpuNums * 2);
        return new ThreadPoolExecutor(corePoolSize, Math.max(corePoolSize * 2, cpuNums * 2), 0L,
                TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(100), new ThreadFactoryBuilder().setNameFormat("PROS-%d").build());
    }
}
