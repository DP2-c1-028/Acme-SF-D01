<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="administrator.currency.list.label.symbol" path="symbol" width="10%"/>
	<acme:list-column code="administrator.currency.list.label.value-against-dollar" path="valueAgainstDollar" width="10%"/>
</acme:list>

<acme:button code="administrator.currency.button.create" action="/administrator/system-configuration/create"/>