
package acme.features.developer.trainingSession;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.training_modules.TrainingModule;
import acme.entities.training_modules.TrainingSession;
import acme.roles.Developer;

@Repository
public interface DeveloperTrainingSessionRepository extends AbstractRepository {

	@Query("SELECT ts FROM TrainingSession ts WHERE ts.trainingModule.id = :id")
	Collection<TrainingSession> findTrainingSessionsByTrainingModuleId(int id);

	@Query("SELECT tm FROM TrainingModule tm WHERE tm.id = :id")
	TrainingModule findOneTrainingModuleById(int id);

	@Query("SELECT ts FROM TrainingSession ts WHERE ts.id = :id")
	TrainingSession findTrainingSessionById(int id);

	@Query("SELECT dev FROM Developer dev WHERE dev.id = :id")
	Developer findOneDeveloperById(int id);

	@Query("SELECT ts FROM TrainingSession ts WHERE ts.code = :code")
	TrainingSession findOneTrainingSessionByCode(String code);
}
