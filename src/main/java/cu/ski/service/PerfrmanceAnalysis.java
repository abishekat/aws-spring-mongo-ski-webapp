package cu.ski.service;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import cu.ski.model.LatencyRecord;

public class PerfrmanceAnalysis {

//	public static void main(String[] args)
//			throws CsvValidationException, NumberFormatException, FileNotFoundException, IOException {
//		main();
//	}

	/*
	 * Measures the performance of the GET and POST form the CSV generated from test
	 * cases.
	 */
	public static String main()
			throws FileNotFoundException, IOException, CsvValidationException, NumberFormatException {
		List<LatencyRecord> latencyRecords = new ArrayList<>();
		try (CSVReader reader = new CSVReader(new FileReader("post-performance.csv"))) {
			String[] line;
			while ((line = reader.readNext()) != null) {
				latencyRecords.add(new LatencyRecord(Long.parseLong(line[0]), line[1], Long.parseLong(line[2]),
						Integer.parseInt(line[3])));
			}
		}

		double sum = 0;
		long min = 0;
		long max = 0;
		List<Long> latencies = new ArrayList<>();
		for (LatencyRecord record : latencyRecords) {
			long latency = record.getLatency();
			sum += latency;
			min = Math.min(min, latency);
			max = Math.max(max, latency);
			latencies.add(latency);
		}
		double mean = sum / latencyRecords.size();
		long median = getMedian(latencies);
		long p99 = getPercentile(latencies, 0.99);

		long totalDuration = TimeUnit.MILLISECONDS.toSeconds(
				latencyRecords.get(latencyRecords.size() - 1).getStartTime() - latencyRecords.get(0).getStartTime());
		double throughput = (double) latencyRecords.size() / (double) totalDuration;

		JSONObject stats = new JSONObject();
		stats.put("mean", mean);
		stats.put("median", median);
		stats.put("throughput", throughput);
		stats.put("p99", p99);
		stats.put("min", min);
		stats.put("max", max);
		return stats.toString();

	}

	private static long getMedian(List<Long> list) {
		Collections.sort(list);
		int middle = list.size() / 2;
		return list.get(middle);
	}

	private static long getPercentile(List<Long> list, double percentile) {
		Collections.sort(list);
		int index = (int) Math.ceil(percentile * list.size()) - 1;
		return list.get(index);
	}

}
