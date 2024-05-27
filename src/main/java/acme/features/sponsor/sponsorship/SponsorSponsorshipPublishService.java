
package acme.features.sponsor.sponsorship;

import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.datatypes.Money;
import acme.client.data.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractService;
import acme.client.views.SelectChoices;
import acme.components.SystemConfigurationRepository;
import acme.entities.invoices.Invoice;
import acme.entities.projects.Project;
import acme.entities.sponsorships.Sponsorship;
import acme.entities.sponsorships.SponsorshipType;
import acme.roles.Sponsor;

@Service
public class SponsorSponsorshipPublishService extends AbstractService<Sponsor, Sponsorship> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private SponsorSponsorshipRepository	repository;

	@Autowired
	private SystemConfigurationRepository	systemConfigurationRepository;

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

		if (!super.getBuffer().getErrors().hasErrors("code")) {

			Sponsorship projectSameCode = this.repository.findOneSponsorshipByCode(object.getCode());

			if (projectSameCode != null)
				super.state(projectSameCode.getId() == object.getId(), "code", "sponsor.sponsorship.form.error.code");
		}

		if (!super.getBuffer().getErrors().hasErrors("totalAmount")) {
			Collection<Invoice> invoices = this.repository.findInvoicesOfASponsorship(object.getId());

			double invoiceTotAmount = invoices.stream().mapToDouble(i -> this.currencyTransformerUsd(i.getQuantity(), i.totalAmount().getAmount()).getAmount()).sum();
			if (object.getAmount() != null)
				super.state(invoiceTotAmount == this.currencyTransformerUsd(object.getAmount(), object.getAmount().getAmount()).getAmount(), "*", "sponsor.sponsorship.form.error.invalidTotalAmount");
		}

		if (!super.getBuffer().getErrors().hasErrors("moment")) {

			Date sponsorshipDate = object.getMoment();
			Date minimumDate = MomentHelper.parse("1969-12-31 0:00", "yyyy-MM-dd HH:mm");
			Date maximumDate = MomentHelper.parse("2200-12-31 23:59", "yyyy-MM-dd HH:mm");

			if (sponsorshipDate != null) {
				Boolean isAfter = sponsorshipDate.after(minimumDate) && sponsorshipDate.before(maximumDate);
				super.state(isAfter, "moment", "sponsor.sponsorship.form.error.moment");
			}
		}

		if (!super.getBuffer().getErrors().hasErrors("moment")) {
			Invoice earliestInvoice;
			Boolean validMoment;
			Date moment = object.getMoment();

			earliestInvoice = this.repository.findInvoiceWithEarliestDateBySponsorshipId(object.getId()).stream().findFirst().orElse(null);
			//System.out.println(earliestInvoice);

			if (earliestInvoice != null) {
				//System.out.println(earliestInvoice);
				validMoment = moment.before(earliestInvoice.getRegistrationTime());
				//System.out.println(validMoment);
				super.state(validMoment, "moment", "sponsor.sponsorship.form.error.creation-moment");
			}
		}

		if (!super.getBuffer().getErrors().hasErrors("durationStartTime")) {
			Date durationStartTime;
			Date moment;
			durationStartTime = object.getDurationStartTime();
			moment = object.getMoment();
			Date minimumDate = MomentHelper.parse("1969-12-31 0:00", "yyyy-MM-dd HH:mm");
			Date maximumDate = MomentHelper.parse("2200-12-31 23:59", "yyyy-MM-dd HH:mm");

			if (moment != null)
				super.state(durationStartTime.after(moment) && durationStartTime.after(minimumDate) && durationStartTime.before(maximumDate), "durationStartTime", "sponsor.sponsorship.form.error.durationStartTime");
		}

		if (!super.getBuffer().getErrors().hasErrors("durationEndTime")) {
			Date durationStartTime;
			Date durationEndTime;

			durationStartTime = object.getDurationStartTime();
			durationEndTime = object.getDurationEndTime();
			Date maximumDate = MomentHelper.parse("2200-12-31 23:59", "yyyy-MM-dd HH:mm");

			if (durationStartTime != null && durationEndTime != null)
				super.state(MomentHelper.isLongEnough(durationStartTime, durationEndTime, 1, ChronoUnit.MONTHS) && durationEndTime.after(durationStartTime) && durationEndTime.before(maximumDate), "durationEndTime",
					"sponsor.sponsorship.form.error.durationEndTime");
		}

		if (!super.getBuffer().getErrors().hasErrors("amount") && this.systemConfigurationRepository.existsCurrency(object.getAmount().getCurrency()))
			super.state(object.getAmount().getAmount() >= 0 && object.getAmount().getAmount() <= 1000000, "amount", "sponsor.sponsorship.form.error.amount");

		if (!super.getBuffer().getErrors().hasErrors("amount")) {
			String symbol = object.getAmount().getCurrency();
			boolean existsCurrency = this.systemConfigurationRepository.existsCurrency(symbol);
			super.state(existsCurrency, "amount", "sponsor.sponsorship.form.error.acceptedCurrency");
		}

		if (!super.getBuffer().getErrors().hasErrors("unpublishedInvoices")) {

			Collection<Invoice> unpublishedInvoices;

			unpublishedInvoices = this.repository.findUnpublishedInvoicesBySponsorshipId(object.getId());

			super.state(unpublishedInvoices.isEmpty(), "*", "sponsor.sponsorship.form.error.unpublished-invoices");
		}

		if (!super.getBuffer().getErrors().hasErrors("project"))
			super.state(!object.getProject().isDraftMode(), "project", "sponsor.sponsorship.form.error.project-not-published");

	}

	private Money currencyTransformerUsd(final Money currency, final Double amount) {
		Money res = new Money();
		res.setCurrency("USD");

		if (currency.getCurrency().equals("USD"))
			res.setAmount(amount);

		else if (currency.getCurrency().equals("EUR"))
			res.setAmount(amount * 1.07);

		else
			res.setAmount(amount * 1.25);

		return res;
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

		SelectChoices choices;

		Collection<Project> projects = this.repository.findProjects();
		SelectChoices choices2;
		Dataset dataset;
		String projectCode;
		int sponsorId;

		sponsorId = super.getRequest().getData("id", int.class);

		Sponsorship s = this.repository.findOneSponsorshipById(sponsorId);

		projectCode = s.getProject() != null ? s.getProject().getCode() : null;

		choices = SelectChoices.from(SponsorshipType.class, object.getType());
		choices2 = SelectChoices.from(projects, "code", s.getProject());

		dataset = super.unbind(object, "code", "moment", "durationStartTime", "durationEndTime", "amount", "type", "email", "link", "project", "draftMode");
		dataset.put("types", choices);
		dataset.put("projects", choices2);
		dataset.put("project", projectCode);

		super.getResponse().addData(dataset);
	}

}
