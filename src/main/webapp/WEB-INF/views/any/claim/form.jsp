<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-textbox code="claim.form.label.code"
		path="code" />
	<acme:input-textbox code="claim.form.label.heading"
		path="heading" />
	<acme:input-moment code="claim.form.label.instantiationMoment"
		path="instantiationMoment" readonly="true" />
	<acme:input-textbox code="claim.form.label.description"
		path="description" />
	<acme:input-textbox code="claim.form.label.department"
		path="department" />
	<acme:input-textbox code="claim.form.label.email"
		path="email" />
	<acme:input-url code="claim.form.label.link"
		path="link" />
	
	<jstl:choose>
		<jstl:when test="${_command == 'create'}">
				<acme:input-checkbox code="any.claim.form.label.confirmation" 
				 path="confirmation"/>
				<acme:submit code="any.claim.list.button.create"
				 action="/any/claim/create"/>
			</jstl:when>
		</jstl:choose>
</acme:form>

