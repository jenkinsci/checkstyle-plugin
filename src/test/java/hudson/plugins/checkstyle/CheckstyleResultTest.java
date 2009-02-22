package hudson.plugins.checkstyle;

import static junit.framework.Assert.*;
import hudson.model.AbstractBuild;
import hudson.plugins.checkstyle.util.BuildResult;
import hudson.plugins.checkstyle.util.BuildResultTest;
import hudson.plugins.checkstyle.util.ParserResult;

/**
 * Tests the class {@link CheckStyleResult}.
 */
public class CheckstyleResultTest extends BuildResultTest<CheckStyleResult> {
    /** {@inheritDoc} */
    @Override
    protected CheckStyleResult createBuildResult(final AbstractBuild<?, ?> build, final ParserResult project) {
        return new CheckStyleResult(build, null, project);
    }

    /** {@inheritDoc} */
    @Override
    protected CheckStyleResult createBuildResult(final AbstractBuild<?, ?> build, final ParserResult project, final CheckStyleResult previous) {
        return new CheckStyleResult(build, null, project, previous);
    }

    /** {@inheritDoc} */
    @Override
    protected void verifyHighScoreMessage(final int expectedZeroWarningsBuildNumber, final boolean expectedIsNewHighScore, final long expectedHighScore, final long gap, final CheckStyleResult result) {
        if (result.hasNoAnnotations() && result.getDelta() == 0) {
            assertTrue(result.getDetails().contains(Messages.Checkstyle_ResultAction_NoWarningsSince(expectedZeroWarningsBuildNumber)));
            if (expectedIsNewHighScore) {
                long days = BuildResult.getDays(expectedHighScore);
                if (days == 1) {
                    assertTrue(result.getDetails().contains(Messages.Checkstyle_ResultAction_OneHighScore()));
                }
                else {
                    assertTrue(result.getDetails().contains(Messages.Checkstyle_ResultAction_MultipleHighScore(days)));
                }
            }
            else {
                long days = BuildResult.getDays(gap);
                if (days == 1) {
                    assertTrue(result.getDetails().contains(Messages.Checkstyle_ResultAction_OneNoHighScore()));
                }
                else {
                    assertTrue(result.getDetails().contains(Messages.Checkstyle_ResultAction_MultipleNoHighScore(days)));
                }
            }
        }
    }
}

