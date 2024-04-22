<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="manager.user-story-project.list.label.project" path="project" width="10%"/>
	<acme:list-column code="manager.user-story-project.list.label.user-story" path="userStory" width="10%"/>
</acme:list>
<acme:button code="manager.project.form.button.create" action="/manager/user-story-project/create"/>