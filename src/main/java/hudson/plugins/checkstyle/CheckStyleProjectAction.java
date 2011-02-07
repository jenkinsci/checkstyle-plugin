package hudson.plugins.checkstyle;

import hudson.model.AbstractProject;
import hudson.plugins.analysis.core.AbstractProjectAction;

/**
 * Entry point to visualize the Checkstyle trend graph in the project screen.
 *
 * @author Ulli Hafner
 */
public class CheckStyleProjectAction extends AbstractProjectAction<CheckStyleResultAction> {
    /**
     * Instantiates a new {@link CheckStyleProjectAction}.
     *
     * @param project
     *            the project that owns this action
     */
    public CheckStyleProjectAction(final AbstractProject<?, ?> project) {
        super(project, CheckStyleResultAction.class, new CheckStyleDescriptor());
    }

    /** {@inheritDoc} */
    public String getDisplayName() {
        return Messages.Checkstyle_ProjectAction_Name();
    }

    /** {@inheritDoc} */
    @Override
    public String getTrendName() {
        return Messages.Checkstyle_Trend_Name();
    }
}

