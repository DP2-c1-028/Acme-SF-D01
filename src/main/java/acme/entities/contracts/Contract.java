
package acme.entities.contracts;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.PositiveOrZero;

import org.hibernate.validator.constraints.Length;

import acme.client.data.AbstractEntity;
import acme.entities.projects.Project;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Contract extends AbstractEntity {

	// Serialisation identifier -----------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@NotBlank
	@Pattern(regexp = "[A-Z]{1-3}-\\d{3}")
	@Column(unique = true)
	private String				code;

	@NotNull
	@Past
	@Temporal(TemporalType.TIMESTAMP)
	private Date				instantiationMoment;

	@NotBlank
	@Length(max = 75)
	private String				providerName;

	@NotBlank
	@Length(max = 100)
	private String				customerName;

	@NotBlank
	@Length(max = 100)
	private String				goals;

	//hay q conectar este atributo al proyecto de alguna forma para que no sea mayor que el cost
	//posible hacerlo mediante una funcion trayendonos el proyecto y mirando que proyect.cost>= budget

	@NotNull
	@PositiveOrZero
	private Double				budget;

	// Derived attributes -----------------------------------------------------

	// Relationships ----------------------------------------------------------

	@ManyToOne(optional = false)
	@Valid
	private Project				project;

}
