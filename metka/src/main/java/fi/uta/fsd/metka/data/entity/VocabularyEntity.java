package fi.uta.fsd.metka.data.entity;

import javax.persistence.*;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: MetkaDev1
 * Date: 11/14/13
 * Time: 1:47 PM
 */
@Entity
@Table(name = "VOCABULARY")
public class VocabularyEntity {

    @Id
    @Column(name = "VOCABULARY_ID")
    private String vocabularyId;

    @Column(name = "NAME")
    private String name;

    @OneToMany(mappedBy = "targetVocabulary")
    private List<TermEntity> termList;

    @Override
    public String toString() {
        return "Entity[name="+this.getClass().getSimpleName()+", id="+vocabularyId+"]";
    }
}
