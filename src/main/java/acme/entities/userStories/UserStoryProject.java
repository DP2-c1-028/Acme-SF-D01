
package acme.entities.userStories;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import acme.client.data.AbstractEntity;
import acme.entities.projects.Project;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class UserStoryProject extends AbstractEntity {
	// Serialisation identifier -----------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Relations -------------------------------------------------------------

	@ManyToOne(optional = false)
	@NotNull
	@Valid
	private Project				project;

	@ManyToOne(optional = false)
	@NotNull
	@Valid
	private UserStory			userStory;

}
