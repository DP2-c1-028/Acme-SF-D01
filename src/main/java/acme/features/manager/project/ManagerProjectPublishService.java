
package acme.features.manager.project;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.models.Dataset;
import acme.client.services.AbstractService;
import acme.components.SystemConfigurationRepository;
import acme.entities.projects.Project;
import acme.entities.userStories.UserStory;
import acme.roles.Manager;

@Service
public class ManagerProjectPublishService extends AbstractService<Manager, Project> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private ManagerProjectRepository		repository;

	@Autowired
	private SystemConfigurationRepository	systemConfigurationRepository;

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
		int id;

		id = super.getRequest().getData("id", int.class);
		object = this.repository.findOneProjectById(id);

		super.getBuffer().addData(object);
	}

	@Override
	public void bind(final Project object) {
		assert object != null;

		super.bind(object, "title", "code", "abstractText", "cost", "link");
	}

	@Override
	public void validate(final Project object) {
		assert object != null;

		if (!super.getBuffer().getErrors().hasErrors("hasFatalError"))
			super.state(!object.isHasFatalError(), "*", "manager.project.form.error.hasFatalError");

		if (!super.getBuffer().getErrors().hasErrors("userStory")) {
			Collection<UserStory> userStories = this.repository.findUserStoryByProjectId(object.getId());
			super.state(!userStories.isEmpty(), "*", "manager.project.form.error.atLeastOneUserStory");
			boolean userStoriesPublished = userStories.stream().allMatch(u -> !u.isDraftMode());
			super.state(userStoriesPublished, "*", "manager.project.form.error.userStoriesPublished");
		}

		if (!super.getBuffer().getErrors().hasErrors("cost") && object.getCost() != null)
			super.state(object.getCost().getAmount() >= 0, "cost", "manager.project.form.error.cost-negative");

		if (!super.getBuffer().getErrors().hasErrors("code")) {

			Project projectSameCode = this.repository.findOneProjectByCode(object.getCode());

			if (projectSameCode != null)
				super.state(projectSameCode.getId() == object.getId(), "code", "manager.project.form.error.code");
		}

		if (!super.getBuffer().getErrors().hasErrors("cost"))
			super.state(object.getCost() != null, "cost", "manager.project.form.error.cost-null");

		if (!super.getBuffer().getErrors().hasErrors("cost") && object.getCost() != null) {
			String symbol = object.getCost().getCurrency();
			boolean existsCurrency = this.systemConfigurationRepository.existsCurrency(symbol);
			super.state(existsCurrency, "cost", "manager.project.form.error.not-valid-currency");
		}

		if (!super.getBuffer().getErrors().hasErrors("cost") && object.getCost() != null)
			super.state(object.getCost().getAmount() < 1000000, "cost", "manager.project.form.error.not-valid-currency");
	}

	@Override
	public void perform(final Project object) {
		assert object != null;

		object.setDraftMode(false);
		this.repository.save(object);
	}

	@Override
	public void unbind(final Project object) {
		assert object != null;

		Dataset dataset;

		dataset = super.unbind(object, "title", "code", "abstractText", "cost", "link", "draftMode");

		super.getResponse().addData(dataset);
	}
}
