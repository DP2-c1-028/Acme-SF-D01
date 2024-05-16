
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
public class ClientContractPublishService extends AbstractService<Client, Contract> {

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
	public void bind(final Contract contract) {
		assert contract != null;

		super.bind(contract, "code", "project", "draftMode", "providerName", "customerName", "instantiationMoment", "budget", "goals");
	}

	@Override
	public void validate(final Contract contract) {
		assert contract != null;

		//feedback 08/05/24: no se podra crear progress logs si el contrato a asociar no esta publicado
		//no hay q comprobar nada de pl en esta seccion debido a esto

		//validacion de publish  contratos publicados + el budget del entrante para valorar no debe superar el coste del proyecto asociado
		if (!super.getBuffer().getErrors().hasErrors("budget") && contract.getProject() != null && this.sysConfigRepository.existsCurrency(contract.getBudget().getCurrency())) {

			int projectId = contract.getProject().getId();
			Collection<Contract> contracts = this.repository.findPublishedContractsByProjectId(projectId);

			//si contratos esta vacio es el primer contrato publicado de ese proyecto y solo se tiene que validar las heredadas de actualización
			if (!contracts.isEmpty()) {

				Double totalBudgetUsd = contracts.stream().mapToDouble(u -> this.sysConfigRepository.convertToUsd(u.getBudget()).getAmount()).sum();
				Double projectCostUsd = this.sysConfigRepository.convertToUsd(contract.getProject().getCost()).getAmount();
				double afterPublishingTotalCostUsd = totalBudgetUsd + this.sysConfigRepository.convertToUsd(contract.getBudget()).getAmount();

				super.state(afterPublishingTotalCostUsd <= projectCostUsd, "*", "client.contract.form.error.publishError");
			}
		}

		//VALIDACIONES HEREDADAS DE ACTUALIZACION

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

		//budget positivo o menor a 1000000
		if (!super.getBuffer().getErrors().hasErrors("budget") && this.sysConfigRepository.existsCurrency(contract.getBudget().getCurrency())) {
			boolean validBudget = contract.getBudget().getAmount() >= 0. && this.sysConfigRepository.convertToUsd(contract.getBudget()).getAmount() <= 1000000.0;
			super.state(validBudget, "budget", "client.contract.form.error.budget-negative");
		}

		//budget no tenga divisa invalida
		if (!super.getBuffer().getErrors().hasErrors("budget") && contract.getBudget() != null) {
			String currency = contract.getBudget().getCurrency();
			boolean existsCurrency = this.sysConfigRepository.existsCurrency(currency);
			super.state(existsCurrency, "budget", "client.contract.form.error.currency");
		}

		// fecha superior al 2000/01/01 00:00
		if (!super.getBuffer().getErrors().hasErrors("instantiationMoment")) {

			Date contractDate = contract.getInstantiationMoment();
			Date minimunDate = MomentHelper.parse("2000-01-01 00:00", "yyyy-MM-dd HH:mm");

			Boolean isAfter = contractDate.after(minimunDate);
			super.state(isAfter, "instantiationMoment", "client.contract.form.error.instantiationMoment");
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
