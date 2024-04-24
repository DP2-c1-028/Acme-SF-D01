
package acme.features.developer.trainingModule;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractService;
import acme.client.views.SelectChoices;
import acme.entities.training_modules.TrainingModule;
import acme.entities.training_modules.TrainingModuleDifficulty;
import acme.roles.Developer;

@Service
public class DeveloperTrainingModuleCreateService extends AbstractService<Developer, TrainingModule> {
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

		object = new TrainingModule();
		Integer developerId = super.getRequest().getPrincipal().getActiveRoleId();
		Developer developer = this.repository.findOneDeveloperById(developerId);
		object.setDeveloper(developer);

		super.getBuffer().addData(object);
	}

	@Override
	public void bind(final TrainingModule object) {
		assert object != null;

		super.bind(object, "code", "creationMoment", "updateMoment", "difficulty", "details", "totalTime", "link", "project");

		Date currentMoment;
		Date creationMoment;

		currentMoment = MomentHelper.getCurrentMoment();
		creationMoment = new Date(currentMoment.getTime() - 500); //Substracts half a second to ensure the moment is in the past and before the update moment
		object.setCreationMoment(creationMoment);

		object.setPublished(false);
	}

	@Override
	public void validate(final TrainingModule object) {
		assert object != null;

		if (!super.getBuffer().getErrors().hasErrors("code")) {

			TrainingModule trainingModuleSameCode = this.repository.findOneTrainingModuleByCode(object.getCode());

			if (trainingModuleSameCode != null)
				super.state(trainingModuleSameCode.getId() == object.getId(), "code", "developer.training-module.form.error.code");
		}

		if (!super.getBuffer().getErrors().hasErrors("updateMoment")) {
			Date creationMoment;
			Date updateMoment;

			creationMoment = object.getCreationMoment();
			updateMoment = object.getUpdateMoment();

			if (updateMoment != null)
				super.state(updateMoment.after(creationMoment), "updateMoment", "developer.training-module.form.error.update-moment");
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

		SelectChoices difficultyChoices;
		SelectChoices projectChoices;

		difficultyChoices = SelectChoices.from(TrainingModuleDifficulty.class, object.getDifficulty());
		projectChoices = SelectChoices.from(this.repository.findAllProjects(), "title", null);

		Dataset dataset;

		dataset = super.unbind(object, "code", "creationMoment", "updateMoment", "difficulty", "details", "totalTime", "link", "published", "project");
		dataset.put("difficulties", difficultyChoices);
		dataset.put("projects", projectChoices);

		super.getResponse().addData(dataset);
	}
}
