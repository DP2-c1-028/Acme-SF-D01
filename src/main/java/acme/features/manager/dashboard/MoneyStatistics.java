
package acme.features.manager.dashboard;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MoneyStatistics {

	Double	minimumCost;
	Double	maximumCost;
	Double	averageCost;
	Double	deviationCost;


	public MoneyStatistics(final Double minimumCost, final Double maximumCost, final Double averageCost, final Double deviationCost) {
		this.minimumCost = minimumCost;
		this.maximumCost = maximumCost;
		this.averageCost = averageCost;
		this.deviationCost = deviationCost;
	}
}
