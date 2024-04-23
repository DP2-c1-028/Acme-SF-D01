
package acme.features.manager.userStoryProject;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.projects.Project;
import acme.entities.userStories.UserStory;
import acme.entities.userStories.UserStoryProject;

@Repository
public interface ManagerUserStoryProjectRepository extends AbstractRepository {

	@Query("select usp from UserStoryProject usp where usp.project.manager.id=:id")
	Collection<UserStoryProject> findAllRelationsByManager(int id);

	@Query("select usp from UserStoryProject usp where usp.id=:id")
	UserStoryProject findOneUserStoryProjectById(int id);

	@Query("select p from Project p where p.manager.id=:managerId")
	Collection<Project> findAllProjectsOwned(int managerId);

	@Query("select us from UserStory us where us.manager.id=:managerId")
	Collection<UserStory> findAllUserStoriesOwned(int managerId);

	@Query("select usp from UserStoryProject usp where usp.project.id=:projectId and usp.userStory.id=:userStoryId")
	UserStoryProject findOneUserStoryProjectByProjectAndUserStory(int projectId, int userStoryId);
}
