<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>


<jstl:if test="${sponsorshipMoneyStatistics.isEmpty() && invoiceMoneyStatistics.isEmpty() }">
	<h2>
		<acme:message code="sponsor.dashboard.form.title.general-indicators"/>
	</h2>
	<acme:message code="sponsor.dashboard.error.no-sponsorship-statistics"/>
	<table class="table table-sm">
	
		<tr>
			<th scope="row">
				<acme:message code="sponsor.dashboard.form.label.total-number-of-sponsorships-with-link"/>
			</th>
			<td>
				<acme:print value="${totalNumberOfSponsorshipsWithLink}"/>
			</td>
		</tr>
	</table>
	
	<h2>
		<acme:message code="sponsor.dashboard.form.title.invoices-indicators"/>
	</h2>
	
	<acme:message code="sponsor.dashboard.error.no-invoice-statistics"/>
	<table class="table table-sm">
	
			<tr>
				<th scope="row">
					<acme:message code="sponsor.dashboard.form.total-number-of-invoices"/>
				</th>
				<td>
					<acme:print value="${totalNumberOfInvoices}"/>
				</td>
			</tr>
	</table>
</jstl:if>




<jstl:if test="${!sponsorshipMoneyStatistics.isEmpty() && !invoiceMoneyStatistics.isEmpty() }">

	<h2>
		<acme:message code="sponsor.dashboard.form.title.general-indicators"/>
	</h2>

	<table class="table table-sm">
	
			<tr>
				<th scope="row">
					<acme:message code="sponsor.dashboard.form.label.total-number-of-sponsorships-with-link"/>
				</th>
				<td>
					<acme:print value="${totalNumberOfSponsorshipsWithLink}"/>
				</td>
			</tr>
		<jstl:forEach var="par" items="${sponsorshipMoneyStatistics}">
			<tr>
				<th>
					<acme:print value="(${par.key})"/>
				</th>
			</tr>
				
			<tr>
				<th scope="row">
					<acme:message code="sponsor.dashboard.form.label.sponsorships-average-amount"/>
				</th>
				<td>
					<acme:print value="${String.format('%.2f', par.value.averageAmount)} (${par.key})"/>
				</td>
			</tr>
			<tr>
			<th scope="row">
					<acme:message code="sponsor.dashboard.form.label.sponsorships-deviation-amount"/>
				</th>
				<td>
					<acme:print value="${String.format('%.2f', par.value.deviationAmount)} (${par.key})"/>
				</td>
			</tr>
			<tr>
				<th scope="row">
					<acme:message code="sponsor.dashboard.form.label.sponsorships-minimum-amount"/>
				</th>
				<td>
					<acme:print value="${String.format('%.2f', par.value.minimumAmount)} (${par.key})"/>
				</td>
			</tr>
			<tr>
			<th scope="row">
					<acme:message code="sponsor.dashboard.form.label.sponsorships-maximum-amount"/>
				</th>
				<td>
					<acme:print value="${String.format('%.2f', par.value.maximumAmount)} (${par.key})"/>
				</td>
			</tr>
		</jstl:forEach>
	</table>
	
	<h2>
		<acme:message code="sponsor.dashboard.form.title.invoices-indicators"/>
	</h2>
	
	<table class="table table-sm">
	
			<tr>
				<th scope="row">
					<acme:message code="sponsor.dashboard.form.total-number-of-invoices"/>
				</th>
				<td>
					<acme:print value="${totalNumberOfInvoices}"/>
				</td>
			</tr>
		<jstl:forEach var="par" items="${invoiceMoneyStatistics}">
			<tr>
				<th>
					<acme:print value="(${par.key})"/>
				</th>
			</tr>
			
			<tr>
				<th scope="row">
					<acme:message code="sponsor.dashboard.form.label.invoices-average-quantity"/>
				</th>
				<td>
					<acme:print value="${String.format('%.2f', par.value.invoicesAverageQuantity)} (${par.key})"/>
				</td>
			</tr>
			<tr>
			<th scope="row">
					<acme:message code="sponsor.dashboard.form.label.invoices-deviation-quantity"/>
				</th>
				<td>
					<acme:print value="${String.format('%.2f', par.value.invoicesDeviationQuantity)} (${par.key})"/>
				</td>
			</tr>
			<tr>
				<th scope="row">
					<acme:message code="sponsor.dashboard.form.label.invoices-minimum-quantity"/>
				</th>
				<td>
					<acme:print value="${String.format('%.2f', par.value.invoicesMinimumQuantity)} (${par.key})"/>
				</td>
			</tr>
			<tr>
			<th scope="row">
					<acme:message code="sponsor.dashboard.form.label.invoices-maximum-quantity"/>
				</th>
				<td>
					<acme:print value="${String.format('%.2f', par.value.invoicesMaximumQuantity)} (${par.key})"/>
				</td>
			</tr>
		</jstl:forEach>
	</table>
</jstl:if>

<jstl:if test="${sponsorshipMoneyStatistics.isEmpty() && !invoiceMoneyStatistics.isEmpty() }">

	<acme:message code="sponsor.dashboard.error.no-sponsorship-statistics"/>
	<table class="table table-sm">
	
		<tr>
			<th scope="row">
				<acme:message code="sponsor.dashboard.form.label.total-number-of-sponsorships-with-link"/>
			</th>
			<td>
				<acme:print value="${totalNumberOfSponsorshipsWithLink}"/>
			</td>
		</tr>
	</table>
	
	<h2>
		<acme:message code="sponsor.dashboard.form.title.invoices-indicators"/>
	</h2>
	
	<table class="table table-sm">
	
			<tr>
				<th scope="row">
					<acme:message code="sponsor.dashboard.form.total-number-of-invoices"/>
				</th>
				<td>
					<acme:print value="${totalNumberOfInvoices}"/>
				</td>
			</tr>
		<jstl:forEach var="par" items="${invoiceMoneyStatistics}">
			<tr>
				<th>
					<acme:print value="(${par.key})"/>
				</th>
			</tr>
			
			<tr>
				<th scope="row">
					<acme:message code="sponsor.dashboard.form.label.invoices-average-quantity"/>
				</th>
				<td>
					<acme:print value="${String.format('%.2f', par.value.invoicesAverageQuantity)} (${par.key})"/>
				</td>
			</tr>
			<tr>
			<th scope="row">
					<acme:message code="sponsor.dashboard.form.label.invoices-deviation-quantity"/>
				</th>
				<td>
					<acme:print value="${String.format('%.2f', par.value.invoicesDeviationQuantity)} (${par.key})"/>
				</td>
			</tr>
			<tr>
				<th scope="row">
					<acme:message code="sponsor.dashboard.form.label.invoices-minimum-quantity"/>
				</th>
				<td>
					<acme:print value="${String.format('%.2f', par.value.invoicesMinimumQuantity)} (${par.key})"/>
				</td>
			</tr>
			<tr>
			<th scope="row">
					<acme:message code="sponsor.dashboard.form.label.invoices-maximum-quantity"/>
				</th>
				<td>
					<acme:print value="${String.format('%.2f', par.value.invoicesMaximumQuantity)} (${par.key})"/>
				</td>
			</tr>
		</jstl:forEach>
	</table>
</jstl:if>

<acme:return/>

