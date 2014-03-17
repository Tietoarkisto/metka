<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%--Includes everything needed to add remove button to a view or edit page.
    Removes a draft revision completely or whole revisionable logically depending on where the button was pressed.
    If trying to remove draft revision then the user has to be the handler of that draft, otherwise error is returned.
    If trying to remove whole revisionable but there is an open draft the removal fails.
--%>
<script>
    MetkaJS.L18N.put("general.confirmation.remove.draft", "<spring:message code='general.confirmation.remove.draft' />");
    MetkaJS.L18N.put("general.confirmation.remove.logical", "<spring:message code='general.confirmation.remove.logical' />");
    MetkaJS.L18N.put("general.confirmation.title.remove", "<spring:message code='general.confirmation.title.remove' />");

    var removeMsg = MetkaJS.L18N.get("general.confirmation.remove."+(MetkaJS.SingleObject.draft?"draft":"logical"));
    removeMsg = removeMsg.replace("{0}", "<spring:message code='${removeConfirmBase}.${page}' />");
    removeMsg = removeMsg.replace("{1}", MetkaJS.SingleObject.id);

    function confirmRemove() {
        confirm(removeMsg,
                "general.confirmation.title.remove",
                function() {
                    MetkaJS.PathBuilder()
                            .add("remove")
                            .add(MetkaJS.Globals.page)
                            .add(MetkaJS.SingleObject.draft?"draft":"logical")
                            .add(MetkaJS.SingleObject.id)
                            .navigate();
                });
    }

</script>
<input type="button" id="removeBtn" class="button" value="<spring:message code='general.buttons.remove'/>" onclick="confirmRemove()"/>