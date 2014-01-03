package fi.uta.fsd.metka.mvc.adapters;

import org.joda.time.DateTime;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Created with IntelliJ IDEA.
 * User: lasseku
 * Date: 12/17/13
 * Time: 3:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class DateTimeAdapter extends XmlAdapter<String, DateTime> {
    @Override
    public DateTime unmarshal(String v) throws Exception {
        return DateTime.parse(v);
    }

    @Override
    public String marshal(DateTime v) throws Exception {
        return v.toString();
    }
}
