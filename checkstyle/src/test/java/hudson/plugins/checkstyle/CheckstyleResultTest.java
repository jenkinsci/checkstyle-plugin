package hudson.plugins.checkstyle;

import hudson.model.AbstractBuild;
import hudson.plugins.analysis.core.BuildHistory;
import hudson.plugins.analysis.core.ParserResult;
import hudson.plugins.analysis.test.BuildResultTest;

/**
 * Tests the class {@link CheckStyleResult}.
 */
public class CheckstyleResultTest extends BuildResultTest<CheckStyleResult> {
    /** {@inheritDoc} */
    @Override
    protected CheckStyleResult createBuildResult(final AbstractBuild<?, ?> build, final ParserResult project, final BuildHistory history) {
        return new CheckStyleResult(build, null, project, history);
    }
}

