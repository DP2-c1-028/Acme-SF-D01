
package acme.features.sponsor.invoice;

import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractService;
import acme.components.SystemConfigurationRepository;
import acme.entities.invoices.Invoice;
import acme.entities.sponsorships.Sponsorship;
import acme.roles.Sponsor;

@Service
public class SponsorInvoiceCreateService extends AbstractService<Sponsor, Invoice> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private SponsorInvoiceRepository		repository;

	@Autowired
	private SystemConfigurationRepository	systemConfigurationRepository;

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
		Invoice object;
		Integer sponsorshipId;
		Sponsorship sponsorship;

		object = new Invoice();
		Integer sponsorId = super.getRequest().getPrincipal().getActiveRoleId();
		Sponsor sponsor = this.repository.findOneSponsorById(sponsorId);

		sponsorshipId = super.getRequest().getData("sponsorshipId", int.class);
		sponsorship = this.repository.findOneSponsorshipById(sponsorshipId);

		object.setSponsor(sponsor);
		object.setSponsorship(sponsorship);
		object.setDraftMode(true);

		super.getBuffer().addData(object);

	}

	@Override
	public void bind(final Invoice object) {
		assert object != null;

		super.bind(object, "code", "registrationTime", "dueDate", "quantity", "tax", "link");
	}

	@Override
	public void validate(final Invoice object) {
		assert object != null;

		if (!super.getBuffer().getErrors().hasErrors("code")) {

			Invoice projectSameCode = this.repository.findOneInvoiceByCode(object.getCode());

			super.state(projectSameCode == null, "code", "sponsor.invoice.form.error.code");
		}

		if (!super.getBuffer().getErrors().hasErrors("registrationTime")) {

			Date registrationTime = object.getRegistrationTime();
			Date minimumDate = MomentHelper.parse("1969-12-31 23:59", "yyyy-MM-dd HH:mm");
			Date maximumDate = MomentHelper.parse("2200-12-31 23:59", "yyyy-MM-dd HH:mm");

			if (registrationTime != null) {
				Boolean isAfter = registrationTime.after(minimumDate) && registrationTime.before(maximumDate);
				super.state(isAfter, "registrationTime", "sponsor.invoice.form.error.registration-time");
			}
		}

		if (!super.getBuffer().getErrors().hasErrors("registrationTime")) {
			Date registrationTime;
			Date moment;

			registrationTime = object.getRegistrationTime();
			moment = object.getSponsorship().getMoment();

			if (registrationTime != null)
				super.state(registrationTime.after(moment), "registrationTime", "sponsor.invoice.form.error.registration-time-bis");
		}

		if (!super.getBuffer().getErrors().hasErrors("dueDate")) {
			Date registrationTime;
			Date dueDate;

			registrationTime = object.getRegistrationTime();
			dueDate = object.getDueDate();
			Date minimumDate = MomentHelper.parse("1969-12-31 23:59", "yyyy-MM-dd HH:mm");
			Date maximumDate = MomentHelper.parse("2200-12-31 23:59", "yyyy-MM-dd HH:mm");

			if (registrationTime != null && dueDate != null)
				super.state(MomentHelper.isLongEnough(registrationTime, dueDate, 1, ChronoUnit.MONTHS) && dueDate.after(registrationTime) && dueDate.after(minimumDate) && dueDate.before(maximumDate), "dueDate", "sponsor.invoice.form.error.dueDate");
		}

		if (!super.getBuffer().getErrors().hasErrors("quantity") && this.systemConfigurationRepository.existsCurrency(object.getQuantity().getCurrency()))
			super.state(object.getQuantity().getAmount() >= 0 && object.getQuantity().getAmount() <= 1000000, "quantity", "sponsor.invoice.form.error.quantity");

		if (!super.getBuffer().getErrors().hasErrors("quantity")) {
			String symbol = object.getQuantity().getCurrency();
			boolean existsCurrency = this.systemConfigurationRepository.existsCurrency(symbol);
			super.state(existsCurrency, "quantity", "sponsor.invoice.form.error.acceptedCurrency");
		}

		if (!super.getBuffer().getErrors().hasErrors("publishedSponsorship")) {
			Integer sponsorshipId;
			Sponsorship sponsorship;

			sponsorshipId = super.getRequest().getData("sponsorshipId", int.class);
			sponsorship = this.repository.findOneSponsorshipById(sponsorshipId);

			super.state(sponsorship.isDraftMode(), "*", "sponsor.invoice.form.error.published-sponsorship");
		}

	}

	@Override
	public void perform(final Invoice object) {
		assert object != null;

		if (object.getTax() == null)
			object.setTax(0.0);

		this.repository.save(object);
	}

	@Override
	public void unbind(final Invoice object) {
		assert object != null;

		Dataset dataset;

		dataset = super.unbind(object, "code", "registrationTime", "dueDate", "quantity", "tax", "link", "draftMode");
		dataset.put("sponsorshipId", super.getRequest().getData("sponsorshipId", int.class));

		super.getResponse().addData(dataset);
	}

}
