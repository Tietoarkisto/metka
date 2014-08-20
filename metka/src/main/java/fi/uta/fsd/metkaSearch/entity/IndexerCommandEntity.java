package fi.uta.fsd.metkaSearch.entity;

import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metkaSearch.commands.indexer.DummyIndexerCommand;
import fi.uta.fsd.metkaSearch.commands.indexer.IndexerCommand;
import fi.uta.fsd.metkaSearch.commands.indexer.RevisionIndexerCommand;
import fi.uta.fsd.metkaSearch.commands.indexer.WikipediaIndexerCommand;
import fi.uta.fsd.metkaSearch.directory.DirectoryManager;
import fi.uta.fsd.metkaSearch.enums.IndexerConfigurationType;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDateTime;

import javax.persistence.*;

@Entity
@Table(name = "INDEXER_COMMAND_QUEUE")
public class IndexerCommandEntity {
    public static IndexerCommandEntity buildFromCommand(IndexerCommand command) {
        IndexerCommandEntity entity = new IndexerCommandEntity();

        entity.setPath(command.getPath());
        entity.setAction(command.getAction());
        entity.setParameters(command.toParameterString());

        return entity;
    }

    @Id
    @SequenceGenerator(name="INDEXER_COMMAND_SEQ", sequenceName="INDEXER_COMMAND_SEQ", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="INDEXER_COMMAND_SEQ")
    @Column(name = "INDEXER_COMMAND_ID", updatable = false)
    private Long id;

    @Column(name = "PATH", updatable = false)
    private String path;

    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE", updatable = false)
    private IndexerConfigurationType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "ACTION", updatable = false)
    private IndexerCommand.Action action;

    @Column(name = "PARAMETERS", updatable = false)
    private String parameters;

    @Column(name = "CREATED", updatable = false)
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
    private LocalDateTime created;

    @Column(name = "REQUESTED")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
    private LocalDateTime requested;

    @Column(name = "HANDLED")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
    private LocalDateTime handled;

    @Column(name = "REPEATED")
    private Boolean repeated;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DirectoryManager.DirectoryPath getPath() {
        boolean useRam = path.substring(0, 2).equals("ME");
        String[] parameters = path.substring(3).split("/");
        IndexerConfigurationType type = IndexerConfigurationType.valueOf(parameters[0]);
        Language language = Language.fromValue(parameters[1]);
        String[] additionals;
        if(parameters.length > 2) {
            additionals = new String[parameters.length-2];
            for(int i = 2; i<parameters.length; i++) {
                additionals[i-2] = parameters[i];
            }
        } else {
            additionals = new String[0];
        }

        return DirectoryManager.formPath(useRam, type, language, additionals);
    }

    public void setPath(DirectoryManager.DirectoryPath path) {
        this.path = path.toString();
        this.type = path.getType();
    }

    public IndexerCommand.Action getAction() {
        return action;
    }

    public IndexerConfigurationType getType() {
        return type;
    }

    public void setType(IndexerConfigurationType type) {
        this.type = type;
    }

    public void setAction(IndexerCommand.Action action) {
        this.action = action;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getRequested() {
        return requested;
    }

    public void setRequested(LocalDateTime requested) {
        this.requested = requested;
    }

    public LocalDateTime getHandled() {
        return handled;
    }

    public void setHandled(LocalDateTime handled) {
        this.handled = handled;
    }

    public Boolean getRepeated() {
        return repeated == null ? false : repeated;
    }

    public void setRepeated(Boolean repeated) {
        this.repeated = (repeated == null ? false : repeated);
    }

    @PrePersist
    private void create() {
        created = new LocalDateTime();
    }

    public IndexerCommand buildCommandFromEntity() {
        IndexerCommand command = null;
        switch(type) {
            case DUMMY:
                command = DummyIndexerCommand.fromParameterString(getPath(), action, parameters);
                break;
            case WIKIPEDIA:
                command = WikipediaIndexerCommand.fromParameterString(getPath(), action, parameters);
                break;
            case REVISION:
                command = RevisionIndexerCommand.fromParameterString(getPath(), action, parameters);
                break;
            default:
                command = null;
        }
        if(command != null) {
            command.setQueueId(id);
        }
        return command;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IndexerCommandEntity that = (IndexerCommandEntity) o;

        if (!id.equals(that.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
