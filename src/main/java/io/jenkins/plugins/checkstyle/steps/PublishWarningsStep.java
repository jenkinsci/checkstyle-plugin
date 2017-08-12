package io.jenkins.plugins.checkstyle.steps;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.jenkinsci.plugins.workflow.steps.SynchronousNonBlockingStepExecution;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import com.google.common.collect.Sets;

import hudson.Extension;
import hudson.model.Run;
import hudson.plugins.analysis.core.BuildHistory;
import hudson.plugins.analysis.core.DefaultResultSelector;
import hudson.plugins.analysis.core.ParserResult;
import hudson.plugins.analysis.core.ReferenceFinder;
import hudson.plugins.analysis.core.ReferenceProvider;
import hudson.plugins.analysis.util.model.Priority;
import hudson.plugins.checkstyle.CheckStyleHealthDescriptor;
import hudson.plugins.checkstyle.CheckStyleResult;
import hudson.plugins.checkstyle.CheckStyleResultAction;

/*
 TODO:

 */
public class PublishWarningsStep extends Step {
    private static final String DEFAULT_MINIMUM_PRIORITY = "low";

    private ParserResult warnings;

    private boolean usePreviousBuildAsReference;
    private boolean useStableBuildAsReference;

    private String defaultEncoding;

    /** Report health as 100% when the number of warnings is less than this value. */
    private String healthy;
    /** Report health as 0% when the number of warnings is greater than this value. */
    private String unHealthy;
    /** Determines which warning priorities should be considered when evaluating the build health. */
    private String minimumPriority = DEFAULT_MINIMUM_PRIORITY;

    @DataBoundConstructor
    public PublishWarningsStep(final ParserResult warnings) {
        this.warnings = warnings;
    }

    public ParserResult getWarnings() {
        return warnings;
    }

    public boolean getUsePreviousBuildAsReference() {
        return usePreviousBuildAsReference;
    }

    /**
     * Determines if the previous build should always be used as the reference build, no matter its overall result.
     *
     * @param usePreviousBuildAsReference if {@code true} then the previous build is always used
     */
    @DataBoundSetter
    public void setUsePreviousBuildAsReference(final boolean usePreviousBuildAsReference) {
        this.usePreviousBuildAsReference = usePreviousBuildAsReference;
    }

    public boolean getUseStableBuildAsReference() {
        return useStableBuildAsReference;
    }

    /**
     * Determines whether only stable builds should be used as reference builds or not.
     *
     * @param useStableBuildAsReference if {@code true} then a stable build is used as reference
     */
    @DataBoundSetter
    public void setUseStableBuildAsReference(final boolean useStableBuildAsReference) {
        this.useStableBuildAsReference = useStableBuildAsReference;
    }

    @CheckForNull
    public String getDefaultEncoding() {
        return defaultEncoding;
    }

    /**
     * Sets the default encoding used to read files (warnings, source code, etc.).
     *
     * @param defaultEncoding the encoding, e.g. "ISO-8859-1"
     */
    @DataBoundSetter
    public void setDefaultEncoding(final String defaultEncoding) {
        this.defaultEncoding = defaultEncoding;
    }

    @CheckForNull
    public String getHealthy() {
        return healthy;
    }

    /**
     * Sets the healthy threshold, i.e. the number of issues when health is reported as 100%.
     *
     * @param healthy the number of issues when health is reported as 100%
     */
    @DataBoundSetter
    public void setHealthy(final String healthy) {
        this.healthy = healthy;
    }

    @CheckForNull
    public String getUnHealthy() {
        return unHealthy;
    }

    /**
     * Sets the healthy threshold, i.e. the number of issues when health is reported as 0%.
     *
     * @param unHealthy the number of issues when health is reported as 0%
     */
    @DataBoundSetter
    public void setUnHealthy(final String unHealthy) {
        this.unHealthy = unHealthy;
    }

    @CheckForNull
    public String getMinimumPriority() {
        return minimumPriority;
    }

    /**
     * Sets the minimum priority to consider when computing the health report. Issues with a priority less than this
     * value will be ignored.
     *
     * @param minimumPriority the priority to consider
     */
    @DataBoundSetter
    public void setMinimumPriority(final String minimumPriority) {
        this.minimumPriority = StringUtils.defaultIfEmpty(minimumPriority, DEFAULT_MINIMUM_PRIORITY);
    }

    @Override
    public StepExecution start(final StepContext stepContext) throws Exception {
        return new Execution(stepContext, this);
    }

    public static class Execution extends SynchronousNonBlockingStepExecution<Void> {
        private final String healthy;
        private final String unHealthy;
        private final String minimumPriority;
        private boolean useStableBuildAsReference;
        private boolean usePreviousBuildAsReference;
        private String defaultEncoding;
        private ParserResult warnings;

        protected Execution(@Nonnull final StepContext context, final PublishWarningsStep step) {
            super(context);

            usePreviousBuildAsReference = step.usePreviousBuildAsReference;
            useStableBuildAsReference = step.useStableBuildAsReference;
            defaultEncoding = step.defaultEncoding;
            healthy = step.healthy;
            unHealthy = step.unHealthy;
            minimumPriority = step.minimumPriority;

            warnings = step.warnings;
            if (warnings == null) {
                throw new NullPointerException("No warnings provided.");
            }
        }

        private Priority getMinimumPriority() {
            return Priority.valueOf(StringUtils.upperCase(minimumPriority));
        }

        @Override
        protected Void run() throws Exception {
            Run run = getContext().get(Run.class);

            DefaultResultSelector selector = new DefaultResultSelector(CheckStyleResultAction.class);
            ReferenceProvider referenceProvider = ReferenceFinder.create(run,
                    selector, usePreviousBuildAsReference, useStableBuildAsReference);
            BuildHistory buildHistory = new BuildHistory(run, new DefaultResultSelector(CheckStyleResultAction.class));
            // TODO: allow other reference provider implementations, how to parametrize these instances?
            CheckStyleResult result = new CheckStyleResult(run, defaultEncoding, warnings, referenceProvider, buildHistory);
            // TODO: why is the health descriptor persisted and not the health? (Is this due to localization?)
            CheckStyleHealthDescriptor healthDescriptor = new CheckStyleHealthDescriptor(healthy, unHealthy, getMinimumPriority());
            run.addAction(new CheckStyleResultAction(run, healthDescriptor, result));

            return null;
        }
    }

    @Extension
    public static class Descriptor extends StepDescriptor {
        @Override
        public Set<? extends Class<?>> getRequiredContext() {
            return Sets.newHashSet(Run.class);
        }

        @Override
        public String getFunctionName() {
            return "publishWarnings";
        }

        @Override
        public String getDisplayName() {
            return "Publish CheckStyle warnings";
        }
    }
}
