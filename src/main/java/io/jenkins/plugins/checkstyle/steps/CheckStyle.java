package io.jenkins.plugins.checkstyle.steps;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import org.kohsuke.stapler.DataBoundConstructor;

import io.jenkins.plugins.analysis.core.steps.IssueParser;

import hudson.Extension;
import hudson.plugins.analysis.util.model.FileAnnotation;
import hudson.plugins.checkstyle.CheckStyleDescriptor;
import hudson.plugins.checkstyle.CheckStylePublisher;
import hudson.plugins.checkstyle.Messages;
import hudson.plugins.checkstyle.parser.CheckStyleParser;

/**
 * FIXME: write comment.
 *
 * @author Ullrich Hafner
 */
@Extension
public class CheckStyle extends IssueParser {

    @DataBoundConstructor
    public CheckStyle() {
        super(CheckStyleDescriptor.PLUGIN_ID, CheckStylePublisher.DEFAULT_PATTERN);
    }

    @Override
    public Collection<FileAnnotation> parse(final File file, final String moduleName) throws InvocationTargetException {
        return new CheckStyleParser().parse(file, moduleName);
    }

    @Override
    protected String getName() {
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

    @Extension
    public static final IssueParserDescriptor D = new IssueParserDescriptor(CheckStyle.class);
}
