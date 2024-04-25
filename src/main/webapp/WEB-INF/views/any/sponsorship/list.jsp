<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="any.sponsorship.list.label.code" path="code" width="10%"/>
	<acme:list-column code="any.sponsorship.list.label.moment" path="moment" width="10%"/>
	<acme:list-column code="any.sponsorship.list.label.durationStartTime" path="durationStartTime" width="10%"/>
	<acme:list-column code="any.sponsorship.list.label.durationEndTime" path="durationEndTime" width="10%"/>
	<acme:list-column code="any.sponsorship.list.label.amount" path="amount" width="10%"/>
	<acme:list-column code="any.sponsorship.list.label.type" path="type" width="10%"/>
	<acme:list-column code="any.sponsorship.list.label.email" path="email" width="10%"/>
	<acme:list-column code="any.sponsorship.list.label.link" path="link" width="10%"/>
	<acme:list-column code="any.sponsorship.list.label.project" path="project" width="10%"/>		
</acme:list>
