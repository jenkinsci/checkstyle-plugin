package hudson.plugins.checkstyle;

import hudson.model.AbstractBuild;
import hudson.plugins.checkstyle.util.AbstractAnnotationsBuildResultTest;
import hudson.plugins.checkstyle.util.ParserResult;

/**
 * Tests the class {@link CheckStyleResult}.
 */
public class CheckstyleResultTest extends AbstractAnnotationsBuildResultTest<CheckStyleResult> {
    /** {@inheritDoc} */
    @Override
    protected CheckStyleResult createBuildResult(final AbstractBuild<?, ?> build, final ParserResult project) {
        return new CheckStyleResult(build, project);
    }

    /** {@inheritDoc} */
    @Override
    protected CheckStyleResult createBuildResult(final AbstractBuild<?, ?> build, final ParserResult project, final CheckStyleResult previous) {
        return new CheckStyleResult(build, project, previous);
    }
}

