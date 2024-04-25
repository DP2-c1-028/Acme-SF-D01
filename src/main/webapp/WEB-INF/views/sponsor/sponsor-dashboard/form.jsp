<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

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
	<tr>
		<th scope="row">
			<acme:message code="sponsor.dashboard.form.label.sponsorships-average-amount"/>
		</th>
		<td>
			<acme:print value="${sponsorshipsAverageAmount}"/>
		</td>
	</tr>
	<tr>
	<th scope="row">
			<acme:message code="sponsor.dashboard.form.label.sponsorships-deviation-amount"/>
		</th>
		<td>
			<acme:print value="${sponsorshipsDeviationAmount}"/>
		</td>
	</tr>
	<tr>
		<th scope="row">
			<acme:message code="sponsor.dashboard.form.label.sponsorships-minimum-amount"/>
		</th>
		<td>
			<acme:print value="${sponsorshipsMinimumAmount}"/>
		</td>
	</tr>
	<tr>
	<th scope="row">
			<acme:message code="sponsor.dashboard.form.label.sponsorships-maximum-amount"/>
		</th>
		<td>
			<acme:print value="${sponsorshipsMaximumAmount}"/>
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
	<tr>
		<th scope="row">
			<acme:message code="sponsor.dashboard.form.label.invoices-average-quantity"/>
		</th>
		<td>
			<acme:print value="${invoicesAverageQuantity}"/>
		</td>
	</tr>
	<tr>
	<th scope="row">
			<acme:message code="sponsor.dashboard.form.label.invoices-deviation-quantity"/>
		</th>
		<td>
			<acme:print value="${invoicesDeviationQuantity}"/>
		</td>
	</tr>
	<tr>
		<th scope="row">
			<acme:message code="sponsor.dashboard.form.label.invoices-minimum-quantity"/>
		</th>
		<td>
			<acme:print value="${invoicesMinimumQuantity}"/>
		</td>
	</tr>
	<tr>
	<th scope="row">
			<acme:message code="sponsor.dashboard.form.label.invoices-maximum-quantity"/>
		</th>
		<td>
			<acme:print value="${invoicesMaximumQuantity}"/>
		</td>
	</tr>

</table>
<acme:return/>

