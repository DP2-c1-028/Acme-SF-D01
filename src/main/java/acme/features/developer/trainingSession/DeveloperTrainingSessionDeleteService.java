
package acme.features.developer.trainingSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.services.AbstractService;
import acme.entities.training_modules.TrainingSession;
import acme.roles.Developer;

@Service
public class DeveloperTrainingSessionDeleteService extends AbstractService<Developer, TrainingSession> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private DeveloperTrainingSessionRepository repository;

	// AbstractService interface ----------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int id;
		int developerId;
		TrainingSession trainingSession;

		id = super.getRequest().getData("id", int.class);
		trainingSession = this.repository.findTrainingSessionById(id);

		developerId = super.getRequest().getPrincipal().getActiveRoleId();

		status = developerId == trainingSession.getTrainingModule().getDeveloper().getId() && !trainingSession.isPublished();

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		TrainingSession object;
		int id;

		id = super.getRequest().getData("id", int.class);

		object = this.repository.findTrainingSessionById(id);

		super.getBuffer().addData(object);
	}

	@Override
	public void bind(final TrainingSession object) {
		assert object != null;

		super.bind(object, "code", "periodStart", "periodEnd", "location", "instructor", "contactEmail", "link");
	}

	@Override
	public void validate(final TrainingSession object) {
		assert object != null;
	}

	@Override
	public void perform(final TrainingSession object) {
		assert object != null;

		this.repository.delete(object);
	}

}
