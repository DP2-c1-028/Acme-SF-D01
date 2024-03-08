
package acme.entities.invoices;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.URL;

import acme.client.data.AbstractEntity;
import acme.client.data.datatypes.Money;
import acme.entities.sponsorships.Sponsorship;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Invoice extends AbstractEntity {

	// Serialisation identifier -----------------------------------------------
	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@NotBlank
	@Pattern(regexp = "IN-[0-9]{4}-[0-9]{4}")
	@Column(unique = true)
	private String				code;

	@Past
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	private Date				registrationTime;

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	private Date				dueDate;

	@NotNull
	private Money				quantity;

	@DecimalMin(value = "0.00")
	@DecimalMax(value = "1.00")
	private Double				tax;

	@URL
	private String				link;

	// Derived Attributes -------------------------------------------------------------


	@Transient
	public Double totalAmount() {
		return this.quantity.getAmount() + this.tax * this.quantity.getAmount();
	}


	@ManyToOne(optional = false)
	@NotNull
	@Valid
	private Sponsorship sponsorship;
}
