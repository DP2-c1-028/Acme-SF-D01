
package acme.features.client.progressLog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.models.Dataset;
import acme.client.services.AbstractService;
import acme.entities.contracts.Contract;
import acme.entities.progress_logs.ProgressLog;
import acme.roles.Client;

@Service
public class ClientProgressLogCreateService extends AbstractService<Client, ProgressLog> {

	@Autowired
	private ClientProgressLogRepository repository;


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

		//es necesario settear el contract al que hace referencida
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

		if (!super.getBuffer().getErrors().hasErrors("recordId")) {

			ProgressLog progressLogWithCode = this.repository.findProgressLogByRecordId(progressLog.getRecordId());

			super.state(progressLogWithCode == null, "recordId", "client.progress-log.form.error.recordId");
		}

		if (!super.getBuffer().getErrors().hasErrors("registrationMoment")) {

			Boolean isAfter = progressLog.getRegistrationMoment().after(progressLog.getContract().getInstantiationMoment());
			super.state(isAfter == true, "registrationMoment", "client.progress-log.form.error.registrationMoment");
		}

		//validacion de modo borrador
		if (!super.getBuffer().getErrors().hasErrors("publishedContract")) {
			Integer contractId;
			Contract contract;

			contractId = super.getRequest().getData("contractId", int.class);
			contract = this.repository.findContractById(contractId);

			super.state(contract.isDraftMode(), "*", "client.progress-log.form.error.published-contract");
		}

		if (!super.getBuffer().getErrors().hasErrors("completeness")) {

			double maxCompleteness = this.repository.findContractProgressLogWithMaxCompleteness(progressLog.getContract().getId());
			//TODO hay q buscar una forma de que cuando quieras publicar no te permita editar el valor, pero si piublicar si no cambias nada.
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
