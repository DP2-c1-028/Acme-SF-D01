
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

	int							totalNumberOfMust;
	int							totalNumberOfShould;
	int							totalNumberOfCould;
	int							totalNumberOfWont;
	Double						averageEstimatedCost;
	Double						deviationEstimatedCost;
	double						minimumEstimatedCost;
	double						maximumEstimatedCost;
	Double						averageCost;
	Double						deviationCost;
	double						minimumCost;
	double						maximumCost;

	// Derived attributes -----------------------------------------------------

	// Relationships ----------------------------------------------------------

}
