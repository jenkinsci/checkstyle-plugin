package hudson.plugins.checkstyle;

import hudson.model.AbstractBuild;
import hudson.plugins.checkstyle.util.ParserResult;

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
     * @param result
     *            the result containing the annotations
     * @param defaultEncoding
     *            the default encoding to be used when reading and parsing files
     * @return the result action
     */
    public CheckStyleResult build(final AbstractBuild<?, ?> build, final ParserResult result, final String defaultEncoding) {
        Object previous = build.getPreviousBuild();
        while (previous instanceof AbstractBuild<?, ?>) {
            AbstractBuild<?, ?> previousBuild = (AbstractBuild<?, ?>)previous;
            CheckStyleResultAction previousAction = previousBuild.getAction(CheckStyleResultAction.class);
            if (previousAction != null) {
                return new CheckStyleResult(build, defaultEncoding, result, previousAction.getResult());
            }
            previous = previousBuild.getPreviousBuild();
        }
        return new CheckStyleResult(build, defaultEncoding, result);
    }

    /**
     * Creates a result that persists the Checkstyle information for the
     * specified build.
     *
     * @param build
     *            the build to create the action for
     * @param result
     *            the result containing the annotations
     * @param defaultEncoding
     *            the default encoding to be used when reading and parsing files
     * @return the result action
     */
    public CheckStyleMavenResult buildMaven(final AbstractBuild<?, ?> build, final ParserResult result, final String defaultEncoding) {
        Object previous = build.getPreviousBuild();
        while (previous instanceof AbstractBuild<?, ?>) {
            AbstractBuild<?, ?> previousBuild = (AbstractBuild<?, ?>)previous;
            CheckStyleResultAction previousAction = previousBuild.getAction(CheckStyleResultAction.class);
            if (previousAction != null) {
                return new CheckStyleMavenResult(build, defaultEncoding, result, previousAction.getResult());
            }
            previous = previousBuild.getPreviousBuild();
        }
        return new CheckStyleMavenResult(build, defaultEncoding, result);
    }
}

