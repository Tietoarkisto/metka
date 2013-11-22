<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ page session="false" %>
<!DOCTYPE HTML>
<html lang="fi">
    <%--<jsp:include page="../inc/head.jsp" />--%>
    <head>
        <meta charset="utf-8">
        <title>YHTEISKUNTATIETEELLINEN TIETOARKISTO - Metka</title>
        <link rel="stylesheet" type="text/css" href="/css/jquery-ui.css">
        <link rel="stylesheet" type="text/css" href="/css/styles.css">
        <script src="/js/custom.js"></script>
        <script src="/js/jquery-1.10.2.js"></script>
        <script src="/js/jquery-ui.js"></script>
        <script src="/js/jquery.tablesorter.min.js"></script>
        <script src="/js/jquery.dataTables.min.js"></script>
        <script src="/js/jquery.tablesorter.pager.js"></script>
        <script src="/js/jquery.dataTables.rowReordering.js"></script>
        <script src="/js/jquery.fastLiveFilter.js"></script>
        <script src="/js/jquery.fancytree.js"></script>

        <script>
            $(function() {
                $("#addNewSeries").dialog({
                    autoOpen: false,
                    height: 300,
                    width: 350,
                    modal: true,
                    buttons: {
                        Cancel: function() {
                            $(this).dialog("close");
                        }
                    }
                });

                /*$("#addNewSeriesBtn")
                        .button()
                        .click(function() {
                            $("#addNewSeries").dialog("open");
                        });*/
            });
        </script>
    </head>

    <body>
        <jsp:include page="../inc/topMenu.jsp" />
        <div id="addNewSeries" title="Lisää sarja">
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
        </div>

        <div class="wrapper">
            <div class="content">
                <h1 class="pageTitle">Sarjahaku</h1>

                <div class="searchFormContainer">
                    <form:form method="post" action="/series/search" modelAttribute="Series">
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
                            <form:select path="abbreviation" class="formSelect" name="sarjanNimi" items="${abbreviations}" />
                        </div>
                        <div class="searchFormButtonsHolder">
                            <input type="submit" class="searchFormInput doSearch" value="Tee haku">
                            <input type="reset" class="searchFormInput" value="Tyhjennä">
                        </div>

                        <div id="addNewButton"><input type="button" id="addNewSeriesBtn" class="searchFormInput" value="Lisää uusi" /></div>
                    </form:form>
                </div>
            </div>
        </div>
    </body>
</html>