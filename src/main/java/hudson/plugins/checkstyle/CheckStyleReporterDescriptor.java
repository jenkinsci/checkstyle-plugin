package hudson.plugins.checkstyle;

import hudson.maven.MavenReporter;
import hudson.plugins.checkstyle.util.PluginDescriptor;
import hudson.plugins.checkstyle.util.ReporterDescriptor;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.StaplerRequest;

/**
 * Descriptor for the class {@link CheckStyleReporter}. Used as a singleton. The
 * class is marked as public so that it can be accessed from views.
 *
 * @author Ulli Hafner
 */
public class CheckStyleReporterDescriptor extends ReporterDescriptor {
    /**
     * Creates a new instance of <code>CheckStyleReporterDescriptor</code>.
     *
     * @param pluginDescriptor
     *            the plug-in descriptor of the publisher
     */
    public CheckStyleReporterDescriptor(final PluginDescriptor pluginDescriptor) {
        super(CheckStyleReporter.class, pluginDescriptor);
    }

    /** {@inheritDoc} */
    @Override
    public MavenReporter newInstance(final StaplerRequest request, final JSONObject formData) throws FormException {
        return request.bindJSON(CheckStyleReporter.class, formData);
    }
}

