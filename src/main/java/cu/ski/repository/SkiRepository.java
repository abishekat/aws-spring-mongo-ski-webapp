package cu.ski.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import cu.ski.model.Resort;
import cu.ski.model.Skier;

@Repository
public interface SkiRepository extends MongoRepository<Skier, String> {
	@Query("{}")
	List<Resort> findAllResorts();

	List<Skier> findByResortIDAndSeasonIDAndDayID(String resortID, String seasonID, String dayID);

	List<Skier> findByResortID(String resortID);

}
