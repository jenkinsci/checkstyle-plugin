package hudson.plugins.checkstyle;

import hudson.maven.MavenBuild;
import hudson.maven.MavenBuildProxy;
import hudson.maven.MavenModule;
import hudson.maven.MavenReporterDescriptor;
import hudson.maven.MojoInfo;
import hudson.model.Action;
import hudson.plugins.checkstyle.parser.PmdCollector;
import hudson.plugins.checkstyle.util.HealthAwareMavenReporter;
import hudson.plugins.checkstyle.util.HealthReportBuilder;
import hudson.plugins.checkstyle.util.model.JavaProject;
import hudson.plugins.pmd.Messages;

import java.io.IOException;
import java.io.PrintStream;

import org.apache.maven.project.MavenProject;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Publishes the results of the PMD analysis  (maven 2 project type).
 *
 * @author Ulli Hafner
 */
public class PmdReporter extends HealthAwareMavenReporter {
    /** Unique identifier of this class. */
    private static final long serialVersionUID = 2272875032054063496L;
    /** Descriptor of this publisher. */
    public static final PmdReporterDescriptor PMD_SCANNER_DESCRIPTOR = new PmdReporterDescriptor(PmdPublisher.PMD_DESCRIPTOR);
    /** Default PMD pattern. */
    private static final String PMD_XML_FILE = "pmd.xml";
    /** Ant file-set pattern of files to work with. */
    @SuppressWarnings("unused")
    private String pattern; // obsolete since release 2.5

    /**
     * Creates a new instance of <code>PmdReporter</code>.
     *
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
    public PmdReporter(final String threshold, final String healthy, final String unHealthy, final String height) {
        super(threshold, healthy, unHealthy, height, "PMD");
    }

    /** {@inheritDoc} */
    @Override
    protected boolean acceptGoal(final String goal) {
        return "pmd".equals(goal) || "site".equals(goal);
    }

    /** {@inheritDoc} */
    @Override
    public JavaProject perform(final MavenBuildProxy build, final MavenProject pom, final MojoInfo mojo, final PrintStream logger) throws InterruptedException, IOException {
        PmdCollector pmdCollector = new PmdCollector(logger, PMD_XML_FILE);

        return getTargetPath(pom).act(pmdCollector);
    }

    /** {@inheritDoc} */
    @Override
    protected void persistResult(final JavaProject project, final MavenBuild build) {
        PmdResult result = new PmdResultBuilder().build(build, project);
        HealthReportBuilder healthReportBuilder = createHealthBuilder(
                Messages.PMD_ResultAction_HealthReportSingleItem(),
                Messages.PMD_ResultAction_HealthReportMultipleItem("%d"));
        build.getActions().add(new MavenPmdResultAction(build, healthReportBuilder, getHeight(), result));
        build.registerAsProjectAction(PmdReporter.this);
    }

    /** {@inheritDoc} */
    @Override
    public Action getProjectAction(final MavenModule module) {
        return new PmdProjectAction(module, getTrendHeight());
    }

    /** {@inheritDoc} */
    @Override
    protected Class<? extends Action> getResultActionClass() {
        return MavenPmdResultAction.class;
    }

    /** {@inheritDoc} */
    @Override
    public MavenReporterDescriptor getDescriptor() {
        return PMD_SCANNER_DESCRIPTOR;
    }
}

