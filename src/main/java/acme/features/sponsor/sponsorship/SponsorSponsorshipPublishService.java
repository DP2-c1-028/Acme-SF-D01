
package acme.features.sponsor.sponsorship;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.models.Dataset;
import acme.client.services.AbstractService;
import acme.entities.invoices.Invoice;
import acme.entities.sponsorships.Sponsorship;
import acme.roles.Sponsor;

@Service
public class SponsorSponsorshipPublishService extends AbstractService<Sponsor, Sponsorship> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private SponsorSponsorshipRepository repository;

	// AbstractService interface ----------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int id;
		int sponsorId;
		Sponsorship sponsorship;

		id = super.getRequest().getData("id", int.class);
		sponsorship = this.repository.findOneSponsorshipById(id);

		sponsorId = super.getRequest().getPrincipal().getActiveRoleId();

		status = sponsorId == sponsorship.getSponsor().getId() && sponsorship.isDraftMode();

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Sponsorship object;
		int id;

		id = super.getRequest().getData("id", int.class);
		object = this.repository.findOneSponsorshipById(id);

		super.getBuffer().addData(object);
	}

	@Override
	public void bind(final Sponsorship object) {
		assert object != null;

		super.bind(object, "code", "moment", "durationStartTime", "durationEndTime", "amount", "type", "email", "link", "project");
	}

	@Override
	public void validate(final Sponsorship object) {
		assert object != null;

		if (!super.getBuffer().getErrors().hasErrors("totalAmount")) {
			Collection<Invoice> invoices = this.repository.findInvoicesOfASponsorship(object.getId());

			double invoiceTotAmount = invoices.stream().mapToDouble(i -> i.totalAmount()).sum();
			super.state(invoiceTotAmount == object.getAmount().getAmount(), "*", "sponsor.sponsorship.form.error.invalidTotalAmount");
		}

	}

	@Override
	public void perform(final Sponsorship object) {
		assert object != null;

		object.setDraftMode(false);
		this.repository.save(object);
	}

	@Override
	public void unbind(final Sponsorship object) {
		assert object != null;

		Dataset dataset;

		dataset = super.unbind(object, "code", "moment", "durationStartTime", "durationEndTime", "amount", "type", "email", "link", "project");

		super.getResponse().addData(dataset);
	}

}
