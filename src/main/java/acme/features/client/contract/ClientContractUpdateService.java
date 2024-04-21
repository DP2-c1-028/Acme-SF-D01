
package acme.features.client.contract;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.datatypes.Money;
import acme.client.data.models.Dataset;
import acme.client.services.AbstractService;
import acme.client.views.SelectChoices;
import acme.entities.contracts.Contract;
import acme.entities.projects.Project;
import acme.roles.Client;

@Service
public class ClientContractUpdateService extends AbstractService<Client, Contract> {

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

		isValid = clientId == contract.getClient().getId() && contract.getProject() != null;

		super.getResponse().setAuthorised(isValid);
	}

	@Override
	public void load() {
		Contract contract;
		Integer contractId;

		contractId = super.getRequest().getData("id", int.class);
		contract = this.repository.findContractById(contractId);

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

		if (!super.getBuffer().getErrors().hasErrors("budget")) {
			Project referencedProject = contract.getProject();

			super.state(this.currencyTransformerUsd(referencedProject.getCost()) >= this.currencyTransformerUsd(contract.getBudget()), "budget", "client.contract.form.error.budget");

		}

		if (!super.getBuffer().getErrors().hasErrors("code")) {

			Contract contractWithCode = this.repository.findContractByCode(contract.getCode());

			if (contractWithCode != null)
				super.state(contractWithCode.getId() == contract.getId(), "code", "client.contract.form.error.code");
		}

	}

	private double currencyTransformerUsd(final Money initial) {
		double res = initial.getAmount();

		if (initial.getCurrency().equals("USD"))
			res = initial.getAmount();

		else if (initial.getCurrency().equals("EUR"))
			res = initial.getAmount() * 1.07;

		else
			res = initial.getAmount() * 1.25;

		return res;
	}

	@Override
	public void perform(final Contract contract) {
		assert contract != null;

		this.repository.save(contract);
	}

	@Override
	public void unbind(final Contract contract) {
		assert contract != null;

		Dataset dataset;
		String projectCode = this.repository.findProjectById(contract.getProject().getId()).getCode();

		Collection<Project> projects = this.repository.findlAllProjects();
		SelectChoices options;

		options = SelectChoices.from(projects, "code", this.repository.findProjectById(contract.getProject().getId()));

		dataset = super.unbind(contract, "code", "project", "providerName", "customerName", "instantiationMoment", "budget", "goals");

		dataset.put("project", projectCode);
		dataset.put("projects", options);

		super.getResponse().addData(dataset);
	}
}
