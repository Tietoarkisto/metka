<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%--Includes everything needed to add remove button to a view or edit page.
    Removes a draft revision completely or whole revisionable logically depending on where the button was pressed.
    If trying to remove draft revision then the user has to be the handler of that draft, otherwise error is returned.
    If trying to remove whole revisionable but there is an open draft the removal fails.
    Required params:
        removeDraft - Tells if the user is viewing a draft or not (if the user is on edit page). Drafts are removed actually, not just logically
        id - removeId of the object being removed. If removeDraft is true then it is assumed the user was viewing the draft page when pressing remove. --%>
<script>
    var removeDraft = ${param.isDraft};

    <c:choose>
        <c:when test="${param.isDraft}">
            <c:set var="removeConfirmBase" value="general.confirmation.remove.draft" />
        </c:when>
        <c:otherwise>
            <c:set var="removeConfirmBase" value="general.confirmation.remove.logical" />
        </c:otherwise>
    </c:choose>
    var removeMsg = "<spring:message code='${removeConfirmBase}' />";
    removeMsg = removeMsg.replace("{0}", "<spring:message code='${removeConfirmBase}.${page}' />");
    removeMsg = removeMsg.replace("{1}", ${param.id});

    function confirmRemove() {
        confirmation(removeMsg,
                "<spring:message code='general.confirmation.title.remove' />",
                function() {
                    location.href = contextPath+"/remove/${page}/"+(removeDraft?"draft":"logical")+"/${param.id}";
                });
    }

</script>
<script src="${pageContext.request.contextPath}/js/custom/remove.js"></script>
<input type="button" id="removeBtn" class="button" value="<spring:message code='general.buttons.remove'/>" onclick="confirmRemove()"/>