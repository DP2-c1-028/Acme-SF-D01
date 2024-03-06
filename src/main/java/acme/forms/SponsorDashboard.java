
package acme.forms;

import acme.client.data.AbstractForm;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SponsorDashboard extends AbstractForm {
	// Serialisation identifier -----------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	Integer						totalNumberOfInvoices;
	Integer						totalNumberOfSponsorshipsWithLink;
	Double						invoicesAverageQuantity;
	Double						invoicesDeviationQuantity;
	Integer						invoicesMinimumQuantity;
	Integer						invoicesMaximumQuantity;

}
