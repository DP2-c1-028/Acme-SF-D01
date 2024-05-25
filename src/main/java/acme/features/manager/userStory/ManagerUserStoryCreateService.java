
package acme.features.manager.userStory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.models.Dataset;
import acme.client.services.AbstractService;
import acme.client.views.SelectChoices;
import acme.entities.projects.Project;
import acme.entities.userStories.Priority;
import acme.entities.userStories.UserStory;
import acme.entities.userStories.UserStoryProject;
import acme.roles.Manager;

@Service
public class ManagerUserStoryCreateService extends AbstractService<Manager, UserStory> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private ManagerUserStoryRepository repository;

	// AbstractService interface ----------------------------------------------


	@Override
	public void authorise() {

		boolean status = true;

		if (super.getRequest().hasData("projectId")) {
			Integer projectId;
			Project project;
			int managerId;

			projectId = super.getRequest().getData("projectId", int.class);
			project = this.repository.findOneProjectById(projectId);

			managerId = super.getRequest().getPrincipal().getActiveRoleId();

			status = managerId == project.getManager().getId() && project.isDraftMode();

		}

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		UserStory object;

		object = new UserStory();
		Integer managerId = super.getRequest().getPrincipal().getActiveRoleId();
		Manager manager = this.repository.findOneManagerById(managerId);

		object.setManager(manager);
		object.setDraftMode(true);

		super.getBuffer().addData(object);
	}

	@Override
	public void bind(final UserStory object) {
		assert object != null;

		super.bind(object, "title", "description", "estimatedCost", "priority", "link", "acceptanceCriteria");
	}

	@Override
	public void validate(final UserStory object) {
		assert object != null;

		if (super.getRequest().hasData("projectId") && !super.getBuffer().getErrors().hasErrors("published project")) {
			Integer projectId;
			Project project;

			projectId = super.getRequest().getData("projectId", int.class);
			project = this.repository.findOneProjectById(projectId);

			super.state(project.isDraftMode(), "*", "manager.user-story.form.error.project-published");
		}
		if (!super.getBuffer().getErrors().hasErrors("estimatedCost")) {
			double maxDouble = Double.MAX_VALUE;
			super.state(object.getEstimatedCost() < maxDouble, "cost", "manager.project.form.error.not-valid-currency");
		}

	}

	@Override
	public void perform(final UserStory object) {
		assert object != null;

		this.repository.save(object);

		if (super.getRequest().hasData("projectId")) {
			Integer projectId;
			Project project;

			projectId = super.getRequest().getData("projectId", int.class);
			project = this.repository.findOneProjectById(projectId);

			UserStoryProject usp = new UserStoryProject();
			usp.setProject(project);
			usp.setUserStory(object);
			this.repository.save(usp);
		}

	}

	@Override
	public void unbind(final UserStory object) {
		assert object != null;

		Dataset dataset;
		SelectChoices choices;

		choices = SelectChoices.from(Priority.class, object.getPriority());

		dataset = super.unbind(object, "title", "description", "estimatedCost", "priority", "link", "acceptanceCriteria", "draftMode");
		dataset.put("priorities", choices);

		if (super.getRequest().hasData("projectId"))
			dataset.put("projectId", super.getRequest().getData("projectId", int.class));

		super.getResponse().addData(dataset);
	}
}
