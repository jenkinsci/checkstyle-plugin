package hudson.plugins.checkstyle;

import hudson.model.AbstractBuild;
import hudson.plugins.checkstyle.util.model.JavaProject;

/**
 * Creates a new Checkstyle result based on the values of a previous build and the
 * current project.
 *
 * @author Ulli Hafner
 */
public class CheckStyleResultBuilder {
    /**
     * Creates a result that persists the Checkstyle information for the
     * specified build.
     *
     * @param build
     *            the build to create the action for
     * @param project
     *            the project containing the annotations
     * @return the result action
     */
    public CheckStyleResult build(final AbstractBuild<?, ?> build, final JavaProject project) {
        Object previous = build.getPreviousBuild();
        while (previous instanceof AbstractBuild<?, ?> && previous != null) {
            AbstractBuild<?, ?> previousBuild = (AbstractBuild<?, ?>)previous;
            CheckStyleResultAction previousAction = previousBuild.getAction(CheckStyleResultAction.class);
            if (previousAction != null) {
                return new CheckStyleResult(build, project, previousAction.getResult().getProject(),
                        previousAction.getResult().getZeroWarningsHighScore());
            }
            previous = previousBuild.getPreviousBuild();
        }
        return new CheckStyleResult(build, project);
    }
}

