<script>
    strings["general.revision.replace"] = "<spring:message code='general.revision.replace'/>";
    strings["general.revision.compare.title"] = "<spring:message code='general.revision.compare.title'/>";
    // Init type specific translations
    strings["${fn:toUpperCase(page)}"] = "<spring:message code='${fn:toUpperCase(page)}'/>";
    <c:forEach var="field" items="${configuration.fields}">
        strings["${fn:toUpperCase(page)}.field.${field.key}"] = "<spring:message code='${fn:toUpperCase(page)}.field.${field.key}'/>"
    </c:forEach>
</script>