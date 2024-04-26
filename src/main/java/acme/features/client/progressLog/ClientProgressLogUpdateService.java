
package acme.features.client.progressLog;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.models.Dataset;
import acme.client.services.AbstractService;
import acme.entities.contracts.Contract;
import acme.entities.progress_logs.ProgressLog;
import acme.roles.Client;

@Service
public class ClientProgressLogUpdateService extends AbstractService<Client, ProgressLog> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ClientProgressLogRepository repository;

	// AbstractService interface ----------------------------------------------


	@Override
	public void authorise() {
		int progressLogId;
		ProgressLog progressLog;
		int clientId;
		boolean isValid;

		progressLogId = super.getRequest().getData("id", int.class);
		progressLog = this.repository.findProgressLogById(progressLogId);
		clientId = super.getRequest().getPrincipal().getActiveRoleId();

		isValid = clientId == progressLog.getClient().getId() && progressLog.isDraftMode() == true;

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
	public void bind(final ProgressLog progressLog) {
		assert progressLog != null;

		super.bind(progressLog, "recordId", "completeness", "comment", "registrationMoment", "responsiblePerson");
	}

	@Override
	public void validate(final ProgressLog progressLog) {
		assert progressLog != null;

		if (!super.getBuffer().getErrors().hasErrors("recordId")) {

			ProgressLog progressLogWithCode = this.repository.findProgressLogByRecordId(progressLog.getRecordId());

			if (progressLogWithCode != null)
				super.state(progressLogWithCode.getId() == progressLog.getId(), "recordId", "client.progress-log.form.error.recordId");
		}

		if (!super.getBuffer().getErrors().hasErrors("registrationMoment")) {

			Date contractDate = progressLog.getContract().getInstantiationMoment();
			Date plDate = progressLog.getRegistrationMoment();

			Boolean isAfter = plDate.after(contractDate);

			super.state(isAfter, "registrationMoment", "client.progress-log.form.error.registrationMoment");
		}

		//validacion de modo borrador
		if (!super.getBuffer().getErrors().hasErrors("publishedContract")) {
			Integer contractId;
			Contract contract;

			contractId = progressLog.getContract().getId();
			contract = this.repository.findContractById(contractId);

			super.state(contract.isDraftMode(), "*", "client.progress-log.form.error.published-contract");
		}

		if (!super.getBuffer().getErrors().hasErrors("completeness")) {

			Double maxCompleteness = this.repository.findContractProgressLogWithMaxCompleteness(progressLog.getContract().getId());
			if (maxCompleteness != null)
				super.state(maxCompleteness < progressLog.getCompleteness(), "completeness", "client.progress-log.form.error.completeness");

		}

	}

	@Override
	public void perform(final ProgressLog progressLog) {
		assert progressLog != null;

		this.repository.save(progressLog);
	}

	@Override
	public void unbind(final ProgressLog progressLog) {
		assert progressLog != null;

		Dataset dataset;

		dataset = super.unbind(progressLog, "recordId", "completeness", "comment", "registrationMoment", "responsiblePerson", "draftMode");

		super.getResponse().addData(dataset);
	}
}
