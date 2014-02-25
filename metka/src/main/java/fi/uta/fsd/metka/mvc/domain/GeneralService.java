package fi.uta.fsd.metka.mvc.domain;

import fi.uta.fsd.metka.data.enums.repositoryResponses.RemoveResponse;
import fi.uta.fsd.metka.data.repository.GeneralRepository;
import fi.uta.fsd.metka.model.configuration.Choicelist;
import fi.uta.fsd.metka.model.configuration.Option;
import fi.uta.fsd.metka.model.configuration.Reference;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.ValueFieldContainer;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static fi.uta.fsd.metka.data.util.ModelAccessUtil.*;

/**
 * Created with IntelliJ IDEA.
 * User: lasseku
 * Date: 1/24/14
 * Time: 10:34 AM
 * To change this template use File | Settings | File Templates.
 */
@Service
public class GeneralService {

    @Autowired
    private GeneralRepository repository;


    /**
     * Return the id of next or previous revisionable of the same type as the current revisionable the user is looking at.
     * Used to navigate to previous or next object.
     *
     * @param currentId Id of the revisionable the user is looking at at the moment
     * @param type What type of revisionable is required (series, publication etc.)
     * @param forward do we want next or previous revisionable
     * @return Id of the adjanced revisionable object. If not found then error is thrown instead.
     */
    public Integer getAdjancedRevisionableId(Integer currentId, String type, boolean forward) throws NotFoundException {
        return repository.getAdjancedRevisionableId(currentId, type, forward);
    }

    /**
     * Removes the draft revision from given revisionable object. There has to be a draft, otherwise error should be returned.
     * To remove a draft the user requesting the removal has to be the same as the handler of that draft.
     * Notice that revision number is not required since all revisionable objects can have at most one draft open at a time.
     *
     * @param type - Type of the revisionable object.
     * @param id - Id of the revisionable object.
     * @return RemoveResponse enum returned by repository. Success or failure of the operation can be determined from this.
     */
    public RemoveResponse removeDraft(String type, Integer id) {
        return repository.removeDraft(type, id);
    }

    /**
     * Performs a logical remove on revisionable entity with given type and id.
     * To logically remove a revisionable entity it has to have at least one approved revision and can not have open drafts,
     * If successful then the revisionable has a value 'true' in its 'removed' column in database.
     *
     * @param type - Type of the revisionable object.
     * @param id - Id of the revisionable object.
     * @return RemoveResponse enum returned by repository. Success or failure of the operation can be determined from this.
     */
    public RemoveResponse removeLogical(String type, Integer id) {
        return repository.removeLogical(type, id);
    }

    public void fillOptions(Choicelist list, Reference ref) throws IOException {
        List<RevisionData> datas = repository.getLatestRevisionsForType(ref.getTargetType(), ref.getApprovedOnly());
        for(RevisionData data : datas) {
            Option option = new Option(extractStringSimpleValue((ValueFieldContainer)data.getField(ref.getValueField())));
            if(StringUtils.isEmpty(ref.getTitleField())) {
                option.setTitle(option.getValue());
            } else {
                option.setTitle(extractStringSimpleValue((ValueFieldContainer)data.getField(ref.getTitleField())));
            }
            list.getOptions().add(option);
        }
        Collections.sort(list.getOptions(), new Comparator<Option>() {
            public int compare(Option o1, Option o2) {
                return o1.getTitle().compareTo(o2.getTitle());
            }
        });
    }
}
