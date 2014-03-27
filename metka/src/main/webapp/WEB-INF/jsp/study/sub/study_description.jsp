<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<c:set var="context" value="${fn:toUpperCase(page)}" />
<div class="tabs tab_study_description">
    <table class="formTable">
        <tbody>
            <jsp:include page="../../../inc/fullRowFormText.jsp">
                <jsp:param name="field" value="alttitle" />
            </jsp:include>
        </tbody>
    </table>

    <jsp:include page="../../../inc/datatableContainer.jsp">
        <jsp:param name="field" value="partitles" />
    </jsp:include>

    <jsp:include page="../../../inc/datatableContainer.jsp">
        <jsp:param name="field" value="authors" />
    </jsp:include>

    <jsp:include page="../../../inc/datatableContainer.jsp">
        <jsp:param name="field" value="otherauthors" />
    </jsp:include>

    <jsp:include page="../../../inc/datatableContainer.jsp">
        <jsp:param name="field" value="producers" />
    </jsp:include>

    <jsp:include page="../../../inc/datatableContainer.jsp">
        <jsp:param name="field" value="keywords" />
    </jsp:include>

    <jsp:include page="../../../inc/datatableContainer.jsp">
        <jsp:param name="field" value="topics" />
    </jsp:include>

    <table class="formTable">
        <tbody>
            <tr>
                <jsp:include page="../../../inc/singleCellFormText.jsp">
                    <jsp:param name="field" value="biblcit" />
                </jsp:include>
                <jsp:include page="../../../inc/singleCellFormText.jsp">
                    <jsp:param name="field" value="abstract" />
                </jsp:include>
            </tr>
        </tbody>
    </table>

    <jsp:include page="../../../inc/datatableContainer.jsp">
        <jsp:param name="field" value="timeperiods" />
    </jsp:include>

    <jsp:include page="../../../inc/datatableContainer.jsp">
        <jsp:param name="field" value="countries" />
    </jsp:include>

    <jsp:include page="../../../inc/datatableContainer.jsp">
        <jsp:param name="field" value="universes" />
    </jsp:include>

    <jsp:include page="../../../inc/datatableContainer.jsp">
        <jsp:param name="field" value="geogcovers" />
    </jsp:include>

    <jsp:include page="../../../inc/datatableContainer.jsp">
        <jsp:param name="field" value="colltime" />
    </jsp:include>

    <jsp:include page="../../../inc/datatableContainer.jsp">
        <jsp:param name="field" value="collectors" />
    </jsp:include>

    <jsp:include page="../../../inc/datatableContainer.jsp">
        <jsp:param name="field" value="analysis" />
    </jsp:include>

    <jsp:include page="../../../inc/datatableContainer.jsp">
        <jsp:param name="field" value="timemethods" />
    </jsp:include>

    <jsp:include page="../../../inc/datatableContainer.jsp">
        <jsp:param name="field" value="collmodes" />
    </jsp:include>

    <jsp:include page="../../../inc/datatableContainer.jsp">
        <jsp:param name="field" value="instruments" />
    </jsp:include>

    <jsp:include page="../../../inc/datatableContainer.jsp">
        <jsp:param name="field" value="sampprocs" />
    </jsp:include>

    <table class="formTable">
        <tbody>
            <jsp:include page="../../../inc/singleCellFormText.jsp">
                <jsp:param name="field" value="resprate" />
            </jsp:include>
        </tbody>
    </table>
    <table class="formTable">
        <tr>
            <td>
                <jsp:include page="../../../inc/singleCellFormText.jsp">
                    <jsp:param name="field" value="datasource" />
                </jsp:include>
            </td>
        </tr>
    </table>

    <table class="formTable">
        <tr>TÄSTÄ PUUTTUU weightyesno check box</tr>
        <tr>
            <jsp:include page="../../../inc/singleCellFormText.jsp">
                <jsp:param name="field" value="weight" />
            </jsp:include>

            <jsp:include page="../../../inc/singleCellFormText.jsp">
                <jsp:param name="field" value="dataprosessing" />
            </jsp:include>
        </tr>
        <tr>
            <jsp:include page="../../../inc/singleCellFormText.jsp">
                <jsp:param name="field" value="collsize" />
            </jsp:include>

            <jsp:include page="../../../inc/singleCellFormText.jsp">
                <jsp:param name="field" value="complete" />
            </jsp:include>
        </tr>
        <tr>
            <jsp:include page="../../../inc/singleCellFormText.jsp">
                <jsp:param name="field" value="disclaimer" />
            </jsp:include>

            <jsp:include page="../../../inc/singleCellFormText.jsp">
                <jsp:param name="field" value="datasetnotes" />
            </jsp:include>
        </tr>
        <tr>
            <jsp:include page="../../../inc/singleCellFormText.jsp">
                <jsp:param name="field" value="appraisal" />
            </jsp:include>

            <td></td>
        </tr>
    </table>

    <jsp:include page="../../../inc/datatableContainer.jsp">
        <jsp:param name="field" value="relatedmaterials" />
    </jsp:include>

    <jsp:include page="../../../inc/datatableContainer.jsp">
        <jsp:param name="field" value="othermaterials" />
    </jsp:include>

    <jsp:include page="../../../inc/datatableContainer.jsp">
        <jsp:param name="field" value="publicationcomments" />
    </jsp:include>

</div>