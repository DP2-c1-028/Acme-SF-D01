
package acme.features.client.contract;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.models.Dataset;
import acme.client.services.AbstractService;
import acme.client.views.SelectChoices;
import acme.entities.contracts.Contract;
import acme.entities.projects.Project;
import acme.roles.Client;

@Service
public class ClientContractDeleteService extends AbstractService<Client, Contract> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ClientContractRepository repository;

	// AbstractService interface ----------------------------------------------


	@Override
	public void authorise() {
		int contractId;
		Contract contract;
		int clientId;
		boolean isValid;

		contractId = super.getRequest().getData("id", int.class);
		contract = this.repository.findContractById(contractId);
		clientId = super.getRequest().getPrincipal().getActiveRoleId();

		isValid = clientId == contract.getClient().getId() && contract.getProject() != null && contract.isDraftMode();

		super.getResponse().setAuthorised(isValid);
	}

	@Override
	public void load() {
		Contract contract;
		Integer id;

		id = super.getRequest().getData("id", int.class);
		contract = this.repository.findContractById(id);

		super.getBuffer().addData(contract);
	}

	@Override
	public void bind(final Contract contract) {
		assert contract != null;

		Integer managerId = super.getRequest().getPrincipal().getActiveRoleId();
		Client client = this.repository.findClientById(managerId);

		contract.setClient(client);
		super.bind(contract, "code", "project", "providerName", "customerName", "instantiationMoment", "budget", "goals");
	}

	@Override
	public void validate(final Contract contract) {
		assert contract != null;
		//feedback 08/05/24: no se podra crear progress logs si el contrato a asociar no esta publicado
		//no hay q comprobar nada de pl en esta seccion debido a esto

	}

	@Override
	public void perform(final Contract contract) {
		assert contract != null;
		//feedback 08/05/24: no se podra crear progress logs si el contrato a asociar no esta publicado
		//no hay q comprobar nada de pl en esta seccion debido a esto

		this.repository.delete(contract);
	}

	@Override
	public void unbind(final Contract contract) {
		assert contract != null;

		Dataset dataset;
		String projectName = this.repository.findProjectById(contract.getProject().getId()).getTitle();

		Collection<Project> projects = this.repository.findlAllProjects();
		SelectChoices options;

		options = SelectChoices.from(projects, "title", this.repository.findProjectById(contract.getProject().getId()));

		dataset = super.unbind(contract, "code", "project", "providerName", "customerName", "instantiationMoment", "budget", "goals");

		dataset.put("project", projectName);
		dataset.put("projects", options);

		super.getResponse().addData(dataset);
	}
}
