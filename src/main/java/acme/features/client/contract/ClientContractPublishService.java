
package acme.features.client.contract;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.datatypes.Money;
import acme.client.data.models.Dataset;
import acme.client.services.AbstractService;
import acme.client.views.SelectChoices;
import acme.entities.contracts.Contract;
import acme.entities.progress_logs.ProgressLog;
import acme.entities.projects.Project;
import acme.entities.systemConfiguration.SystemConfiguration;
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

		isValid = clientId == contract.getClient().getId() && contract.getProject() != null && contract.isDraftMode();

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

		//validacion de publish contando solo los contratos publicados + el budget del entrante para valorar
		if (!super.getBuffer().getErrors().hasErrors("budget") && contract.getProject() != null) {

			int projectId = contract.getProject().getId();
			Collection<Contract> contracts = this.repository.findPublishedContractsByProjectId(projectId);
			System.out.println(contracts);
			if (!contracts.isEmpty()) {

				Double totalBudgetUsd = contracts.stream().mapToDouble(u -> this.currencyTransformerUsd(u.getBudget())).sum();
				Double projectCostUsd = this.currencyTransformerUsd(contract.getProject().getCost());
				double afterPublishingTotalCostUsd = totalBudgetUsd + this.currencyTransformerUsd(contract.getBudget());

				super.state(afterPublishingTotalCostUsd <= projectCostUsd, "*", "client.contract.form.error.publishError");
			}
		}

		if (!super.getBuffer().getErrors().hasErrors("unpublishedProgressLogs")) {

			Collection<ProgressLog> unpublishedProgressLogs;

			unpublishedProgressLogs = this.repository.findUnpublishedProgressLogsByContractId(contract.getId());

			super.state(unpublishedProgressLogs.isEmpty(), "*", "client.contract.form.error.publishError-progressLog");
		}

		//validaciones de actualización
		if (!super.getBuffer().getErrors().hasErrors("budget") && contract.getProject() != null) {
			Project referencedProject = contract.getProject();
			super.state(this.currencyTransformerUsd(referencedProject.getCost()) >= this.currencyTransformerUsd(contract.getBudget()), "budget", "client.contract.form.error.budget-negative");

		}

		if (!super.getBuffer().getErrors().hasErrors("code")) {

			Contract contractWithCode = this.repository.findContractByCode(contract.getCode());

			if (contractWithCode != null)
				super.state(contractWithCode.getId() == contract.getId(), "code", "client.contract.form.error.code");
		}

		if (!super.getBuffer().getErrors().hasErrors("project"))
			super.state(!contract.getProject().isDraftMode(), "project", "client.contract.form.error.project");

		if (!super.getBuffer().getErrors().hasErrors("budget")) {
			boolean validBudget = contract.getBudget().getAmount() >= 0.;
			super.state(validBudget, "budget", "client.contract.form.error.budget-negative");
		}

		//por la nueva retrsiccion de que no puedes crear progressLogs hasta q no este publicado el contrato puede q se tenga q borrar
		if (!super.getBuffer().getErrors().hasErrors("instantiationMoment")) {

			ProgressLog earliestPl = this.repository.findEarliestPublisehdLogByContractId(contract.getId());

			if (earliestPl != null)
				super.state(earliestPl.getRegistrationMoment().after(contract.getInstantiationMoment()), "instantiationMoment", "client.contract.form.error.invalidDate");
		}

		if (!super.getBuffer().getErrors().hasErrors("budget"))
			super.state(this.isCurrencyAccepted(contract.getBudget()), "budget", "client.contract.form.error.currency");

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

	public boolean isCurrencyAccepted(final Money moneda) {
		SystemConfiguration moneys;
		moneys = this.repository.findSystemConfiguration();

		String[] listaMonedas = moneys.getAcceptedCurrencies().split(",");
		for (String divisa : listaMonedas)
			if (moneda.getCurrency().equals(divisa))
				return true;

		return false;
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
