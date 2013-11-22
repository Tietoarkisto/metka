<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<header>
    <div class="headerContainer">
        <nav>
            <ul class="mainNavUl">
                <li><a href="/desktop" ${page == "desktop" ? 'class="selected"': ''}><spring:message code="topMenu.navi.desktop"/></a></li>
                <li><a href="/expertSearch" ${page == "expertSearch" ? 'class="selected"': ''}><spring:message code="topMenu.navi.expertSearch"/></a></li>
                <li><a href="/study/search" ${page == "study" ? 'class="selected"': ''}><spring:message code="topMenu.navi.studies"/></a></li>
                <li><a href="/publication/search" ${page == "publication" ? 'class="selected"': ''}><spring:message code="topMenu.navi.publications"/></a></li>
                <li><a href="/series/search" ${page == "series" ? 'class="selected"': ''}><spring:message code="topMenu.navi.series"/></a></li>
                <li><a href="/binder/all" ${page == "binder" ? 'class="selected"': ''}><spring:message code="topMenu.navi.binders"/></a></li>
                <li><a href="/report/all" ${page == "report" ? 'class="selected"': ''}><spring:message code="topMenu.navi.reports"/></a></li>
                <li><a href="/settings" ${page == "settings" ? 'class="selected"': ''}><spring:message code="topMenu.navi.settings"/></a></li>
                <li><a href="help" ${page == "settings" ? 'class="selected"': ''}><spring:message code="topMenu.navi.help"/></a></li>
            </ul>

            <div style="clear:both;"></div>
        </nav>
        <div class="searchContainer">
            <a class="logoutLink" href="#"><spring:message code="topMenu.logout"/></a>
            <form action="material.html" class="quickSearchForm">
                <input type="text" autocomplete="off" dir="ltr" class="searchInput" placeholder="Aineiston id-nro" />
                <input type="submit" value="Hae" class="searchButton"/>
            </form>
        </div>
        
        <div class="languageSelect">
			<input type="radio" name="language" value="fi"/><spring:message code="topMenu.language.finnish"/>
			<input type="radio" name="language" value="en"/><spring:message code="topMenu.language.english"/>
			<input type="radio" name="language" value="sv"/><spring:message code="topMenu.language.swedish"/>
		</div>
    </div>
</header>