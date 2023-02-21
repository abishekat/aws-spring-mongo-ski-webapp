package cu.ski.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.aggregation.StringOperators.IndexOfBytes.SubstringBuilder;
import org.springframework.util.StringUtils;
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
			String id = jsonObject.getString("seasonID");
			seasonID.add(id);
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
		String year = jsonObject.getString("year").toString();
		Skier skier = new Skier(resortID, year);

		return skierService.createSeason(skier);
	}

}
