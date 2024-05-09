
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
import acme.entities.systemConfiguration.SystemConfiguration;
import acme.roles.Client;

@Service
public class ClientContractCreateService extends AbstractService<Client, Contract> {

	@Autowired
	private ClientContractRepository repository;


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

		//validacion del D02 budget debe ser menor o igual que coste
		if (!super.getBuffer().getErrors().hasErrors("budget") && contract.getProject() != null) {
			Project referencedProject = contract.getProject();
			super.state(this.currencyTransformerUsd(referencedProject.getCost()) >= this.currencyTransformerUsd(contract.getBudget()), "budget", "client.contract.form.error.budget");

		}

		if (!super.getBuffer().getErrors().hasErrors("code")) {

			Contract contractWithCode = this.repository.findContractByCode(contract.getCode());

			super.state(contractWithCode == null, "code", "client.contract.form.error.code");
		}

		if (!super.getBuffer().getErrors().hasErrors("project"))
			super.state(!contract.getProject().isDraftMode(), "project", "client.contract.form.error.project");

		if (!super.getBuffer().getErrors().hasErrors("budget")) {
			boolean validBudget = contract.getBudget().getAmount() >= 0.;
			super.state(validBudget, "budget", "client.contract.form.error.budget-negative");
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
	public void perform(final Contract contract) {
		assert contract != null;
		System.out.println(contract);
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
