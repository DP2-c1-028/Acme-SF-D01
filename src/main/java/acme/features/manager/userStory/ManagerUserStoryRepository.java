
package acme.features.manager.userStory;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.projects.Project;
import acme.entities.userStories.UserStory;
import acme.entities.userStories.UserStoryProject;
import acme.roles.Manager;

@Repository
public interface ManagerUserStoryRepository extends AbstractRepository {

	@Query("select usp.userStory from UserStoryProject usp where usp.project.id=:id")
	Collection<UserStory> findAllUserStoriesByProjectId(int id);

	@Query("select usp from UserStoryProject usp where usp.userStory.id=:id")
	Collection<UserStoryProject> findAllRelationsByUserStoryId(int id);

	@Query("select us from UserStory us where us.id = :id")
	UserStory findOneUserStoryById(int id);

	@Query("select m from Manager m where m.id = :id")
	Manager findOneManagerById(int id);

	@Query("select p from Project p where p.id = :id")
	Project findOneProjectById(int id);

}
