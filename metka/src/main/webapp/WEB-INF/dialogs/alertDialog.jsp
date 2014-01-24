<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<div class="popupContainer" id="alertDialog">
    <div id="content"></div>

    <br/>
    <div class="popupButtonsHolder">
        <input id="alertCloseBtn" type="button" class="searchFormInput" value="<spring:message code='general.buttons.ok'/>" />
    </div>
</div>