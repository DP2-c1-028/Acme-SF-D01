
package acme.features.developer.trainingSession;

import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractService;
import acme.entities.training_modules.TrainingModule;
import acme.entities.training_modules.TrainingSession;
import acme.roles.Developer;

@Service
public class DeveloperTrainingSessionCreateService extends AbstractService<Developer, TrainingSession> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private DeveloperTrainingSessionRepository repository;

	// AbstractService interface ----------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		TrainingSession object;
		Integer trainingModuleId;
		TrainingModule trainingModule;

		object = new TrainingSession();

		trainingModuleId = super.getRequest().getData("trainingModuleId", int.class);
		trainingModule = this.repository.findOneTrainingModuleById(trainingModuleId);
		System.out.println(trainingModuleId);
		System.out.println(trainingModule);
		object.setTrainingModule(trainingModule);

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

			periodStart = object.getPeriodStart();
			trainingModuleCreationMoment = object.getTrainingModule().getCreationMoment();

			super.state(MomentHelper.isLongEnough(trainingModuleCreationMoment, periodStart, 1, ChronoUnit.WEEKS), "periodStart", "developer.training-session.form.error.period-start");
		}

		if (!super.getBuffer().getErrors().hasErrors("periodEnd")) {
			Date periodStart;
			Date periodEnd;

			periodStart = object.getPeriodStart();
			periodEnd = object.getPeriodEnd();

			super.state(MomentHelper.isLongEnough(periodStart, periodEnd, 1, ChronoUnit.WEEKS), "periodEnd", "developer.training-session.form.error.period-end");
		}

	}

	@Override
	public void perform(final TrainingSession object) {
		assert object != null;
		this.repository.save(object);
	}

	@Override
	public void unbind(final TrainingSession object) {
		assert object != null;

		Dataset dataset;

		dataset = super.unbind(object, "code", "periodStart", "periodEnd", "location", "instructor", "contactEmail", "link", "published", "trainingModule");
		dataset.put("trainingModuleId", super.getRequest().getData("trainingModuleId", int.class));

		super.getResponse().addData(dataset);
	}

}
