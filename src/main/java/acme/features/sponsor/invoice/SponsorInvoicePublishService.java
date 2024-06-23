
package acme.features.sponsor.invoice;

import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractService;
import acme.components.SystemConfigurationRepository;
import acme.entities.invoices.Invoice;
import acme.roles.Sponsor;

@Service
public class SponsorInvoicePublishService extends AbstractService<Sponsor, Invoice> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private SponsorInvoiceRepository		repository;

	@Autowired
	private SystemConfigurationRepository	systemConfigurationRepository;

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

		super.bind(object, "code", "registrationTime", "dueDate", "quantity", "tax", "link");
	}

	@Override
	public void validate(final Invoice object) {
		assert object != null;

		Collection<Invoice> allInvoices;

		allInvoices = this.repository.findAllInvoicesBySponsorshipId(object.getSponsorship().getId());

		if (!super.getBuffer().getErrors().hasErrors("code")) {

			Invoice projectSameCode = this.repository.findOneInvoiceByCode(object.getCode());

			if (projectSameCode != null)
				super.state(projectSameCode.getId() == object.getId(), "code", "sponsor.invoice.form.error.code");
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

		if (!super.getBuffer().getErrors().hasErrors()) {
			double valorAntiguo = this.systemConfigurationRepository.convertToUsd(this.repository.findOneInvoiceById(object.getId()).totalAmount()).getAmount();
			double sumaNueva = this.systemConfigurationRepository.convertToUsd(object.totalAmount()).getAmount() - valorAntiguo;
			for (Invoice i : allInvoices)
				sumaNueva += this.systemConfigurationRepository.convertToUsd(i.totalAmount()).getAmount();

			if (this.systemConfigurationRepository.existsCurrency(object.getQuantity().getCurrency()))
				super.state(sumaNueva <= this.systemConfigurationRepository.convertToUsd(object.getSponsorship().getAmount()).getAmount(), "*", "sponsor.invoice.form.error.incorrect-sum");
		}

	}

	@Override
	public void perform(final Invoice object) {
		assert object != null;

		if (object.getTax() == null)
			object.setTax(0.0);

		object.setDraftMode(false);
		this.repository.save(object);
	}

	@Override
	public void unbind(final Invoice object) {
		assert object != null;

		Dataset dataset;

		dataset = super.unbind(object, "code", "registrationTime", "dueDate", "quantity", "tax", "link", "draftMode");
		dataset.put("totalAmount", object.totalAmount());

		super.getResponse().addData(dataset);
	}

}
