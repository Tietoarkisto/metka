package fi.uta.fsd.metka.storage.repository;

import fi.uta.fsd.metka.model.transfer.TransferData;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface RevisionSaveRepository {
    /**
     * Save given TransferData to database and marks any found errors to returned data
     * @param transferData Data to be saved
     * @return Pair<ReturnResult, TransferData> Left value is a ReturnResult telling about actual operation errors
     *      (like REVISION_NOT_FOUND, SERIALIZATION_FAILED etc.) right value is the provided TransferData including any possible field errors
     */
    public Pair<ReturnResult, TransferData> saveRevision(TransferData transferData);
}
