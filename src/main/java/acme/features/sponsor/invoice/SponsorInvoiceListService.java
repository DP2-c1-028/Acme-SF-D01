
package acme.features.sponsor.invoice;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.models.Dataset;
import acme.client.services.AbstractService;
import acme.entities.invoices.Invoice;
import acme.entities.sponsorships.Sponsorship;
import acme.roles.Sponsor;

@Service
public class SponsorInvoiceListService extends AbstractService<Sponsor, Invoice> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private SponsorInvoiceRepository repository;

	// AbstractService interface ----------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int sponsorshipId;
		int sponsorId;
		Sponsorship sponsorship;

		sponsorshipId = super.getRequest().getData("sponsorshipId", int.class);
		sponsorship = this.repository.findOneSponsorshipById(sponsorshipId);

		sponsorId = super.getRequest().getPrincipal().getActiveRoleId();

		status = sponsorId == sponsorship.getSponsor().getId();

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Collection<Invoice> objects;
		int sponsorshipId;

		sponsorshipId = super.getRequest().getData("sponsorshipId", int.class);

		objects = this.repository.findAllInvoicesBySponsorshipId(sponsorshipId);

		super.getBuffer().addData(objects);
	}

	@Override
	public void unbind(final Invoice object) {
		assert object != null;

		Dataset dataset;

		dataset = super.unbind(object, "code", "registrationTime", "dueDate", "quantity", "tax", "link", "draftMode");
		dataset.put("totalAmount", object.totalAmount());
		super.getResponse().addData(dataset);
	}

	@Override
	public void unbind(final Collection<Invoice> objects) {
		assert objects != null;

		int sponsorshipId;
		Sponsorship sponsorship;

		sponsorshipId = super.getRequest().getData("sponsorshipId", int.class);
		sponsorship = this.repository.findOneSponsorshipById(sponsorshipId);

		super.getResponse().addGlobal("sponsorshipId", sponsorshipId);
		super.getResponse().addGlobal("canCreate", sponsorship.isDraftMode());
	}

}
