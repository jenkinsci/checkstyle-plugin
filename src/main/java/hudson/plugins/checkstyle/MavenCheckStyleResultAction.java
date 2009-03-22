package hudson.plugins.checkstyle;

import hudson.maven.AggregatableAction;
import hudson.maven.MavenAggregatedReport;
import hudson.maven.MavenBuild;
import hudson.maven.MavenModule;
import hudson.maven.MavenModuleSet;
import hudson.maven.MavenModuleSetBuild;
import hudson.model.AbstractBuild;
import hudson.model.Action;
import hudson.plugins.checkstyle.util.HealthDescriptor;
import hudson.plugins.checkstyle.util.TrendReportHeightValidator;

import java.util.List;
import java.util.Map;

/**
 * A {@link CheckStyleResultAction} for native maven jobs. This action
 * additionally provides result aggregation for sub-modules and for the main
 * project.
 *
 * @author Ulli Hafner
 */
public class MavenCheckStyleResultAction extends CheckStyleResultAction implements AggregatableAction, MavenAggregatedReport {
    /** Unique identifier of this class. */
    private static final long serialVersionUID = 1273798369273225973L;
    /** Determines the height of the trend graph. */
    private final String height;
    /** The default encoding to be used when reading and parsing files. */
    private final String defaultEncoding;

    /**
     * Creates a new instance of <code>MavenCheckStyleResultAction</code>.
     *
     * @param owner
     *            the associated build of this action
     * @param healthDescriptor
     *            health descriptor to use
     * @param height
     *            the height of the trend graph
     * @param defaultEncoding
     *            the default encoding to be used when reading and parsing files
     */
    public MavenCheckStyleResultAction(final AbstractBuild<?, ?> owner, final HealthDescriptor healthDescriptor,
            final String height, final String defaultEncoding) {
        super(owner, healthDescriptor);
        this.height = height;
        this.defaultEncoding = defaultEncoding;
    }

    /**
     * Creates a new instance of <code>MavenCheckStyleResultAction</code>.
     *
     * @param owner
     *            the associated build of this action
     * @param healthDescriptor
     *            health descriptor to use
     * @param height
     *            the height of the trend graph
     * @param result
     *            the result in this build
     * @param defaultEncoding
     *            the default encoding to be used when reading and parsing files
     */
    public MavenCheckStyleResultAction(final AbstractBuild<?, ?> owner, final HealthDescriptor healthDescriptor,
            final String height, final String defaultEncoding, final CheckStyleResult result) {
        super(owner, healthDescriptor, result);
        this.height = height;
        this.defaultEncoding = defaultEncoding;
    }

    /** {@inheritDoc} */
    public MavenAggregatedReport createAggregatedAction(final MavenModuleSetBuild build, final Map<MavenModule, List<MavenBuild>> moduleBuilds) {
        return new MavenCheckStyleResultAction(build, getHealthDescriptor(), height, defaultEncoding);
    }

    /** {@inheritDoc} */
    public Action getProjectAction(final MavenModuleSet moduleSet) {
        return new CheckStyleProjectAction(moduleSet, TrendReportHeightValidator.defaultHeight(height));
    }

    /** {@inheritDoc} */
    public Class<? extends AggregatableAction> getIndividualActionType() {
        return getClass();
    }

    /**
     * Called whenever a new module build is completed, to update the aggregated
     * report. When multiple builds complete simultaneously, Hudson serializes
     * the execution of this method, so this method needs not be
     * concurrency-safe.
     *
     * @param moduleBuilds
     *            Same as <tt>MavenModuleSet.getModuleBuilds()</tt> but provided
     *            for convenience and efficiency.
     * @param newBuild
     *            Newly completed build.
     */
    public void update(final Map<MavenModule, List<MavenBuild>> moduleBuilds, final MavenBuild newBuild) {
        CheckStyleResult annotationsResult = new CheckStyleResultBuilder().buildMaven(getOwner(), createAggregatedResult(moduleBuilds), defaultEncoding);
        setResult(annotationsResult);
        updateBuildHealth(newBuild, annotationsResult);
    }
}