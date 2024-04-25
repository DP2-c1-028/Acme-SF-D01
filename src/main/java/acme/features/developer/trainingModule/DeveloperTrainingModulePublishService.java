
package acme.features.developer.trainingModule;

import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractService;
import acme.client.views.SelectChoices;
import acme.entities.training_modules.TrainingModule;
import acme.entities.training_modules.TrainingModuleDifficulty;
import acme.entities.training_modules.TrainingSession;
import acme.roles.Developer;

@Service
public class DeveloperTrainingModulePublishService extends AbstractService<Developer, TrainingModule> {
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

		status = developerId == trainingModule.getDeveloper().getId() && !trainingModule.isPublished();

		super.getResponse().setAuthorised(status);
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

		Integer developerId = super.getRequest().getPrincipal().getActiveRoleId();
		Developer developer = this.repository.findOneDeveloperById(developerId);
		object.setDeveloper(developer);
		super.bind(object, "code", "creationMoment", "updateMoment", "difficulty", "details", "totalTime", "link", "project");
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

		if (!super.getBuffer().getErrors().hasErrors("unpublishable")) {

			boolean publishable;

			publishable = !this.repository.findTrainingSessionsByTrainingModuleId(object.getId()).isEmpty();

			super.state(publishable, "*", "developer.training-module.form.error.unpublishable");
		}

		if (!super.getBuffer().getErrors().hasErrors("unpublishedSessions")) {

			Collection<TrainingSession> unpublishedSessions;

			unpublishedSessions = this.repository.findUnpublishedTrainingSessionsByTrainingModuleId(object.getId());

			super.state(unpublishedSessions.isEmpty(), "*", "developer.training-module.form.error.unpublished-sessions");
		}

		if (!super.getBuffer().getErrors().hasErrors("creationMoment")) {
			Date trainingSessionEarliestPeriodStart;

			trainingSessionEarliestPeriodStart = this.repository.findTrainingSessionWithEarliestDateByTrainingModuleId(object.getId()).getPeriodStart();

			super.state(object.getCreationMoment().before(trainingSessionEarliestPeriodStart) && MomentHelper.isLongEnough(object.getCreationMoment(), trainingSessionEarliestPeriodStart, 1, ChronoUnit.WEEKS), "creationMoment",
				"developer.training-module.form.error.creation-moment");
		}

		if (!super.getBuffer().getErrors().hasErrors("project"))
			super.state(!object.getProject().isDraftMode(), "project", "developer.training-module.form.error.project");

	}

	@Override
	public void perform(final TrainingModule object) {
		assert object != null;

		object.setPublished(true);

		this.repository.save(object);
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
