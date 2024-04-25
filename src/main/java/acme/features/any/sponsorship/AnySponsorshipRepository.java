
package acme.features.any.sponsorship;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.invoices.Invoice;
import acme.entities.projects.Project;
import acme.entities.sponsorships.Sponsorship;
import acme.entities.systemConfiguration.SystemConfiguration;
import acme.roles.Sponsor;

@Repository
public interface AnySponsorshipRepository extends AbstractRepository {

	@Query("select s from Sponsorship s where s.sponsor.id = :id")
	Collection<Sponsorship> findSponsorshipBySponsorId(int id);

	@Query("select s from Sponsorship s where s.id = :id")
	Sponsorship findOneSponsorshipById(int id);

	@Query("select s from Sponsor s where s.id = :id")
	Sponsor findOneSponsorById(int id);

	@Query("select p from Project p where p.id = :id")
	Project findOneProjectById(int id);

	@Query("select p from Project p where p.draftMode = false")
	Collection<Project> findProjects();

	@Query("select p from Project p where p.id = :id")
	Project findOneProjectNameById(int id);

	@Query("select s from Sponsorship s where s.code=:code")
	Sponsorship findOneSponsorshipByCode(String code);

	@Query("select i from Invoice i where i.sponsorship.id=:id")
	Collection<Invoice> findInvoicesOfASponsorship(int id);

	@Query("select sc from SystemConfiguration sc")
	SystemConfiguration findSystemConfiguration();

	@Query("select i from Invoice i where (i.sponsorship.id = :id and i.draftMode = true)")
	Collection<Invoice> findUnpublishedInvoicesBySponsorshipId(int id);

	@Query("select i from Invoice i where (i.sponsorship.id = :id and i.draftMode = false)")
	Collection<Invoice> findPublishedInvoicesBySponsorshipId(int id);

	@Query("select s from Sponsorship s where s.draftMode = false")
	Collection<Sponsorship> findSponsorshipsPublished();

}
