
package acme.features.developer.dashboard;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.models.Dataset;
import acme.client.services.AbstractService;
import acme.forms.DeveloperDashboard;
import acme.roles.Developer;

@Service
public class DeveloperDashboardShowService extends AbstractService<Developer, DeveloperDashboard> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private DeveloperDashboardRepository repository;

	// AbstractService interface ----------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		int developerId;
		DeveloperDashboard dashboard;

		int totalTrainingModulesWithUpdateMoment;
		int totalTrainingSessionsWithLink;
		Double trainingModulesAverageTime;
		Double trainingModulesDeviationTime;
		Integer trainingModulesMinimumTime;
		Integer trainingModulesMaximumTime;

		developerId = super.getRequest().getPrincipal().getActiveRoleId();
		Collection<Integer> times = this.repository.findTrainingModuleTotalTimesByDeveloperId(developerId);

		totalTrainingModulesWithUpdateMoment = this.repository.countTrainingModulesWithUpdateMomentByDeveloperId(developerId);
		totalTrainingSessionsWithLink = this.repository.countTrainingSessionsWithLinkByDeveloperId(developerId);
		trainingModulesAverageTime = this.repository.averageTimesByDeveloperId(developerId);
		trainingModulesDeviationTime = this.trainingModulesDeviationTime(times);
		trainingModulesMinimumTime = this.repository.minimumTimeByDeveloperId(developerId);
		trainingModulesMaximumTime = this.repository.maximumTimeByDeveloperId(developerId);

		dashboard = new DeveloperDashboard();
		dashboard.setTotalTrainingModulesWithUpdateMoment(totalTrainingModulesWithUpdateMoment);
		dashboard.setTotalTrainingSessionsWithLink(totalTrainingSessionsWithLink);
		dashboard.setTrainingModulesAverageTime(trainingModulesAverageTime);
		dashboard.setTrainingModulesDeviationTime(trainingModulesDeviationTime);
		dashboard.setTrainingModulesMinimumTime(trainingModulesMinimumTime);
		dashboard.setTrainingModulesMaximumTime(trainingModulesMaximumTime);

		super.getBuffer().addData(dashboard);
	}

	private Double trainingModulesDeviationTime(final Collection<Integer> times) {
		if (times.isEmpty())
			return null;

		Double deviation;

		double average = times.stream().mapToInt(Integer::intValue).average().orElse(0.0);

		double sumOfSquares = times.stream().mapToDouble(time -> Math.pow(time - average, 2)).sum();

		double vari = sumOfSquares / times.size();

		deviation = Math.sqrt(vari);

		return deviation;
	}

	@Override
	public void unbind(final DeveloperDashboard object) {
		Dataset dataset;

		dataset = super.unbind(object, "totalTrainingModulesWithUpdateMoment", "totalTrainingSessionsWithLink", "trainingModulesAverageTime", "trainingModulesDeviationTime", "trainingModulesMinimumTime", "trainingModulesMaximumTime");

		super.getResponse().addData(dataset);
	}
}
