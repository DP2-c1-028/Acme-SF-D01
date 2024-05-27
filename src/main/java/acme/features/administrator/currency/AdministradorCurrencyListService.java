
package acme.features.administrator.currency;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.accounts.Administrator;
import acme.client.data.models.Dataset;
import acme.client.services.AbstractService;
import acme.entities.currency.Currency;

@Service
public class AdministradorCurrencyListService extends AbstractService<Administrator, Currency> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorCurrencyRepository repository;

	// AbstractService interface ----------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);

	}

	@Override
	public void load() {

		Collection<Currency> currencies = this.repository.findCurrencies();

		super.getBuffer().addData(currencies);
	}

	@Override
	public void unbind(final Currency currency) {

		assert currency != null;

		Dataset dataset;

		dataset = super.unbind(currency, "symbol", "valueAgainstDollar");

		super.getResponse().addData(dataset);
	}
}
