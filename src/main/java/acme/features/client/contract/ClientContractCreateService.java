
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

		Integer projectId = super.getRequest().getPrincipal().getActiveRoleId();
		Project project = this.repository.findProjectById(projectId);
		contract.setProject(project);

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
	}

	@Override
	public void perform(final Contract object) {
		assert object != null;
		System.out.println(object);
		this.repository.save(object);
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
