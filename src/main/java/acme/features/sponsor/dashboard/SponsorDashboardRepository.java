
package acme.features.sponsor.dashboard;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.data.datatypes.Money;
import acme.client.repositories.AbstractRepository;

@Repository
public interface SponsorDashboardRepository extends AbstractRepository {

	@Query("select count(i) from Invoice i where i.tax <= 0.21 and i.sponsor.id=:sponsorId")
	int totalNumberOfInvoices(int sponsorId);

	@Query("select count(s) from Sponsorship s where s.link IS NOT NULL and s.sponsor.id=:sponsorId")
	int totalNumberOfSponsorshipsWithLink(int sponsorId);

	@Query("select s.amount from Sponsorship s where s.sponsor.id=:sponsorId")
	Collection<Money> sponsorshipAmounts(int sponsorId);

	@Query("select i.quantity from Invoice i where i.sponsor.id=:sponsorId")
	Collection<Money> invoiceQuantity(int sponsorId);

}
