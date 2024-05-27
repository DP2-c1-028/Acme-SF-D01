
package acme.features.client.dashboard;

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
import acme.forms.ClientDashboard;
import acme.roles.Client;

@Service
public class ClientDashboardShowService extends AbstractService<Client, ClientDashboard> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ClientDashboardRepository		repository;

	@Autowired
	private SystemConfigurationRepository	sysConfigRepository;

	// AbstractService interface ----------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		ClientDashboard dashboard;
		int totalLogsWithCompletenessBelow25;
		int totalLogsWithCompletenessBetween25And50;
		int totalLogsWithCompletenessBetween50And75;
		int totalLogsWithCompletenessAbove75;
		int clientId;

		Double averageBudget;
		Double minimunBudget;
		Double maximunBudget;
		Double budgetDeviation = null;

		clientId = super.getRequest().getPrincipal().getActiveRoleId();
		double percentaje25 = 25.0;
		double percentaje50 = 50.0;
		double percentaje75 = 75.0;

		//CALCULO INFO PROGRESS LOGS
		totalLogsWithCompletenessBelow25 = this.repository.logsBelowCompletenessValue(clientId, percentaje25);
		totalLogsWithCompletenessBetween25And50 = this.repository.logsBetweenCompletenessValuesForClient(clientId, percentaje25, percentaje50);
		totalLogsWithCompletenessBetween50And75 = this.repository.logsBetweenCompletenessValuesForClient(clientId, percentaje50, percentaje75);
		totalLogsWithCompletenessAbove75 = this.repository.logsAboveCompletenessValue(clientId, percentaje75);

		//OPERACIONES CON CONTRACTS

		Collection<String> contractCurrencies = this.repository.allCurrenciesInPublishedContracts(clientId);

		Map<String, ClientMoneyStatistics> moneyStatistics = new HashMap<>();

		dashboard = new ClientDashboard();

		for (String currency : contractCurrencies) {

			Collection<Money> contractBudgets = this.repository.findAllBudgetsFromClient(clientId).stream().map(m -> this.sysConfigRepository.convertFromCurrencyToAnother(m, currency)).collect(Collectors.toCollection(ArrayList<Money>::new));

			minimunBudget = contractBudgets.stream().mapToDouble(Money::getAmount).min().orElse(Double.NaN);
			maximunBudget = contractBudgets.stream().mapToDouble(Money::getAmount).max().orElse(Double.NaN);
			averageBudget = contractBudgets.stream().mapToDouble(Money::getAmount).average().orElse(Double.NaN);
			budgetDeviation = this.contractsDeviationQuantity(contractBudgets.stream().mapToDouble(Money::getAmount).boxed().toList());

			ClientMoneyStatistics ms = new ClientMoneyStatistics(minimunBudget, maximunBudget, averageBudget, budgetDeviation);

			moneyStatistics.put(currency, ms);
		}

		dashboard.setMoneyStatistics(moneyStatistics);

		//PONER INFO EN EL DASHBOARD
		dashboard.setTotalLogsWithCompletenessBelow25(totalLogsWithCompletenessBelow25);
		dashboard.setTotalLogsWithCompletenessBetween25And50(totalLogsWithCompletenessBetween25And50);
		dashboard.setTotalLogsWithCompletenessBetween50And75(totalLogsWithCompletenessBetween50And75);
		dashboard.setTotalLogsWithCompletenessAbove75(totalLogsWithCompletenessAbove75);

		super.getBuffer().addData(dashboard);
	}

	private Double contractsDeviationQuantity(final List<Double> amounts) {

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
	public void unbind(final ClientDashboard clientDashboard) {
		Dataset dataset;

		dataset = super.unbind(clientDashboard, //
			"totalLogsWithCompletenessBelow25", "totalLogsWithCompletenessBetween25And50", // 
			"totalLogsWithCompletenessBetween50And75", "totalLogsWithCompletenessAbove75", //
			"moneyStatistics");

		super.getResponse().addData(dataset);
	}
}
