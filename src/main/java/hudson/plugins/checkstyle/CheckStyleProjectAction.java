package hudson.plugins.checkstyle;

import hudson.model.AbstractProject;
import hudson.plugins.checkstyle.util.AbstractProjectAction;

/**
 * Entry point to visualize the Checkstyle trend graph in the project screen.
 * Drawing of the graph is delegated to the associated
 * {@link CheckStyleResultAction}.
 *
 * @author Ulli Hafner
 */
public class CheckStyleProjectAction extends AbstractProjectAction<CheckStyleResultAction> {
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
    public CheckStyleProjectAction(final AbstractProject<?, ?> project, final int height) {
        super(project, CheckStyleResultAction.class, CheckStylePublisher.CHECKSTYLE_DESCRIPTOR, height);
    }

    /** {@inheritDoc} */
    public String getDisplayName() {
        return Messages.Checkstyle_ProjectAction_Name();
    }

    /** {@inheritDoc} */
    @Override
    public String getCookieName() {
        return "Checkstyle_displayMode";
    }

    /** {@inheritDoc} */
    @Override
    public String getTrendName() {
        return Messages.Checkstyle_Trend_Name();
    }
}

