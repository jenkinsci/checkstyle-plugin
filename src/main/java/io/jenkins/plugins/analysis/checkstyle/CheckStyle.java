package io.jenkins.plugins.analysis.checkstyle;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.kohsuke.stapler.DataBoundConstructor;

import edu.hm.hafner.analysis.Issue;
import edu.hm.hafner.analysis.IssueBuilder;
import edu.hm.hafner.analysis.Issues;
import io.jenkins.plugins.analysis.core.steps.DefaultLabelProvider;
import io.jenkins.plugins.analysis.core.steps.StaticAnalysisTool;

import hudson.Extension;
import hudson.plugins.checkstyle.CheckStyleDescriptor;
import hudson.plugins.checkstyle.Messages;
import hudson.plugins.checkstyle.rules.CheckStyleRules;

/**
 * Provides a parser and customized messages for CheckStyle.
 *
 * @author Ullrich Hafner
 */
public class CheckStyle extends StaticAnalysisTool {
    /**
     * Creates a new instance of {@link CheckStyle}.
     */
    @DataBoundConstructor
    public CheckStyle() {
        // empty constructor required for stapler
    }

    @Override
    public Issues<Issue> parse(final File file, final IssueBuilder builder) throws InvocationTargetException {
        return new CheckStyleParser().parse(file, builder);
    }

    /** Registers this tool as extension point implementation. */
    @Extension
    public static final class Descriptor extends StaticAnalysisToolDescriptor {
        public Descriptor() {
            super(new CheckStyleLabelProvider());
        }
    }

    static class CheckStyleLabelProvider extends DefaultLabelProvider {
        CheckStyleLabelProvider() {
            super(CheckStyleDescriptor.PLUGIN_ID);
        }

        @Override
        public String getName() {
            return "CheckStyle";
        }

        @Override
        public String getLinkName() {
            return Messages.Checkstyle_ProjectAction_Name();
        }

        @Override
        public String getTrendName() {
            return Messages.Checkstyle_Trend_Name();
        }

        @Override
        public String getDescription(final Issue issue) {
            return CheckStyleRules.getInstance().getDescription(issue.getType());
        }

        @Override
        public String getSmallIconUrl() {
            return get().getIconUrl();
        }

        private CheckStyleDescriptor get() {
            return new CheckStyleDescriptor();
        }

        @Override
        public String getLargeIconUrl() {
            return get().getSummaryIconUrl();
        }
    }
}
