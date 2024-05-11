/*
 * AdministratorDashboardShowService.java
 *
 * Copyright (C) 2012-2024 Rafael Corchuelo.
 *
 * In keeping with the traditional purpose of furthering education and research, it is
 * the policy of the copyright owner to permit non-commercial use and redistribution of
 * this software. It has been tested carefully, but it is not guaranteed for any particular
 * purposes. The copyright owner does not offer any warranties or representations, nor do
 * they accept any liabilities with respect to them.
 */

package acme.features.manager.dashboard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.datatypes.Money;
import acme.client.data.models.Dataset;
import acme.client.services.AbstractService;
import acme.components.SystemConfigurationRepository;
import acme.forms.ManagerDashboard;
import acme.roles.Manager;

@Service
public class ManagerDashboardShowService extends AbstractService<Manager, ManagerDashboard> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ManagerDashboardRepository		repository;

	@Autowired
	private SystemConfigurationRepository	systemConfigurationRepository;

	// AbstractService interface ----------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		ManagerDashboard dashboard;
		int totalNumberOfMust;
		int totalNumberOfShould;
		int totalNumberOfCould;
		int totalNumberOfWont;
		Double averageEstimatedCost;
		Double deviationEstimatedCost;
		Double minimumEstimatedCost;
		Double maximumEstimatedCost;
		Double minimumCost;
		Double maximumCost;
		Double averageCost;
		Double deviationCost;

		int managerId;

		managerId = super.getRequest().getPrincipal().getActiveRoleId();

		dashboard = new ManagerDashboard();

		// User Stories
		List<Double> estimatedCosts = this.repository.userStoriesEstimatedCosts(managerId).stream().toList();

		totalNumberOfMust = this.repository.totalNumberOfMust(managerId);
		totalNumberOfShould = this.repository.totalNumberOfShould(managerId);
		totalNumberOfCould = this.repository.totalNumberOfCould(managerId);
		totalNumberOfWont = this.repository.totalNumberOfWont(managerId);
		averageEstimatedCost = this.repository.averageEstimatedCost(managerId);
		deviationEstimatedCost = this.deviation(estimatedCosts);
		minimumEstimatedCost = this.repository.minimumEstimatedCost(managerId);
		maximumEstimatedCost = this.repository.maximumEstimatedCost(managerId);

		dashboard.setTotalNumberOfMust(totalNumberOfMust);
		dashboard.setTotalNumberOfShould(totalNumberOfShould);
		dashboard.setTotalNumberOfCould(totalNumberOfCould);
		dashboard.setTotalNumberOfWont(totalNumberOfWont);
		dashboard.setAverageEstimatedCost(averageEstimatedCost);
		dashboard.setDeviationEstimatedCost(deviationEstimatedCost);
		dashboard.setMinimumEstimatedCost(minimumEstimatedCost);
		dashboard.setMaximumEstimatedCost(maximumEstimatedCost);

		//Projects
		Collection<Money> projectCosts = this.repository.projectCosts(managerId).stream().map(m -> this.systemConfigurationRepository.convertToUsd(m)).collect(Collectors.toCollection(ArrayList<Money>::new));

		minimumCost = projectCosts.stream().mapToDouble(Money::getAmount).min().orElse(Double.NaN);
		maximumCost = projectCosts.stream().mapToDouble(Money::getAmount).max().orElse(Double.NaN);
		averageCost = projectCosts.stream().mapToDouble(Money::getAmount).average().orElse(Double.NaN);
		deviationCost = this.deviation(projectCosts.stream().mapToDouble(Money::getAmount).boxed().toList());

		dashboard.setAverageCost(averageCost);
		dashboard.setDeviationCost(deviationCost);
		dashboard.setMinimumCost(minimumCost);
		dashboard.setMaximumCost(maximumCost);

		super.getBuffer().addData(dashboard);
	}

	private Double deviation(final List<Double> values) {

		if (values.isEmpty())
			return null;

		Stream<Double> valuesStream = values.stream();

		// Calcular la media de los números
		double average = valuesStream.collect(Collectors.averagingDouble(Double::doubleValue));

		// Crear un nuevo Stream<Double> con los cuadrados de la diferencia entre cada número y la media
		valuesStream = values.stream();
		Stream<Double> squaredDifferencesStream = valuesStream.map(num -> Math.pow(num - average, 2));

		// Calcular la suma de los cuadrados de las diferencias
		double sumOfSquaredDifferences = squaredDifferencesStream.reduce(0.0, Double::sum);

		// Calcular la desviación estándar
		return Math.sqrt(sumOfSquaredDifferences / values.size());
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
	public void unbind(final ManagerDashboard object) {
		Dataset dataset;

		dataset = super.unbind(object, //
			"totalNumberOfMust", "totalNumberOfShould", // 
			"totalNumberOfCould", "totalNumberOfWont", //
			"averageEstimatedCost", "deviationEstimatedCost", //
			"minimumEstimatedCost", "maximumEstimatedCost", //
			"minimumCost", "maximumCost", "averageCost", "deviationCost");

		super.getResponse().addData(dataset);
	}

}
