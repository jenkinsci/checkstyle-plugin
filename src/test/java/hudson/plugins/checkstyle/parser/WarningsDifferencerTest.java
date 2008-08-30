package hudson.plugins.checkstyle.parser;

import hudson.plugins.checkstyle.util.AnnotationDifferencer;
import hudson.plugins.checkstyle.util.AnnotationDifferencerTest;
import hudson.plugins.checkstyle.util.model.FileAnnotation;
import hudson.plugins.checkstyle.util.model.Priority;

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

