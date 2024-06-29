
package acme.entities.notice;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import acme.client.data.AbstractEntity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Notice extends AbstractEntity {
	// Serialisation identifier -----------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Past
	private Date				instantiationMoment;

	@NotBlank
	@Length(max = 75)
	private String				title;

	@NotBlank
	private String				username;

	@NotBlank
	@Pattern(regexp = "^.+, .+$")
	private String				fullName;

	@Length(max = 100)
	@NotBlank
	private String				message;

	@Email
	private String				email;

	@URL
	private String				link;


	@Transient
	public String author() {
		StringBuilder author;

		author = new StringBuilder();
		author.append(this.username);
		author.append(" - ");
		author.append(this.fullName);

		return author.toString();
	}

	@AssertTrue(message = "El campo 'author' no debe exceder los 75 caracteres")
	@Transient
	public boolean isAuthorLengthValid() {
		return this.author().length() <= 75;
	}
}
