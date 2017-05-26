package fi.uta.fsd.metka.ddi;

import org.apache.xmlbeans.XmlOptions;

/**
 * Created by henrisu on 28.11.2016.
 */
public class MetkaXmlOptions {

    public static final XmlOptions DDI_EXPORT_XML_OPTIONS =
            buildMetkaDDIExportXmlOptions();

    private static final XmlOptions buildMetkaDDIExportXmlOptions()
    {
        XmlOptions xmlOptions = new XmlOptions();
        xmlOptions.setSaveCDataEntityCountThreshold(0);
        xmlOptions.put( XmlOptions.SAVE_INNER );
        xmlOptions.put( XmlOptions.SAVE_PRETTY_PRINT );
        xmlOptions.put( XmlOptions.SAVE_AGGRESSIVE_NAMESPACES );
        xmlOptions.put( XmlOptions.SAVE_USE_DEFAULT_NAMESPACE );

        return xmlOptions;
    }
}
