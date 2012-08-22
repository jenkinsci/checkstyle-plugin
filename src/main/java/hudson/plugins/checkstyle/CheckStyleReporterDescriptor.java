package hudson.plugins.checkstyle;

import hudson.Extension;
import hudson.plugins.analysis.core.ReporterDescriptor;

/**
 * Descriptor for the class {@link CheckStyleReporter}. Used as a singleton. The
 * class is marked as public so that it can be accessed from views.
 *
 * @author Ulli Hafner
 */
@Extension(ordinal = 100, optional = true) // NOCHECKSTYLE
public class CheckStyleReporterDescriptor extends ReporterDescriptor {
    /**
     * Creates a new instance of <code>CheckStyleReporterDescriptor</code>.
     */
    public CheckStyleReporterDescriptor() {
        super(CheckStyleReporter.class, new CheckStyleDescriptor());
    }
}

