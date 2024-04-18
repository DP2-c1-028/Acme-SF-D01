
package acme.features.developer.trainingSession;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.models.Dataset;
import acme.client.services.AbstractService;
import acme.entities.training_modules.TrainingModule;
import acme.entities.training_modules.TrainingSession;
import acme.roles.Developer;

@Service
public class DeveloperTrainingSessionListService extends AbstractService<Developer, TrainingSession> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private DeveloperTrainingSessionRepository repository;

	// AbstractService interface ----------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int trainingModuleId;
		int developerId;
		TrainingModule trainingModule;

		trainingModuleId = super.getRequest().getData("trainingModuleId", int.class);
		trainingModule = this.repository.findOneTrainingModuleById(trainingModuleId);

		developerId = super.getRequest().getPrincipal().getActiveRoleId();

		status = developerId == trainingModule.getDeveloper().getId();

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Collection<TrainingSession> objects;
		int trainingModuleId;

		trainingModuleId = super.getRequest().getData("trainingModuleId", int.class);

		objects = this.repository.findTrainingSessionsByTrainingModuleId(trainingModuleId);

		super.getBuffer().addData(objects);
	}

	@Override
	public void unbind(final TrainingSession object) {
		assert object != null;

		Dataset dataset;

		dataset = super.unbind(object, "code", "periodStart", "periodEnd", "location", "instructor", "contactEmail", "link", "published", "trainingModule");

		super.getResponse().addData(dataset);
	}

	@Override
	public void unbind(final Collection<TrainingSession> objects) {
		assert objects != null;

		int trainingModuleId;

		trainingModuleId = super.getRequest().getData("trainingModuleId", int.class);

		super.getResponse().addGlobal("trainingModuleId", trainingModuleId);
	}
}
