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

import java.util.Collection;
import java.util.stream.DoubleStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.accounts.Administrator;
import acme.client.data.datatypes.Money;
import acme.client.data.models.Dataset;
import acme.client.services.AbstractService;
import acme.forms.ManagerDashboard;

@Service
public class ManagerDashboardShowService extends AbstractService<Administrator, ManagerDashboard> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ManagerDashboardRepository repository;

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
		double minimumEstimatedCost;
		double maximumEstimatedCost;
		int managerId;

		managerId = super.getRequest().getPrincipal().getActiveRoleId();

		totalNumberOfMust = this.repository.totalNumberOfMust(managerId);
		totalNumberOfShould = this.repository.totalNumberOfShould(managerId);
		totalNumberOfCould = this.repository.totalNumberOfCould(managerId);
		totalNumberOfWont = this.repository.totalNumberOfWont(managerId);
		averageEstimatedCost = this.repository.averageEstimatedCost(managerId);
		deviationEstimatedCost = 0.;
		minimumEstimatedCost = this.repository.minimumEstimatedCost(managerId);
		maximumEstimatedCost = this.repository.maximumEstimatedCost(managerId);

		dashboard = new ManagerDashboard();
		// User Stories
		dashboard.setTotalNumberOfMust(totalNumberOfMust);
		dashboard.setTotalNumberOfShould(totalNumberOfShould);
		dashboard.setTotalNumberOfCould(totalNumberOfCould);
		dashboard.setTotalNumberOfWont(totalNumberOfWont);
		dashboard.setAverageEstimatedCost(averageEstimatedCost);
		dashboard.setDeviationEstimatedCost(deviationEstimatedCost);
		dashboard.setMinimumEstimatedCost(minimumEstimatedCost);
		dashboard.setMaximumEstimatedCost(maximumEstimatedCost);

		//Projects
		Collection<Money> projectCosts = this.repository.projectCosts(managerId);
		DoubleStream projectCostsAumount = projectCosts.stream().mapToDouble(Money::getAmount);

		dashboard.setAverageCost(projectCostsAumount.average().getAsDouble());
		dashboard.setDeviationCost(0.);
		dashboard.setMinimumCost(projectCostsAumount.min().getAsDouble());
		dashboard.setMaximumCost(projectCostsAumount.max().getAsDouble());

		super.getBuffer().addData(dashboard);
	}

	@Override
	public void unbind(final ManagerDashboard object) {
		Dataset dataset;

		dataset = super.unbind(object, //
			"totalNumberOfMust", "totalNumberOfShould", // 
			"totalNumberOfCould", "totalNumberOfWont", //
			"averageEstimatedCost", "deviationEstimatedCost");

		super.getResponse().addData(dataset);
	}

}
