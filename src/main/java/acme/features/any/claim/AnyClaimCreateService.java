
package acme.features.any.claim;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.accounts.Any;
import acme.client.data.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractService;
import acme.entities.claim.Claim;

@Service
public class AnyClaimCreateService extends AbstractService<Any, Claim> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AnyClaimRepository repository;

	// AbstractService interface ----------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Claim object;

		object = new Claim();

		super.getBuffer().addData(object);

	}

	@Override
	public void bind(final Claim object) {
		assert object != null;

		super.bind(object, "code", "heading", "description", "department", "email", "link");

		Date currentMoment;
		Date instantiationMoment;

		currentMoment = MomentHelper.getCurrentMoment();
		instantiationMoment = new Date(currentMoment.getTime() - 1000); //Subtracts one second to ensure the moment is in the past

		object.setInstantiationMoment(instantiationMoment);
	}

	@Override
	public void validate(final Claim object) {
		assert object != null;

		//TODO no funciona, ver porqué
		if (!super.getBuffer().getErrors().hasErrors("code")) {
			Claim uniqueCode;
			uniqueCode = this.repository.findOneClaimByCode(object.getCode());
			super.state(uniqueCode == null, "code", "validation.claim.code.duplicate");
		}

		if (!super.getBuffer().getErrors().hasErrors("confirmation")) {
			boolean confirmation;
			confirmation = super.getRequest().getData("confirmation", boolean.class);
			super.state(confirmation, "confirmation", "validation.claim.publish.message");
		}
	}

	@Override
	public void perform(final Claim object) {
		assert object != null;

		this.repository.save(object);
	}

	@Override
	public void unbind(final Claim object) {
		assert object != null;

		Dataset dataset;

		dataset = super.unbind(object, "code", "heading", "description", "department", "email", "link");
		dataset.put("confirmation", false);

		super.getResponse().addData(dataset);
	}

}
