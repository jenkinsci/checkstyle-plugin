package hudson.plugins.checkstyle;

import hudson.model.AbstractBuild;
import hudson.plugins.checkstyle.parser.Warning;
import hudson.plugins.checkstyle.util.AnnotationsBuildResult;
import hudson.plugins.checkstyle.util.model.JavaProject;

/**
 * Represents the results of the Checkstyle analysis. One instance of this class is persisted for
 * each build via an XML file.
 *
 * @author Ulli Hafner
 */
public class CheckStyleResult extends AnnotationsBuildResult {
    /** Unique identifier of this class. */
    private static final long serialVersionUID = 2768250056765266658L;
    static {
        XSTREAM.alias("warning", Warning.class);
    }

    /**
     * Creates a new instance of <code>CheckStyleResult</code>.
     *
     * @param build
     *            the current build as owner of this action
     * @param project
     *            the parsed result with all annotations
     */
    public CheckStyleResult(final AbstractBuild<?, ?> build, final JavaProject project) {
        super(build, project);
    }

    /**
     * Creates a new instance of <code>CheckStyleResult</code>.
     *
     * @param build
     *            the current build as owner of this action
     * @param project
     *            the parsed result with all annotations
     * @param previous
     *            the result of the previous build
     */
    public CheckStyleResult(final AbstractBuild<?, ?> build, final JavaProject project, final CheckStyleResult previous) {
        super(build, project, previous);
    }

    /**
     * Returns a summary message for the summary.jelly file.
     *
     * @return the summary message
     */
    public String getSummary() {
        return ResultSummary.createSummary(this);
    }

    /**
     * Returns the detail messages for the summary.jelly file.
     *
     * @return the summary message
     */
    public String getDetails() {
        String message = ResultSummary.createDeltaMessage(this);
        if (getNumberOfAnnotations() == 0 && getDelta() == 0) {
            return message + "<li>" + Messages.Checkstyle_ResultAction_NoWarningsSince(getZeroWarningsSinceBuild()) + "</li>";
        }
        return message;
    }

    /**
     * Returns the name of the file to store the serialized annotations.
     *
     * @return the name of the file to store the serialized annotations
     */
    @Override
    protected String getSerializationFileName() {
        return "checkstyle-warnings.xml";
    }

    /** {@inheritDoc} */
    public String getDisplayName() {
        return Messages.Checkstyle_ProjectAction_Name();
    }

    /**
     * Returns the results of the previous build.
     *
     * @return the result of the previous build, or <code>null</code> if no
     *         such build exists
     */
    @Override
    protected JavaProject getPreviousResult() {
        CheckStyleResultAction action = getOwner().getAction(CheckStyleResultAction.class);
        if (action.hasPreviousResultAction()) {
            return action.getPreviousResultAction().getResult().getProject();
        }
        else {
            return new JavaProject();
        }
    }

    /**
     * Returns whether a previous build result exists.
     *
     * @return <code>true</code> if a previous build result exists.
     */
    @Override
    protected boolean hasPreviousResult() {
        return getOwner().getAction(CheckStyleResultAction.class).hasPreviousResultAction();
    }
}
