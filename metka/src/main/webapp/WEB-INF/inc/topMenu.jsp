<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<header class="navbar navbar-default navbar-static-top">
    <div class="container">
        <nav>
            <ul class="nav navbar-nav nav navbar-nav navbar-left">
                <li${page == "desktop" ? ' class="active"': ''}><a href="${contextPath}/desktop"><spring:message code="topmenu.desktop"/></a></li>
                <li${page == "expertSearch" ? ' class="active"': ''}><a href="${contextPath}/expertSearch"><spring:message code="topmenu.expert"/></a></li>
                <li${page == "study" ? ' class="active"': ''}><a href="${contextPath}/study/search"><spring:message code="topmenu.study"/></a></li>
                <li${page == "variables" ? ' class="active"': ''}><a href="${contextPath}/variables/search"><spring:message code="topmenu.variables"/></a></li>
                <li${page == "publication" ? ' class="active"': ''}><a href="${contextPath}/publication/search"><spring:message code="topmenu.publication"/></a></li>
                <li${page == "series" ? ' class="active"': ''}><a href="${contextPath}/series/search"><spring:message code="topmenu.series"/></a></li>
                <li${page == "binder" ? ' class="active"': ''}><a href="${contextPath}/binder/all"><spring:message code="topmenu.binder"/></a></li>
                <li${page == "report" ? ' class="active"': ''}><a href="${contextPath}/report/all"><spring:message code="topmenu.report"/></a></li>
                <li${page == "settings" ? ' class="active"': ''}><a href="${contextPath}/settings"><spring:message code="topmenu.settings"/></a></li>
            </ul>
            <div class="col-sm-3 col-md-3 pull-right">
                <div class="row">
                    <div class="col-md-12">
                        <div class="row">
                            <div class="col-md-12" style="padding-top: 30px; padding-right: 30px;">
                                <a href="#" class="navbar-link pull-right">Kirjaudu ulos</a>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-12">
                                <form class="navbar-form">
                                    <div class="input-group">
                                        <input type="text" class="form-control" autocomplete="on" placeholder="Aineistonumero">
                                        <div class="input-group-btn">
                                            <button class="btn btn-primary" type="submit">Hae</button>
                                        </div>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <%--<div class="languageSelect">
                <input type="radio" name="language" value="fi"/>fi
                <input type="radio" name="language" value="en"/>en
                <input type="radio" name="language" value="sv"/>sv
            </div>--%>
        </nav>
    </div>
</header>
<jsp:include page="../dialogs/alertDialog.jsp" />