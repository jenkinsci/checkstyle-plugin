package hudson.plugins.checkstyle;

import hudson.maven.AggregatableAction;
import hudson.maven.MavenAggregatedReport;
import hudson.maven.MavenBuild;
import hudson.maven.MavenModule;
import hudson.maven.MavenModuleSet;
import hudson.maven.MavenModuleSetBuild;
import hudson.model.Action;
import hudson.model.AbstractBuild;
import hudson.plugins.analysis.core.BuildResult;
import hudson.plugins.analysis.core.HealthDescriptor;
import hudson.plugins.analysis.core.ParserResult;
import hudson.plugins.analysis.util.PluginLogger;

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
    /** The default encoding to be used when reading and parsing files. */
    private final String defaultEncoding;

    /**
     * Creates a new instance of <code>MavenCheckStyleResultAction</code>.
     *
     * @param owner
     *            the associated build of this action
     * @param healthDescriptor
     *            health descriptor to use
     * @param defaultEncoding
     *            the default encoding to be used when reading and parsing files
     */
    public MavenCheckStyleResultAction(final AbstractBuild<?, ?> owner, final HealthDescriptor healthDescriptor,
            final String defaultEncoding) {
        super(owner, healthDescriptor);
        this.defaultEncoding = defaultEncoding;
    }

    /**
     * Creates a new instance of <code>MavenCheckStyleResultAction</code>.
     *
     * @param owner
     *            the associated build of this action
     * @param healthDescriptor
     *            health descriptor to use
     * @param result
     *            the result in this build
     * @param defaultEncoding
     *            the default encoding to be used when reading and parsing files
     */
    public MavenCheckStyleResultAction(final AbstractBuild<?, ?> owner, final HealthDescriptor healthDescriptor,
            final String defaultEncoding, final CheckStyleResult result) {
        super(owner, healthDescriptor, result);
        this.defaultEncoding = defaultEncoding;
    }

    /** {@inheritDoc} */
    public MavenAggregatedReport createAggregatedAction(final MavenModuleSetBuild build, final Map<MavenModule, List<MavenBuild>> moduleBuilds) {
        return new MavenCheckStyleResultAction(build, getHealthDescriptor(), defaultEncoding);
    }

    /** {@inheritDoc} */
    public Action getProjectAction(final MavenModuleSet moduleSet) {
        return new CheckStyleProjectAction(moduleSet);
    }

    /** {@inheritDoc} */
    public Class<? extends AggregatableAction> getIndividualActionType() {
        return getClass();
    }

    /**
     * Called whenever a new module build is completed, to update the aggregated
     * report. When multiple builds complete simultaneously, Jenkins serializes
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
        MavenCheckStyleResultAction additionalAction = newBuild.getAction(MavenCheckStyleResultAction.class);
        if (additionalAction != null) {
            CheckStyleResult existingResult = getResult();
            CheckStyleResult additionalResult = additionalAction.getResult();

            log("Aggregating results of " + newBuild.getProject().getDisplayName());

            if (existingResult == null) {
                setResult(additionalResult);
                getOwner().setResult(additionalResult.getPluginResult());
            }
            else {
                setResult(aggregate(existingResult, additionalResult, getLogger()));
            }
        }
    }

    /**
     * Creates a new instance of {@link BuildResult} that contains the aggregated
     * results of this result and the provided additional result.
     *
     * @param existingResult
     *            the existing result
     * @param additionalResult
     *            the result that will be added to the existing result
     * @param logger
     *            the plug-in logger
     * @return the aggregated result
     */
    public CheckStyleResult aggregate(final CheckStyleResult existingResult, final CheckStyleResult additionalResult, final PluginLogger logger) {
        ParserResult aggregatedAnnotations = new ParserResult();
        aggregatedAnnotations.addAnnotations(existingResult.getAnnotations());
        aggregatedAnnotations.addAnnotations(additionalResult.getAnnotations());

        CheckStyleResult createdResult = new CheckStyleResult(getOwner(), existingResult.getDefaultEncoding(), aggregatedAnnotations);
        createdResult.evaluateStatus(existingResult.getThresholds(), existingResult.canUseDeltaValues(), logger);
        return createdResult;
    }

    /** Backward compatibility. @deprecated */
    @SuppressWarnings("unused")
    @edu.umd.cs.findbugs.annotations.SuppressWarnings("")
    @Deprecated
    private transient String height;
}