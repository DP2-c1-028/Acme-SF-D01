
package acme.features.client.contract;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractService;
import acme.client.views.SelectChoices;
import acme.components.SystemConfigurationRepository;
import acme.entities.contracts.Contract;
import acme.entities.projects.Project;
import acme.roles.Client;

@Service
public class ClientContractCreateService extends AbstractService<Client, Contract> {

	@Autowired
	private ClientContractRepository		repository;

	@Autowired
	private SystemConfigurationRepository	sysConfigRepository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);

	}

	@Override
	public void load() {
		Contract contract;

		contract = new Contract();
		Integer clientId = super.getRequest().getPrincipal().getActiveRoleId();
		Client client = this.repository.findClientById(clientId);

		contract.setClient(client);
		contract.setDraftMode(true);
		super.getBuffer().addData(contract);

	}

	@Override
	public void bind(final Contract contract) {
		assert contract != null;

		super.bind(contract, "code", "instantiationMoment", "providerName", "customerName", "goals", "budget", "project");

	}

	@Override
	public void validate(final Contract contract) {

		assert contract != null;

		if (!super.getBuffer().getErrors().hasErrors("budget") && contract.getProject() != null && this.sysConfigRepository.existsCurrency(contract.getBudget().getCurrency())) {
			Project referencedProject = contract.getProject();
			Double projectCost = this.sysConfigRepository.convertToUsd(referencedProject.getCost()).getAmount();
			Double budgetUSD = this.sysConfigRepository.convertToUsd(contract.getBudget()).getAmount();

			super.state(projectCost >= budgetUSD, "budget", "client.contract.form.error.budget");
		}

		if (!super.getBuffer().getErrors().hasErrors("code")) {
			Contract contractWithCode = this.repository.findContractByCode(contract.getCode());
			super.state(contractWithCode == null, "code", "client.contract.form.error.code");
		}

		if (!super.getBuffer().getErrors().hasErrors("project"))
			super.state(!contract.getProject().isDraftMode(), "project", "client.contract.form.error.project");

		if (!super.getBuffer().getErrors().hasErrors("budget") && this.sysConfigRepository.existsCurrency(contract.getBudget().getCurrency())) {
			boolean validBudget = contract.getBudget().getAmount() >= 0. && this.sysConfigRepository.convertToUsd(contract.getBudget()).getAmount() <= 1000000.0;
			super.state(validBudget, "budget", "client.contract.form.error.budget-negative");
		}

		if (!super.getBuffer().getErrors().hasErrors("budget") && contract.getBudget() != null) {
			String currency = contract.getBudget().getCurrency();
			boolean existsCurrency = this.sysConfigRepository.existsCurrency(currency);
			super.state(existsCurrency, "budget", "client.contract.form.error.currency");
		}

		if (!super.getBuffer().getErrors().hasErrors("instantiationMoment")) {

			Date contractDate = contract.getInstantiationMoment();
			Date minimunDate = MomentHelper.parse("2000-01-01 00:00", "yyyy-MM-dd HH:mm");

			Boolean isAfter = contractDate.after(minimunDate);
			super.state(isAfter, "instantiationMoment", "client.contract.form.error.instantiationMoment");
		}

	}

	@Override
	public void perform(final Contract contract) {
		assert contract != null;
		contract.setId(0);
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

		Project project = contract.getProject() != null ? contract.getProject() : null;

		options = SelectChoices.from(projects, "code", project);

		dataset = super.unbind(contract, "code", "project", "providerName", "customerName", "instantiationMoment", "budget", "goals", "draftMode");

		dataset.put("project", projectCode);
		dataset.put("projects", options);

		super.getResponse().addData(dataset);
	}
}
