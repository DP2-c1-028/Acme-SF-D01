
package acme.features.manager.project;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.models.Dataset;
import acme.client.services.AbstractService;
import acme.entities.projects.Project;
import acme.roles.Manager;

@Service
public class ManagerProjectUpdateService extends AbstractService<Manager, Project> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private ManagerProjectRepository repository;

	// AbstractService interface ----------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int id;
		int managerId;
		Project project;

		id = super.getRequest().getData("id", int.class);
		project = this.repository.findOneProjectById(id);

		managerId = super.getRequest().getPrincipal().getActiveRoleId();

		status = managerId == project.getManager().getId() && project.isDraftMode();

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Project object;
		Integer id;

		id = super.getRequest().getData("id", int.class);
		object = this.repository.findOneProjectById(id);

		super.getBuffer().addData(object);
	}

	@Override
	public void bind(final Project object) {
		assert object != null;

		Integer managerId = super.getRequest().getPrincipal().getActiveRoleId();
		Manager manager = this.repository.findOneManagerById(managerId);
		object.setManager(manager);
		super.bind(object, "title", "code", "abstractText", "cost", "link");
	}

	@Override
	public void validate(final Project object) {
		assert object != null;

		if (!super.getBuffer().getErrors().hasErrors("code")) {

			Project projectSameCode = this.repository.findOneProjectByCode(object.getCode());

			super.state(projectSameCode == null, "code", "manager.project.form.error.code");
		}

	}

	@Override
	public void perform(final Project object) {
		assert object != null;

		this.repository.save(object);
	}

	@Override
	public void unbind(final Project object) {
		assert object != null;

		Dataset dataset;

		dataset = super.unbind(object, "title", "code", "abstractText", "cost", "link");

		super.getResponse().addData(dataset);
	}
}
