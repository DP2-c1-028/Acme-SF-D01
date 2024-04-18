<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<h2>
	<acme:message code="developer.dashboard.form.title.general-indicators"/>
</h2>

<table class="table table-sm">
	<tr>
		<th scope="row">
			<acme:message code="developer.dashboard.form.label.total-training-modules-with-update-moment"/>
		</th>
		<td>
			<acme:print value="${totalTrainingModulesWithUpdateMoment}"/>
		</td>
	</tr>	
	<tr>
		<th scope="row">
			<acme:message code="developer.dashboard.form.label.total-training-sessions-with-link"/>
		</th>
		<td>
			<acme:print value="${totalTrainingSessionsWithLink}"/>
		</td>
	</tr>	
	<tr>
		<th scope="row">
			<acme:message code="developer.dashboard.form.label.training-modules-average-time"/>
		</th>
		<td>
			<acme:print value="${trainingModulesAverageTime}"/>
		</td>
	</tr>
	<tr>
	<th scope="row">
			<acme:message code="developer.dashboard.form.label.training-modules-deviation-time"/>
		</th>
		<td>
			<acme:print value="${trainingModulesDeviationTime}"/>
		</td>
	</tr>
	<tr>
		<th scope="row">
			<acme:message code="developer.dashboard.form.label.training-modules-minimum-time"/>
		</th>
		<td>
			<acme:print value="${trainingModulesMinimumTime}"/>
		</td>
	</tr>
	<tr>
	<th scope="row">
			<acme:message code="developer.dashboard.form.label.training-modules-maximum-time"/>
		</th>
		<td>
			<acme:print value="${trainingModulesMaximumTime}"/>
		</td>
	</tr>
</table>
<acme:return/>