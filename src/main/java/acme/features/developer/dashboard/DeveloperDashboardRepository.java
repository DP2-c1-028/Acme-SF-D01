
package acme.features.developer.dashboard;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface DeveloperDashboardRepository extends AbstractRepository {

	@Query("SELECT tm.totalTime FROM TrainingModule tm WHERE tm.developer.id = :developerId")
	Collection<Integer> findTrainingModuleTotalTimesByDeveloperId(int developerId);

	@Query("SELECT COUNT(tm) FROM TrainingModule tm WHERE tm.updateMoment IS NOT NULL AND tm.developer.id = :developerId")
	int countTrainingModulesWithUpdateMomentByDeveloperId(int developerId);

	@Query("SELECT COUNT(ts) FROM TrainingSession ts WHERE ts.link IS NOT NULL AND ts.trainingModule.developer.id = :developerId")
	int countTrainingSessionsWithLinkByDeveloperId(int developerId);

}
