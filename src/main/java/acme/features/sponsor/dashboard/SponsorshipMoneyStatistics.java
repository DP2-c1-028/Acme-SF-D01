
package acme.features.sponsor.dashboard;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SponsorshipMoneyStatistics {

	Double	minimumAmount;
	Double	maximumAmount;
	Double	averageAmount;
	Double	deviationAmount;


	public SponsorshipMoneyStatistics(final Double minimumAmount, final Double maximumAmount, final Double averageAmount, final Double deviationAmount) {
		this.minimumAmount = minimumAmount;
		this.maximumAmount = maximumAmount;
		this.averageAmount = averageAmount;
		this.deviationAmount = deviationAmount;

	}
}
