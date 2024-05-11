<%@page language="java"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-textbox code="administrator.currency.form.label.symbol" path="symbol"/>	
	<acme:input-double code="administrator.currency.form.label.value-against-dollar" path="valueAgainstDollar"/>

	<jstl:choose>
		<jstl:when test="${acme:anyOf(_command, 'show|update')}">
			<acme:submit code="administrator.currency.form.button.update" action="/administrator/currency/update"/>
		</jstl:when>
		<jstl:when test="${acme:anyOf(_command, 'create')}">
			<acme:submit code="administrator.currency.form.button.create" action="/administrator/currency/create"/>
		</jstl:when>
	</jstl:choose>
</acme:form>
