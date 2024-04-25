
package acme.features.sponsor.dashboard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.datatypes.Money;
import acme.client.data.models.Dataset;
import acme.client.services.AbstractService;
import acme.forms.SponsorDashboard;
import acme.roles.Sponsor;

@Service
public class SponsorDashboardShowService extends AbstractService<Sponsor, SponsorDashboard> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private SponsorDashboardRepository repository;

	// AbstractService interface ----------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		int sponsorId;
		SponsorDashboard dashboard;
		int totalNumberOfInvoices;
		int totalNumberOfSponsorshipsWithLink;

		Money sponsorshipsAverageAmount;
		Money sponsorshipDeviationAmount;
		Money sponsorshipsMinimumAmount;
		Money sponsorshipsMaximumAmount;

		Money invoicesAverageQuantity;
		Money invoicesDeviationQuantity;
		Money invoicesMinimumQuantity;
		Money invoicesMaximumQuantity;

		sponsorId = super.getRequest().getPrincipal().getActiveRoleId();
		Collection<Money> amounts = this.repository.sponsorshipAmounts(sponsorId).stream().map(this::currencyTransformerUsd).collect(Collectors.toCollection(ArrayList<Money>::new));
		Collection<Money> quantities = this.repository.invoiceQuantity(sponsorId).stream().map(this::currencyTransformerUsd).collect(Collectors.toCollection(ArrayList<Money>::new));

		totalNumberOfInvoices = this.repository.totalNumberOfInvoices(sponsorId);
		totalNumberOfSponsorshipsWithLink = this.repository.totalNumberOfSponsorshipsWithLink(sponsorId);
		sponsorshipsAverageAmount = this.sponsorshipsAverageAmount(amounts);
		sponsorshipDeviationAmount = this.sponsorshipDeviationAmount(amounts);
		sponsorshipsMinimumAmount = this.sponsorshipsMinimumAmount(amounts);
		sponsorshipsMaximumAmount = this.sponsorshipsMaximumAmount(amounts);

		invoicesAverageQuantity = this.invoicesAverageQuantity(quantities);
		invoicesDeviationQuantity = this.invoicesDeviationQuantity(quantities);
		invoicesMinimumQuantity = this.invoicesMinimumQuantity(quantities);
		invoicesMaximumQuantity = this.invoicesMaximumQuantity(quantities);

		dashboard = new SponsorDashboard();
		dashboard.setTotalNumberOfInvoices(totalNumberOfInvoices);
		dashboard.setTotalNumberOfSponsorshipsWithLink(totalNumberOfSponsorshipsWithLink);
		dashboard.setSponsorshipsAverageAmount(sponsorshipsAverageAmount.getAmount());
		dashboard.setSponsorshipsDeviationAmount(sponsorshipDeviationAmount.getAmount());
		dashboard.setSponsorshipsMinimumAmount(sponsorshipsMinimumAmount.getAmount());
		dashboard.setSponsorshipsMaximumAmount(sponsorshipsMaximumAmount.getAmount());

		dashboard.setInvoicesAverageQuantity(invoicesAverageQuantity.getAmount());
		dashboard.setInvoicesDeviationQuantity(invoicesDeviationQuantity.getAmount());
		dashboard.setInvoicesMinimumQuantity(invoicesMinimumQuantity.getAmount());
		dashboard.setInvoicesMaximumQuantity(invoicesMaximumQuantity.getAmount());

		super.getBuffer().addData(dashboard);
	}

	// Metodos de sponsorship

	private Money sponsorshipsAverageAmount(final Collection<Money> amounts) {
		Money dinero = new Money();
		dinero.setCurrency("USD");
		if (amounts.isEmpty()) {
			dinero.setAmount(null);
			return dinero;
		}
		dinero.setAmount(amounts.stream().map(x -> x.getAmount()).mapToDouble(Double::doubleValue).average().orElse(0.0));
		return dinero;
	}

	private Money sponsorshipsMaximumAmount(final Collection<Money> amounts) {
		Money dinero = new Money();
		dinero.setCurrency("USD");
		if (amounts.isEmpty()) {
			dinero.setAmount(null);
			return dinero;
		}
		dinero.setAmount(amounts.stream().map(x -> x.getAmount()).mapToDouble(Double::doubleValue).max().orElse(0.0));
		return dinero;
	}

	private Money sponsorshipsMinimumAmount(final Collection<Money> amounts) {
		Money dinero = new Money();
		dinero.setCurrency("USD");
		if (amounts.isEmpty()) {
			dinero.setAmount(null);
			return dinero;
		}
		dinero.setAmount(amounts.stream().map(x -> x.getAmount()).mapToDouble(Double::doubleValue).min().orElse(0.0));
		return dinero;
	}

	private Money sponsorshipDeviationAmount(final Collection<Money> amounts) {
		Money dinero = new Money();
		dinero.setCurrency("USD");

		if (amounts.isEmpty()) {
			dinero.setAmount(null);
			return dinero;
		}

		double average = amounts.stream().mapToDouble(Money::getAmount).average().orElse(0.0);

		double sumOfSquares = amounts.stream().mapToDouble(x -> Math.pow(x.getAmount() - average, 2)).sum();

		double vari = sumOfSquares / amounts.size();

		double dev = Math.sqrt(vari);

		dinero.setAmount(dev);

		return dinero;
	}

	// Metodos de invoice

	private Money invoicesAverageQuantity(final Collection<Money> quantites) {
		Money dinero = new Money();
		dinero.setCurrency("USD");
		if (quantites.isEmpty()) {
			dinero.setAmount(null);
			return dinero;
		}
		dinero.setAmount(quantites.stream().map(x -> x.getAmount()).mapToDouble(Double::doubleValue).average().orElse(0.0));
		return dinero;
	}

	private Money invoicesMaximumQuantity(final Collection<Money> quantites) {
		Money dinero = new Money();
		dinero.setCurrency("USD");
		if (quantites.isEmpty()) {
			dinero.setAmount(null);
			return dinero;
		}
		dinero.setAmount(quantites.stream().map(x -> x.getAmount()).mapToDouble(Double::doubleValue).max().orElse(0.0));
		return dinero;
	}

	private Money invoicesMinimumQuantity(final Collection<Money> quantites) {
		Money dinero = new Money();
		dinero.setCurrency("USD");
		if (quantites.isEmpty()) {
			dinero.setAmount(null);
			return dinero;
		}
		dinero.setAmount(quantites.stream().map(x -> x.getAmount()).mapToDouble(Double::doubleValue).min().orElse(0.0));
		return dinero;
	}

	private Money invoicesDeviationQuantity(final Collection<Money> quantites) {
		Money dinero = new Money();
		dinero.setCurrency("USD");

		if (quantites.isEmpty()) {
			dinero.setAmount(null);
			return dinero;
		}

		double average = quantites.stream().mapToDouble(Money::getAmount).average().orElse(0.0);

		double sumOfSquares = quantites.stream().mapToDouble(x -> Math.pow(x.getAmount() - average, 2)).sum();

		double vari = sumOfSquares / quantites.size();

		double dev = Math.sqrt(vari);

		dinero.setAmount(dev);

		return dinero;
	}

	private Money currencyTransformerUsd(final Money initial) {
		Money res = new Money();
		res.setCurrency("USD");

		if (initial.getCurrency().equals("USD"))
			res.setAmount(initial.getAmount());

		else if (initial.getCurrency().equals("EUR"))
			res.setAmount(initial.getAmount() * 1.07);

		else
			res.setAmount(initial.getAmount() * 1.25);

		return res;
	}

	@Override
	public void unbind(final SponsorDashboard object) {
		Dataset dataset;

		dataset = super.unbind(object, "totalNumberOfInvoices", "totalNumberOfSponsorshipsWithLink", "sponsorshipsAverageAmount", "sponsorshipsDeviationAmount", "sponsorshipsMinimumAmount", "sponsorshipsMaximumAmount", "invoicesAverageQuantity",
			"invoicesDeviationQuantity", "invoicesMinimumQuantity", "invoicesMaximumQuantity");

		super.getResponse().addData(dataset);
	}

}
