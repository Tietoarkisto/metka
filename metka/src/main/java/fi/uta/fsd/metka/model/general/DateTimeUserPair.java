package fi.uta.fsd.metka.model.general;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.uta.fsd.metkaAuthentication.AuthenticationUtil;
import org.joda.time.LocalDateTime;

/**
 * Defines a timestamp of sorts where something is recorded by
 * when it was done and by whom.
 * Specification and documentation is found from uml/uml_general.graphml
 */
@JsonIgnoreProperties("_comment")
public class DateTimeUserPair {
    public static DateTimeUserPair build() {
        return build(new LocalDateTime());
    }

    public static DateTimeUserPair build(LocalDateTime time) {
        return new DateTimeUserPair(time, AuthenticationUtil.getUserName());
    }

    private final LocalDateTime time;
    private final String user;

    @JsonCreator
    public DateTimeUserPair(@JsonProperty("time") LocalDateTime time, @JsonProperty("user") String user) {
        this.time = time;
        this.user = user;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public String getUser() {
        return user;
    }

    public DateTimeUserPair copy() {
        return new DateTimeUserPair(time, user);
    }
}
