
package acme.features.manager.userStoryProject;

import java.util.List;

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
public class ManagerUserStoryProjectShowService extends AbstractService<Manager, UserStoryProject> {

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

		status = managerId == userStoryProject.getProject().getManager().getId();

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
	public void unbind(final UserStoryProject object) {
		assert object != null;

		Dataset dataset;
		SelectChoices choicesProject;
		SelectChoices choicesUserStory;
		int managerId;

		managerId = super.getRequest().getPrincipal().getActiveRoleId();

		List<Project> projectsOwned = this.repository.findAllProjectsOwnedAndNotPublished(managerId).stream().toList();
		List<UserStory> userStoriesOwned = this.repository.findAllUserStoriesOwned(managerId).stream().toList();

		if (projectsOwned.isEmpty())
			projectsOwned.add(object.getProject());

		Project preselectedProject = projectsOwned.stream().toList().get(0);
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
		dataset.put("draftMode", object.getProject().isDraftMode());

		super.getResponse().addData(dataset);
	}
}
