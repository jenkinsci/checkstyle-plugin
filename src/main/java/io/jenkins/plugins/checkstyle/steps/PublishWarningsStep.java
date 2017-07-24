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
import hudson.plugins.checkstyle.CheckStyleResult;
import hudson.plugins.checkstyle.CheckStyleResultAction;

/*
 TODO:

 */
public class PublishWarningsStep extends Step {
    private ParserResult warnings;
    private String defaultEncoding;

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
    public String getDefaultEncoding() {
        return defaultEncoding;
    }

    @DataBoundConstructor
    public PublishWarningsStep(final ParserResult warnings) {
        this.warnings = warnings;
    }

    @Override
    public StepExecution start(final StepContext stepContext) throws Exception {
        return new Execution(stepContext, this);
    }

    public static class Execution extends SynchronousNonBlockingStepExecution<Void> {
        private final ParserResult warnings;
        private final String defaultEncoding;

        protected Execution(@Nonnull final StepContext context, final PublishWarningsStep step) {
            super(context);

            this.warnings = step.warnings;
            this.defaultEncoding = step.defaultEncoding;
        }

        @Override
        protected Void run() throws Exception {
            Run run = getContext().get(Run.class);

            // FIXME: Split previous and reference result from history
            CheckStyleResult result = new CheckStyleResult(run, defaultEncoding, warnings, false,false);
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
