
package acme.entities.currency;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;

import acme.client.data.AbstractEntity;
import acme.entities.systemConfiguration.SystemConfiguration;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Currency extends AbstractEntity {

	// Serialisation identifier ----------------------------------------------

	protected static final long	serialVersionUID	= 1L;

	// Attributes ------------------------------------------------------------

	@Pattern(regexp = "^[A-Z]{3}$")
	@NotBlank
	protected String			symbol;

	@NotNull
	@Positive
	protected Double			valueAgainstDollar;

	// Relations -------------------------------------------------------------

	@ManyToOne(optional = false)
	@NotNull
	@Valid
	private SystemConfiguration	systemConfiguration;

}
