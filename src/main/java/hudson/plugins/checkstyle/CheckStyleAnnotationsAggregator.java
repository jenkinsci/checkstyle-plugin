package hudson.plugins.checkstyle;

import hudson.Launcher;
import hudson.matrix.MatrixRun;
import hudson.matrix.MatrixBuild;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.plugins.analysis.core.AnnotationsAggregator;
import hudson.plugins.analysis.core.HealthDescriptor;
import hudson.plugins.analysis.core.ParserResult;

/**
 * Aggregates {@link CheckStyleResultAction}s of {@link MatrixRun}s into
 * {@link MatrixBuild}.
 *
 * @author Ulli Hafner
 */

public class CheckStyleAnnotationsAggregator extends AnnotationsAggregator {
    /**
     * Creates a new instance of {@link CheckStyleAnnotationsAggregator}.
     *
     * @param build
     *            the matrix build
     * @param launcher
     *            the launcher
     * @param listener
     *            the build listener
     * @param healthDescriptor
     *            health descriptor
     * @param defaultEncoding
     *            the default encoding to be used when reading and parsing files
     */
    public CheckStyleAnnotationsAggregator(final MatrixBuild build, final Launcher launcher,
            final BuildListener listener, final HealthDescriptor healthDescriptor, final String defaultEncoding) {
        super(build, launcher, listener, healthDescriptor, defaultEncoding);
    }

    @Override
    protected Action createAction(final HealthDescriptor healthDescriptor, final String defaultEncoding, final ParserResult aggregatedResult) {
        return new CheckStyleResultAction(build, healthDescriptor, new CheckStyleResult(build, defaultEncoding, aggregatedResult));
    }

    @Override
    protected boolean hasResult(final MatrixRun run) {
        return getAction(run) != null;
    }

    @Override
    protected CheckStyleResult getResult(final MatrixRun run) {
        return getAction(run).getResult();
    }

    private CheckStyleResultAction getAction(final MatrixRun run) {
        return run.getAction(CheckStyleResultAction.class);
    }
}

