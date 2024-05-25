
package acme.forms;

import java.util.Map;

import acme.client.data.AbstractForm;
import acme.features.sponsor.dashboard.InvoiceMoneyStatistics;
import acme.features.sponsor.dashboard.SponsorshipMoneyStatistics;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SponsorDashboard extends AbstractForm {
	// Serialisation identifier -----------------------------------------------

	private static final long				serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	int										totalNumberOfInvoices;
	int										totalNumberOfSponsorshipsWithLink;

	Map<String, SponsorshipMoneyStatistics>	sponsorshipMoneyStatistics;
	Map<String, InvoiceMoneyStatistics>		invoiceMoneyStatistics;

}
