package hudson.plugins.checkstyle;

import hudson.Plugin;
import hudson.maven.MavenReporters;
import hudson.tasks.BuildStep;

/**
 * Registers the PMD plug-in publisher.
 *
 * @author Ulli Hafner
 */
public class CheckStylePlugin extends Plugin {
    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("PMD")
    public void start() throws Exception {
        BuildStep.PUBLISHERS.addRecorder(CheckStylePublisher.PMD_DESCRIPTOR);

        MavenReporters.LIST.add(CheckStyleReporter.PMD_SCANNER_DESCRIPTOR);
    }
}
