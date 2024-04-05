
package acme.features.manager.project;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.projects.Project;
import acme.roles.Manager;

@Repository
public interface ManagerProjectRepository extends AbstractRepository {

	@Query("select p from Project p where p.manager.id = :id")
	Collection<Project> findProjectsByManagerId(int id);

	@Query("select p from Project p where p.id = :id")
	Project findOneProjectById(int id);

	@Query("select m from Manager m where m.id = :id")
	Manager findOneManagerById(int id);

}
