<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ page session="false" %>
<!DOCTYPE HTML>
<html lang="fi">
	<head>
    	<jsp:include page="../../inc/head.jsp" />
    </head>
    <body>
        <jsp:include page="../../inc/topMenu.jsp" />
        <div class="wrapper">
            <div class="content">
                <h1 class="pageTitle">Sarjahaku</h1>

                <div class="searchFormContainer">
                    <form:form method="post" action="/series/search" modelAttribute="info.query">
                        <div class="searchFormRowHolder">
                            <form:label path="id">Sarjan numero</form:label>
                            <form:input path="id" cssClass="searchInput" />
                        </div>
                        <div class="searchFormRowHolder">
                            <form:label path="name">Sarjan nimi</form:label>
                            <form:input path="name" cssClass="searchInput" />
                        </div>
                        <div class="searchFormRowHolder">
                            <form:label path="abbreviation">Sarjan lyhenne</form:label>
                            <form:select path="abbreviation" class="formSelect" name="sarjanNimi" items="${info.abbreviations}" />
                        </div>
                        <div class="searchFormButtonsHolder">
                            <input type="submit" class="searchFormInput doSearch" value="Tee haku">
                            <!-- TODO: Fix this reset button
                            <input type="reset" class="searchFormInput" value="Tyhjennä">-->
                        </div>

                        <div id="addNewButton">
                            <input type="button" id="addNewSeriesBtn" class="searchFormInput" value="Lisää uusi"
                                    onclick="location.href='/series/add'"/>
                        </div>
                    </form:form>
                </div>
            </div>
        </div>
    </body>
</html>