package cu.ski.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import cu.ski.model.Skier;

@Repository
public interface SkiRepository extends MongoRepository<Skier, String> {

}
