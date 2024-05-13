
package acme.features.client.contract;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.models.Dataset;
import acme.client.services.AbstractService;
import acme.client.views.SelectChoices;
import acme.components.SystemConfigurationRepository;
import acme.entities.contracts.Contract;
import acme.entities.projects.Project;
import acme.roles.Client;

@Service
public class ClientContractUpdateService extends AbstractService<Client, Contract> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ClientContractRepository		repository;

	@Autowired
	private SystemConfigurationRepository	sysConfigRepository;

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

		//feedback 08/05/24: no se podra crear progress logs si el contrato a asociar no esta publicado
		//no hay q comprobar nada de pl en esta seccion debido a esto

		//validacion del D02 budget debe ser menor o igual que coste
		if (!super.getBuffer().getErrors().hasErrors("budget") && contract.getProject() != null && this.sysConfigRepository.existsCurrency(contract.getBudget().getCurrency())) {
			Project referencedProject = contract.getProject();
			Double projectCost = this.sysConfigRepository.convertToUsd(referencedProject.getCost()).getAmount();
			Double budgetUSD = this.sysConfigRepository.convertToUsd(contract.getBudget()).getAmount();

			super.state(projectCost >= budgetUSD, "budget", "client.contract.form.error.budget");
		}

		//ccodigo del cr no duplicado
		if (!super.getBuffer().getErrors().hasErrors("code")) {
			Contract contractWithCode = this.repository.findContractByCode(contract.getCode());

			if (contractWithCode != null)
				super.state(contractWithCode.getId() == contract.getId(), "code", "client.contract.form.error.code");
		}

		//cr linkeado a proyecto publicado
		if (!super.getBuffer().getErrors().hasErrors("project"))
			super.state(!contract.getProject().isDraftMode(), "project", "client.contract.form.error.project");

		//budget positivo o 0
		if (!super.getBuffer().getErrors().hasErrors("budget")) {
			boolean validBudget = contract.getBudget().getAmount() >= 0.;
			super.state(validBudget, "budget", "client.contract.form.error.budget-negative");
		}

		//budget no tenga divisa invalida
		if (!super.getBuffer().getErrors().hasErrors("budget") && contract.getBudget() != null) {
			String currency = contract.getBudget().getCurrency();
			boolean existsCurrency = this.sysConfigRepository.existsCurrency(currency);
			super.state(existsCurrency, "budget", "client.contract.form.error.currency");
		}
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
		String projectCode;

		projectCode = contract.getProject() != null ? contract.getProject().getCode() : null;

		Collection<Project> projects = this.repository.findlAllPublishedProjects();

		SelectChoices options;

		Project project = contract.getProject() != null ? contract.getProject() : (Project) projects.toArray()[0];

		options = SelectChoices.from(projects, "code", project);

		dataset = super.unbind(contract, "code", "project", "providerName", "customerName", "instantiationMoment", "budget", "goals", "draftMode");

		dataset.put("project", projectCode);
		dataset.put("projects", options);

		super.getResponse().addData(dataset);
	}
}
