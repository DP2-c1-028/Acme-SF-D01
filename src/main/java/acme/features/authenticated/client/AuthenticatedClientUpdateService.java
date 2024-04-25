
package acme.features.authenticated.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.accounts.Authenticated;
import acme.client.data.accounts.Principal;
import acme.client.data.models.Dataset;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractService;
import acme.client.views.SelectChoices;
import acme.roles.Client;
import acme.roles.CompanyType;

@Service
public class AuthenticatedClientUpdateService extends AbstractService<Authenticated, Client> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AuthenticatedClientRepository repository;

	// AbstractService interface ----------------------------------------------ç


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Client client;
		Principal principal;
		int userAccountId;

		principal = super.getRequest().getPrincipal();
		userAccountId = principal.getAccountId();
		client = this.repository.findClientByUserAccountId(userAccountId);

		super.getBuffer().addData(client);
	}

	@Override
	public void bind(final Client client) {
		assert client != null;

		super.bind(client, "identification", "companyName", "type", "email", "link");
	}

	@Override
	public void validate(final Client client) {
		assert client != null;

		if (!super.getBuffer().getErrors().hasErrors("identification")) {

			Client clientWithCode = this.repository.findClientByIdentification(client.getIdentification());

			if (clientWithCode != null)
				super.state(clientWithCode.getId() == client.getId(), "identification", "authenticated.client.form.error.identification");
		}
	}

	@Override
	public void perform(final Client client) {
		assert client != null;

		this.repository.save(client);
	}

	@Override
	public void unbind(final Client client) {
		assert client != null;

		Dataset dataset;

		SelectChoices choices;
		choices = SelectChoices.from(CompanyType.class, client.getType());

		dataset = super.unbind(client, "identification", "companyName", "type", "email", "link");
		dataset.put("types", choices);

		super.getResponse().addData(dataset);
	}

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equals("POST"))
			PrincipalHelper.handleUpdate();
	}

}
