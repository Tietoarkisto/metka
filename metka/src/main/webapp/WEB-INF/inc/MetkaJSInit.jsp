<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<script>
    /* Define MetkaJS namespace. Should include all global variables as well as all functions and other objects that
       should be accessible from anywhere */
    MetkaJS = function() {
        return {
            Globals: {
                page: "${page}",
                contextPath: "${pageContext.request.contextPath}",
                strings: new Array()
            },
            containerConfig: null,
            SingleObject: null,
            view: function(id, revision) {
                MetkaJS.PathBuilder()
                        .add(MetkaJS.Globals.page)
                        .add("view")
                        .add(id)
                        .add(revision)
                        .navigate();
            },
            ErrorManager: null,
            L18N: null,
            PathBuilder: function() {return new function() {
                this.path = MetkaJS.Globals.contextPath;
                this.add = function(part) {
                    if(part !== 'undefined' && part != null)
                        this.path += "/"+part;
                    return this;
                }
                this.build = function() {
                    return this.path;
                }
                this.navigate = function() {
                    location.href = this.path;
                }
            }}
        };
    }();

    MetkaJS.L18N = function() {
        var strings = new Array();
        return {
            put: function(key, value) {
                strings[key] = value;
            },
            get: function(key) {
                var loc = strings[key];
                if(loc == null || loc === 'undefined') {
                    loc = key;
                }
                return loc;
            }
        }
    }();

    // Insert default confirmation dialog title
    MetkaJS.L18N.put("general.confirmation.title.confirm", "<spring:message code='general.confirmation.title.confirm' />");
    // Insert default error title
    MetkaJS.L18N.put("general.errors.title.notice", "<spring:message code='general.errors.title.notice' />");
    // Insert localisation for text DRAFT
    MetkaJS.L18N.put("general.title.DRAFT", "<spring:message code="general.title.DRAFT"/>");

    <%-- Initialise single object if applicable --%>
<c:if test="${not empty single}">
    MetkaJS.SingleObject = function() {
        return {
            id: ${single.id},
            revision: ${single.revision},
            draft: false,
            edit: function() {
                MetkaJS.PathBuilder().
                        add(MetkaJS.Globals.page).
                        add("edit").
                        add(MetkaJS.SingleObject.id).
                        navigate();
            },
            adjacent: function(next) {
                MetkaJS.PathBuilder()
                        .add(next?"next":"prev")
                        .add(MetkaJS.Globals.page)
                        .add(MetkaJS.SingleObject.id)
                        .navigate();
            }
        };
    }();
</c:if>
<%-- There are displayable errors, make error handler --%>
<c:if test="${not empty displayableErrors}">
    MetkaJS.ErrorManager = function() {
        var errors = new Array();

        function showError(error) {
            var str = MetkaJS.L18N.get(error.message);
            for(var i = 0; i < error.data.length; i++) {
                str = str.replace("{"+i+"}", error.data[i]);
            }
            alert(str, error.title);
        }

        return {
            ErrorObject: function(title, message) {return new function() {
                this.title = title;
                this.message = message;
                this.data = new Array();
                this.pushData = function(data) {
                    this.data[this.data.length] = data;
                    return this;
                }
                this.setData = function(index, data) {
                    this.data[index] = data;
                    return this;
                }
            }},
            push: function(error) {
                errors[errors.length] = error;
            },
            showAll: function() {
                while(errors.length > 0) {
                    showError(errors.pop());
                }
            },
            show: function() {

            },
            topError: function() {
                return errors[errors.length - 1];
            }
        }
    }();

    <%-- List displayable errors --%>
<c:forEach items="${displayableErrors}" var="errorObject">
    <c:if test="${not empty errorObject.title}">MetkaJS.L18N.put("${errorObject.title}", "<spring:message code='${errorObject.title}' />");</c:if>
    MetkaJS.L18N.put("${errorObject.msg}", "<spring:message code='${errorObject.msg}' />");
    MetkaJS.ErrorManager.push(MetkaJS.ErrorManager.ErrorObject("${errorObject.title}", "${errorObject.msg}"));
    <c:forEach items="${errorObject.data}" var="dataStr">
    MetkaJS.ErrorManager.topError().pushData("<spring:message code='${dataStr}' />");
    </c:forEach>
</c:forEach>
</c:if>

    <%-- If containerConfig JSON is provided insert it to globals. Otherwise MetkaJS.containerConfig will remain null --%>
    <c:if test="${not empty containerConfig}">MetkaJS.containerConfig = JSON.parse('${containerConfig}');</c:if>
</script>