
package acme.features.sponsor.invoice;

import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.datatypes.Money;
import acme.client.data.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractService;
import acme.components.SystemConfigurationRepository;
import acme.entities.invoices.Invoice;
import acme.entities.systemConfiguration.SystemConfiguration;
import acme.roles.Sponsor;

@Service
public class SponsorInvoiceUpdateService extends AbstractService<Sponsor, Invoice> {

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

		Integer sponsorId = super.getRequest().getPrincipal().getActiveRoleId();
		Sponsor sponsor = this.repository.findOneSponsorById(sponsorId);
		object.setSponsor(sponsor);
		super.bind(object, "code", "registrationTime", "dueDate", "quantity", "tax", "link");
	}

	@Override
	public void validate(final Invoice object) {
		assert object != null;

		if (!super.getBuffer().getErrors().hasErrors("code")) {

			Invoice projectSameCode = this.repository.findOneInvoiceByCode(object.getCode());

			if (projectSameCode != null)
				super.state(projectSameCode.getId() == object.getId(), "code", "sponsor.invoice.form.error.code");
		}

		if (!super.getBuffer().getErrors().hasErrors("registrationTime")) {

			Date invoiceDate = object.getRegistrationTime();
			Date minimumDate = MomentHelper.parse("1969-12-31 0:00", "yyyy-MM-dd HH:mm");

			if (invoiceDate != null) {
				Boolean isAfter = invoiceDate.after(minimumDate);
				super.state(isAfter, "registrationTime", "sponsor.invoice.form.error.registration-time");
			}
		}

		if (!super.getBuffer().getErrors().hasErrors("dueDate")) {
			Date registrationTime;
			Date dueDate;

			registrationTime = object.getRegistrationTime();
			dueDate = object.getDueDate();

			if (registrationTime != null && dueDate != null)
				super.state(MomentHelper.isLongEnough(registrationTime, dueDate, 1, ChronoUnit.MONTHS) && dueDate.after(registrationTime), "dueDate", "sponsor.invoice.form.error.dueDate");
		}

		if (!super.getBuffer().getErrors().hasErrors("quantity") && this.systemConfigurationRepository.existsCurrency(object.getQuantity().getCurrency()))
			super.state(object.getQuantity().getAmount() >= 0 && this.systemConfigurationRepository.convertToUsd(object.getQuantity()).getAmount() <= 1000000, "quantity", "sponsor.invoice.form.error.quantity");

		if (!super.getBuffer().getErrors().hasErrors("quantity")) {
			String symbol = object.getQuantity().getCurrency();
			boolean existsCurrency = this.systemConfigurationRepository.existsCurrency(symbol);
			super.state(existsCurrency, "quantity", "sponsor.invoice.form.error.acceptedCurrency");
		}

		if (!super.getBuffer().getErrors().hasErrors("publishedSponsorship"))
			super.state(object.getSponsorship().isDraftMode(), "*", "sponsor.invoice.form.error.published-sponsorship");

	}

	public boolean isCurrencyAccepted(final Money moneda) {
		SystemConfiguration moneys;
		moneys = this.repository.findSystemConfiguration();

		String[] listaMonedas = moneys.getAcceptedCurrencies().split(",");
		for (String divisa : listaMonedas)
			if (moneda.getCurrency().equals(divisa))
				return true;

		return false;
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
		dataset.put("totalAmount", object.totalAmount());

		super.getResponse().addData(dataset);
	}

}
