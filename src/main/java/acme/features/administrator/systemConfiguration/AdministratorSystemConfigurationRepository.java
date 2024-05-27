
package acme.features.administrator.systemConfiguration;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.currency.Currency;
import acme.entities.systemConfiguration.SystemConfiguration;

@Repository
public interface AdministratorSystemConfigurationRepository extends AbstractRepository {

	@Query("select sc from SystemConfiguration sc")
	Collection<SystemConfiguration> findSystemConfiguration();

	@Query("select c from Currency c")
	Collection<Currency> findCurrencies();

	@Query("select c.symbol from Currency c")
	Collection<String> findCurrencySymbols();

}
