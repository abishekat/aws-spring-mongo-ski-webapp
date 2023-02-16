package cu.assignment.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import cu.assignment.model.AudioItem;

@Repository
public interface AudioRepository extends MongoRepository<AudioItem, String> {
	@Query("{}")
	List<AudioItem> findAllArtistName();
	
	@Query("{'artistName':'?0'}")
	List<AudioItem> findByName(String artistName);
}
