
package acme.features.sponsor.invoice;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.invoices.Invoice;
import acme.entities.sponsorships.Sponsorship;
import acme.roles.Sponsor;

@Repository
public interface SponsorInvoiceRepository extends AbstractRepository {

	@Query("select i from Invoice i where i.sponsorship.id=:id")
	Collection<Invoice> findAllInvoicesBySponsorshipId(int id);

	@Query("select i from Invoice i where i.id = :id")
	Invoice findOneInvoiceById(int id);

	@Query("select i from Sponsor i where i.id = :id")
	Sponsor findOneSponsorById(int id);

	@Query("select s from Invoice s where s.code=:code")
	Invoice findOneInvoiceByCode(String code);

	@Query("select i from Sponsorship i where i.id = :id")
	Sponsorship findOneSponsorshipById(int id);

}
