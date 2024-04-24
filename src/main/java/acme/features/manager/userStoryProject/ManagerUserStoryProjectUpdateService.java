
package acme.features.manager.userStoryProject;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.models.Dataset;
import acme.client.services.AbstractService;
import acme.client.views.SelectChoices;
import acme.entities.projects.Project;
import acme.entities.userStories.UserStory;
import acme.entities.userStories.UserStoryProject;
import acme.roles.Manager;

@Service
public class ManagerUserStoryProjectUpdateService extends AbstractService<Manager, UserStoryProject> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private ManagerUserStoryProjectRepository repository;

	// AbstractService interface ----------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int id;
		int managerId;
		UserStoryProject userStoryProject;

		id = super.getRequest().getData("id", int.class);
		userStoryProject = this.repository.findOneUserStoryProjectById(id);

		managerId = super.getRequest().getPrincipal().getActiveRoleId();

		status = managerId == userStoryProject.getProject().getManager().getId() && userStoryProject.getProject().isDraftMode();

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		UserStoryProject object;
		int id;

		id = super.getRequest().getData("id", int.class);
		object = this.repository.findOneUserStoryProjectById(id);

		super.getBuffer().addData(object);
	}

	@Override
	public void bind(final UserStoryProject object) {
		assert object != null;

		super.bind(object, "project", "userStory");
	}

	@Override
	public void validate(final UserStoryProject object) {
		assert object != null;
		int managerId;

		managerId = super.getRequest().getPrincipal().getActiveRoleId();

		if (!super.getBuffer().getErrors().hasErrors("userStoryProject") && object.getProject() != null && object.getUserStory() != null) {

			UserStoryProject uspSameData = this.repository.findOneUserStoryProjectByProjectAndUserStory(object.getProject().getId(), object.getUserStory().getId());

			if (uspSameData != null)
				super.state(uspSameData.getId() == object.getId(), "*", "manager.user-story-project.form.error.same");
		}

		if (!super.getBuffer().getErrors().hasErrors("project") && object.getProject() != null)
			super.state(object.getProject().getManager().getId() == managerId, "*", "manager.user-story-project.form.error.project-owned");

		if (!super.getBuffer().getErrors().hasErrors("userStory") && object.getUserStory() != null)
			super.state(object.getUserStory().getManager().getId() == managerId, "*", "manager.user-story-project.form.error.user-story-owned");

	}

	@Override
	public void perform(final UserStoryProject object) {
		assert object != null;

		this.repository.save(object);
	}

	@Override
	public void unbind(final UserStoryProject object) {
		assert object != null;

		Dataset dataset;
		SelectChoices choicesProject;
		SelectChoices choicesUserStory;
		int managerId;

		managerId = super.getRequest().getPrincipal().getActiveRoleId();

		Collection<Project> projectsOwned = this.repository.findAllProjectsOwnedAndNotPublished(managerId);
		Collection<UserStory> userStoriesOwned = this.repository.findAllUserStoriesOwned(managerId);

		Project preselectedProject = projectsOwned.stream().toList().isEmpty() ? null : projectsOwned.stream().toList().get(0);

		UserStory preselectedUserStory = userStoriesOwned.stream().toList().isEmpty() ? null : userStoriesOwned.stream().toList().get(0);

		choicesProject = SelectChoices.from(projectsOwned, "code", preselectedProject);
		choicesUserStory = SelectChoices.from(userStoriesOwned, "title", preselectedUserStory);

		dataset = new Dataset();

		String projectCode = preselectedProject != null ? preselectedProject.getCode() : null;
		String userStoryTitle = preselectedUserStory != null ? preselectedUserStory.getTitle() : null;

		dataset.put("project", projectCode);
		dataset.put("userStory", userStoryTitle);
		dataset.put("projects", choicesProject);
		dataset.put("userStories", choicesUserStory);
		dataset.put("id", object.getId());
		dataset.put("version", object.getVersion());

		super.getResponse().addData(dataset);
	}
}
