<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ page session="false" %>
<!DOCTYPE HTML>
<html lang="fi">
    <jsp:include page="../inc/head.jsp" />
    <body>
        <jsp:include page="../inc/topMenu.jsp" />
        <div class="wrapper">
            <div class="content">
                <h1 class="pageTitle">Sarjahaku</h1>
                <form:form method="post" action="/series/add" modelAttribute="Series">
                    <table>
                        <tr>
                            <td><form:label path="abbreviation"><spring:message code="series.abb" /></form:label></td>
                            <td><form:input path="abbreviation" /></td>
                        </tr>
                        <tr>
                            <td><form:label path="name"><spring:message code="series.name" /></form:label></td>
                            <td><form:input path="name" /></td>
                        </tr>
                        <tr>
                            <td><form:label path="description"><spring:message code="series.desc" /></form:label></td>
                            <td><form:input path="description" /></td>
                        </tr>
                        <tr>
                            <td colspan="2"><input type="submit" value="<spring:message code='submit.add' />"></td>
                        </tr>
                    </table>
                </form:form>

                <div class="searchFormContainer">
                    <form:form method="post" action="/series/search" modelAttribute="Series">
                        <div class="searchFormRowHolder">
                            <label>Sarjan numero</label>
                            <form:input path="id" class="searchInput" />
                        </div>
                        <div class="searchFormRowHolder">
                            <label>Sarjan lyhenne</label>
                            <select class="formSelect" name="sarjanNimi">
                                <option></option>
                            </select>
                        </div>
                        <div class="searchFormRowHolder">
                            <label>Sarjan nimi</label>
                            <input type="text" class="searchInput" name="julkaisuId" />
                        </div>
                        <div class="searchFormButtonsHolder">
                            <input type="submit" class="searchFormInput doSearch" value="Tee haku">
                            <input class="searchFormInput" type="reset" value="Tyhjennä">
                        </div>

                        <div id="addNewButton"><div id="addNewSeriesButton" class="searchFormInput" />Lisää</div></div>
                    </form:form>
                </div>
            </div>
        </div>
    </body>
</html>