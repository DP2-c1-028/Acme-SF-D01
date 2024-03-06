
package acme.forms;

import acme.client.data.AbstractForm;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ManagerDashboard extends AbstractForm {

	// Serialisation identifier -----------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	Integer						totalNumberOfMust;
	Integer						totalNumberOfShould;
	Integer						totalNumberOfCould;
	Integer						totalNumberOfWont;
	Double						averageEstimatedCost;
	Double						deviationEstimatedCost;
	Double						minimumEstimatedCost;
	Double						maximumEstimatedCost;
	Double						averageCost;
	Double						deviationCost;
	Double						minimumCost;
	Double						maximumCost;

	// Derived attributes -----------------------------------------------------

	// Relationships ----------------------------------------------------------

}
