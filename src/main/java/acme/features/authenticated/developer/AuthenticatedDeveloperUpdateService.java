
package acme.features.authenticated.developer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.accounts.Authenticated;
import acme.client.data.accounts.Principal;
import acme.client.data.models.Dataset;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractService;
import acme.roles.Developer;

@Service
public class AuthenticatedDeveloperUpdateService extends AbstractService<Authenticated, Developer> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AuthenticatedDeveloperRepository repository;

	// AbstractService interface ----------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Developer developer;
		Principal principal;
		int userAccountId;

		principal = super.getRequest().getPrincipal();
		userAccountId = principal.getAccountId();
		developer = this.repository.findDeveloperByUserAccountId(userAccountId);

		super.getBuffer().addData(developer);
	}

	@Override
	public void bind(final Developer developer) {
		assert developer != null;

		super.bind(developer, "degree", "specialisation", "skills", "email", "link");
	}

	@Override
	public void validate(final Developer developer) {
		assert developer != null;
	}

	@Override
	public void perform(final Developer developer) {
		assert developer != null;

		this.repository.save(developer);
	}

	@Override
	public void unbind(final Developer developer) {
		assert developer != null;

		Dataset dataset;

		dataset = super.unbind(developer, "degree", "specialisation", "skills", "email", "link");

		super.getResponse().addData(dataset);
	}

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equals("POST"))
			PrincipalHelper.handleUpdate();
	}

}
