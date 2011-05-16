package hudson.plugins.checkstyle;

import hudson.maven.MavenAggregatedReport;
import hudson.maven.MavenBuild;
import hudson.maven.MavenModule;
import hudson.maven.MavenModuleSet;
import hudson.maven.MavenModuleSetBuild;
import hudson.model.Action;
import hudson.plugins.analysis.core.HealthDescriptor;
import hudson.plugins.analysis.core.MavenResultAction;

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
     * Creates a new instance of {@link CheckStyleMavenResultAction}. This instance
     * will have no result set in the beginning. The result will be set
     * successively after each of the modules are build.
     *
     * @param owner
     *            the associated build of this action
     * @param healthDescriptor
     *            health descriptor to use
     * @param defaultEncoding
     *            the default encoding to be used when reading and parsing files
     */
    public CheckStyleMavenResultAction(final MavenModuleSetBuild owner, final HealthDescriptor healthDescriptor,
            final String defaultEncoding) {
        super(new CheckStyleResultAction(owner, healthDescriptor), defaultEncoding, "CHECKSTYLE");
    }

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
    public CheckStyleMavenResultAction(final MavenBuild owner, final HealthDescriptor healthDescriptor,
            final String defaultEncoding, final CheckStyleResult result) {
        super(new CheckStyleResultAction(owner, healthDescriptor, result), defaultEncoding, "CHECKSTYLE");
    }

    /** {@inheritDoc} */
    public MavenAggregatedReport createAggregatedAction(final MavenModuleSetBuild build, final Map<MavenModule, List<MavenBuild>> moduleBuilds) {
        return new CheckStyleMavenResultAction(build, getHealthDescriptor(), getDisplayName());
    }

    /** {@inheritDoc} */
    public Action getProjectAction(final MavenModuleSet moduleSet) {
        return new CheckStyleProjectAction(moduleSet);
    }

    @Override
    public Class<? extends MavenResultAction<CheckStyleResult>> getIndividualActionType() {
        return CheckStyleMavenResultAction.class;
    }

    @Override
    protected CheckStyleResult createResult(final CheckStyleResult... results) {
        return new CheckStyleResult(getOwner(), results[0].getDefaultEncoding(), aggregate(results));
    }
}

