<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<header>
    <jsp:include page="../dialogs/alertDialog.jsp" />
    <div class="headerContainer">
        <nav>
            <ul class="mainNavUl">
                <li><a href="${contextPath}/desktop" ${page == "desktop" ? 'class="selected"': ''}><spring:message code="topmenu.desktop"/></a></li>
                <li><a href="${contextPath}/expertSearch" ${page == "expertSearch" ? 'class="selected"': ''}><spring:message code="topmenu.expert"/></a></li>
                <li><a href="${contextPath}/study/search" ${page == "study" ? 'class="selected"': ''}><spring:message code="topmenu.study"/></a></li>
                <li><a href="${contextPath}/variables/search" ${page == "variables" ? 'class="selected"': ''}><spring:message code="topmenu.variables"/></a></li>
                <li><a href="${contextPath}/publication/search" ${page == "publication" ? 'class="selected"': ''}><spring:message code="topmenu.publication"/></a></li>
                <li><a href="${contextPath}/series/search" ${page == "series" ? 'class="selected"': ''}><spring:message code="topmenu.series"/></a></li>
                <li><a href="${contextPath}/binder/all" ${page == "binder" ? 'class="selected"': ''}><spring:message code="topmenu.binder"/></a></li>
                <li><a href="${contextPath}/report/all" ${page == "report" ? 'class="selected"': ''}><spring:message code="topmenu.report"/></a></li>
                <li><a href="${contextPath}/settings" ${page == "settings" ? 'class="selected"': ''}><spring:message code="topmenu.settings"/></a></li>
            </ul>
            <div style="clear:both;"></div>
        </nav>
        <div class="quickSearchContainer">
            <a class="logoutLink" href="#">Kirjaudu ulos</a>
            <form action="material.html">
                <spring:message code="STUDY.field.id" var="searchPlaceholder" />
                <input type="text" autocomplete="off" dir="ltr" class="searchInput" placeholder="${searchPlaceholder}" />
                <input type="submit" value="Hae" class="searchButton"/>
            </form>
        </div>

        <%--<div class="languageSelect">
            <input type="radio" name="language" value="fi"/>fi
            <input type="radio" name="language" value="en"/>en
            <input type="radio" name="language" value="sv"/>sv
        </div>--%>
    </div>
</header>