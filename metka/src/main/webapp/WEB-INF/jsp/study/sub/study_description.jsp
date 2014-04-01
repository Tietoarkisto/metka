<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<c:set var="context" value="${fn:toUpperCase(page)}" />
<c:set var="readonly" value="${empty param.readonly ? false : param.readonly}" />
<div class="tabs tab_study_description">
    <table class="formTable">
        <tbody>
            <jsp:include page="../../../inc/fullRowFormText.jsp">
                <jsp:param name="field" value="alttitle" />
                <jsp:param name="readonly" value="${readonly}" />
            </jsp:include>
        </tbody>
    </table>

    <jsp:include page="../../../inc/datatableContainer.jsp">
        <jsp:param name="field" value="partitles" />
        <jsp:param name="readonly" value="${readonly}" />
    </jsp:include>

    <jsp:include page="../../../inc/datatableContainer.jsp">
        <jsp:param name="field" value="authors" />
        <jsp:param name="readonly" value="${readonly}" />
    </jsp:include>

    <jsp:include page="../../../inc/datatableContainer.jsp">
        <jsp:param name="field" value="otherauthors" />
        <jsp:param name="readonly" value="${readonly}" />
    </jsp:include>

    <jsp:include page="../../../inc/datatableContainer.jsp">
        <jsp:param name="field" value="producers" />
        <jsp:param name="readonly" value="${readonly}" />
    </jsp:include>

    <jsp:include page="../../../inc/datatableContainer.jsp">
        <jsp:param name="field" value="keywords" />
        <jsp:param name="readonly" value="${readonly}" />
    </jsp:include>

    <jsp:include page="../../../inc/datatableContainer.jsp">
        <jsp:param name="field" value="topics" />
        <jsp:param name="readonly" value="${readonly}" />
    </jsp:include>

    <table class="formTable">
        <tbody>
            <tr>
                <jsp:include page="../../../inc/singleCellFormText.jsp">
                    <jsp:param name="field" value="biblcit" />
                    <jsp:param name="readonly" value="${readonly}" />
                </jsp:include>
                <jsp:include page="../../../inc/singleCellFormText.jsp">
                    <jsp:param name="field" value="abstract" />
                    <jsp:param name="readonly" value="${readonly}" />
                </jsp:include>
            </tr>
        </tbody>
    </table>

    <jsp:include page="../../../inc/datatableContainer.jsp">
        <jsp:param name="field" value="timeperiods" />
        <jsp:param name="readonly" value="${readonly}" />
    </jsp:include>

    <jsp:include page="../../../inc/datatableContainer.jsp">
        <jsp:param name="field" value="countries" />
        <jsp:param name="readonly" value="${readonly}" />
    </jsp:include>

    <jsp:include page="../../../inc/datatableContainer.jsp">
        <jsp:param name="field" value="universes" />
        <jsp:param name="readonly" value="${readonly}" />
    </jsp:include>

    <jsp:include page="../../../inc/datatableContainer.jsp">
        <jsp:param name="field" value="geogcovers" />
        <jsp:param name="readonly" value="${readonly}" />
    </jsp:include>

    <jsp:include page="../../../inc/datatableContainer.jsp">
        <jsp:param name="field" value="colltime" />
        <jsp:param name="readonly" value="${readonly}" />
    </jsp:include>

    <jsp:include page="../../../inc/datatableContainer.jsp">
        <jsp:param name="field" value="collectors" />
        <jsp:param name="readonly" value="${readonly}" />
    </jsp:include>

    <jsp:include page="../../../inc/datatableContainer.jsp">
        <jsp:param name="field" value="analysis" />
        <jsp:param name="readonly" value="${readonly}" />
    </jsp:include>

    <jsp:include page="../../../inc/datatableContainer.jsp">
        <jsp:param name="field" value="timemethods" />
        <jsp:param name="readonly" value="${readonly}" />
    </jsp:include>

    <jsp:include page="../../../inc/datatableContainer.jsp">
        <jsp:param name="field" value="collmodes" />
        <jsp:param name="readonly" value="${readonly}" />
    </jsp:include>

    <jsp:include page="../../../inc/datatableContainer.jsp">
        <jsp:param name="field" value="instruments" />
        <jsp:param name="readonly" value="${readonly}" />
    </jsp:include>

    <jsp:include page="../../../inc/datatableContainer.jsp">
        <jsp:param name="field" value="sampprocs" />
        <jsp:param name="readonly" value="${readonly}" />
    </jsp:include>

    <table class="formTable">
        <tbody>
            <jsp:include page="../../../inc/singleCellFormText.jsp">
                <jsp:param name="field" value="resprate" />
                <jsp:param name="readonly" value="${readonly}" />
            </jsp:include>
        </tbody>
    </table>
    <table class="formTable">
        <tr>
            <td>
                <jsp:include page="../../../inc/singleCellFormText.jsp">
                    <jsp:param name="field" value="datasource" />
                    <jsp:param name="readonly" value="${readonly}" />
                </jsp:include>
            </td>
        </tr>
    </table>

    <table class="formTable">
        <tr>TÄSTÄ PUUTTUU weightyesno check box</tr>
        <tr>
            <jsp:include page="../../../inc/singleCellFormText.jsp">
                <jsp:param name="field" value="weight" />
                <jsp:param name="readonly" value="${readonly}" />
            </jsp:include>

            <jsp:include page="../../../inc/singleCellFormText.jsp">
                <jsp:param name="field" value="dataprosessing" />
                <jsp:param name="readonly" value="${readonly}" />
            </jsp:include>
        </tr>
        <tr>
            <jsp:include page="../../../inc/singleCellFormText.jsp">
                <jsp:param name="field" value="collsize" />
                <jsp:param name="readonly" value="${readonly}" />
            </jsp:include>

            <jsp:include page="../../../inc/singleCellFormText.jsp">
                <jsp:param name="field" value="complete" />
                <jsp:param name="readonly" value="${readonly}" />
            </jsp:include>
        </tr>
        <tr>
            <jsp:include page="../../../inc/singleCellFormText.jsp">
                <jsp:param name="field" value="disclaimer" />
                <jsp:param name="readonly" value="${readonly}" />
            </jsp:include>

            <jsp:include page="../../../inc/singleCellFormText.jsp">
                <jsp:param name="field" value="datasetnotes" />
                <jsp:param name="readonly" value="${readonly}" />
            </jsp:include>
        </tr>
        <tr>
            <jsp:include page="../../../inc/singleCellFormText.jsp">
                <jsp:param name="field" value="appraisal" />
                <jsp:param name="readonly" value="${readonly}" />
            </jsp:include>

            <td></td>
        </tr>
    </table>

    <jsp:include page="../../../inc/datatableContainer.jsp">
        <jsp:param name="field" value="relatedmaterials" />
        <jsp:param name="readonly" value="${readonly}" />
    </jsp:include>

    <jsp:include page="../../../inc/datatableContainer.jsp">
        <jsp:param name="field" value="othermaterials" />
        <jsp:param name="readonly" value="${readonly}" />
    </jsp:include>

    <jsp:include page="../../../inc/datatableContainer.jsp">
        <jsp:param name="field" value="publicationcomments" />
        <jsp:param name="readonly" value="${readonly}" />
    </jsp:include>

</div>