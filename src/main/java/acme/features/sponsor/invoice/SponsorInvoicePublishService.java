
package acme.features.sponsor.invoice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.models.Dataset;
import acme.client.services.AbstractService;
import acme.entities.invoices.Invoice;
import acme.roles.Sponsor;

@Service
public class SponsorInvoicePublishService extends AbstractService<Sponsor, Invoice> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private SponsorInvoiceRepository repository;

	// AbstractService interface ----------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int id;
		int sponsorId;
		Invoice invoice;

		id = super.getRequest().getData("id", int.class);
		invoice = this.repository.findOneInvoiceById(id);

		sponsorId = super.getRequest().getPrincipal().getActiveRoleId();

		status = sponsorId == invoice.getSponsor().getId() && invoice.isDraftMode();

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Invoice object;
		int id;

		id = super.getRequest().getData("id", int.class);
		object = this.repository.findOneInvoiceById(id);

		super.getBuffer().addData(object);
	}

	@Override
	public void bind(final Invoice object) {
		assert object != null;

		super.bind(object, "code", "moment", "durationStartTime", "durationEndTime", "amount", "type", "email", "link", "project");
	}

	@Override
	public void validate(final Invoice object) {
		assert object != null;

	}

	@Override
	public void perform(final Invoice object) {
		assert object != null;

		object.setDraftMode(false);
		this.repository.save(object);
	}

	@Override
	public void unbind(final Invoice object) {
		assert object != null;

		Dataset dataset;

		dataset = super.unbind(object, "code", "moment", "durationStartTime", "durationEndTime", "amount", "type", "email", "link", "project", "draftMode");

		super.getResponse().addData(dataset);
	}

}
