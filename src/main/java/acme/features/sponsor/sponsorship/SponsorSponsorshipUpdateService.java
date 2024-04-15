
package acme.features.sponsor.sponsorship;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.models.Dataset;
import acme.client.services.AbstractService;
import acme.client.views.SelectChoices;
import acme.entities.projects.Project;
import acme.entities.sponsorships.Sponsorship;
import acme.entities.sponsorships.SponsorshipType;
import acme.roles.Sponsor;

@Service
public class SponsorSponsorshipUpdateService extends AbstractService<Sponsor, Sponsorship> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private SponsorSponsorshipRepository repository;

	// AbstractService interface ----------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int id;
		int sponsorId;
		Sponsorship sponsorship;

		id = super.getRequest().getData("id", int.class);
		sponsorship = this.repository.findOneSponsorshipById(id);

		sponsorId = super.getRequest().getPrincipal().getActiveRoleId();

		status = sponsorId == sponsorship.getSponsor().getId() && sponsorship.isDraftMode();

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Collection<Sponsorship> objects;
		int sponsorId;

		sponsorId = super.getRequest().getPrincipal().getActiveRoleId();

		objects = this.repository.findSponsorshipBySponsorId(sponsorId);

		super.getBuffer().addData(objects);
	}

	@Override
	public void bind(final Sponsorship object) {
		assert object != null;

		super.bind(object, "code", "moment", "durationStartTime", "durationEndTime", "amount", "type", "email", "link");
	}

	@Override
	public void validate(final Sponsorship object) {
		assert object != null;

		if (!super.getBuffer().getErrors().hasErrors("code")) {

			Sponsorship projectSameCode = this.repository.findOneSponsorshipByCode(object.getCode());

			if (projectSameCode != null)
				super.state(projectSameCode.getId() == object.getId(), "code", "sponsor.sponsorship.form.error.code");
		}
	}

	@Override
	public void perform(final Sponsorship object) {
		assert object != null;

		this.repository.save(object);
	}

	@Override
	public void unbind(final Sponsorship object) {
		assert object != null;

		SelectChoices choices;
		Collection<Project> projects = this.repository.findProjects();
		SelectChoices choices2;
		Dataset dataset;

		choices = SelectChoices.from(SponsorshipType.class, object.getType());
		choices2 = SelectChoices.from(projects, "title", object.getProject());

		dataset = super.unbind(object, "code", "moment", "durationStartTime", "durationEndTime", "amount", "type", "email", "link", "draftMode");
		dataset.put("types", choices);
		dataset.put("projects", choices2);

		super.getResponse().addData(dataset);
	}

}
