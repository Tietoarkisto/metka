<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<c:set var="context" value="${empty param.context ? fn:toUpperCase(page) : fn:toUpperCase(param.context)}" />
<c:set var="readonly" value="${empty param.readonly ? false : param.readonly}" />
<div class="popupContainer" id="${param.field}ContainerDialog" title="<spring:message code="${context}.dialog.${param.field}" />">
    <input type="hidden" id="${param.field}ContainerDialogRowId" />
    <table id="${param.field}ContainerDialogTable" class="formTable">
        <tbody>
            <c:forEach var="subkey" items="${configuration[context].fields[param.field].subfields}">
                <c:if test="${configuration[context].fields[subkey].type == 'STRING'}">
                    <jsp:include page="inc/dialogTextRow.jsp">
                        <jsp:param name="container" value="${param.field}" />
                        <jsp:param name="field" value="${subkey}" />
                    </jsp:include>
                </c:if>
                <c:if test="${configuration[context].fields[subkey].type == 'DATE'}">
                    <jsp:include page="inc/dialogDateRow.jsp">
                        <jsp:param name="container" value="${param.field}" />
                        <jsp:param name="field" value="${subkey}" />
                    </jsp:include>
                </c:if>
                <c:if test="${configuration[context].fields[subkey].type == 'CHOICE'}">
                    <jsp:include page="inc/dialogChoiceRow.jsp">
                        <jsp:param name="container" value="${param.field}" />
                        <jsp:param name="field" value="${subkey}" />
                    </jsp:include>
                </c:if>
            </c:forEach>
        </tbody>
    </table>

    <div class="popupButtonsHolder">
        <input type="button" class="button" onclick="MetkaJS.dialogClose('${param.field}ContainerDialog')" value="<spring:message code='general.buttons.close'/>" />
        <c:if test="${readonly == false}">
        <input type="button" class="button generalDialogAdd" onclick="MetkaJS.DialogHandlers.generalContainerHandler.process('${param.field}', '${context}')" value="<spring:message code="general.buttons.ok"/>"/>
        </c:if>
    </div>
</div>