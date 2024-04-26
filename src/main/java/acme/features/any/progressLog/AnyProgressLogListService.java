
package acme.features.any.progressLog;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.accounts.Any;
import acme.client.data.models.Dataset;
import acme.client.services.AbstractService;
import acme.entities.contracts.Contract;
import acme.entities.progress_logs.ProgressLog;

@Service
public class AnyProgressLogListService extends AbstractService<Any, ProgressLog> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AnyProgressLogRepository repository;

	// AbstractService interface ----------------------------------------------


	@Override
	public void authorise() {
		int contractId;
		Contract contract;
		boolean isValid;

		contractId = super.getRequest().getData("contractId", int.class);
		contract = this.repository.findContractById(contractId);

		isValid = !contract.isDraftMode() && contract != null;

		super.getResponse().setAuthorised(isValid);

	}

	@Override
	public void load() {

		Collection<ProgressLog> progressLogs;
		int contractId;

		contractId = super.getRequest().getData("contractId", int.class);
		progressLogs = this.repository.findProgressLogsByContractId(contractId);

		super.getBuffer().addData(progressLogs);
	}

	@Override
	public void unbind(final ProgressLog progressLog) {

		assert progressLog != null;
		Dataset dataset;

		dataset = super.unbind(progressLog, "recordId", "draftMode", "completeness", "comment", "registrationMoment", "responsiblePerson");

		super.getResponse().addData(dataset);
	}

	@Override
	public void unbind(final Collection<ProgressLog> progressLogs) {
		assert progressLogs != null;

		int contractId;

		contractId = super.getRequest().getData("contractId", int.class);

		super.getResponse().addGlobal("contractId", contractId);
	}
}
