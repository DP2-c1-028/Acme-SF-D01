
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
public class ManagerUserStoryProjectCreateService extends AbstractService<Manager, UserStoryProject> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private ManagerUserStoryProjectRepository repository;

	// AbstractService interface ----------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		UserStoryProject object;

		object = new UserStoryProject();
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

		if (!super.getBuffer().getErrors().hasErrors("userStoryProject")) {

			UserStoryProject uspSameData = this.repository.findOneUserStoryProjectByProjectAndUserStory(object.getProject().getId(), object.getUserStory().getId());

			super.state(uspSameData == null, "*", "manager.user-story-project.form.error.same");
		}
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

		Collection<Project> projectsOwned = this.repository.findAllProjectsOwned(managerId);
		Collection<UserStory> userStoriesOwned = this.repository.findAllUserStoriesOwned(managerId);

		Project preselectedProject = (Project) projectsOwned.toArray()[0];
		UserStory preselectedUserStory = (UserStory) userStoriesOwned.toArray()[0];

		choicesProject = SelectChoices.from(projectsOwned, "code", preselectedProject);
		choicesUserStory = SelectChoices.from(userStoriesOwned, "title", preselectedUserStory);

		dataset = new Dataset();

		dataset.put("project", preselectedProject.getCode());
		dataset.put("userStory", preselectedUserStory.getTitle());
		dataset.put("projects", choicesProject);
		dataset.put("userStories", choicesUserStory);
		dataset.put("id", object.getId());
		dataset.put("version", object.getVersion());

		super.getResponse().addData(dataset);
	}
}
