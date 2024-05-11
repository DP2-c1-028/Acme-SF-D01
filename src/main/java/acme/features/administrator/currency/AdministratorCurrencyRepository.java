
package acme.features.administrator.currency;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.currency.Currency;
import acme.entities.systemConfiguration.SystemConfiguration;

@Repository
public interface AdministratorCurrencyRepository extends AbstractRepository {

	@Query("select sc from SystemConfiguration sc")
	SystemConfiguration findSystemConfiguration();

	@Query("select c from Currency c")
	Collection<Currency> findCurrencies();

	@Query("select c from Currency c where c.id=:id")
	Currency findCurrencyById(int id);

}
