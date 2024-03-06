
package acme.entities.notice;

import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import acme.client.data.AbstractEntity;

public class Notice extends AbstractEntity {
	// Serialisation identifier -----------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Temporal(TemporalType.TIMESTAMP)
	@Past
	private Date				instantiationMoment;

	@Length(max = 75)
	private String				title;

	@Pattern(regexp = "[A-Za-z0-9]+ - [A-Za-z]+, [A-Za-z]+")
	private String				author;

	@Length(max = 100)
	@NotBlank
	private String				message;

	@Email
	private String				emailAddress;

	@URL
	private String				link;

}
