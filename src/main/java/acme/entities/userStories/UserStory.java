
package acme.entities.userStories;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import acme.client.data.AbstractEntity;
import acme.entities.projects.Project;
import acme.roles.Manager;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class UserStory extends AbstractEntity {
	// Serialisation identifier -----------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@NotBlank
	@Length(max = 75)
	private String				title;

	@NotBlank
	@Length(max = 100)
	private String				description;

	@PositiveOrZero
	@NotNull
	private Double				estimatedCost;

	@Valid
	@NotNull
	private Priority			priority;

	@NotBlank
	@Length(max = 100)
	private String				acceptanceCriteria;

	@URL
	private String				optionalLink;

	// Relations -------------------------------------------------------------

	@ManyToOne
	@Valid
	private Manager				manager;

	@ManyToOne
	@Valid
	private Project				project;

}
