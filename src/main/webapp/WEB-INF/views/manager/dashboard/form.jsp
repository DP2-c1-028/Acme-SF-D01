<%--
- form.jsp
-
- Copyright (C) 2012-2024 Rafael Corchuelo.
-
- In keeping with the traditional purpose of furthering education and research, it is
- the policy of the copyright owner to permit non-commercial use and redistribution of
- this software. It has been tested carefully, but it is not guaranteed for any particular
- purposes.  The copyright owner does not offer any warranties or representations, nor do
- they accept any liabilities with respect to them.
--%>

<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<h2>
	<acme:message code="manager.dashboard.form.title.general-indicators"/>
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

