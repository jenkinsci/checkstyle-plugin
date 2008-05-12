package hudson.plugins.checkstyle;

import hudson.plugins.checkstyle.util.PluginDescriptor;
import hudson.plugins.checkstyle.util.ReporterDescriptor;

/**
 * Descriptor for the class {@link PmdReporter}. Used as a singleton. The
 * class is marked as public so that it can be accessed from views.
 *
 * @author Ulli Hafner
 */
public class PmdReporterDescriptor extends ReporterDescriptor {
    /**
     * Creates a new instance of <code>PmdReporterDescriptor</code>.
     *
     * @param pluginDescriptor
     *            the plug-in descriptor of the publisher
     */
    public PmdReporterDescriptor(final PluginDescriptor pluginDescriptor) {
        super(PmdReporter.class, pluginDescriptor);
    }
}

