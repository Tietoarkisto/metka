<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<div class="popupContainer" id="${param.field}ContainerDialog" title="<spring:message code="${fn:toUpperCase(page)}.dialog.${param.field}" />">
    <input type="hidden" id="${param.field}ContainerDialogRowId" />
    <table id="${param.field}ContainerDialogTable" class="formTable">
        <tbody>
            <c:forEach var="subkey" items="${configuration.fields[param.field].subfields}">
                <c:if test="${configuration.fields[subkey].type == 'STRING'}">
                    <jsp:include page="inc/dialogTextRow.jsp">
                        <jsp:param name="container" value="${param.field}" />
                        <jsp:param name="field" value="${subkey}" />
                    </jsp:include>
                </c:if>
                <c:if test="${configuration.fields[subkey].type == 'DATE'}">
                    <jsp:include page="inc/dialogDateRow.jsp">
                        <jsp:param name="container" value="${param.field}" />
                        <jsp:param name="field" value="${subkey}" />
                    </jsp:include>
                </c:if>
                <c:if test="${configuration.fields[subkey].type == 'CHOICE'}">
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
        <input type="button" class="button generalDialogAdd" onclick="MetkaJS.DatatableHandler.process('${param.field}')" value="<spring:message code="general.buttons.ok"/>"/>
    </div>
</div>