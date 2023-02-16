package cu.assignment.utils;

import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import cu.assignment.repository.AudioRepository;

@SpringBootApplication
@EnableMongoRepositories("cu.assignment.repository")
@ComponentScan("cu.assignment.*")
@EnableAsync
public class ServerlessSpringApplication {

	@Autowired
	AudioRepository audioRepository;

	@Bean("threadPoolTaskExecutor")
	public Executor threadPoolTaskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(4);
		executor.setMaxPoolSize(4);
		executor.setThreadNamePrefix("default_task_executor_thread");
		executor.initialize();
		return executor;
	}

	public static void main(String[] args) {
		SpringApplication.run(ServerlessSpringApplication.class, args);
	}

}
