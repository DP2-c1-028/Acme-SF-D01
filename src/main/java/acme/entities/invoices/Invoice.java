
package acme.entities.invoices;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
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

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import acme.client.data.AbstractEntity;
import acme.client.data.datatypes.Money;
import acme.entities.sponsorships.Sponsorship;
import acme.roles.Sponsor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(indexes = {
	@Index(columnList = "sponsorship_id"), @Index(columnList = "sponsorship_id, draftMode"), @Index(columnList = "code")
})

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
	@Length(max = 255)
	private String				link;

	private boolean				draftMode;

	// Derived Attributes -------------------------------------------------------------


	@Transient
	public Money totalAmount() {
		Double amount;
		String currency;
		if (this.quantity == null) {
			amount = 0.0;
			currency = "";
		} else if (this.tax == null) {
			amount = this.quantity.getAmount();
			currency = this.quantity.getCurrency();
		} else {
			amount = this.quantity.getAmount() + this.tax * this.quantity.getAmount();
			currency = this.quantity.getCurrency();
		}
		Money value = new Money();
		value.setAmount(amount);
		value.setCurrency(currency);
		return value;
	}


	@ManyToOne(optional = false)
	@NotNull
	@Valid
	private Sponsorship	sponsorship;

	@ManyToOne(optional = false)
	@NotNull
	@Valid
	private Sponsor		sponsor;
}
