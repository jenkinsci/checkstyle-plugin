package hudson.plugins.checkstyle;

import hudson.model.Run;
import hudson.plugins.analysis.core.AbstractResultAction;
import hudson.plugins.analysis.core.HealthDescriptor;
import hudson.plugins.analysis.core.PluginDescriptor;

/**
 * Controls the live cycle of the Checkstyle results. This action persists the
 * results of the Checkstyle analysis of a build and displays the results on the
 * build page. The actual visualization of the results is defined in the
 * matching <code>summary.jelly</code> file.
 * <p>
 * Moreover, this class renders the Checkstyle result trend.
 * </p>
 *
 * @author Ulli Hafner
 */
public class CheckStyleResultAction extends AbstractResultAction<CheckStyleResult> {
    /**
     * Creates a new instance of <code>CheckStyleResultAction</code>.
     *
     * @param owner
     *            the associated build of this action
     * @param healthDescriptor
     *            health descriptor
     * @param result
     *            the result in this build
     */
    public CheckStyleResultAction(final Run<?, ?> owner, final HealthDescriptor healthDescriptor,
            final CheckStyleResult result) {
        super(owner, new CheckStyleHealthDescriptor(healthDescriptor), result);
    }

    @Override
    public String getDisplayName() {
        return Messages.Checkstyle_ProjectAction_Name();
    }

    @Override
    protected PluginDescriptor getDescriptor() {
        return new CheckStyleDescriptor();
    }
}
