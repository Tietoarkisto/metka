<script>
    strings["general.revision.replace"] = "<spring:message code='general.revision.replace'/>";
    strings["general.revision.compare.title"] = "<spring:message code='general.revision.compare.title'/>";
    // Init type specific translations
    <c:if test="${type == 'SERIES'}">
        strings["SERIES"] = "<spring:message code='SERIES'/>";
        strings["SERIES.field.id"] = "<spring:message code='SERIES.field.id'/>";
        strings["SERIES.field.abbreviation"] = "<spring:message code='SERIES.field.abbreviation'/>";
        strings["SERIES.field.name"] = "<spring:message code='SERIES.field.name'/>";
        strings["SERIES.field.description"] = "<spring:message code='SERIES.field.description'/>";
    </c:if>
    // Add more as needed
</script>