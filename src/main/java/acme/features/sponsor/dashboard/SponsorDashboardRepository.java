
package acme.features.sponsor.dashboard;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.data.datatypes.Money;
import acme.client.repositories.AbstractRepository;

@Repository
public interface SponsorDashboardRepository extends AbstractRepository {

	@Query("select count(i) from Invoice i where i.tax <= 0.21 and i.sponsor.id=:sponsorId and i.draftMode=false")
	int totalNumberOfInvoices(int sponsorId);

	@Query("select count(s) from Sponsorship s where s.link IS NOT NULL and s.sponsor.id=:sponsorId and s.draftMode=false")
	int totalNumberOfSponsorshipsWithLink(int sponsorId);

	@Query("select s.amount from Sponsorship s where s.sponsor.id=:sponsorId and s.draftMode=false")
	Collection<Money> sponsorshipAmounts(int sponsorId);

	@Query("select i.quantity from Invoice i where i.sponsor.id=:sponsorId and i.draftMode=false")
	Collection<Money> invoiceQuantity(int sponsorId);

	@Query("select s.amount.currency from Sponsorship s where s.sponsor.id= :sponsorId and s.draftMode=false")
	Collection<String> allCurrenciesInPublishedSponsorships(int sponsorId);

	@Query("select s.amount from Sponsorship s where s.draftMode=false and  s.sponsor.id= :id")
	Collection<Money> findAllAmountsFromSponsor(int id);

	@Query("select i.quantity.currency from Invoice i where i.sponsor.id= :sponsorId and i.draftMode=false")
	Collection<String> allCurrenciesInPublishedInvoices(int sponsorId);

	@Query("select i.quantity from Invoice i where i.draftMode=false and  i.sponsor.id= :id")
	Collection<Money> findAllQuantitiesFromSponsor(int id);

}
