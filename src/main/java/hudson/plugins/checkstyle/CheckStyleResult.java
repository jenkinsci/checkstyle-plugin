package hudson.plugins.checkstyle;

import hudson.model.AbstractBuild;
import hudson.plugins.checkstyle.parser.Warning;
import hudson.plugins.checkstyle.util.BuildResult;
import hudson.plugins.checkstyle.util.ParserResult;
import hudson.plugins.checkstyle.util.model.JavaProject;

/**
 * Represents the results of the Checkstyle analysis. One instance of this class
 * is persisted for each build via an XML file.
 *
 * @author Ulli Hafner
 */
public class CheckStyleResult extends BuildResult {
    /** Unique identifier of this class. */
    private static final long serialVersionUID = 2768250056765266658L;
    static {
        XSTREAM.alias("warning", Warning.class);
    }

    /**
     * Creates a new instance of {@link CheckStyleResult}.
     *
     * @param build
     *            the current build as owner of this action
     * @param defaultEncoding
     *            the default encoding to be used when reading and parsing files
     * @param result
     *            the parsed result with all annotations
     */
    public CheckStyleResult(final AbstractBuild<?, ?> build, final String defaultEncoding,
            final ParserResult result) {
        super(build, defaultEncoding, result);
    }

    /**
     * Creates a new instance of {@link CheckStyleResult}.
     *
     * @param build
     *            the current build as owner of this action
     * @param defaultEncoding
     *            the default encoding to be used when reading and parsing files
     * @param result
     *            the parsed result with all annotations
     * @param previous
     *            the result of the previous build
     */
    public CheckStyleResult(final AbstractBuild<?, ?> build, final String defaultEncoding,
            final ParserResult result, final CheckStyleResult previous) {
        super(build, defaultEncoding, result, previous);
    }

    /**
     * Returns a summary message for the summary.jelly file.
     *
     * @return the summary message
     */
    public String getSummary() {
        return ResultSummary.createSummary(this);
    }

    /** {@inheritDoc} */
    @Override
    public String getDetails() {
        String message = ResultSummary.createDeltaMessage(this);
        if (getNumberOfAnnotations() == 0 && getDelta() == 0) {
            message += "<li>" + Messages.Checkstyle_ResultAction_NoWarningsSince(getZeroWarningsSinceBuild()) + "</li>";
            message += createHighScoreMessage();
        }
        return message;
    }


    /**
     * Creates a highscore message.
     *
     * @return a highscore message
     */
    private String createHighScoreMessage() {
        if (isNewZeroWarningsHighScore()) {
            long days = getDays(getZeroWarningsHighScore());
            if (days == 1) {
                return "<li>" + Messages.Checkstyle_ResultAction_OneHighScore() + "</li>";
            }
            else {
                return "<li>" + Messages.Checkstyle_ResultAction_MultipleHighScore(days) + "</li>";
            }
        }
        else {
            long days = getDays(getHighScoreGap());
            if (days == 1) {
                return "<li>" + Messages.Checkstyle_ResultAction_OneNoHighScore() + "</li>";
            }
            else {
                return "<li>" + Messages.Checkstyle_ResultAction_MultipleNoHighScore(days) + "</li>";
            }
        }
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
