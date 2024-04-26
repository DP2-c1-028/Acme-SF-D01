
package acme.features.any.progressLog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.accounts.Any;
import acme.client.data.models.Dataset;
import acme.client.services.AbstractService;
import acme.entities.progress_logs.ProgressLog;

@Service
public class AnyProgressLogShowService extends AbstractService<Any, ProgressLog> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AnyProgressLogRepository repository;

	// AbstractService interface ----------------------------------------------


	@Override
	public void authorise() {
		int progressLogId;
		ProgressLog progressLog;
		boolean isValid;

		progressLogId = super.getRequest().getData("id", int.class);
		progressLog = this.repository.findProgressLogById(progressLogId);

		isValid = progressLog != null && !progressLog.isDraftMode();

		super.getResponse().setAuthorised(isValid);
	}

	@Override
	public void load() {

		ProgressLog progressLog;
		int progressLogId;

		progressLogId = super.getRequest().getData("id", int.class);
		progressLog = this.repository.findProgressLogById(progressLogId);

		super.getBuffer().addData(progressLog);
	}

	@Override
	public void unbind(final ProgressLog progressLog) {

		assert progressLog != null;
		Dataset dataset;

		dataset = super.unbind(progressLog, "recordId", "draftMode", "completeness", "comment", "registrationMoment", "responsiblePerson");

		super.getResponse().addData(dataset);
	}
}
