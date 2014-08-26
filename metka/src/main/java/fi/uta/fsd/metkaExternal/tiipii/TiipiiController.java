package fi.uta.fsd.metkaExternal.tiipii;

import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.access.calls.ValueDataFieldCall;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.ValueDataField;
import fi.uta.fsd.metka.storage.repository.RevisionCreationRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.transfer.revision.RevisionCreateRequest;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

// TODO: When external interfaces are truly possible this should be in a separate context from actual metka application (e.g. external)
@Controller("tiipii")
public class TiipiiController {
    @Autowired
    private RevisionCreationRepository create;

    @RequestMapping(value = "createStudy", method = RequestMethod.POST)
    public @ResponseBody StudyCreateResult createStudy(@RequestBody RevisionCreateRequest request) {
        Pair<ReturnResult, RevisionData> result = create.create(request);
        if(result.getLeft() != ReturnResult.REVISION_CREATED) {
            return new StudyCreateResult(result.getLeft(), null);
        }

        Pair<StatusCode, ValueDataField> pair = result.getRight().dataField(ValueDataFieldCall.get("studyid"));
        if(pair.getLeft() != StatusCode.FIELD_FOUND || !pair.getRight().hasValueFor(Language.DEFAULT)) {
            return new StudyCreateResult(ReturnResult.PARAMETERS_MISSING, null);
        }

        return new StudyCreateResult(ReturnResult.REVISION_CREATED, pair.getRight().getActualValueFor(Language.DEFAULT));
    }
}
