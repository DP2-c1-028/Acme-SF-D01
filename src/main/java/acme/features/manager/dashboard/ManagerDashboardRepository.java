
package acme.features.manager.dashboard;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.data.datatypes.Money;
import acme.client.repositories.AbstractRepository;

@Repository
public interface ManagerDashboardRepository extends AbstractRepository {

	/*
	 * @Query("select avg(select count(j) from Job j where j.employer.id = e.id) from Employer e")
	 * Double averageNumberOfJobsPerEmployer();
	 * 
	 * @Query("select avg(select count(a) from Application a where a.worker.id = w.id) from Worker w")
	 * Double averageNumberOfApplicationsPerWorker();
	 * 
	 * @Query("select avg(select count(a) from Application a where exists(select j from Job j where j.employer.id = e.id and a.job.id = j.id)) from Employer e")
	 * Double averageNumberOfApplicationsPerEmployer();
	 * 
	 * @Query("select 1.0 * count(a) / (select count(b) from Application b) from Application a where a.status = acme.entities.jobs.ApplicationStatus.PENDING")
	 * Double ratioOfPendingApplications();
	 * 
	 * @Query("select 1.0 * count(a) / (select count(b) from Application b) from Application a where a.status = acme.entities.jobs.ApplicationStatus.ACCEPTED")
	 * Double ratioOfAcceptedApplications();
	 * 
	 * @Query("select 1.0 * count(a) / (select count(b) from Application b) from Application a where a.status = acme.entities.jobs.ApplicationStatus.REJECTED")
	 * Double ratioOfRejectedApplications();
	 */
	@Query("select count(us) from UserStory us where us.priority = acme.entities.userStories.Priority.MUST and us.manager.id=:managerId and us.draftMode=false")
	int totalNumberOfMust(int managerId);

	@Query("select count(us) from UserStory us where us.priority = acme.entities.userStories.Priority.SHOULD and us.manager.id=:managerId and us.draftMode=false")
	int totalNumberOfShould(int managerId);

	@Query("select count(us) from UserStory us where us.priority = acme.entities.userStories.Priority.COULD and us.manager.id=:managerId and us.draftMode=false")
	int totalNumberOfCould(int managerId);

	@Query("select count(us) from UserStory us where us.priority = acme.entities.userStories.Priority.WONT and us.manager.id=:managerId and us.draftMode=false")
	int totalNumberOfWont(int managerId);

	@Query("select avg(us.estimatedCost) from UserStory us where us.manager.id=:managerId and us.draftMode=false")
	Double averageEstimatedCost(int managerId);

	@Query("select max(us.estimatedCost) from UserStory us where us.manager.id=:managerId and us.draftMode=false")
	Double maximumEstimatedCost(int managerId);

	@Query("select min(us.estimatedCost) from UserStory us where us.manager.id=:managerId and us.draftMode=false")
	Double minimumEstimatedCost(int managerId);

	@Query("select p.cost from Project p where p.manager.id=:managerId and p.draftMode=false")
	Collection<Money> projectCosts(int managerId);

	@Query("select us.estimatedCost from UserStory us where us.manager.id=:managerId and us.draftMode=false")
	Collection<Double> userStoriesEstimatedCosts(int managerId);
}
