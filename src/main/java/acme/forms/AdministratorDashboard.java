
package acme.forms;

import acme.client.data.AbstractForm;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdministratorDashboard extends AbstractForm {

	// Serialisation identifier -----------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	int							totalNumberOfAuditorPrincipals;
	int							totalNumberOfClientPrincipals;
	int							totalNumberOfDeveloperPrincipals;
	int							totalNumberOfManagerPrincipals;
	int							totalNumberOfSponsorPrincipals;
	Double						noticeRatioWithEmailAndLink;
	Double						criticalObjectiveRatio;
	Double						nonCriticalObjectiveRatio;
	Double						averageValueInTheRisk;
	int							minimumValueInTheRisk;
	int							maximumValueInTheRisk;
	Double						standardDeviationValueInTheRisk;
	Double						averageNumberOfRecientClaims;
	int							minimumNumberOfRecientClaims;
	int							maximumNumberOfRecientClaims;
	Double						standardDeviationOfNumberOfRecientClaims;

	// Derived attributes -----------------------------------------------------

	// Relationships ----------------------------------------------------------

}
