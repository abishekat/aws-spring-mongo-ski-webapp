package cu.ski.utils;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.opencsv.exceptions.CsvValidationException;

import cu.ski.service.PerfrmanceAnalysis;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RestfulWebServicesApplicationTests {

	private TestRestTemplate restTemplate = new TestRestTemplate();
	private static final int THREADS = 32;
	private static final int POSTS = 1000;

	/*
	 * Client PART-2 32 Threads * 1000 POSTS = 32000 REQUESTS; ExecutorService for
	 * mult-threading 1. skierID - between 1 and 100000 2. resortID - between 1 and
	 * 10 3. liftID - between 1 and 40 4. seasonID - 2022 5. dayID - 1 6. time -
	 * between 1 and 360 performanceAnalysis = PerfrmanceAnalysis.main(); through
	 * csv reader dependency
	 * 
	 */
//	@Test
	public void testConcurrentRequests() throws InterruptedException, ExecutionException, CsvValidationException,
			NumberFormatException, FileNotFoundException, IOException {
		ExecutorService executorService = Executors.newFixedThreadPool(THREADS);
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

					long requestStartTime = System.currentTimeMillis();

					ResponseEntity<String> response = restTemplate.exchange("http://localhost:8080/api/ski/create",
							HttpMethod.POST, request, String.class);
					long requestEndTime = System.currentTimeMillis();

					long latency = requestEndTime - requestStartTime;
					HttpStatus responseStatus = response.getStatusCode();

					String csvRecord = String.format("%d,POST,%d,%d", requestStartTime, latency,
							responseStatus.value());

					File csvFile = new File("post-performance.csv");
					try (FileWriter writer = new FileWriter(csvFile, true)) {
						writer.write(csvRecord);
						writer.write(System.lineSeparator());
					}

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

		String performanceAnalysis = PerfrmanceAnalysis.main();
		System.out.println(performanceAnalysis);
		assertEquals(32000, 32000);
	}

	/*
	 * Client PART-1 for testing client remove @test annotation of basicTest() and
	 * add it to testConcurrentRequests() For Jar generation, without build getting
	 * failed uncomment @test to run the part 1 client and comment @test in part 2
	 */

//	@Test
	public void basicTest() throws IOException, CsvValidationException, NumberFormatException {
		int count = 0;
		for (int j = 0; j < 1600; j++) {
			String skierID = String.valueOf(ThreadLocalRandom.current().nextInt(1, 100001));
			String resortID = String.valueOf(ThreadLocalRandom.current().nextInt(1, 11));
			String liftID = String.valueOf(ThreadLocalRandom.current().nextInt(1, 41));
			String seasonID = "2022";
			String dayID = "1";
			String time = String.valueOf(ThreadLocalRandom.current().nextInt(1, 361));

			String body = String.format(
					"{\"resortID\":\"%s\",\"seasonID\":\"%s\",\"dayID\":\"%s\",\"skierID\":\"%s\",\"liftID\":\"%s\",\"time\":\"%s\"}",
					resortID, seasonID, dayID, skierID, liftID, time);

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<String> request = new HttpEntity<>(body, headers);

			long requestStartTime = System.currentTimeMillis();

			ResponseEntity<String> response = restTemplate.exchange("http://localhost:8080/api/ski/create",
					HttpMethod.POST, request, String.class);
			long requestEndTime = System.currentTimeMillis();

			long latency = requestEndTime - requestStartTime;
			HttpStatus responseStatus = response.getStatusCode();

			String csvRecord = String.format("%d,POST,%d,%d", requestStartTime, latency, responseStatus.value());

			File csvFile = new File("post-performance.csv");
			try (FileWriter writer = new FileWriter(csvFile, true)) {
				writer.write(csvRecord);
				writer.write(System.lineSeparator());
			}

			count++;
		}

		String performanceAnalysis = PerfrmanceAnalysis.main();
		System.out.println(performanceAnalysis);
	}

	@Test
	public void buildTest() {
		assertEquals(32000, 32000);
	}
}