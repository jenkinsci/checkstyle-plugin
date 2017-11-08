package io.jenkins.plugins.analysis.checkstyle;


import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import edu.hm.hafner.analysis.Issue;
import edu.hm.hafner.analysis.IssueBuilder;
import edu.hm.hafner.analysis.Issues;
import edu.hm.hafner.analysis.Priority;
import static edu.hm.hafner.analysis.assertj.Assertions.*;
import io.jenkins.plugins.analysis.checkstyle.CheckStyle.CheckStyleLabelProvider;

import hudson.plugins.checkstyle.rules.CheckStyleRules;

/**
 * Tests the extraction of CheckStyle analysis results.
 */
class CheckStyleParserTest {
    /**
     * Parses a file with one fatal error.
     *
     * @see <a href="http://issues.jenkins-ci.org/browse/JENKINS-25511">Issue 25511</a>
     */
    @Test
    void issue25511() {
        Issues<Issue> issues = parse("issue25511.xml");

        assertThat(issues).hasSize(2);

        assertThat(issues.get(0)).hasMessage("',' is not followed by whitespace.");
        assertThat(issues.get(1)).hasMessage("Type hint \"kEvent\" missing for $event at position 1");
    }

    /**
     * Tests parsing of file with some warnings that are in the same line but different column.
     *
     * @see <a href="http://issues.jenkins-ci.org/browse/JENKINS-19122">Issue 19122</a>
     */
    @Test
    void testColumnPositions() {
        Issues<Issue> issues = parse("issue19122.xml");

        assertThat(issues).hasSize(58);
    }

    /**
     * Tests parsing of a file with Scala style warnings.
     *
     * @see <a href="http://www.scalastyle.org">Scala Style Homepage</a>
     * @see <a href="http://issues.jenkins-ci.org/browse/JENKINS-17287">Issue 17287</a>
     */
    @Test
    void testParsingOfScalaStyleFormat() {
        Issues<Issue> issues = parse("scalastyle-output.xml");

        assertThat(issues).hasSize(2);
    }

    /**
     * Tests parsing of a simple Checkstyle file.
     */
    @Test
    void analyseCheckStyleFile() {
        CheckStyleRules.getInstance().initialize();

        Issues<Issue> issues = parse("checkstyle.xml");

        assertThat(issues).hasSize(6);
        assertThat(issues.getFiles()).hasSize(1);
        assertThat(issues.getFiles()).containsExactly(
                "X:/Build/Results/jobs/Maven/workspace/tasks/src/main/java/hudson/plugins/tasks/parser/CsharpNamespaceDetector.java");

        Issue actual = issues.get(2);
        assertThat(actual)
                .hasLineStart(22)
                .hasCategory("Design")
                .hasType("DesignForExtensionCheck")
                .hasPriority(Priority.HIGH)
                .hasMessage("Die Methode 'detectPackageName' ist nicht fr Vererbung entworfen - muss abstract, final oder leer sein.");

        CheckStyleLabelProvider labelProvider = new CheckStyleLabelProvider();
        assertThat(actual).hasDescription(StringUtils.EMPTY);
        assertThat(labelProvider.getDescription(actual)).contains("finds classes that are designed for extension");
    }

    private Issues<Issue> parse(final String fileName) {
        InputStream inputStream = null;
        try {
            inputStream = hudson.plugins.checkstyle.parser.CheckStyleParserTest.class.getResourceAsStream(fileName);

            return new CheckStyleParser().parse(new InputStreamReader(inputStream), new IssueBuilder());
        }
        finally {
            IOUtils.closeQuietly(inputStream);
        }
    }
}
