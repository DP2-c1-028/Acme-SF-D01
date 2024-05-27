
package acme.features.sponsor.dashboard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.datatypes.Money;
import acme.client.data.models.Dataset;
import acme.client.services.AbstractService;
import acme.components.SystemConfigurationRepository;
import acme.forms.SponsorDashboard;
import acme.roles.Sponsor;

@Service
public class SponsorDashboardShowService extends AbstractService<Sponsor, SponsorDashboard> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private SponsorDashboardRepository		repository;

	@Autowired
	private SystemConfigurationRepository	systemConfigurationRepository;

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

		Double averageAmount;
		Double deviationAmount;
		Double minimumAmount;
		Double maximumAmount;

		Double invoicesAverageQuantity;
		Double invoicesDeviationQuantity;
		Double invoicesMinimumQuantity;
		Double invoicesMaximumQuantity;

		sponsorId = super.getRequest().getPrincipal().getActiveRoleId();
		//Collection<Money> amounts = this.repository.sponsorshipAmounts(sponsorId).stream().map(m -> this.systemConfigurationRepository.convertToUsd(m)).collect(Collectors.toCollection(ArrayList<Money>::new));
		//Collection<Money> quantities = this.repository.invoiceQuantity(sponsorId).stream().map(m -> this.systemConfigurationRepository.convertToUsd(m)).collect(Collectors.toCollection(ArrayList<Money>::new));

		Collection<String> sponsorshipCurrencies = this.repository.allCurrenciesInPublishedSponsorships(sponsorId);

		Map<String, SponsorshipMoneyStatistics> moneyStatistics = new HashMap<>();

		dashboard = new SponsorDashboard();

		for (String currency : sponsorshipCurrencies) {

			Collection<Money> sponsorshipsAmounts = this.repository.findAllAmountsFromSponsor(sponsorId).stream().map(m -> this.systemConfigurationRepository.convertFromCurrencyToAnother(m, currency))
				.collect(Collectors.toCollection(ArrayList<Money>::new));

			minimumAmount = sponsorshipsAmounts.stream().mapToDouble(Money::getAmount).min().orElse(Double.NaN);
			maximumAmount = sponsorshipsAmounts.stream().mapToDouble(Money::getAmount).max().orElse(Double.NaN);
			averageAmount = sponsorshipsAmounts.stream().mapToDouble(Money::getAmount).average().orElse(Double.NaN);
			deviationAmount = this.deviationQuantity(sponsorshipsAmounts.stream().mapToDouble(Money::getAmount).boxed().toList());

			SponsorshipMoneyStatistics ms = new SponsorshipMoneyStatistics(minimumAmount, maximumAmount, averageAmount, deviationAmount);

			moneyStatistics.put(currency, ms);

		}

		dashboard.setSponsorshipMoneyStatistics(moneyStatistics);

		Collection<String> invoicesCurrencies = this.repository.allCurrenciesInPublishedInvoices(sponsorId);

		Map<String, InvoiceMoneyStatistics> moneyStatistics2 = new HashMap<>();

		for (String currency : invoicesCurrencies) {

			Collection<Money> invoicesAmounts = this.repository.findAllQuantitiesFromSponsor(sponsorId).stream().map(m -> this.systemConfigurationRepository.convertFromCurrencyToAnother(m, currency)).collect(Collectors.toCollection(ArrayList<Money>::new));

			invoicesMinimumQuantity = invoicesAmounts.stream().mapToDouble(Money::getAmount).min().orElse(Double.NaN);
			invoicesMaximumQuantity = invoicesAmounts.stream().mapToDouble(Money::getAmount).max().orElse(Double.NaN);
			invoicesAverageQuantity = invoicesAmounts.stream().mapToDouble(Money::getAmount).average().orElse(Double.NaN);
			invoicesDeviationQuantity = this.deviationQuantity(invoicesAmounts.stream().mapToDouble(Money::getAmount).boxed().toList());

			InvoiceMoneyStatistics ms = new InvoiceMoneyStatistics(invoicesMinimumQuantity, invoicesMaximumQuantity, invoicesAverageQuantity, invoicesDeviationQuantity);

			moneyStatistics2.put(currency, ms);
		}

		dashboard.setInvoiceMoneyStatistics(moneyStatistics2);

		totalNumberOfInvoices = this.repository.totalNumberOfInvoices(sponsorId);
		totalNumberOfSponsorshipsWithLink = this.repository.totalNumberOfSponsorshipsWithLink(sponsorId);

		dashboard.setTotalNumberOfInvoices(totalNumberOfInvoices);
		dashboard.setTotalNumberOfSponsorshipsWithLink(totalNumberOfSponsorshipsWithLink);

		super.getBuffer().addData(dashboard);
	}

	private Double deviationQuantity(final List<Double> amounts) {

		if (amounts.isEmpty())
			return null;

		Stream<Double> valuesStream = amounts.stream();

		double average = valuesStream.collect(Collectors.averagingDouble(Double::doubleValue));
		valuesStream = amounts.stream();
		Stream<Double> squaredDifferencesStream = valuesStream.map(num -> Math.pow(num - average, 2));

		double sumOfSquaredDifferences = squaredDifferencesStream.reduce(0.0, Double::sum);

		return Math.sqrt(sumOfSquaredDifferences / amounts.size());
	}

	@Override
	public void unbind(final SponsorDashboard object) {
		Dataset dataset;

		dataset = super.unbind(object, "totalNumberOfInvoices", "totalNumberOfSponsorshipsWithLink", "sponsorshipMoneyStatistics", "invoiceMoneyStatistics");
		System.out.println(object.getSponsorshipMoneyStatistics());
		super.getResponse().addData(dataset);
	}

}
