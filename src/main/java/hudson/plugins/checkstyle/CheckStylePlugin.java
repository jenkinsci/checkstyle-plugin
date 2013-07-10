package hudson.plugins.checkstyle;

import org.apache.xerces.parsers.SAXParser;

import hudson.Plugin;

import hudson.plugins.checkstyle.rules.CheckStyleRules;

/**
 * Initializes the Checkstyle messages and descriptions.
 *
 * @author Ulli Hafner
 */
public class CheckStylePlugin extends Plugin {
    /** Property of SAX parser factory. */
    static final String SAX_DRIVER_PROPERTY = "org.xml.sax.driver";

    @Override
    public void start() {
        String oldProperty = System.getProperty(SAX_DRIVER_PROPERTY);
        System.setProperty(SAX_DRIVER_PROPERTY, SAXParser.class.getName());

        CheckStyleRules.getInstance().initialize();

        if (oldProperty != null) {
            System.setProperty(SAX_DRIVER_PROPERTY, oldProperty);
        }
    }
}
