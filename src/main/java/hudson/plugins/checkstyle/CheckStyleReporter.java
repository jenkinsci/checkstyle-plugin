package hudson.plugins.checkstyle;

import hudson.maven.MavenBuild;
import hudson.maven.MavenBuildProxy;
import hudson.maven.MavenModule;
import hudson.maven.MavenReporterDescriptor;
import hudson.maven.MojoInfo;
import hudson.model.Action;
import hudson.plugins.checkstyle.parser.CheckStyleParser;
import hudson.plugins.checkstyle.util.FilesParser;
import hudson.plugins.checkstyle.util.HealthAwareMavenReporter;
import hudson.plugins.checkstyle.util.ParserResult;
import hudson.plugins.checkstyle.util.model.Priority;

import java.io.IOException;
import java.io.PrintStream;

import org.apache.maven.project.MavenProject;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Publishes the results of the Checkstyle analysis (maven 2 project type).
 *
 * @author Ulli Hafner
 */
public class CheckStyleReporter extends HealthAwareMavenReporter {
    /** Unique identifier of this class. */
    private static final long serialVersionUID = 2272875032054063496L;
    /** Descriptor of this publisher. */
    public static final CheckStyleReporterDescriptor CHECKSTYLE_SCANNER_DESCRIPTOR = new CheckStyleReporterDescriptor(CheckStylePublisher.CHECKSTYLE_DESCRIPTOR);
    /** Default Checkstyle pattern. */
    private static final String CHECKSTYLE_XML_FILE = "checkstyle-result.xml";

    /**
     * Creates a new instance of <code>CheckStyleReporter</code>.
     *
     * @param threshold
     *            Annotation threshold to be reached if a build should be considered as
     *            unstable.
     * @param newThreshold
     *            New annotations threshold to be reached if a build should be
     *            considered as unstable.
     * @param failureThreshold
     *            Annotation threshold to be reached if a build should be considered as
     *            failure.
     * @param newFailureThreshold
     *            New annotations threshold to be reached if a build should be
     *            considered as failure.
     * @param healthy
     *            Report health as 100% when the number of warnings is less than
     *            this value
     * @param unHealthy
     *            Report health as 0% when the number of warnings is greater
     *            than this value
     * @param height
     *            the height of the trend graph
     * @param minimumPriority
     *            determines which warning priorities should be considered when
     *            evaluating the build stability and health
     */
    @DataBoundConstructor
    public CheckStyleReporter(final String threshold, final String newThreshold,
            final String failureThreshold, final String newFailureThreshold,
            final String healthy, final String unHealthy,
            final String height, final Priority minimumPriority) {
        super(threshold, newThreshold, failureThreshold, newFailureThreshold,
                healthy, unHealthy, height, minimumPriority, "CHECKSTYLE");
    }

    /** {@inheritDoc} */
    @Override
    protected boolean acceptGoal(final String goal) {
        return "checkstyle".equals(goal) || "site".equals(goal);
    }

    /** {@inheritDoc} */
    @Override
    public ParserResult perform(final MavenBuildProxy build, final MavenProject pom, final MojoInfo mojo, final PrintStream logger) throws InterruptedException, IOException {
        FilesParser checkstyleCollector = new FilesParser(logger, CHECKSTYLE_XML_FILE, new CheckStyleParser(getDefaultEncoding()), true, false);

        return getTargetPath(pom).act(checkstyleCollector);
    }

    /** {@inheritDoc} */
    @Override
    protected CheckStyleResult persistResult(final ParserResult project, final MavenBuild build) {
        CheckStyleResult result = new CheckStyleResultBuilder().build(build, project, getDefaultEncoding());
        build.getActions().add(new MavenCheckStyleResultAction(build, this, getHeight(), getDefaultEncoding(), result));
        build.registerAsProjectAction(CheckStyleReporter.this);

        return result;
    }

    /** {@inheritDoc} */
    @Override
    public Action getProjectAction(final MavenModule module) {
        return new CheckStyleProjectAction(module, getTrendHeight());
    }

    /** {@inheritDoc} */
    @Override
    protected Class<? extends Action> getResultActionClass() {
        return MavenCheckStyleResultAction.class;
    }

    /** {@inheritDoc} */
    @Override
    public MavenReporterDescriptor getDescriptor() {
        return CHECKSTYLE_SCANNER_DESCRIPTOR;
    }
}

