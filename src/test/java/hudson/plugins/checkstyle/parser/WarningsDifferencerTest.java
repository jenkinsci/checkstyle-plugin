package hudson.plugins.checkstyle.parser;

import java.util.HashSet;

import org.junit.Test;

import static org.junit.Assert.*;

import hudson.plugins.analysis.core.IssueDifference;
import hudson.plugins.analysis.test.AnnotationDifferencerTest;
import hudson.plugins.analysis.util.model.FileAnnotation;
import hudson.plugins.analysis.util.model.Priority;

/**
 * Tests the {@link IssueDifference} for warnings.
 */
public class WarningsDifferencerTest extends AnnotationDifferencerTest {
    @Override
    public FileAnnotation createAnnotation(final String fileName, final Priority priority, final String message, final String category,
            final String type, final int start, final int end) {
        Warning warning = new Warning(priority, message, message, message, start, end);
        warning.setFileName(fileName);
        return warning;
    }

    /**
     * Verifies that 2 identical warnings are equal.
     */
    @Test
    public void issue5344() {
        Warning first = createWarning();
        Warning second = createWarning();
        assertEquals("Warnings are not equal.", first, second);

        HashSet<FileAnnotation> current = new HashSet<FileAnnotation>();
        current.add(first);
        HashSet<FileAnnotation> previous = new HashSet<FileAnnotation>();
        previous.add(second);

        assertTrue("New warnings found.", new IssueDifference(current, previous).getNewIssues().isEmpty());
    }

    /**
     * Creates a warning.
     *
     * @return a warning
     */
    private Warning createWarning() {
        Warning warning = new Warning(Priority.NORMAL, "Missing a Javadoc comment.", "JavaDoc", "JavadocMethodCheck", 14);
        warning.setFileName("/svr/hudson-data/jobs/*****.java");
        warning.setModuleName("***** web-archive");
        warning.setPackageName("*****");
        warning.setContextHashCode(-1131232024);
        return warning;
    }
}

