<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-textbox code="developer.training-module.form.label.code" path="code" />
	<acme:input-moment code="developer.training-module.form.label.creation-moment" path="creationMoment" readonly="true" />
	<acme:input-moment code="developer.training-module.form.label.update-moment" path="updateMoment" readonly="true" />
	<acme:input-select code="developer.training-module.form.label.difficulty" path="difficulty"  
		choices="${difficulties}" readonly="${acme:anyOf(difficulties, 'BASIC|INTERMEDIATE|ADVANCED')}"/>
	<acme:input-textbox code="developer.training-module.form.label.details" path="details" />
	<acme:input-integer code="developer.training-module.form.label.total-time" path="totalTime" />
	<acme:input-url code="developer.training-module.form.label.link" path="link" />
	<acme:input-select code="developer.training-module.form.label.project" path="project"  
		choices="${projects}"/>	
	
	<jstl:choose>
		<jstl:when test="${acme:anyOf(_command, 'show|update|delete')}">
			<acme:submit code="developer.training-module.form.button.update"
				action="/developer/training-module/update" />
			<acme:submit code="developer.training-module.form.button.delete"
				action="/developer/training-module/delete" />
		</jstl:when>
		<jstl:when test="${_command == 'create'}">
			<acme:submit code="developer.training-module.form.button.create"
				action="/developer/training-module/create" />
		</jstl:when>
	</jstl:choose>
</acme:form>