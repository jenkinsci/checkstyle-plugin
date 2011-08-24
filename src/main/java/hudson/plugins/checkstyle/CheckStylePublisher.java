package hudson.plugins.checkstyle;

import hudson.Launcher;
import hudson.matrix.MatrixAggregator;
import hudson.matrix.MatrixBuild;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.plugins.analysis.core.BuildResult;
import hudson.plugins.analysis.core.FilesParser;
import hudson.plugins.analysis.core.HealthAwarePublisher;
import hudson.plugins.analysis.core.ParserResult;
import hudson.plugins.analysis.util.PluginLogger;
import hudson.plugins.analysis.util.StringPluginLogger;
import hudson.plugins.checkstyle.parser.CheckStyleParser;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Publishes the results of the Checkstyle analysis  (freestyle project type).
 *
 * @author Ulli Hafner
 */
public class CheckStylePublisher extends HealthAwarePublisher {
    /** Unique ID of this class. */
    private static final long serialVersionUID = 6369581633551160418L;

    private static final String PLUGIN_NAME = "CHECKSTYLE";

    /** Default Checkstyle pattern. */
    private static final String DEFAULT_PATTERN = "**/checkstyle-result.xml";
    /** Ant file-set pattern of files to work with. */
    private final String pattern;
    /** Determine whether path have to be displayed in the GUI. */ 
    private final boolean shouldShowPath;

    /**
     * Creates a new instance of <code>CheckstylePublisher</code>.
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
     * @param defaultEncoding
     *            the default encoding to be used when reading and parsing files
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
     * @param shouldDetectModules
     *            determines whether module names should be derived from Maven POM or Ant build files
     * @param pattern
     *            Ant file-set pattern to scan for Checkstyle files
     * @param shouldShowPath
     *            determines whether path have to be displayed in the GUI
     */
    // CHECKSTYLE:OFF
    @SuppressWarnings("PMD.ExcessiveParameterList")
    @DataBoundConstructor
    public CheckStylePublisher(final String healthy, final String unHealthy, final String thresholdLimit,
            final String defaultEncoding, final boolean useDeltaValues,
            final String unstableTotalAll, final String unstableTotalHigh, final String unstableTotalNormal, final String unstableTotalLow,
            final String unstableNewAll, final String unstableNewHigh, final String unstableNewNormal, final String unstableNewLow,
            final String failedTotalAll, final String failedTotalHigh, final String failedTotalNormal, final String failedTotalLow,
            final String failedNewAll, final String failedNewHigh, final String failedNewNormal, final String failedNewLow,
            final boolean canRunOnFailed, final boolean shouldDetectModules,
            final String pattern, final boolean shouldShowPath) {
        super(healthy, unHealthy, thresholdLimit, defaultEncoding, useDeltaValues,
                unstableTotalAll, unstableTotalHigh, unstableTotalNormal, unstableTotalLow,
                unstableNewAll, unstableNewHigh, unstableNewNormal, unstableNewLow,
                failedTotalAll, failedTotalHigh, failedTotalNormal, failedTotalLow,
                failedNewAll, failedNewHigh, failedNewNormal, failedNewLow,
                canRunOnFailed, shouldDetectModules, PLUGIN_NAME);
        this.pattern = pattern;
        this.shouldShowPath = shouldShowPath;
    }
    // CHECKSTYLE:ON

    /**
     * Returns the Ant file-set pattern of files to work with.
     *
     * @return Ant file-set pattern of files to work with
     */
    public String getPattern() {
        return pattern;
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

    /** {@inheritDoc} */
    @Override
    public Action getProjectAction(final AbstractProject<?, ?> project) {
        return new CheckStyleProjectAction(project);
    }

    /** {@inheritDoc} */
    @Override
    public BuildResult perform(final AbstractBuild<?, ?> build, final PluginLogger logger) throws InterruptedException, IOException {
        logger.log("Collecting checkstyle analysis files...");

        FilesParser parser = new FilesParser(new StringPluginLogger(PLUGIN_NAME),
                StringUtils.defaultIfEmpty(getPattern(), DEFAULT_PATTERN),
                new CheckStyleParser(getDefaultEncoding(), shouldShowPath(), build.getProject().getRootDir().getPath() + File.separator + build.getWorkspace().getName()),
                shouldDetectModules(), isMavenBuild(build));
        ParserResult project = build.getWorkspace().act(parser);
        logger.logLines(project.getLogMessages());

        CheckStyleResult result = new CheckStyleResult(build, getDefaultEncoding(), project);
        build.getActions().add(new CheckStyleResultAction(build, this, result));

        return result;
    }

    /** {@inheritDoc} */
    @Override
    public CheckStyleDescriptor getDescriptor() {
        return (CheckStyleDescriptor)super.getDescriptor();
    }

    /** {@inheritDoc} */
    public MatrixAggregator createAggregator(final MatrixBuild build, final Launcher launcher,
            final BuildListener listener) {
        return new CheckStyleAnnotationsAggregator(build, launcher, listener, this, getDefaultEncoding());
    }
}
