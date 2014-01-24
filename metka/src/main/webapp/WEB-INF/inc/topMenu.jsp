<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<header>
    <jsp:include page="../dialogs/alertDialog.jsp" />
    <div class="headerContainer">
        <nav>
            <ul class="mainNavUl">
                <li><a href="/desktop" ${page == "desktop" ? 'class="selected"': ''}>Työpöytä</a></li>
                <li><a href="/expertSearch" ${page == "expertSearch" ? 'class="selected"': ''}>Eksperttihaku</a></li>
                <li><a href="/study/search" ${page == "study" ? 'class="selected"': ''}>Aineistot</a></li>
                <li><a href="/publication/search" ${page == "publication" ? 'class="selected"': ''}>Julkaisut</a></li>
                <li><a href="${contextPath}/series/search" ${page == "series" ? 'class="selected"': ''}>Sarjat</a></li>
                <li><a href="/binder/all" ${page == "binder" ? 'class="selected"': ''}>Mapit</a></li>
                <li><a href="/report/all" ${page == "report" ? 'class="selected"': ''}>Raportit</a></li>
                <li><a href="/settings" ${page == "settings" ? 'class="selected"': ''}>Asetukset</a></li>
            </ul>

            <div style="clear:both;"></div>
        </nav>
        <div class="searchContainer">
            <a class="logoutLink" href="#">Kirjaudu ulos</a>
            <form action="material.html" class="quickSearchForm">
                <input type="text" autocomplete="off" dir="ltr" class="searchInput" placeholder="Aineiston id-nro" />
                <input type="submit" value="Hae" class="searchButton"/>
            </form>
        </div>

        <div class="languageSelect">
            <input type="radio" name="language" value="fi"/>fi
            <input type="radio" name="language" value="en"/>en
            <input type="radio" name="language" value="sv"/>sv
        </div>
    </div>
</header>