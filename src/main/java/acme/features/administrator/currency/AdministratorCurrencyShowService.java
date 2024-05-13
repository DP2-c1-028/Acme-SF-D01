
package acme.features.administrator.currency;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.accounts.Administrator;
import acme.client.data.models.Dataset;
import acme.client.services.AbstractService;
import acme.entities.currency.Currency;

@Service
public class AdministratorCurrencyShowService extends AbstractService<Administrator, Currency> {

	// Internal state ---------------------------------------------------------

	@Autowired
	protected AdministratorCurrencyRepository repository;

	// AbstractService interface ----------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Currency object;

		int id;

		id = super.getRequest().getData("id", int.class);

		object = this.repository.findCurrencyById(id);

		super.getBuffer().addData(object);
	}

	@Override
	public void unbind(final Currency object) {
		assert object != null;

		Dataset dataset;

		dataset = super.unbind(object, "symbol", "valueAgainstDollar");

		super.getResponse().addData(dataset);
	}

}
