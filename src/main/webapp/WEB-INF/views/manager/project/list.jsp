<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="manager.project.list.label.title" path="title" width="10%"/>
	<acme:list-column code="manager.project.list.label.code" path="code" width="10%"/>
	<acme:list-column code="manager.project.list.label.abstract" path="abstractText" width="10%"/>
	<acme:list-column code="manager.project.list.label.cost" path="cost" width="10%"/>
	<acme:list-column code="manager.project.list.label.link" path="link" width="10%"/>
	<acme:list-column code="manager.project.list.label.published" path="published" width="10%"/>				
</acme:list>
<acme:button code="manager.project.form.button.create" action="/manager/project/create"/>