package com.sharpenedhacks.apigateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;

/**
 * Spring Boot Main.
 */
@Configuration
@ComponentScan
@EnableAutoConfiguration
public class AppMain {

    private static final Logger LOG = LoggerFactory.getLogger(AppMain.class);
    private static final int THREADS_PER_PROCESSOR = 10;
    //Setting to a higher timeout as external API can get return slow responses...
    private static final int EXTERNAL_HTTP_TIMEOUT = 10 * 1000;

    @Autowired
    private Environment env;

    private ThreadPoolTaskExecutor taskExecutor;

    @Bean
    public AsyncRestTemplate asyncRestTemplate() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        //ensure that configured taskExecutor is associated with AsyncRestTemplate
        requestFactory.setTaskExecutor((AsyncListenableTaskExecutor) taskExecutor());
        requestFactory.setReadTimeout(EXTERNAL_HTTP_TIMEOUT);
        requestFactory.setConnectTimeout(EXTERNAL_HTTP_TIMEOUT);
        return new AsyncRestTemplate(requestFactory, new RestTemplate(requestFactory));
    }

    /**
     * Custom configuration of Thread Pool based on number of processors and Spring properties.
     */
    @Bean
    public TaskExecutor taskExecutor() {
        if (taskExecutor != null) {
            return taskExecutor;
        }
        taskExecutor = new ThreadPoolTaskExecutor();
        int poolSize = Runtime.getRuntime().availableProcessors() * env.getProperty("batch.core.threadsCount", Integer.class, THREADS_PER_PROCESSOR);
        taskExecutor.setCorePoolSize(poolSize);
        LOG.info("Created taskExecutor thread pool size " + taskExecutor.getCorePoolSize());
        return taskExecutor;
    }

    public static void main(String[] args) {
        SpringApplication.run(AppMain.class, args);
    }

}
