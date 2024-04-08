
package acme.features.client.progressLog;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.progress_logs.ProgressLog;

@Repository
public interface ClientProgressLogRepository extends AbstractRepository {

	@Query("Select pl from ProgressLog pl where pl.client.id = :id")
	Collection<ProgressLog> findProgressLogsByClientId(int id);

	@Query("Select pl from ProgressLog pl where pl.id = :id")
	ProgressLog findProgressLogById(int id);

}
