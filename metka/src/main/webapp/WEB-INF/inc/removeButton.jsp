<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%--Includes everything needed to add remove button to a view or edit page.
    Removes a draft revision completely or whole revisionable logically depending on where the button was pressed.
    If trying to remove draft revision then the user has to be the handler of that draft, otherwise error is returned.
    If trying to remove whole revisionable but there is an open draft the removal fails.
--%>
<script>
    MetkaGlobals.strings["general.confirmation.remove.draft"] = "<spring:message code='general.confirmation.remove.draft' />";
    MetkaGlobals.strings["general.confirmation.remove.logical"] = "<spring:message code='general.confirmation.remove.logical' />";

    var removeMsg = MetkaGlobals.strings["general.confirmation.remove."+(SingleObject.draft?"draft":"logical")];
    removeMsg = removeMsg.replace("{0}", "<spring:message code='${removeConfirmBase}.${page}' />");
    removeMsg = removeMsg.replace("{1}", SingleObject.id);

    function confirmRemove() {
        confirm(removeMsg,
                "<spring:message code='general.confirmation.title.remove' />",
                function() {
                    location.href = contextPath+"/remove/"+MetkaGlobals.page+"/"+(SingleObject.draft?"draft":"logical")+"/"+SingleObject.id;
                });
    }

</script>
<input type="button" id="removeBtn" class="button" value="<spring:message code='general.buttons.remove'/>" onclick="confirmRemove()"/>