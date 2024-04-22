
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
public class ClientContractPublishService extends AbstractService<Client, Contract> {

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
	public void bind(final Contract contract) {
		assert contract != null;

		super.bind(contract, "code", "project", "draftMode", "providerName", "customerName", "instantiationMoment", "budget", "goals");
	}

	@Override
	public void validate(final Contract contract) {
		assert contract != null;

		int clientId = contract.getClient().getId();
		int projectId = contract.getProject().getId();

		if (!super.getBuffer().getErrors().hasErrors("budget")) {
			Collection<Contract> contracts = this.repository.findProjectContractsByClientId(clientId, projectId);

			Double totalBudgetUsd = contracts.stream().mapToDouble(u -> this.repository.currencyTransformerUsd(u.getBudget())).sum();
			Double projectCostUsd = this.repository.currencyTransformerUsd(contract.getProject().getCost());

			super.state(totalBudgetUsd <= projectCostUsd, "*", "client.contract.form.error.publishError");
		}
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
	public void perform(final Contract contract) {
		assert contract != null;

		contract.setDraftMode(false);
		this.repository.save(contract);
	}

	@Override
	public void unbind(final Contract contract) {

		assert contract != null;

		Dataset dataset;
		String projectName = this.repository.findProjectById(contract.getProject().getId()).getTitle();

		Collection<Project> projects = this.repository.findlAllProjects();
		SelectChoices options;

		options = SelectChoices.from(projects, "title", this.repository.findProjectById(contract.getProject().getId()));

		dataset = super.unbind(contract, "code", "project", "draftMode", "providerName", "customerName", "instantiationMoment", "budget", "goals");

		dataset.put("project", projectName);
		dataset.put("projects", options);

		super.getResponse().addData(dataset);
	}
}
