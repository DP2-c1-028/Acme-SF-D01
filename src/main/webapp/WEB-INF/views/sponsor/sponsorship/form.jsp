<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-textbox code="sponsor.sponsorship.form.label.code"
		path="code" />
	<acme:input-moment code="sponsor.sponsorship.form.label.moment"
		path="moment" />
	<acme:input-moment code="sponsor.sponsorship.form.label.durationStartTime"
		path="durationStartTime" />
	<acme:input-moment code="sponsor.sponsorship.form.label.durationEndTime"
		path="durationEndTime" />
	<acme:input-money code="sponsor.sponsorship.form.label.amount"
		path="amount" />
	<acme:input-select code="sponsor.sponsorship.form.label.type"
		path="type" choices="${types}" readonly="${acme:anyOf(types, 'FINANCIAL|INKIND')}"/>
	<acme:input-textbox code="sponsor.sponsorship.list.label.email"
		path="email" />
	<acme:input-url code="sponsor.sponsorship.form.label.link"
		path="link" />
	
	<jstl:choose>
		<jstl:when test="${acme:anyOf(_command, 'show|update|delete')}">
		<acme:input-textbox code="sponsor.sponsorship.form.label.project"
				path="project" readonly="true"/>
			<acme:submit code="sponsor.sponsorship.form.button.update"
				action="/sponsor/sponsorship/update" />
			<acme:submit code="sponsor.sponsorship.form.button.delete"
				action="/sponsor/sponsorship/delete" />
			<acme:submit code="sponsor.sponsorship.form.button.publish"
				action="/sponsor/sponsorship/publish" />
		</jstl:when>
		<jstl:when test="${_command == 'create'}">
			<acme:input-select code="sponsor.sponsorship.form.label.project"
				path="project" choices="${projects}"/>
			<acme:submit code="sponsor.sponsorship.form.button.create"
				action="/sponsor/sponsorship/create" />
		</jstl:when>
	</jstl:choose>
</acme:form>

<acme:button code="sponsor.sponsorship.form.button.invoices" action="/sponsor/invoices/list?sponsorshipId=${id}"/>

