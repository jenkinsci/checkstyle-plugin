package hudson.plugins.checkstyle;

import hudson.maven.MavenAggregatedReport;
import hudson.maven.MavenBuild;
import hudson.maven.MavenModule;
import hudson.maven.MavenModuleSet;
import hudson.maven.MavenModuleSetBuild;
import hudson.model.Action;
import hudson.model.AbstractBuild;
import hudson.plugins.analysis.core.HealthDescriptor;
import hudson.plugins.analysis.core.MavenResultAction;
import hudson.plugins.analysis.core.ParserResult;

import java.util.List;
import java.util.Map;

/**
 * A {@link CheckStyleResultAction} for native Maven jobs. This action
 * additionally provides result aggregation for sub-modules and for the main
 * project.
 *
 * @author Ulli Hafner
 */
public class CheckStyleMavenResultAction extends MavenResultAction<CheckStyleResult> {
    /**
     * Creates a new instance of {@link CheckStyleMavenResultAction}.
     *
     * @param owner
     *            the associated build of this action
     * @param healthDescriptor
     *            health descriptor to use
     * @param defaultEncoding
     *            the default encoding to be used when reading and parsing files
     * @param result
     *            the result in this build
     */
    public CheckStyleMavenResultAction(final AbstractBuild<?, ?> owner, final HealthDescriptor healthDescriptor,
            final String defaultEncoding, final CheckStyleResult result) {
        super(new CheckStyleResultAction(owner, healthDescriptor, result), defaultEncoding, "CHECKSTYLE");
    }

    /** {@inheritDoc} */
    public MavenAggregatedReport createAggregatedAction(final MavenModuleSetBuild build, final Map<MavenModule, List<MavenBuild>> moduleBuilds) {
        return new CheckStyleMavenResultAction(build, getHealthDescriptor(), getDisplayName(),
                new CheckStyleResult(build, getDefaultEncoding(), new ParserResult(), false));
    }

    /** {@inheritDoc} */
    public Action getProjectAction(final MavenModuleSet moduleSet) {
        return new CheckStyleProjectAction(moduleSet, CheckStyleMavenResultAction.class);
    }

    @Override
    public Class<? extends MavenResultAction<CheckStyleResult>> getIndividualActionType() {
        return CheckStyleMavenResultAction.class;
    }

    @Override
    protected CheckStyleResult createResult(final CheckStyleResult existingResult, final CheckStyleResult additionalResult) {
        return new CheckStyleReporterResult(getOwner(), additionalResult.getDefaultEncoding(),
                aggregate(existingResult, additionalResult), existingResult.useOnlyStableBuildsAsReference());
    }
}

