package io.jenkins.plugins.checkstyle.steps;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.util.Set;

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
import hudson.plugins.analysis.core.NullHealthDescriptor;
import hudson.plugins.analysis.core.ParserResult;
import hudson.plugins.analysis.core.ReferenceFinder;
import hudson.plugins.analysis.core.ReferenceProvider;
import hudson.plugins.checkstyle.CheckStyleResult;
import hudson.plugins.checkstyle.CheckStyleResultAction;

/*
 TODO:

 */
public class PublishWarningsStep extends Step {
    private ParserResult warnings;
    private String defaultEncoding;
    private boolean usePreviousBuildAsReference;
    private boolean useStableBuildAsReference;

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

    @Override
    public StepExecution start(final StepContext stepContext) throws Exception {
        return new Execution(stepContext, this);
    }

    public static class Execution extends SynchronousNonBlockingStepExecution<Void> {
        private boolean useStableBuildAsReference;
        private boolean usePreviousBuildAsReference;
        private String defaultEncoding;
        private ParserResult warnings;

        protected Execution(@Nonnull final StepContext context, final PublishWarningsStep step) {
            super(context);

            usePreviousBuildAsReference = step.usePreviousBuildAsReference;
            useStableBuildAsReference = step.useStableBuildAsReference;
            defaultEncoding = step.defaultEncoding;

            warnings = step.warnings;
            if (warnings == null) {
                throw new NullPointerException("No warnings provided.");
            }
        }

        @Override
        protected Void run() throws Exception {
            Run run = getContext().get(Run.class);

            ReferenceProvider referenceProvider = ReferenceFinder.create(run, CheckStyleResultAction.class,
                    usePreviousBuildAsReference, useStableBuildAsReference);
            // TODO: allow other reference provider implementations, how to parmeterize these instances?
            CheckStyleResult result = new CheckStyleResult(run, defaultEncoding, warnings, referenceProvider);
            // FIXME: split out health descriptor
            // FIXME: remove thresholds from health descriptor
            run.addAction(new CheckStyleResultAction(run, new NullHealthDescriptor(), result));

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
