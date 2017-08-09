package io.jenkins.plugins.checkstyle.steps;

import javax.annotation.Nonnull;
import java.util.Set;

import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.jenkinsci.plugins.workflow.steps.SynchronousNonBlockingStepExecution;
import org.kohsuke.stapler.DataBoundConstructor;

import com.google.common.collect.Sets;

import hudson.Extension;
import hudson.model.Run;
import hudson.plugins.analysis.core.ParserResult;

/*
 TODO:

 */
public class AggregateWarningsStep extends Step {
    private ParserResult warnings = new ParserResult();

    @DataBoundConstructor
    public AggregateWarningsStep(final ParserResult... results) {
        for (ParserResult result : results) {
            this.warnings.addProject(result);
        }
    }

    public ParserResult getWarnings() {
        return warnings;
    }

    @Override
    public StepExecution start(final StepContext stepContext) throws Exception {
        return new Execution(stepContext, this);
    }

    public static class Execution extends SynchronousNonBlockingStepExecution<ParserResult> {
        private ParserResult warnings;

        protected Execution(@Nonnull final StepContext context, final AggregateWarningsStep step) {
            super(context);

            warnings = step.warnings;
            if (warnings == null) {
                throw new NullPointerException("No warnings provided.");
            }
        }

        @Override
        protected ParserResult run() throws Exception {
            // TODO: implement filter

            return warnings;
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
            return "mergeWarnings";
        }

        @Override
        public String getDisplayName() {
            return "Merge CheckStyle warnings";
        }
    }
}
