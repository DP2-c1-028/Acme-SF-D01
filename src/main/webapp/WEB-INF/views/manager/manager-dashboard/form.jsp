<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<h2>
	<acme:message code="manager.dashboard.form.title.general-indicators-projects"/>
</h2>

<jstl:if test="${moneyStatistics.isEmpty()}">
	<acme:message code="manager.dashboard.error.no-money-statistics"/>
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
			<acme:message code="manager.dashboard.form.label.average-cost"/>
		</th>
		<td>
			<acme:print value="${String.format('%.2f', par.value.averageCost)} (${par.key})"/>
		</td>
		</tr>
		<tr>
			<th scope="row">
				<acme:message code="manager.dashboard.form.label.deviation-cost"/>
			</th>
			<td>
				<acme:print value="${String.format('%.2f', par.value.deviationCost)} (${par.key})"/>
			</td>
		</tr>
		<tr>
			<th scope="row">
				<acme:message code="manager.dashboard.form.label.max-cost"/>
			</th>
			<td>
				<acme:print value="${String.format('%.2f', par.value.maximumCost)} (${par.key})"/>
			</td>
		</tr>
		<tr>
			<th scope="row">
				<acme:message code="manager.dashboard.form.label.min-cost"/>
			</th>
			<td>
				<acme:print value="${String.format('%.2f', par.value.minimumCost)} (${par.key})"/>
			</td>
		</tr>
	</jstl:forEach>
	
</table>

<h2>
	<acme:message code="manager.dashboard.form.title.general-indicators-user-stories"/>
</h2>

<table class="table table-sm">
	<tr>
		<th scope="row">
			<acme:message code="manager.dashboard.form.label.average-estimated-cost"/>
		</th>
		<td>
			<acme:print value="${averageEstimatedCost}"/>
		</td>
	</tr>
	<tr>
		<th scope="row">
			<acme:message code="manager.dashboard.form.label.deviation-estimated-cost"/>
		</th>
		<td>
			<acme:print value="${deviationEstimatedCost}"/>
		</td>
	</tr>
	<tr>
		<th scope="row">
			<acme:message code="manager.dashboard.form.label.max-estimated-cost"/>
		</th>
		<td>
			<acme:print value="${maximumEstimatedCost}"/>
		</td>
	</tr>
	<tr>
		<th scope="row">
			<acme:message code="manager.dashboard.form.label.min-estimated-cost"/>
		</th>
		<td>
			<acme:print value="${minimumEstimatedCost}"/>
		</td>
	</tr>
</table>



<h2>
	<acme:message code="manager.dashboard.form.title.priorities"/>
</h2>

<div>
	<canvas id="canvas"></canvas>
</div>

<script type="text/javascript">
	$(document).ready(function() {
		var data = {
			labels : [
					"MUST", "SHOULD", "COULD", "WONT"
			],
			datasets : [
				{
					data : [
						<jstl:out value="${totalNumberOfMust}"/>, 
						<jstl:out value="${totalNumberOfShould}"/>, 
						<jstl:out value="${totalNumberOfCould}"/>,
						<jstl:out value="${totalNumberOfWont}"/>
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

