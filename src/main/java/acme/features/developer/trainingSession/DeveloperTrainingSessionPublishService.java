
package acme.features.developer.trainingSession;

import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractService;
import acme.entities.training_modules.TrainingSession;
import acme.roles.Developer;

@Service
public class DeveloperTrainingSessionPublishService extends AbstractService<Developer, TrainingSession> {

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

		if (!super.getBuffer().getErrors().hasErrors("code")) {

			TrainingSession trainingSessionSameCode = this.repository.findOneTrainingSessionByCode(object.getCode());

			if (trainingSessionSameCode != null)
				super.state(trainingSessionSameCode.getId() == object.getId(), "code", "developer.training-session.form.error.code");
		}

		if (!super.getBuffer().getErrors().hasErrors("periodStart")) {
			Date periodStart;
			Date trainingModuleCreationMoment;
			Boolean periodStartIsValid;

			periodStart = object.getPeriodStart();
			trainingModuleCreationMoment = object.getTrainingModule().getCreationMoment();

			periodStartIsValid = MomentHelper.isLongEnough(trainingModuleCreationMoment, periodStart, 1, ChronoUnit.WEEKS) && periodStart.after(trainingModuleCreationMoment);

			super.state(periodStartIsValid, "periodStart", "developer.training-session.form.error.period-start");
		}

		if (!super.getBuffer().getErrors().hasErrors("periodEnd")) {
			Date periodStart;
			Date periodEnd;
			Boolean periodEndIsValid;

			periodStart = object.getPeriodStart();
			periodEnd = object.getPeriodEnd();

			periodEndIsValid = MomentHelper.isLongEnough(periodStart, periodEnd, 1, ChronoUnit.WEEKS) && periodEnd.after(periodStart);

			super.state(periodEndIsValid, "periodEnd", "developer.training-session.form.error.period-end");
		}
	}

	@Override
	public void perform(final TrainingSession object) {
		assert object != null;

		object.setPublished(true);

		this.repository.save(object);
	}

	@Override
	public void unbind(final TrainingSession object) {
		assert object != null;

		Dataset dataset;

		dataset = super.unbind(object, "code", "periodStart", "periodEnd", "location", "instructor", "contactEmail", "link", "published");

		super.getResponse().addData(dataset);
	}

}
