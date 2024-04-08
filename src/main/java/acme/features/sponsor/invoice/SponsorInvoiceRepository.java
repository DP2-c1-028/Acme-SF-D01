
package acme.features.sponsor.invoice;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.invoices.Invoice;

@Repository
public interface SponsorInvoiceRepository extends AbstractRepository {

	@Query("select i from Invoice i where i.id=:id")
	Collection<Invoice> findAllInvoicesBySponsorshipId(int id);

	@Query("select i from Invoice i where i.id = :id")
	Invoice findOneInvoiceById(int id);

}
