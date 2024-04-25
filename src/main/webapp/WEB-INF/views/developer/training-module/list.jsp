<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="developer.training-module.list.label.code" path="code" width="10%"/>
	<acme:list-column code="developer.training-module.list.label.creation-moment" path="creationMoment" width="10%"/>
	<acme:list-column code="developer.training-module.list.label.difficulty" path="difficulty" width="10%"/>
	<acme:list-column code="developer.training-module.list.label.total-time" path="totalTime" width="10%"/>	
	<acme:list-column code="developer.training-module.list.label.project" path="project" width="10%"/>	
</acme:list>
<acme:button code="developer.training-module.list.button.create" action="/developer/training-module/create"/>
