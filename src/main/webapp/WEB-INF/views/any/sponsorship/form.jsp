<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-textbox code="any.sponsorship.form.label.code"
		path="code" />
	<acme:input-moment code="any.sponsorship.form.label.moment"
		path="moment" />
	<acme:input-moment code="any.sponsorship.form.label.durationStartTime"
		path="durationStartTime" />
	<acme:input-moment code="any.sponsorship.form.label.durationEndTime"
		path="durationEndTime" />
	<acme:input-money code="any.sponsorship.form.label.amount"
		path="amount" />
	<acme:input-textbox code="any.sponsorship.form.label.type"
		path="type"/>
	<acme:input-textbox code="any.sponsorship.list.label.email"
		path="email" />
	<acme:input-url code="any.sponsorship.form.label.link"
		path="link" />
		<acme:input-textbox code="any.sponsorship.form.label.project"
		path="project"/>
</acme:form>



