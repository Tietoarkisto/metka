<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%--Includes everything needed to add remove button to a view or edit page.
    Removes a draft revision completely or whole revisionable logically depending on where the button was pressed.
    If trying to remove draft revision then the user has to be the handler of that draft, otherwise error is returned.
    If trying to remove whole revisionable but there is an open draft the removal fails.
--%>
<script>


</script>
<input type="button" id="removeBtn" class="button" value="<spring:message code='general.buttons.remove'/>" onclick="confirmRemove()"/>