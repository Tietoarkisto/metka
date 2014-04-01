<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<c:set var="readonly" value="${empty param.readonly ? false : param.readoly}" />
<div class="tabs tab_codebook">
    <jsp:include page="../../../inc/datatableContainer.jsp">
        <jsp:param name="field" value="cbattachments" />
        <jsp:param name="readonly" value="${readonly}" />
    </jsp:include>
</div>