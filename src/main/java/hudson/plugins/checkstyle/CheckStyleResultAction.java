package hudson.plugins.checkstyle;

import hudson.model.AbstractBuild;
import hudson.plugins.annotations.util.AbstractResultAction;
import hudson.plugins.annotations.util.HealthDescriptor;
import hudson.plugins.annotations.util.PluginDescriptor;

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
     * @param result
     *            the result in this build
     * @param healthDescriptor
     *            health descriptor
     */
    public CheckStyleResultAction(final AbstractBuild<?, ?> owner, final CheckStyleResult result, final HealthDescriptor healthDescriptor) {
        super(owner, result, new CheckStyleHealthDescriptor(healthDescriptor));
    }

    /**
     * Creates a new instance of <code>CheckStyleResultAction</code>.
     *
     * @param owner
     *            the associated build of this action
     * @param healthDescriptor
     *            health descriptor
     */
    public CheckStyleResultAction(final AbstractBuild<?, ?> owner, final HealthDescriptor healthDescriptor) {
        super(owner, new CheckStyleHealthDescriptor(healthDescriptor));
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
