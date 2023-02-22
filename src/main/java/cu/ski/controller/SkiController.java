package cu.ski.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cu.ski.model.Resort;
import cu.ski.model.Skier;
import cu.ski.service.SkierService;

@RestController
@RequestMapping("/api/ski")
public class SkiController {

	@Autowired
	SkierService skierService;

	@GetMapping("/resorts")
	public List<Resort> getAllResorts() {
		return skierService.getAllResorts();
	}

	@PostMapping("/create/resort")
	public Skier createResort(@RequestBody Skier skier) {
		return skierService.createResort(skier);
	}

	@GetMapping("/resorts/{resortID}/seasons/{seasonID}/day/{dayID}/skiers")
	public String findByResortIDAndSeasonIDAndDayID(@PathVariable String resortID, @PathVariable String seasonID,
			@PathVariable String dayID) {

		JSONArray jsonArray = new JSONArray(skierService.findByResortIDAndSeasonIDAndDayID(resortID, seasonID, dayID));
		String numSkiers = Integer.toString(jsonArray.length());
		String resortName = null;
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			resortName = jsonObject.getString("resortName");
		}

		JSONObject resortTotalSkiers = new JSONObject();
		resortTotalSkiers.put("time", resortName);
		resortTotalSkiers.put("numSkiers", numSkiers);
		return resortTotalSkiers.toString();
	}

	@GetMapping("/resorts/{resortID}/seasons")
	public String findByResortID(@PathVariable String resortID) {
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
	}

	@PostMapping("/resorts/{resortID}/seasons")
	public Skier createSeason(@RequestBody String request, @PathVariable String resortID) {

		JSONObject jsonObject = new JSONObject(request);
		String seasonID = jsonObject.getString("year").toString();
		Skier skier = new Skier(resortID, seasonID);

		return skierService.createSeason(skier);
	}

	@PostMapping("/skiers/{resortID}/seasons/{seasonID}/days/{dayID}/skiers/{skierID}")
	public Skier newLiftRide(@RequestBody String request, @PathVariable String resortID, @PathVariable String seasonID,
			@PathVariable String dayID, @PathVariable String skierID) {

		JSONObject jsonObject = new JSONObject(request);
		String time = jsonObject.get("time").toString();
		String liftID = jsonObject.get("liftID").toString();
		Skier skier = new Skier(resortID, skierID, liftID, dayID, seasonID, time);

		return skierService.newLiftRide(skier);
	}

	@GetMapping("/skiers/{resortID}/seasons/{seasonID}/days/{dayID}/skiers/{skierID}")
	public String getSkiDayVertical(@PathVariable String resortID, @PathVariable String seasonID,
			@PathVariable String dayID, @PathVariable String skierID) {
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
	}

	@GetMapping("/skiers/{skierID}/vertical")
	public String getSkierVerticalForSeason(@PathVariable String skierID) {
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
	}

	@GetMapping("/statistics")
	public String performCurl() {
//		http://localhost:8080/actuator/metrics/http.server.requests?tag=uri:/api/ski/resorts
		String curlCommand = "curl http://localhost:8080/actuator/metrics/http.server.requests?tag=uri:/api/ski/resorts";

		try {
			Process process = new ProcessBuilder(curlCommand.split(" ")).start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String obj = reader.lines().collect(Collectors.joining("\n"));
			process.waitFor();
			if (obj != null) {
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
			}
			return "null";
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return "Error occurred";
		}
	}

}
