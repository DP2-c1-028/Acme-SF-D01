
package acme.features.client.progressLog;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.contracts.Contract;
import acme.entities.progress_logs.ProgressLog;
import acme.roles.Client;

@Repository
public interface ClientProgressLogRepository extends AbstractRepository {

	@Query("select p from ProgressLog p where p.contract.id = :id")
	Collection<ProgressLog> findProgressLogsByContractId(int id);

	@Query("select p from ProgressLog p where p.id = :id")
	ProgressLog findProgressLogById(int id);

	@Query("select p from ProgressLog p where p.recordId = :recordId")
	ProgressLog findProgressLogByRecordId(String recordId);

	@Query("select max(p.completeness) from ProgressLog p where p.contract.id = :id")
	double findContractProgressLogWithMaxCompleteness(int id);

	@Query("select c from Client c where c.id = :id")
	Client findClientById(int id);

	@Query("select c from Contract c where c.id = :id")
	Contract findContractById(int id);

}
