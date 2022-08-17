package com.structure.fi.cryptoexchange;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@SpringBootApplication
@EnableAsync
public class CryptoExchangeApplication {
	@Value("${executor.threadpool.size}")
	private int poolSize;
	@Value("${executor.threadpool.queuecapacity}")
	private int queueCapacity;

	@Bean(name = "startAsynProcessExecutor")
	public TaskExecutor workExecutor(){
		ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
		threadPoolTaskExecutor.setThreadNamePrefix("Async-");
		threadPoolTaskExecutor.setCorePoolSize(poolSize);
		threadPoolTaskExecutor.setMaxPoolSize(poolSize);
		threadPoolTaskExecutor.setQueueCapacity(queueCapacity);
		threadPoolTaskExecutor.afterPropertiesSet();
		return threadPoolTaskExecutor;
	}
	public static void main(String[] args) {
		SpringApplication.run(CryptoExchangeApplication.class, args);
	}
}