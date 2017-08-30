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
import hudson.plugins.checkstyle.parser.CheckStyleParser;

/**
 * FIXME: write comment.
 *
 * @author Ullrich Hafner
 */
@Extension
public class CheckStyle extends IssueParser {
    private static final String DEFAULT_PATTERN = "**/checkstyle-result.xml";

    @DataBoundConstructor
    public CheckStyle() {
        super(CheckStyleDescriptor.PLUGIN_ID, CheckStylePublisher.DEFAULT_PATTERN);
    }

    @Override
    public Collection<FileAnnotation> parse(final File file, final String moduleName) throws InvocationTargetException {
        return new CheckStyleParser().parse(file, moduleName);
    }

    @Extension
    public static final IssueParserDescriptor D = new IssueParserDescriptor(CheckStyle.class);
}
