
package acme.features.developer.trainingModule;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractService;
import acme.entities.training_modules.TrainingModule;
import acme.roles.Developer;

@Service
public class DeveloperTrainingModuleUpdateService extends AbstractService<Developer, TrainingModule> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private DeveloperTrainingModuleRepository repository;

	// AbstractService interface ----------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		TrainingModule object;
		Integer id;

		id = super.getRequest().getData("id", int.class);
		object = this.repository.findOneTrainingModuleById(id);

		super.getBuffer().addData(object);
	}

	@Override
	public void bind(final TrainingModule object) {
		assert object != null;

		Integer DeveloperId = super.getRequest().getPrincipal().getActiveRoleId();
		Developer Developer = this.repository.findOneDeveloperById(DeveloperId);
		object.setDeveloper(Developer);
		super.bind(object, "code", "creationMoment", "updateMoment", "difficulty", "details", "totalTime", "link", "project");

		Date currentMoment;
		Date updateMoment;

		currentMoment = MomentHelper.getCurrentMoment();
		updateMoment = new Date(currentMoment.getTime() - 1000); //Substracts one second to ensure the moment is in the past
		object.setUpdateMoment(updateMoment);
	}

	@Override
	public void validate(final TrainingModule object) {
		assert object != null;

		if (!super.getBuffer().getErrors().hasErrors("published")) {
			Boolean unpublished;
			unpublished = this.repository.findTrainingSessionsByTrainingModuleId(object.getId()).isEmpty();
			super.state(unpublished, "published", "developer.training-module.form.error.published");
		}
	}

	@Override
	public void perform(final TrainingModule object) {
		assert object != null;

		this.repository.save(object);
	}

	@Override
	public void unbind(final TrainingModule object) {
		assert object != null;

		Dataset dataset;

		dataset = super.unbind(object, "code", "creationMoment", "updateMoment", "difficulty", "details", "totalTime", "link", "project");

		super.getResponse().addData(dataset);
	}

}
