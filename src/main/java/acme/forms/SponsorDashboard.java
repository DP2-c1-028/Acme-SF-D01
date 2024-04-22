
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

	Double						sponsorshipsAverageAmount;
	Double						sponsorshipsDeviationAmount;
	Double						sponsorshipsMinimumAmount;
	Double						sponsorshipsMaximumAmount;

	Double						invoicesAverageQuantity;
	Double						invoicesDeviationQuantity;
	Double						invoicesMinimumQuantity;
	Double						invoicesMaximumQuantity;

}
