<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-select code="manager.user-story-project.form.label.project"
		path="project" choices="${projects}" />
	<acme:input-select code="manager.user-story-project.form.label.user-story"
		path="userStory" choices="${userStories}" />
	
	<jstl:choose>
		<jstl:when test="${acme:anyOf(_command, 'show|update|delete')}">
			<acme:submit code="manager.user-story-project.form.button.update"
				action="/manager/user-story-project/update" />
			<acme:submit code="manager.user-story.form.button.delete"
				action="/manager/user-story-project/delete" />
		</jstl:when>
		<jstl:when test="${_command == 'create'}">
			<acme:submit code="manager.user-story-project.form.button.create"
				action="/manager/user-story-project/create" />
		</jstl:when>
	</jstl:choose>
</acme:form>

