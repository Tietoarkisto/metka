package fi.uta.fsd.metka.adapters;

import org.joda.time.LocalTime;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Created with IntelliJ IDEA.
 * User: lasseku
 * Date: 12/17/13
 * Time: 3:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class LocalTimeAdapter extends XmlAdapter<String, LocalTime> {
    @Override
    public LocalTime unmarshal(String v) throws Exception {
        return LocalTime.parse(v);
    }

    @Override
    public String marshal(LocalTime v) throws Exception {
        return v.toString();
    }
}
