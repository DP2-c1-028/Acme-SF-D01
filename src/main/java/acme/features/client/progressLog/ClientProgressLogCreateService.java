
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
		super.getResponse().setAuthorised(true);

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

		//Contract referencedContract = progressLog.getContract();
		//super.state(referencedContract != null, "*", "client.progressLog.form.error.invalidContract");

		if (!super.getBuffer().getErrors().hasErrors("recordId")) {

			ProgressLog progressLogWithCode = this.repository.findProgressLogByRecordId(progressLog.getRecordId());

			super.state(progressLogWithCode == null, "recordId", "client.progress-log.form.error.recordId");
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
