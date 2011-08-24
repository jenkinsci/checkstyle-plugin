package hudson.plugins.checkstyle;

import hudson.maven.MavenAggregatedReport;
import hudson.maven.MavenBuildProxy;
import hudson.maven.MojoInfo;
import hudson.maven.MavenBuild;
import hudson.maven.MavenModule;
import hudson.plugins.analysis.core.FilesParser;
import hudson.plugins.analysis.core.HealthAwareReporter;
import hudson.plugins.analysis.core.ParserResult;
import hudson.plugins.analysis.util.PluginLogger;
import hudson.plugins.analysis.util.StringPluginLogger;
import hudson.plugins.checkstyle.parser.CheckStyleParser;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.maven.project.MavenProject;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Publishes the results of the Checkstyle analysis (maven 2 project type).
 *
 * @author Ulli Hafner
 */
public class CheckStyleReporter extends HealthAwareReporter<CheckStyleResult> {
    /** Unique identifier of this class. */
    private static final long serialVersionUID = 2272875032054063496L;

    private static final String PLUGIN_NAME = "CHECKSTYLE";

    /** Default Checkstyle pattern. */
    private static final String CHECKSTYLE_XML_FILE = "checkstyle-result.xml";
    
    /** Do we extract the path if the package cannot be found. */
    private boolean shouldShowPath;

    /**
     * Creates a new instance of <code>CheckStyleReporter</code>.
     *
     * @param healthy
     *            Report health as 100% when the number of warnings is less than
     *            this value
     * @param unHealthy
     *            Report health as 0% when the number of warnings is greater
     *            than this value
     * @param thresholdLimit
     *            determines which warning priorities should be considered when
     *            evaluating the build stability and health
     * @param useDeltaValues
     *            determines whether the absolute annotations delta or the
     *            actual annotations set difference should be used to evaluate
     *            the build stability
     * @param unstableTotalAll
     *            annotation threshold
     * @param unstableTotalHigh
     *            annotation threshold
     * @param unstableTotalNormal
     *            annotation threshold
     * @param unstableTotalLow
     *            annotation threshold
     * @param unstableNewAll
     *            annotation threshold
     * @param unstableNewHigh
     *            annotation threshold
     * @param unstableNewNormal
     *            annotation threshold
     * @param unstableNewLow
     *            annotation threshold
     * @param failedTotalAll
     *            annotation threshold
     * @param failedTotalHigh
     *            annotation threshold
     * @param failedTotalNormal
     *            annotation threshold
     * @param failedTotalLow
     *            annotation threshold
     * @param failedNewAll
     *            annotation threshold
     * @param failedNewHigh
     *            annotation threshold
     * @param failedNewNormal
     *            annotation threshold
     * @param failedNewLow
     *            annotation threshold
     * @param canRunOnFailed
     *            determines whether the plug-in can run for failed builds, too
     * @param shouldShowPath
     *            determines whether path have to be displayed in the GUI
     */
    // CHECKSTYLE:OFF
    @SuppressWarnings("PMD.ExcessiveParameterList")
    @DataBoundConstructor
    public CheckStyleReporter(final String healthy, final String unHealthy, final String thresholdLimit, final boolean useDeltaValues,
            final String unstableTotalAll, final String unstableTotalHigh, final String unstableTotalNormal, final String unstableTotalLow,
            final String unstableNewAll, final String unstableNewHigh, final String unstableNewNormal, final String unstableNewLow,
            final String failedTotalAll, final String failedTotalHigh, final String failedTotalNormal, final String failedTotalLow,
            final String failedNewAll, final String failedNewHigh, final String failedNewNormal, final String failedNewLow,
            final boolean canRunOnFailed, final boolean shouldShowPath) {
        super(healthy, unHealthy, thresholdLimit, useDeltaValues,
                unstableTotalAll, unstableTotalHigh, unstableTotalNormal, unstableTotalLow,
                unstableNewAll, unstableNewHigh, unstableNewNormal, unstableNewLow,
                failedTotalAll, failedTotalHigh, failedTotalNormal, failedTotalLow,
                failedNewAll, failedNewHigh, failedNewNormal, failedNewLow,
                canRunOnFailed, PLUGIN_NAME);
        this.shouldShowPath = shouldShowPath;
    }
    // CHECKSTYLE:ON

    @Override
    protected boolean acceptGoal(final String goal) {
        return "checkstyle".equals(goal) || "check".equals(goal) || "site".equals(goal);
    }

    @Override
    public ParserResult perform(final MavenBuildProxy build, final MavenProject pom,
            final MojoInfo mojo, final PluginLogger logger) throws InterruptedException, IOException {
        FilesParser checkstyleCollector = new FilesParser(new StringPluginLogger(PLUGIN_NAME),
                CHECKSTYLE_XML_FILE, new CheckStyleParser(getDefaultEncoding(), shouldShowPath(), build.getProjectRootDir().getName())
                , getModuleName(pom));

        return getTargetPath(pom).act(checkstyleCollector);
    }
    
    /**
     * Returns whether path have to be displayed in the GUI.
     *
     * @return whether path have to be displayed in the GUI
     */
    public boolean getShouldShowPath() {
        return shouldShowPath;
    }
    
    /**
     * Returns whether path have to be displayed in the GUI.
     *
     * @return whether path have to be displayed in the GUI
     */
    public boolean shouldShowPath() {
        return shouldShowPath;
    }


    @Override
    protected CheckStyleResult createResult(final MavenBuild build, final ParserResult project) {
        return new CheckStyleReporterResult(build, getDefaultEncoding(), project);
    }

    @Override
    protected MavenAggregatedReport createMavenAggregatedReport(final MavenBuild build, final CheckStyleResult result) {
        return new CheckStyleMavenResultAction(build, this, getDefaultEncoding(), result);
    }

    @Override
    public List<CheckStyleProjectAction> getProjectActions(final MavenModule module) {
        return Collections.singletonList(new CheckStyleProjectAction(module, getResultActionClass()));
    }

    @Override
    protected Class<CheckStyleMavenResultAction> getResultActionClass() {
        return CheckStyleMavenResultAction.class;
    }
}

