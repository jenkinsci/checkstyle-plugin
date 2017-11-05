package io.jenkins.plugins.analysis.checkstyle;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.kohsuke.stapler.DataBoundConstructor;

import edu.hm.hafner.analysis.Issue;
import edu.hm.hafner.analysis.Issues;
import io.jenkins.plugins.analysis.core.steps.DefaultLabelProvider;
import io.jenkins.plugins.analysis.core.steps.StaticAnalysisTool;

import hudson.Extension;
import hudson.plugins.checkstyle.CheckStyleDescriptor;
import hudson.plugins.checkstyle.Messages;
import hudson.plugins.checkstyle.parser.CheckStyleParser;

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
    public Issues<Issue> parse(final File file, final String moduleName) throws InvocationTargetException {
        Issues<Issue> issues = new CheckStyleParser().parseIssues(file, moduleName);

        return withOrigin(issues, CheckStyleDescriptor.PLUGIN_ID);
    }

    /** Registers this tool as extension point implementation. */
    @Extension
    public static final class Descriptor extends StaticAnalysisToolDescriptor {
        public Descriptor() {
            super(new CheckStyleLabelProvider());
        }
    }

    private static class CheckStyleLabelProvider extends DefaultLabelProvider {
        private CheckStyleLabelProvider() {
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
