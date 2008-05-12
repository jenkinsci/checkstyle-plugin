package hudson.plugins.checkstyle;

import hudson.model.AbstractBuild;
import hudson.plugins.checkstyle.util.AbstractResultAction;
import hudson.plugins.checkstyle.util.HealthReportBuilder;
import hudson.plugins.checkstyle.util.PluginDescriptor;

import java.util.NoSuchElementException;

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
    /** Unique identifier of this class. */
    private static final long serialVersionUID = -5329651349674842873L;

    /**
     * Creates a new instance of <code>CheckStyleResultAction</code>.
     *
     * @param owner
     *            the associated build of this action
     * @param healthReportBuilder
     *            health builder to use
     * @param result
     *            the result in this build
     */
    public CheckStyleResultAction(final AbstractBuild<?, ?> owner, final HealthReportBuilder healthReportBuilder, final CheckStyleResult result) {
        super(owner, healthReportBuilder, result);
    }

    /**
     * Creates a new instance of <code>CheckStyleResultAction</code>.
     *
     * @param owner
     *            the associated build of this action
     * @param healthReportBuilder
     *            health builder to use
     */
    public CheckStyleResultAction(final AbstractBuild<?, ?> owner, final HealthReportBuilder healthReportBuilder) {
        super(owner, healthReportBuilder);
    }

    /** {@inheritDoc} */
    public String getDisplayName() {
        return Messages.Checkstyle_ProjectAction_Name();
    }

    /** {@inheritDoc} */
    @Override
    protected PluginDescriptor getDescriptor() {
        return CheckStylePublisher.CHECKSTYLE_DESCRIPTOR;
    }

    /**
     * Gets the Checkstyle result of the previous build.
     *
     * @return the Checkstyle result of the previous build.
     * @throws NoSuchElementException
     *             if there is no previous build for this action
     */
    public CheckStyleResultAction getPreviousResultAction() {
        AbstractResultAction<CheckStyleResult> previousBuild = getPreviousBuild();
        if (previousBuild instanceof CheckStyleResultAction) {
            return (CheckStyleResultAction)previousBuild;
        }
        throw new NoSuchElementException("There is no previous build for action " + this);
    }

    /** {@inheritDoc} */
    public String getMultipleItemsTooltip(final int numberOfItems) {
        return Messages.Checkstyle_ResultAction_MultipleWarnings(numberOfItems);
    }

    /** {@inheritDoc} */
    public String getSingleItemTooltip() {
        return Messages.Checkstyle_ResultAction_OneWarning();
    }
}
