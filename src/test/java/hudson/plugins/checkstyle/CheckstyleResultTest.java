package hudson.plugins.checkstyle;

import hudson.model.Run;
import hudson.plugins.analysis.core.HistoryProvider;
import hudson.plugins.analysis.core.ParserResult;
import hudson.plugins.analysis.core.ReferenceProvider;
import hudson.plugins.analysis.test.BuildResultTest;

/**
 * Tests the class {@link CheckStyleResult}.
 */
public class CheckstyleResultTest extends BuildResultTest<CheckStyleResult> {
    @Override
    protected CheckStyleResult createBuildResult(final Run<?, ?> build, final ParserResult project, final ReferenceProvider referenceProvider, final HistoryProvider historyProvider) {
            return new CheckStyleResult(build, referenceProvider, project, "UTF8", false) {
                @Override
                protected HistoryProvider createBuildHistory(final Run<?, ?> build) {
                    return historyProvider;
                }
            };
    }
}

