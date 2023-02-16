package cu.assignment.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cu.assignment.model.AudioItem;
import cu.assignment.repository.AudioRepository;

@Service
public class AudioService {

	@Autowired
	AudioRepository audioRepository;

	public AudioItem createAudioItem(AudioItem audioItem) {

		return audioRepository.save(audioItem);

	}

	public AudioItem getAudioItemById(String id) {
		return audioRepository.findById(id).get();
	}

	public List<AudioItem> findAllArtists() {
		return audioRepository.findAllArtistName();
	}

	public List<AudioItem> getAudioItemByName(String name) {
		return audioRepository.findByName(name);
	}

}
