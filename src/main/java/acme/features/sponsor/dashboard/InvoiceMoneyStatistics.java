
package acme.features.sponsor.dashboard;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvoiceMoneyStatistics {

	Double	invoicesMinimumQuantity;
	Double	invoicesMaximumQuantity;
	Double	invoicesAverageQuantity;
	Double	invoicesDeviationQuantity;


	public InvoiceMoneyStatistics(final Double invoicesMinimumQuantity, final Double invoicesMaximumQuantity, final Double invoicesAverageQuantity, final Double invoicesDeviationQuantity) {

		this.invoicesMinimumQuantity = invoicesMinimumQuantity;
		this.invoicesMaximumQuantity = invoicesMaximumQuantity;
		this.invoicesAverageQuantity = invoicesAverageQuantity;
		this.invoicesDeviationQuantity = invoicesDeviationQuantity;
	}
}
