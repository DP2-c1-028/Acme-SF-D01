<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="sponsor.invoice.list.label.code" path="code" width="10%"/>
	<acme:list-column code="sponsor.invoice.list.label.registrationTime" path="registrationTime" width="10%"/>
	<acme:list-column code="sponsor.invoice.list.label.dueDate" path="dueDate" width="10%"/>
	<acme:list-column code="sponsor.invoice.list.label.quantity" path="quantity" width="10%"/>
	<acme:list-column code="sponsor.invoice.list.label.tax" path="tax" width="10%"/>
	<acme:list-column code="sponsor.invoice.list.label.link" path="link" width="10%"/>		
</acme:list>

<acme:button code="sponsor.invoice.list.button.create" action="/sponsor/invoice/create"/>
