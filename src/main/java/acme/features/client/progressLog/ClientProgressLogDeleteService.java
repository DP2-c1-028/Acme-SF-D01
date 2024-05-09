
package acme.features.client.progressLog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.models.Dataset;
import acme.client.services.AbstractService;
import acme.entities.progress_logs.ProgressLog;
import acme.roles.Client;

@Service
public class ClientProgressLogDeleteService extends AbstractService<Client, ProgressLog> {

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

		isValid = progressLog != null && clientId == progressLog.getClient().getId();

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

		Integer clientId = super.getRequest().getPrincipal().getActiveRoleId();
		Client client = this.repository.findClientById(clientId);

		progressLog.setClient(client);
		super.bind(progressLog, "recordId", "completeness", "comment", "registrationMoment", "responsiblePerson");
	}

	@Override
	public void validate(final ProgressLog progressLog) {
		assert progressLog != null;

		// no es necesario meter validaciones de borrado debido al feedback recibido
	}

	@Override
	public void perform(final ProgressLog progressLog) {
		assert progressLog != null;

		this.repository.delete(progressLog);
	}

	@Override
	public void unbind(final ProgressLog progressLog) {

		assert progressLog != null;
		Dataset dataset;

		dataset = super.unbind(progressLog, "recordId", "draftMode", "completeness", "comment", "registrationMoment", "responsiblePerson");

		super.getResponse().addData(dataset);
	}

}
