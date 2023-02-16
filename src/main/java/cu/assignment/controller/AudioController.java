package cu.assignment.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cu.assignment.model.AudioItem;
import cu.assignment.service.AudioService;

@RestController
@RequestMapping("/api/audio")
public class AudioController {
	@Autowired
	AudioService audioService;
	
	@PostMapping("/create")
	public AudioItem createAudioItem(@RequestBody AudioItem audioItem) {
		return audioService.createAudioItem(audioItem);
	}

	@GetMapping("/getById/{id}")
	public AudioItem getAudioItemById(@PathVariable String id) {
		return audioService.getAudioItemById(id);
	}
	
	@GetMapping("/getAllArtists")
	public List<AudioItem> findAllArtists() {
		return audioService.findAllArtists();
	}
	
	@GetMapping("/audioItemByName/{name}")
	public List<AudioItem> getAudioItemByName(@PathVariable String name) {
		return audioService.getAudioItemByName(name);
	}
	
}
