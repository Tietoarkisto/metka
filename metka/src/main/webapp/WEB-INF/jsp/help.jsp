<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ page session="false" %>
<!DOCTYPE HTML>
<html lang="fi">
	<head>
    	<jsp:include page="../inc/head.jsp" />
    </head>
    <body>
        <jsp:include page="../inc/topMenu.jsp" />
        <div class="wrapper">
            <div class="content">
	            <h1 class="pageTitle"><spring:message code="help.title"/></h1>
				<div class="helpContainer">
					<div class="help1">
						foo
					</div>
					<div class="help2">
						bar
					</div>
					<div class="help3">
						foobar
					</div>
					<div class="help4">
						foo
					</div>
	            </div>
        	</div>
        </div>
    </body>
</html>