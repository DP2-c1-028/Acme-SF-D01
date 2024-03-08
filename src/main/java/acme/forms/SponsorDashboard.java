
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

	int							totalNumberOfInvoices;
	int							totalNumberOfSponsorshipsWithLink;
	Double						invoicesAverageQuantity;
	Double						invoicesDeviationQuantity;
	int							invoicesMinimumQuantity;
	int							invoicesMaximumQuantity;

}
