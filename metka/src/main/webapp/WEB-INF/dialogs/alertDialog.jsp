<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<div class="popupContainer" id="alertDialog">
    <div id="alertContent"></div>

    <br/>
    <div class="popupButtonsHolder">
        <input id="alertCloseBtn" type="button" class="button" value="<spring:message code='general.buttons.ok'/>" />
    </div>
</div>

<div class="popupContainer" id="confirmationDialog">
    <div id="confirmationContent"></div>

    <br/>
    <div class="popupButtonsHolder">
        <input id="confirmationYesBtn" type="button" class="button" value="<spring:message code='general.buttons.yes'/>" />
        <input id="confirmationNoBtn" type="button" class="button" value="<spring:message code='general.buttons.no'/>" />
    </div>
</div>