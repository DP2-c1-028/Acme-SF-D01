
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

	Integer						totalCodeAuditsStatic;
	Integer						totalCodeAuditsDynamic;
	Double						auditRecordsAverageTime;
	Double						auditRecordsDeviationTime;
	Integer						auditRecordsMinimumTime;
	Integer						auditRecordsMaximumTime;
	Double						periodAverageTime;
	Double						periodDeviationTime;
	Integer						periodMinimumTime;
	Integer						periodMaximumTime;

	// Derived attributes -----------------------------------------------------

	// Relationships ----------------------------------------------------------

}
