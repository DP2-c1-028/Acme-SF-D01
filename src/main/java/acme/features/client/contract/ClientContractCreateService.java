
package acme.features.client.contract;

import java.util.ArrayList;
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

		//Integer projectId = super.getRequest().getData("project", int.class);
		//Project project = this.repository.findProjectById(projectId);

		//contract.setProject(project);
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
		Project referencedProject = contract.getProject();
		assert referencedProject.getCost() >= contract.getBudget().getAmount();

		/*
		 * if( referencedProject.getCost().getCurrency.equals(contract.getBudget().getCurrency()) ){
		 * assert referencedProject().getCost().getAmount() >= contract.getBudget().getAmount()
		 * }else{
		 * assert this.currencyTransformer(referencedProject.getCost(), "EUR") >= this.currencyTransformer(contract.getBudget(), "EUR");
		 * }
		 * 
		 */
	}

	private double currencyTransformer(final Money initial, final String currency) {
		double res = initial.getAmount();
		ArrayList<Double> factor = new ArrayList<>();
		factor.add(1.0);
		factor.add(1.07);
		factor.add(0.9);//EURtoEUR, EURtoUSD, EURtoGBP

		factor.add(0.93);
		factor.add(1.0);
		factor.add(0.00);//USDtoEUR, USDtoUSD, USDtoGBP

		factor.add(1.1);
		factor.add(0.00);
		factor.add(1.0);//GBtoEUR, GBPtoUSD, GBPtoGBP

		ArrayList<String> lista = new ArrayList<>();
		lista.add("EUR");
		lista.add("USD");
		lista.add("GBP");

		for (int i = 0; i < lista.size(); i++)
			for (int j = 0; j < lista.size(); j++)
				if (lista.get(i) == initial.getCurrency() && lista.get(j) == currency)
					res = initial.getAmount() * factor.get(i + j);

		return res;
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

		SelectChoices choices;

		Collection<Project> projects = this.repository.findlAllProjects();
		Dataset dataset;

		choices = SelectChoices.from(projects, "title", null);

		dataset = super.unbind(contract, "code", "instantiationMoment", "providerName", "customerName", "goals", "budget", "project");
		dataset.put("projects", choices);

		super.getResponse().addData(dataset);
	}
}
