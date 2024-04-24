
package acme.features.developer.trainingModule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.models.Dataset;
import acme.client.services.AbstractService;
import acme.client.views.SelectChoices;
import acme.entities.training_modules.TrainingModule;
import acme.entities.training_modules.TrainingModuleDifficulty;
import acme.roles.Developer;

@Service
public class DeveloperTrainingModuleShowService extends AbstractService<Developer, TrainingModule> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private DeveloperTrainingModuleRepository repository;

	// AbstractService interface ----------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int id;
		int developerId;
		TrainingModule trainingModule;

		id = super.getRequest().getData("id", int.class);
		trainingModule = this.repository.findOneTrainingModuleById(id);

		developerId = super.getRequest().getPrincipal().getActiveRoleId();

		status = developerId == trainingModule.getDeveloper().getId();

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		TrainingModule object;
		int id;

		id = super.getRequest().getData("id", int.class);
		object = this.repository.findOneTrainingModuleById(id);

		super.getBuffer().addData(object);
	}

	@Override
	public void unbind(final TrainingModule object) {
		assert object != null;

		SelectChoices difficultyChoices;
		SelectChoices projectChoices;

		difficultyChoices = SelectChoices.from(TrainingModuleDifficulty.class, object.getDifficulty());
		projectChoices = SelectChoices.from(this.repository.findPublishedProjects(), "title", object.getProject());

		Dataset dataset;

		dataset = super.unbind(object, "code", "creationMoment", "updateMoment", "difficulty", "details", "totalTime", "link", "published", "project");
		dataset.put("difficulties", difficultyChoices);
		dataset.put("projects", projectChoices);

		super.getResponse().addData(dataset);
	}
}
