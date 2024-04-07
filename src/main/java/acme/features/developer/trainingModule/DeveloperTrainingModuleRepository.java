
package acme.features.developer.trainingModule;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.training_modules.TrainingModule;
import acme.entities.training_modules.TrainingSession;
import acme.roles.Developer;

@Repository
public interface DeveloperTrainingModuleRepository extends AbstractRepository {

	@Query("SELECT tm FROM TrainingModule tm WHERE tm.developer.id = :id")
	Collection<TrainingModule> findTrainingModulesByDeveloperId(int id);

	@Query("SELECT tm FROM TrainingModule tm WHERE tm.id = :id")
	TrainingModule findOneTrainingModuleById(int id);

	@Query("SELECT dev FROM Developer dev WHERE dev.id = :id")
	Developer findOneDeveloperById(int id);

	@Query("SELECT ts FROM TrainingSession ts WHERE ts.trainingModule.id = :id")
	Collection<TrainingSession> findTrainingSessionsByTrainingModuleId(int id);
}
