
package acme.forms;

import java.util.Map;

import acme.client.data.AbstractForm;
import acme.features.client.dashboard.ClientMoneyStatistics;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientDashboard extends AbstractForm {

	// Serialisation identifier -----------------------------------------------

	private static final long			serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	int									totalLogsWithCompletenessBelow25;

	int									totalLogsWithCompletenessBetween25And50;

	int									totalLogsWithCompletenessBetween50And75;

	int									totalLogsWithCompletenessAbove75;

	Map<String, ClientMoneyStatistics>	moneyStatistics;

}
