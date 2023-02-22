package cu.ski.utils;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RestfulWebServicesApplicationTests {

	private TestRestTemplate restTemplate = new TestRestTemplate();
	private static final int THREADS = 32;
	private static final int POSTS = 1000;

	@Test
	public void testConcurrentRequests() throws InterruptedException, ExecutionException {
		ExecutorService executorService = Executors.newFixedThreadPool(THREADS);
		long startTime = System.currentTimeMillis();
		List<Callable<Integer>> tasks = new ArrayList<>();
		for (int i = 0; i < THREADS; i++) {
			tasks.add(() -> {
				int count = 0;
				for (int j = 0; j < POSTS; j++) {
					String skierID = String.valueOf(ThreadLocalRandom.current().nextInt(1, 100001));
					String resortID = String.valueOf(ThreadLocalRandom.current().nextInt(1, 11));
					String liftID = String.valueOf(ThreadLocalRandom.current().nextInt(1, 41));
					String seasonID = "2022";
					String dayID = "1";
					String time = String.valueOf(ThreadLocalRandom.current().nextInt(1, 361));

					String body = String.format(
							"{\"resortID\":\"%s\",\"seasonID\":\"%s\",\"dayID\":\"%s\",\"skierID\":\"%s\",\"liftID\":\"%s\",\"time\":\"%s\"}",
							resortID, seasonID, dayID, skierID, liftID, time);
					System.out.println(":::::::::::::::::");

					HttpHeaders headers = new HttpHeaders();
					headers.setContentType(MediaType.APPLICATION_JSON);

					HttpEntity<String> request = new HttpEntity<>(body, headers);
					ResponseEntity<String> response = restTemplate.exchange(
							"http://localhost:8080/api/ski/create/resort", HttpMethod.POST, request, String.class);
					System.out.println(":::::::::::::::::");
					count++;
				}
				return count;
			});
		}

		List<Future<Integer>> results = executorService.invokeAll(tasks);

		int totalRequests = 0;
		for (Future<Integer> result : results) {
			totalRequests += result.get();
		}

		executorService.shutdown();
		long endTime = System.currentTimeMillis();
		long totalDuration = endTime - startTime;
		long totalThroughput = (TimeUnit.MILLISECONDS.toSeconds(totalDuration) % 60) / 32000;
		System.out.println("totalDuration ::: " + totalDuration + "::: total throughput :::" + totalThroughput);
		System.out.println(":::::::totalRequests::::::::::" + totalRequests);
		assertEquals(32000, totalRequests);
	}
}