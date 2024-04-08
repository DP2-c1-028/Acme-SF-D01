
package acme.features.client.contract;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.models.Dataset;
import acme.client.services.AbstractService;
import acme.entities.contracts.Contract;
import acme.features.manager.project.ManagerProjectRepository;
import acme.roles.Client;

@Service
public class ClientContractListService extends AbstractService<Client, Contract> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ClientContractRepository	repository;

	//TODO Puede estar mal asiq revisar
	@Autowired
	private ManagerProjectRepository	projectRepository;

	// AbstractService interface ----------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {

		Collection<Contract> contracts;
		int clientId;

		clientId = super.getRequest().getPrincipal().getActiveRoleId();
		contracts = this.repository.findContractsByClientId(clientId);

		super.getBuffer().addData(contracts);
	}

	@Override
	public void unbind(final Contract contract) {

		assert contract != null;
		Dataset dataset;
		String projectName = this.projectRepository.findOneProjectById(contract.getProject().getId()).getTitle();

		dataset = super.unbind(contract, "code", "project", "providerName", "customerName", "instantiationMoment", "budget", "goals");

		dataset.put("project", projectName);
		super.getResponse().addData(dataset);
	}
}
