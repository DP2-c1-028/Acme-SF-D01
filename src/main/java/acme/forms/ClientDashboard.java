
package acme.forms;

import acme.client.data.AbstractForm;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientDashboard extends AbstractForm {

	// Serialisation identifier -----------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	int							totalLogsWithCompletenessBelow25;

	int							totalLogsWithCompletenessBetween25And50;

	int							totalLogsWithCompletenessBetween50And75;

	int							totalLogsWithCompletenessAbove75;

	Double						averageBudgetOfContracts;

	Double						deviationOfContractBudgets;

	Double						minimunBudgetOfContracts;

	Double						maximunBudgetOfContracts;

}
