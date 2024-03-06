
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

	Integer						totalLogsWithCompletenessBelow25;

	Integer						totalLogsWithCompletenessBetween25And50;

	Integer						totalLogsWithCompletenessBetween50And75;

	Integer						totalLogsWithCompletenessAbove75;

	Double						averageBudgetOfContracts;

	Double						deviationOfContractBudgets;

	Double						minimunBudgetOfContracts;

	Double						maximunBudgetOfContracts;

}
