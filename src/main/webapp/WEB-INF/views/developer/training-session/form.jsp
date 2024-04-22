<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-textbox code="developer.training-session.form.label.code" path="code" />
	<acme:input-moment code="developer.training-session.form.label.period-start" path="periodStart" />
	<acme:input-moment code="developer.training-session.form.label.period-end" path="periodEnd" />
	<acme:input-textbox code="developer.training-session.form.label.location" path="location" />
	<acme:input-textbox code="developer.training-session.form.label.instructor" path="instructor" />
	<acme:input-email code="developer.training-session.form.label.contact-email" path="contactEmail"/>
	<acme:input-url code="developer.training-session.form.label.link" path="link" />
	
	<jstl:choose>
		<jstl:when test="${acme:anyOf(_command, 'show|update|delete|publish')}">
			<acme:input-checkbox code="developer.training-session.form.label.published" path="published" readonly="true"/>
			<jstl:choose>
				<jstl:when test="${published==false}">
					<acme:submit code="developer.training-session.form.button.update"
						action="/developer/training-session/update" />
					<acme:submit code="developer.training-session.form.button.delete"
						action="/developer/training-session/delete" />
					<acme:submit code="developer.training-session.form.button.publish"
						action="/developer/training-session/publish" />
				</jstl:when>
			</jstl:choose>
		</jstl:when>
		<jstl:when test="${_command == 'create'}">
			<acme:submit code="developer.training-session.form.button.create"
				action="/developer/training-session/create?trainingModuleId=${trainingModuleId}" />
		</jstl:when>
	</jstl:choose>
</acme:form>