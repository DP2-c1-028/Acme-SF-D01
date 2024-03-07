
package acme.forms;

import acme.client.data.AbstractForm;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuditorDashboard extends AbstractForm {

	// Serialisation identifier -----------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	int							totalCodeAuditsStatic;
	int							totalCodeAuditsDynamic;
	Double						auditRecordsAverageTime;
	Double						auditRecordsDeviationTime;
	int							auditRecordsMinimumTime;
	int							auditRecordsMaximumTime;
	Double						periodAverageTime;
	Double						periodDeviationTime;
	int							periodMinimumTime;
	int							periodMaximumTime;

	// Derived attributes -----------------------------------------------------

	// Relationships ----------------------------------------------------------

}
