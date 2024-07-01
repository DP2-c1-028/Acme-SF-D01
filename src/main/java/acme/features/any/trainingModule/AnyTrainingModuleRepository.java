
package acme.features.any.trainingModule;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.training_modules.TrainingModule;

@Repository
public interface AnyTrainingModuleRepository extends AbstractRepository {

	@Query("SELECT tm FROM TrainingModule tm WHERE tm.published = true")
	Collection<TrainingModule> findPublishedTrainingModules();

	@Query("SELECT tm FROM TrainingModule tm WHERE tm.id = :id")
	TrainingModule findOneTrainingModuleById(int id);
}
