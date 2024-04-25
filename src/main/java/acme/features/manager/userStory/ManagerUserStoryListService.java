
package acme.features.manager.userStory;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.models.Dataset;
import acme.client.services.AbstractService;
import acme.entities.projects.Project;
import acme.entities.userStories.UserStory;
import acme.roles.Manager;

@Service
public class ManagerUserStoryListService extends AbstractService<Manager, UserStory> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private ManagerUserStoryRepository repository;

	// AbstractService interface ----------------------------------------------


	@Override
	public void authorise() {

		boolean status = true;

		if (super.getRequest().hasData("projectId")) {
			int projectId;
			int managerId;
			Project project;

			projectId = super.getRequest().getData("projectId", int.class);
			project = this.repository.findOneProjectById(projectId);

			managerId = super.getRequest().getPrincipal().getActiveRoleId();

			status = managerId == project.getManager().getId();
		}

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Collection<UserStory> objects;
		int projectId;
		int managerId;

		managerId = super.getRequest().getPrincipal().getActiveRoleId();

		if (super.getRequest().hasData("projectId")) {
			projectId = super.getRequest().getData("projectId", int.class);
			objects = this.repository.findAllUserStoriesByProjectId(projectId);
		} else
			objects = this.repository.findAllUserStories(managerId);

		super.getBuffer().addData(objects);
	}

	@Override
	public void unbind(final UserStory object) {
		assert object != null;

		Dataset dataset;

		dataset = super.unbind(object, "title", "description", "estimatedCost", "priority", "link", "acceptanceCriteria");

		super.getResponse().addData(dataset);
	}

	@Override
	public void unbind(final Collection<UserStory> objects) {
		assert objects != null;

		if (super.getRequest().hasData("projectId")) {
			int projectId;
			Project project;

			projectId = super.getRequest().getData("projectId", int.class);
			project = this.repository.findOneProjectById(projectId);

			super.getResponse().addGlobal("projectId", projectId);
			super.getResponse().addGlobal("canCreate", project.isDraftMode());
		}
	}

}
