package hudson.plugins.checkstyle.parser;

import hudson.plugins.analysis.core.AnnotationDifferencer;
import hudson.plugins.analysis.test.AnnotationDifferencerTest;
import hudson.plugins.analysis.util.model.FileAnnotation;
import hudson.plugins.analysis.util.model.Priority;

/**
 * Tests the {@link AnnotationDifferencer} for warnings.
 */
public class WarningsDifferencerTest extends AnnotationDifferencerTest {
    /** {@inheritDoc} */
    @Override
    public FileAnnotation createAnnotation(final String fileName, final Priority priority, final String message, final String category,
            final String type, final int start, final int end) {
        Warning warning = new Warning(priority, message, message, message, start, end);
        warning.setFileName(fileName);
        return warning;
    }
}

