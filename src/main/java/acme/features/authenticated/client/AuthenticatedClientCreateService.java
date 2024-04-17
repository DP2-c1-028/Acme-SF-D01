
package acme.features.authenticated.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.accounts.Authenticated;
import acme.client.data.accounts.Principal;
import acme.client.data.accounts.UserAccount;
import acme.client.data.models.Dataset;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractService;
import acme.client.views.SelectChoices;
import acme.roles.Client;
import acme.roles.CompanyType;

@Service
public class AuthenticatedClientCreateService extends AbstractService<Authenticated, Client> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AuthenticatedClientRepository repository;

	// AbstractService<Authenticated, Consumer> ---------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Client client;
		Principal principal;
		int userAccountId;
		UserAccount userAccount;

		principal = super.getRequest().getPrincipal();
		userAccountId = principal.getAccountId();
		userAccount = this.repository.findUserAccountById(userAccountId);

		client = new Client();
		client.setUserAccount(userAccount);

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

			super.state(clientWithCode == null, "identification", "client.contract.form.error.code");
		}
	}

	@Override
	public void perform(final Client client) {
		assert client != null;

		this.repository.save(client);
	}

	@Override
	public void unbind(final Client client) {
		Dataset dataset;
		SelectChoices choices;
		choices = SelectChoices.from(CompanyType.class, null);

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
