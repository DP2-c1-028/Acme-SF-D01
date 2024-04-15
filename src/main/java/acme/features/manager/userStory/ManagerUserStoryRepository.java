
package acme.features.manager.userStory;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.userStories.UserStory;

@Repository
public interface ManagerUserStoryRepository extends AbstractRepository {

	@Query("select usp.userStory from UserStoryProject usp where usp.project.id=:id")
	Collection<UserStory> findAllUserStoriesByProjectId(int id);

	@Query("select us from UserStory us where us.id = :id")
	UserStory findOneUserStoryById(int id);

}
