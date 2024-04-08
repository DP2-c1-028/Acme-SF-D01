
package acme.features.client.contract;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.models.Dataset;
import acme.client.services.AbstractService;
import acme.client.views.SelectChoices;
import acme.entities.contracts.Contract;
import acme.entities.projects.Project;
import acme.features.manager.project.ManagerProjectRepository;
import acme.roles.Client;

@Service
public class ClientContractShowService extends AbstractService<Client, Contract> {

	// Internal state ---------------------------------------------------------
	@Autowired
	private ClientContractRepository	repository;

	//TODO Puede estar mal asiq revisar
	@Autowired
	private ManagerProjectRepository	projectRepository;
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

		isValid = clientId == contract.getClient().getId();

		super.getResponse().setAuthorised(isValid);
	}

	@Override
	public void load() {

		Contract contract;
		int contractId;

		contractId = super.getRequest().getData("id", int.class);
		contract = this.repository.findContractById(contractId);

		super.getBuffer().addData(contract);
	}

	@Override
	public void unbind(final Contract contract) {

		assert contract != null;

		Dataset dataset;
		String projectName = this.projectRepository.findOneProjectById(contract.getProject().getId()).getTitle();
		Collection<Project> projects = this.repository.findlAllProjects();
		SelectChoices options;

		//TODO TMABIEN MIRAR AQUI SI ESTA BIEN O HAYQ  CREAR OTRO METODO EN EL CLIENTCONTRACTREPOSITORY
		options = SelectChoices.from(projects, "title", this.projectRepository.findOneProjectById(contract.getProject().getId()));
		dataset = super.unbind(contract, "code", "project", "providerName", "customerName", "instantiationMoment", "budget", "goals");

		dataset.put("project", projectName);
		dataset.put("projects", options);

		super.getResponse().addData(dataset);
	}

}
