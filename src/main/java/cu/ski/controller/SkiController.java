package cu.ski.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import cu.ski.model.Resort;
import cu.ski.model.Skier;
import cu.ski.service.SkierService;

@RestController
@RequestMapping("/api/ski")
public class SkiController {

	@Autowired
	SkierService skierService;

	/*
	 * Get a list of ski resorts in the database
	 */
	@GetMapping("/resorts")
	@Retryable(value = { ResponseStatusException.class }, maxAttempts = 5, backoff = @Backoff(delay = 500))
	public List<Resort> getAllResorts() {
		try {
			List<Resort> resorts = skierService.getAllResorts();
			if (resorts == null || resorts.isEmpty()) {
				throw new ResponseStatusException(HttpStatus.NO_CONTENT, "No resorts found");
			}
			return resorts;
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
					"An error occurred while fetching resorts", e);
		}
	}

	/*
	 * Create a skier in the database
	 */
	@PostMapping("/create")
	@Retryable(value = { ResponseStatusException.class }, maxAttempts = 5, backoff = @Backoff(delay = 500))
	public Skier createResort(@RequestBody Skier skier) {
		Skier skiObject;
		try {
			if (skier == null) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Skier object is null");
			}
			skiObject = skierService.createResort(skier);

		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
					"An error occurred while creating resort", e);
		}
		return skiObject;
	}

	/*
	 * Get number of unique skiers at resort/season/day
	 */
	@GetMapping("/resorts/{resortID}/seasons/{seasonID}/day/{dayID}/skiers")
	@Retryable(value = { ResponseStatusException.class }, maxAttempts = 5, backoff = @Backoff(delay = 500))
	public String findByResortIDAndSeasonIDAndDayID(@PathVariable String resortID, @PathVariable String seasonID,
			@PathVariable String dayID) {

		if (resortID == null || resortID.isEmpty() || seasonID == null || seasonID.isEmpty() || dayID == null
				|| dayID.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid path parameters");
		}

		try {
			JSONArray jsonArray = new JSONArray(
					skierService.findByResortIDAndSeasonIDAndDayID(resortID, seasonID, dayID));
			String numSkiers = Integer.toString(jsonArray.length());
			String resortName = null;
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				if (jsonObject.has(resortName)) {
					resortName = jsonObject.getString("resortName");
				}
			}

			JSONObject resortTotalSkiers = new JSONObject();
			resortTotalSkiers.put("time", resortName);
			resortTotalSkiers.put("numSkiers", numSkiers);
			return resortTotalSkiers.toString();
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
					"An error occurred while fetching unique skiers", e);
		}
	}

	/*
	 * Get a list of seasons for the specified resort
	 */
	@GetMapping("/resorts/{resortID}/seasons")
	@Retryable(value = { ResponseStatusException.class }, maxAttempts = 5, backoff = @Backoff(delay = 500))
	public String findByResortID(@PathVariable String resortID) {

		if (resortID == null || resortID.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid path parameter");
		}

		try {
			JSONArray jsonArray = new JSONArray(skierService.findByResortID(resortID));
			List<String> seasonID = new ArrayList<String>();
			Set<String> seasonIDSet = new HashSet<>();
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				if (jsonObject.has("seasonID")) {
					String id = jsonObject.getString("seasonID");
					seasonID.add(id);
				}
			}
			for (String id : seasonID) {
				seasonIDSet.add(id);
			}

			List<String> uniqueSeasonID = new ArrayList<>(seasonIDSet);

			JSONArray seasonsArray = new JSONArray();
			for (String id : uniqueSeasonID) {
				seasonsArray.put(id);
			}
			JSONObject seasonObj = new JSONObject();
			seasonObj.put("seasons", seasonsArray);
			return seasonObj.toString();
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
					"An error occurred while fetching seasons for specific resort", e);
		}
	}

	/*
	 * Add a new season for a resort
	 */
	@PostMapping("/resorts/{resortID}/seasons")
	@Retryable(value = { ResponseStatusException.class }, maxAttempts = 5, backoff = @Backoff(delay = 500))
	public Skier createSeason(@RequestBody String request, @PathVariable String resortID) {
		if (resortID == null || resortID.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid path parameter");
		}

		try {
			JSONObject jsonObject = new JSONObject(request);
			String seasonID = jsonObject.getString("year").toString();
			Skier skier = new Skier(resortID, seasonID);

			return skierService.createSeason(skier);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
					"An error occurred while adding new sesason", e);
		}
	}

	/*
	 * Create a new lift ride for the skier
	 * 
	 * Uses ConcurrentLinkedQueue for generated in a single dedicated thread and be
	 * made available to the threads that make API calls.
	 */
	private static ConcurrentLinkedQueue<Skier> eventQueue = new ConcurrentLinkedQueue<>();

	@PostMapping("/skiers/{resortID}/seasons/{seasonID}/days/{dayID}/skiers/{skierID}")
	@Retryable(value = { ResponseStatusException.class }, maxAttempts = 5, backoff = @Backoff(delay = 500))
	public Skier newLiftRide(@RequestBody String request, @PathVariable String resortID, @PathVariable String seasonID,
			@PathVariable String dayID, @PathVariable String skierID) {

		if (resortID == null || resortID.isEmpty() || seasonID == null || seasonID.isEmpty() || dayID == null
				|| dayID.isEmpty() || skierID == null || skierID.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid path parameter");
		}
		try {
			JSONObject jsonObject = new JSONObject(request);
			String time = jsonObject.get("time").toString();
			String liftID = jsonObject.get("liftID").toString();
			Skier skier = new Skier(resortID, skierID, liftID, dayID, seasonID, time);
			eventQueue.add(skier);

			return skier;
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
					"An error occurred while creating lift ride event", e);
		}
	}

	/*
	 * Polls the eventQueue : checks and sends event and decrements its index
	 */
	private final Thread eventThread = new Thread(() -> {
		while (true) {
			Skier event = eventQueue.poll();
			if (event != null) {
				skierService.newLiftRide(event);
			}
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	});

	/*
	 * Get ski day vertical for a skier
	 */
	@GetMapping("/skiers/{resortID}/seasons/{seasonID}/days/{dayID}/skiers/{skierID}")
	@Retryable(value = { ResponseStatusException.class }, maxAttempts = 5, backoff = @Backoff(delay = 500))
	public String getSkiDayVertical(@PathVariable String resortID, @PathVariable String seasonID,
			@PathVariable String dayID, @PathVariable String skierID) {

		if (resortID == null || resortID.isEmpty() || seasonID == null || seasonID.isEmpty() || dayID == null
				|| dayID.isEmpty() || skierID == null || skierID.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid path parameter");
		}

		try {
			JSONArray jsonArray = new JSONArray(skierService.getSkiDayVertical(resortID, seasonID, dayID, skierID));
			Long id = (long) 0;
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				if (jsonObject.has("vertical")) {
					id = Long.parseLong(jsonObject.get("vertical").toString());
					id += id;
				}
			}
			return id.toString();
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
					"An error occurred while getting skier vertical for a day", e);
		}
	}

	/*
	 * Get the total vertical for the skier for specified seasons at the specified
	 * resort
	 */
	@GetMapping("/skiers/{skierID}/vertical")
	@Retryable(value = { ResponseStatusException.class }, maxAttempts = 5, backoff = @Backoff(delay = 500))
	public String getSkierVerticalForSeason(@PathVariable String skierID) {

		if (skierID == null || skierID.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid path parameter");
		}

		try {
			JSONArray jsonArray = new JSONArray(skierService.getSkierVerticalForSeason(skierID));

			JSONObject output = new JSONObject();

			JSONArray resortGroups = new JSONArray();

			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject object = jsonArray.getJSONObject(i);
				String resortID = object.getString("resortID");
				int vertical = object.optInt("vertical", 0);
				JSONObject seasonIDTotalVertical = new JSONObject();
				boolean found = false;
				for (int j = 0; j < resortGroups.length(); j++) {
					JSONObject resortObject = resortGroups.getJSONObject(j);
					if (resortObject.getString("resortID").equals(resortID)) {
						JSONArray seasonIDTotalVerticalArray = resortObject.getJSONArray("seasonIDTotalVertical");
						boolean foundSeasonID = false;
						for (int k = 0; k < seasonIDTotalVerticalArray.length(); k++) {
							JSONObject totalVerticalObject = seasonIDTotalVerticalArray.getJSONObject(k);
							if (totalVerticalObject.getString("seasonID").equals(object.getString("seasonID"))) {
								int total = totalVerticalObject.getInt("totalVertical") + vertical;
								totalVerticalObject.put("totalVertical", total);
								foundSeasonID = true;
								break;
							}
						}
						if (!foundSeasonID) {
							seasonIDTotalVertical.put("seasonID", object.getString("seasonID"));
							seasonIDTotalVertical.put("totalVertical", vertical);
							seasonIDTotalVerticalArray.put(seasonIDTotalVertical);
						}
						found = true;
						break;
					}
				}
				if (!found) {
					JSONObject newResortObject = new JSONObject();
					newResortObject.put("resortID", resortID);
					JSONArray seasonIDTotalVerticalArray = new JSONArray();
					seasonIDTotalVertical.put("seasonID", object.getString("seasonID"));
					seasonIDTotalVertical.put("totalVertical", vertical);
					seasonIDTotalVerticalArray.put(seasonIDTotalVertical);
					newResortObject.put("seasonIDTotalVertical", seasonIDTotalVerticalArray);
					resortGroups.put(newResortObject);
				}
			}

			output.put("resorts", resortGroups);

			return output.toString();
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
					"An error occurred while getting total vertical for the resort", e);
		}
	}

	/*
	 * Get the API performance statistics: uses sprig-boot-actuator dependency to
	 * get the metrics.
	 */
	@GetMapping("/statistics")
	@Retryable(value = { ResponseStatusException.class }, maxAttempts = 5, backoff = @Backoff(delay = 500))
	public String performCurl() {
//		http://localhost:8080/actuator/metrics/http.server.requests?tag=uri:/api/ski/resorts
		String curlCommand = "curl http://concordia.abishekarumugam.com/actuator/metrics/http.server.requests?tag=uri:/api/ski/resorts";

		try {
			Process process = new ProcessBuilder(curlCommand.split(" ")).start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String obj = reader.lines().collect(Collectors.joining("\n"));
			if (obj == null || obj.isEmpty()) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Response");
			}
			process.waitFor();
			JSONObject jsonObj = new JSONObject(obj);
			JSONArray jsonArray = new JSONArray(jsonObj.get("measurements").toString());
			int count = 0;
			double totalTime = 0;
			double max = 0;
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				String statistic = jsonObject.getString("statistic");
				double value = jsonObject.getDouble("value");
				if (statistic.equals("COUNT")) {
					count = (int) value;
				} else if (statistic.equals("TOTAL_TIME")) {
					totalTime = value;
				} else if (statistic.equals("MAX")) {
					max = value;
				}
			}
			JSONObject output = new JSONObject();
			output.put("URL", "/resorts");
			output.put("operation", "GET");
			output.put("count", count);
			output.put("totaltime", totalTime);
			output.put("max", max);
			JSONArray outputArray = new JSONArray();
			outputArray.put(output);
			JSONObject newOutputObj = new JSONObject();
			newOutputObj.put("endpointStats", outputArray);
			return newOutputObj.toString();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return "Error occurred";
		}
	}

	/*
	 * To start the thread: as init() to poll eventQueue.
	 */
	@PostConstruct
	public void init() {
		eventThread.start();
	}
}
