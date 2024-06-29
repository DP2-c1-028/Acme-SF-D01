
package acme.features.developer.dashboard;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface DeveloperDashboardRepository extends AbstractRepository {

	@Query("SELECT COUNT(tm) FROM TrainingModule tm WHERE tm.updateMoment IS NOT NULL AND tm.developer.id = :developerId AND tm.published = true")
	int countTrainingModulesWithUpdateMomentByDeveloperId(int developerId);

	@Query("SELECT COUNT(ts) FROM TrainingSession ts WHERE ts.link IS NOT NULL AND ts.link NOT LIKE '' AND ts.trainingModule.developer.id = :developerId AND ts.published = true")
	int countTrainingSessionsWithLinkByDeveloperId(int developerId);

	@Query("SELECT AVG(tm.totalTime) FROM TrainingModule tm WHERE tm.developer.id = :developerId AND tm.published = true")
	Double averageTimesByDeveloperId(int developerId);

	@Query("SELECT SQRT((SUM(tm.totalTime * tm.totalTime) / COUNT(tm.totalTime)) - (AVG(tm.totalTime) * AVG(tm.totalTime))) FROM TrainingModule tm WHERE tm.developer.id = :developerId AND tm.published = true")
	Double deviatonByDeveloperId(int developerId);

	@Query("SELECT MAX(tm.totalTime) FROM TrainingModule tm WHERE tm.developer.id = :developerId AND tm.published = true")
	Integer maximumTimeByDeveloperId(int developerId);

	@Query("SELECT MIN(tm.totalTime) FROM TrainingModule tm WHERE tm.developer.id = :developerId AND tm.published = true")
	Integer minimumTimeByDeveloperId(int developerId);

}
