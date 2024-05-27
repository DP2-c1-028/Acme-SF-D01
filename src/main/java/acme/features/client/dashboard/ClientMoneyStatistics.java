
package acme.features.client.dashboard;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientMoneyStatistics {

	Double	minimunBudget;
	Double	maximunBudget;
	Double	averageBudget;
	Double	budgetDeviation;


	public ClientMoneyStatistics(final Double minimunBudget, final Double maximunBudget, final Double averageBudget, final Double deviationBudget) {
		this.minimunBudget = minimunBudget;
		this.maximunBudget = maximunBudget;
		this.averageBudget = averageBudget;
		this.budgetDeviation = deviationBudget;
	}
}
