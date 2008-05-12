package hudson.plugins.checkstyle;

import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.Descriptor;
import hudson.plugins.checkstyle.parser.CheckStyleParser;
import hudson.plugins.checkstyle.util.FilesParser;
import hudson.plugins.checkstyle.util.HealthAwarePublisher;
import hudson.plugins.checkstyle.util.HealthReportBuilder;
import hudson.plugins.checkstyle.util.model.JavaProject;
import hudson.tasks.Publisher;

import java.io.IOException;
import java.io.PrintStream;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Publishes the results of the Checkstyle analysis  (freestyle project type).
 *
 * @author Ulli Hafner
 */
public class CheckStylePublisher extends HealthAwarePublisher {
    /** Default Checkstyle pattern. */
    private static final String DEFAULT_PATTERN = "**/checkstyle-result.xml";
    /** Descriptor of this publisher. */
    public static final CheckStyleDescriptor CHECKSTYLE_DESCRIPTOR = new CheckStyleDescriptor();
    /** Ant file-set pattern of files to work with. */
    private final String pattern;

    /**
     * Creates a new instance of <code>CheckstylePublisher</code>.
     *
     * @param pattern
     *            Ant file-set pattern to scan for Checkstyle files
     * @param threshold
     *            Bug threshold to be reached if a build should be considered as
     *            unstable.
     * @param healthy
     *            Report health as 100% when the number of warnings is less than
     *            this value
     * @param unHealthy
     *            Report health as 0% when the number of warnings is greater
     *            than this value
     * @param height
     *            the height of the trend graph
     */
    @DataBoundConstructor
    public CheckStylePublisher(final String pattern, final String threshold, final String healthy, final String unHealthy, final String height) {
        super(threshold, healthy, unHealthy, height, "CHECKSTYLE");
        this.pattern = pattern;
    }

    /**
     * Returns the Ant file-set pattern of files to work with.
     *
     * @return Ant file-set pattern of files to work with
     */
    public String getPattern() {
        return pattern;
    }

    /** {@inheritDoc} */
    @Override
    public Action getProjectAction(final AbstractProject<?, ?> project) {
        return new CheckStyleProjectAction(project, getTrendHeight());
    }

    /** {@inheritDoc} */
    @Override
    public JavaProject perform(final AbstractBuild<?, ?> build, final PrintStream logger) throws InterruptedException, IOException {
        log(logger, "Collecting checkstyle analysis files...");

        FilesParser parser = new FilesParser(logger, StringUtils.defaultIfEmpty(getPattern(), DEFAULT_PATTERN), new CheckStyleParser());
        JavaProject project = build.getProject().getWorkspace().act(parser);
        CheckStyleResult result = new CheckStyleResultBuilder().build(build, project);
        HealthReportBuilder healthReportBuilder = createHealthReporter(
                Messages.Checkstyle_ResultAction_HealthReportSingleItem(),
                Messages.Checkstyle_ResultAction_HealthReportMultipleItem("%d"));
        build.getActions().add(new CheckStyleResultAction(build, healthReportBuilder, result));

        return project;
    }

    /** {@inheritDoc} */
    public Descriptor<Publisher> getDescriptor() {
        return CHECKSTYLE_DESCRIPTOR;
    }
}
