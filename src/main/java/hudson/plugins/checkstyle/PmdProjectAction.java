package hudson.plugins.checkstyle;

import hudson.model.AbstractProject;
import hudson.plugins.checkstyle.util.AbstractProjectAction;
import hudson.plugins.checkstyle.Messages;

/**
 * Entry point to visualize the PMD trend graph in the project screen.
 * Drawing of the graph is delegated to the associated
 * {@link PmdResultAction}.
 *
 * @author Ulli Hafner
 */
public class PmdProjectAction extends AbstractProjectAction<PmdResultAction> {
    /** Unique identifier of this class. */
    private static final long serialVersionUID = -654316141132780561L;

    /**
     * Instantiates a new find bugs project action.
     *
     * @param project
     *            the project that owns this action
     * @param height
     *            the height of the trend graph
     */
    public PmdProjectAction(final AbstractProject<?, ?> project, final int height) {
        super(project, PmdResultAction.class, PmdPublisher.PMD_DESCRIPTOR, height);
    }

    /** {@inheritDoc} */
    public String getDisplayName() {
        return Messages.Checkstyle_ProjectAction_Name();
    }

    /** {@inheritDoc} */
    @Override
    public String getCookieName() {
        return "PMD_displayMode";
    }

    /** {@inheritDoc} */
    @Override
    public String getTrendName() {
        return Messages.Checkstyle_Trend_Name();
    }
}

