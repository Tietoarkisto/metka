<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<%-- Include jQuery and plugins --%>
<script src="${contextPath}/lib/jquery/jquery-1.10.2.js"></script>
<script src="${contextPath}/lib/jquery/jquery-ui.js"></script>
<script src="${contextPath}/lib/jquery/jquery.fileupload.js"></script>

<script>
    /**
     * Like $.each, except 'this' refers to the jQuery object
     */
    $.fn.eachTo = function (c, f) {
        var that = this;
        $.each(c, function () {
            return f.apply(that, arguments);
        });
        return this;
    };

    /**
     * Calls 'f' in the context of jQuery object.
     * Useful when jQuery object needs to be: instantiated, manipulated using custom logic and then chained/returned
     */
    $.fn.me = function (f) {
        f.call(this);
        return this;
    };

    $.fn.if = function (x, f) {
        return x ? this.me(f) : this;
    };
</script>

<%--
<script src="${contextPath}/js/jquery/jquery.tablesorter.min.js"></script>
<script src="${contextPath}/js/jquery/jquery.dataTables.min.js"></script>
<script src="${contextPath}/js/jquery/jquery.tablesorter.pager.js"></script>
<script src="${contextPath}/js/jquery/jquery.dataTables.rowReordering.js"></script>
<script src="${contextPath}/js/jquery/jquery.fastLiveFilter.js"></script>
<script src="${contextPath}/js/jquery/jquery.fancytree.js"></script>
<script src="${contextPath}/js/jquery/jquery.fancytree.filter.js"></script>
<script src="${contextPath}/js/jquery/jquery.fastLiveFilter.js"></script>--%>