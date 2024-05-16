<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<h2>
	<acme:message code="client.dashboard.form.title.budget-factors"/>
</h2>

<jstl:if test="${moneyStatistics.isEmpty()}">
	<acme:message code="client.dashboard.error.no-money-statistics"/>
</jstl:if>

<table class="table table-sm">

	<jstl:forEach var="par" items="${moneyStatistics}">
		<tr>
			<th>
				<acme:print value="(${par.key})"/>
			</th>
		</tr>
		<tr>
			<th scope="row">
				<acme:message code="client.dashboard.form.label.min-budget"/>
			</th>
			<td>
				<acme:print value="${String.format('%.2f', par.value.minimunBudget)} (${par.key})"/>
			</td>
		</tr>
		<tr>
			<th scope="row">
				<acme:message code="client.dashboard.form.label.max-budget"/>
			</th>
			<td>
				<acme:print value="${String.format('%.2f', par.value.maximunBudget)} (${par.key})"/>
			</td>
		</tr>
		
		<tr>
			<th scope="row">
				<acme:message code="client.dashboard.form.label.average"/>
			</th>
			<td>
				<acme:print value="${String.format('%.2f', par.value.averageBudget)} (${par.key})"/>
			</td>
		</tr>
		<tr>
			<th scope="row">
				<acme:message code="client.dashboard.form.label.deviation"/>
			</th>
			<td>
				<acme:print value="${String.format('%.2f', par.value.budgetDeviation)} (${par.key})"/>
			</td>
		</tr>
	</jstl:forEach>
</table>

<h2>
	<acme:message code="client.dashboard.form.title.logs-rate"/>
</h2>

<div>
	<canvas id="canvas"></canvas>
</div>

<script type="text/javascript">
	$(document).ready(function() {
		var data = {
			labels : [
					" [0:25) %", "[25:50) %", "[50:75) %", "[75:100) %"
			],
			datasets : [
				{
					data : [
						<jstl:out value="${totalLogsWithCompletenessBelow25}"/>, 
						<jstl:out value="${totalLogsWithCompletenessBetween25And50}"/>, 
						<jstl:out value="${totalLogsWithCompletenessBetween50And75}"/>,
						<jstl:out value="${totalLogsWithCompletenessAbove75}"/>
					]
				}
			]
		};
		var options = {
			scales : {
				yAxes : [
					{
						ticks : {
							suggestedMin : 0.0,
							suggestedMax : 10.0
						}
					}
				]
			},
			legend : {
				display : false
			}
		};
	
		var canvas, context;
	
		canvas = document.getElementById("canvas");
		context = canvas.getContext("2d");
		new Chart(context, {
			type : "bar",
			data : data,
			options : options
		});
	});
</script>

<acme:return/>