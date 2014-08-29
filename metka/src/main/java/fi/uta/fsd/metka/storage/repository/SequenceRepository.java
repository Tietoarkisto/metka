package fi.uta.fsd.metka.storage.repository;

import fi.uta.fsd.metka.storage.entity.SequenceEntity;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface SequenceRepository {
    SequenceEntity getNewSequenceValue(String key);

    SequenceEntity getNewSequenceValue(String key, Long initialValue);
}
