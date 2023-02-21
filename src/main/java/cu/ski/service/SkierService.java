package cu.ski.service;

import java.util.List;

import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cu.ski.model.Resort;
import cu.ski.model.Skier;
import cu.ski.repository.SkiRepository;

@Service
public class SkierService {

	@Autowired
	SkiRepository skiRepository;

	public List<Resort> getAllResorts() {
		return skiRepository.findAllResorts();
	}

	public Skier createResort(Skier skier) {
		return skiRepository.save(skier);
	}

	public List<Skier> findByResortIDAndSeasonIDAndDayID(String resortID, String seasonID, String dayID) {
		return skiRepository.findByResortIDAndSeasonIDAndDayID(resortID, seasonID, dayID);
	}

	public List<Skier> findByResortID(String resortID) {
		return skiRepository.findByResortID(resortID);
	}

	public Skier createSeason(Skier skier) {
		return skiRepository.save(skier);
	}

	public Skier newLiftRide(Skier skier) {
		return skiRepository.save(skier);
	}

	public List<Skier> getSkiDayVertical(String resortID, String seasonID, String dayID, String skierID) {
		return skiRepository.findByResortIDAndSeasonIDAndDayIDAndSkierID(resortID, seasonID, dayID, skierID);
	}

	public List<Skier> getSkierVerticalForSeason(String skierID) {
		return skiRepository.findByskierID(skierID);
	}

}
