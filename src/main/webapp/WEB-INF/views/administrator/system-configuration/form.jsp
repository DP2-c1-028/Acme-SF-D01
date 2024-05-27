<%@page language="java"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-textbox code="administrator.system-configuration.form.label.systemCurrency" path="systemCurrency"/>
	<acme:input-textbox readonly="true" code="administrator.system-configuration.form.label.acceptedCurrencies" path="acceptedCurrencies"/>

	<jstl:choose>
		<jstl:when test="${acme:anyOf(_command, 'show|update')}">
			<acme:submit code="administrator.system-configuration.form.button.update" action="/administrator/system-configuration/update"/>
			<acme:button code="administrator.system-configuration.form.button.currencies" action="/administrator/currency/list"/>
		</jstl:when>
	</jstl:choose>
</acme:form>
