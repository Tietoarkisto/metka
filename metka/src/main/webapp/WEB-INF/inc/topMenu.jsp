<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<header>
    <jsp:include page="../dialogs/alertDialog.jsp" />
    <div class="headerContainer">
        <nav>
            <ul class="mainNavUl">
                <li><a href="${contextPath}/desktop" ${page == "desktop" ? 'class="selected"': ''}>Työpöytä</a></li>
                <li><a href="${contextPath}/expertSearch" ${page == "expertSearch" ? 'class="selected"': ''}>Eksperttihaku</a></li>
                <li><a href="${contextPath}/study/search" ${page == "study" ? 'class="selected"': ''}>Aineistot</a></li>
                <li><a href="${contextPath}/publication/search" ${page == "publication" ? 'class="selected"': ''}>Julkaisut</a></li>
                <li><a href="${contextPath}/series/search" ${page == "series" ? 'class="selected"': ''}>Sarjat</a></li>
                <li><a href="${contextPath}/binder/all" ${page == "binder" ? 'class="selected"': ''}>Mapit</a></li>
                <li><a href="${contextPath}/report/all" ${page == "report" ? 'class="selected"': ''}>Raportit</a></li>
                <li><a href="${contextPath}/settings" ${page == "settings" ? 'class="selected"': ''}>Asetukset</a></li>
            </ul>

            <div style="clear:both;"></div>
        </nav>
        <div class="quickSearchContainer">
            <a class="logoutLink" href="#">Kirjaudu ulos</a>
            <form action="material.html">
                <spring:message code="STUDY.field.study_number" var="searchPlaceholder" />
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