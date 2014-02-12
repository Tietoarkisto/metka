@XmlJavaTypeAdapters({
        @XmlJavaTypeAdapter(type = DateTime.class, value = DateTimeAdapter.class),
        @XmlJavaTypeAdapter(type = LocalDate.class, value = LocalDateAdapter.class),
        @XmlJavaTypeAdapter(type = LocalTime.class, value = LocalTimeAdapter.class)
})
package fi.uta.fsd.metka.model.data.container;

import fi.uta.fsd.metka.mvc.adapters.DateTimeAdapter;
import fi.uta.fsd.metka.mvc.adapters.LocalDateAdapter;
import fi.uta.fsd.metka.mvc.adapters.LocalTimeAdapter;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;