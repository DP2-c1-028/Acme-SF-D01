
package acme.features.auditor.dashboard;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface AuditorDashboardRepository extends AbstractRepository {

	@Query("select count(ca) from CodeAudit ca where ca.type = acme.entities.codeAudits.CodeAuditType.Static and ca.auditor.id = :auditorId")
	int totalCodeAuditsStatic(int auditorId);

	@Query("select count(ca) from CodeAudit ca where ca.type = acme.entities.codeAudits.CodeAuditType.Dynamic and ca.auditor.id = :auditorId")
	int totalCodeAuditsDynamic(int auditorId);

	@Query("select (select count(ar) from AuditRecord ar where ar.codeAudit.id = ca.id) from CodeAudit ca where ca.auditor.id = :auditorId")
	Collection<Double> auditRecordsPerAudit(int auditorId);

	@Query("select avg(select count(ar) from AuditRecord ar where ar.codeAudit.id = ca.id) from CodeAudit ca where ca.auditor.id = :auditorId")
	Double auditRecordsAverage(int auditorId);

	@Query("select min(select count(ar) from AuditRecord ar where ar.codeAudit.id = ca.id) from CodeAudit ca where ca.auditor.id = :auditorId")
	int auditRecordsMinimum(int auditorId);

	@Query("select max(select count(ar) from AuditRecord ar where ar.codeAudit.id = ca.id) from CodeAudit ca where ca.auditor.id = :auditorId")
	int auditRecordsMaximum(int auditorId);

	@Query("select avg(time_to_sec(timediff(ar.auditEndTime, ar.auditStartTime)) / 3600) from AuditRecord ar where ar.codeAudit.auditor.id = :auditorId")
	Double periodAverageTime(int auditorId);

	@Query("select stddev(time_to_sec(timediff(ar.auditEndTime, ar.auditStartTime)) / 3600) from AuditRecord ar where ar.codeAudit.auditor.id = :auditorId")
	Double periodDeviationTime(int auditorId);

	@Query("select min(time_to_sec(timediff(ar.auditEndTime, ar.auditStartTime)) / 3600) from AuditRecord ar where ar.codeAudit.auditor.id = :auditorId")
	Double periodMinimumTime(int auditorId);

	@Query("select max(time_to_sec(timediff(ar.auditEndTime, ar.auditStartTime)) / 3600) from AuditRecord ar where ar.codeAudit.auditor.id = :auditorId")
	Double periodMaximumTime(int auditorId);

}
