
package acme.features.client.progressLog;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.models.Dataset;
import acme.client.services.AbstractService;
import acme.entities.contracts.Contract;
import acme.entities.progress_logs.ProgressLog;
import acme.roles.Client;

@Service
public class ClientProgressLogCreateService extends AbstractService<Client, ProgressLog> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ClientProgressLogRepository repository;

	// AbstractService interface ----------------------------------------------


	@Override
	public void authorise() {
		int contractId;
		Contract contract;
		int clientId;
		boolean isValid;

		contractId = super.getRequest().getData("contractId", int.class);
		contract = this.repository.findContractById(contractId);
		clientId = super.getRequest().getPrincipal().getActiveRoleId();

		isValid = clientId == contract.getClient().getId();

		super.getResponse().setAuthorised(isValid);

	}

	@Override
	public void load() {
		ProgressLog progressLog;

		progressLog = new ProgressLog();
		Integer clientId = super.getRequest().getPrincipal().getActiveRoleId();
		Client client = this.repository.findClientById(clientId);

		int contractId = super.getRequest().getData("contractId", int.class);
		Contract contract = this.repository.findContractById(contractId);

		progressLog.setClient(client);
		progressLog.setContract(contract);
		progressLog.setDraftMode(true);

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

		//duplicas de codigo
		if (!super.getBuffer().getErrors().hasErrors("recordId")) {

			ProgressLog progressLogWithCode = this.repository.findProgressLogByRecordId(progressLog.getRecordId());

			super.state(progressLogWithCode == null, "recordId", "client.progress-log.form.error.recordId");
		}

		//fecha pl despues de contrato
		if (!super.getBuffer().getErrors().hasErrors("registrationMoment")) {
			Date contractDate = progressLog.getContract().getInstantiationMoment();
			Date plDate = progressLog.getRegistrationMoment();

			Boolean isAfter = plDate.after(contractDate);
			super.state(isAfter, "registrationMoment", "client.progress-log.form.error.registrationMoment");
		}

		//no haya 2 pl creados a la misma vez
		if (!super.getBuffer().getErrors().hasErrors("registrationMoment")) {

			Collection<ProgressLog> sameDate = this.repository.findContractProgressLogByDate(progressLog.getContract().getId(), progressLog.getId(), progressLog.getRegistrationMoment());
			super.state(sameDate.isEmpty(), "registrationMoment", "client.progress-log.form.error.sameMoment");
		}

		//crear progressLogs solo cuando esta el contrato publicado
		if (!super.getBuffer().getErrors().hasErrors("contract")) {
			Integer contractId;
			Contract contract;

			contractId = super.getRequest().getData("contractId", int.class);
			contract = this.repository.findContractById(contractId);

			super.state(!contract.isDraftMode(), "*", "client.progress-log.form.error.unpublished-contract");
		}

		//la completitud debe ir en aumento conforme se crean pl
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

		dataset = super.unbind(progressLog, "recordId", "completeness", "comment", "registrationMoment", "responsiblePerson");
		dataset.put("contractId", super.getRequest().getData("contractId", int.class));

		super.getResponse().addData(dataset);
	}

}
