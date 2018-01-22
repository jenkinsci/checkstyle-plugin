package hudson.plugins.checkstyle.parser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import hudson.model.FreeStyleProject;
import hudson.model.UnprotectedRootAction;
import hudson.plugins.checkstyle.CheckStylePublisher;
import hudson.tasks.Shell;
import hudson.util.HttpResponses;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.junit.Test;

import com.google.common.collect.Sets;

import static org.junit.Assert.*;

import hudson.model.Result;
import hudson.plugins.analysis.core.BuildResultEvaluator;
import hudson.plugins.analysis.core.IssueDifference;
import hudson.plugins.analysis.core.ParserResult;
import hudson.plugins.analysis.core.Thresholds;
import hudson.plugins.analysis.util.model.FileAnnotation;
import hudson.plugins.analysis.util.model.MavenModule;
import hudson.plugins.analysis.util.model.Priority;
import hudson.plugins.analysis.util.model.WorkspaceFile;
import hudson.plugins.checkstyle.rules.CheckStyleRules;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.TestExtension;
import org.jvnet.hudson.test.WithoutJenkins;
import org.kohsuke.stapler.HttpResponse;

/**
 * Tests the extraction of CheckStyle analysis results.
 */
public class CheckStyleParserTest {

    @Rule
    public JenkinsRule j = new JenkinsRule();

    /**
     * Parses a sequence of files
     *
     * @throws InvocationTargetException if the file could not be read
     */
    @Test
    @WithoutJenkins
    public void shouldComputeDeltas() throws InvocationTargetException {
        BuildResultEvaluator evaluator = new BuildResultEvaluator("url");
        StringBuilder logger = new StringBuilder();
        Thresholds t = new Thresholds();
        t.unstableNewAll = "0";

        Collection<FileAnnotation> first = parse("checkstyle-result-build1.xml");
        assertEquals("Wrong number of annotations detected.", 1, first.size());

        Result result = evaluator.evaluateBuildResult(logger, t, first);
        assertEquals("Wrong plug-in result: ", Result.SUCCESS, result);

        Collection<FileAnnotation> second = parse("checkstyle-result-build2.xml");
        assertEquals("Wrong number of annotations detected.", 3, second.size());

        Set<FileAnnotation> newAnnotations = new IssueDifference(Sets.newHashSet(second), Sets.newHashSet(first)).getNewIssues();
        assertEquals("Wrong number of annotations detected.", 2, newAnnotations.size());

        result = evaluator.evaluateBuildResult(logger, t, first, newAnnotations);
        assertEquals("Wrong plug-in result: ", Result.UNSTABLE, result);

        Collection<FileAnnotation> third = parse("checkstyle-result-build3.xml");
        assertEquals("Wrong number of annotations detected.", 1, third.size());

        Set<FileAnnotation> newAnnotationsInThird = new IssueDifference(Sets.newHashSet(third), Sets.newHashSet(first)).getNewIssues();
        assertEquals("Wrong number of annotations detected.", 1, newAnnotationsInThird.size());

        result = evaluator.evaluateBuildResult(logger, t, first, newAnnotationsInThird);
        assertEquals("Wrong plug-in result: ", Result.UNSTABLE, result);

        Collection<FileAnnotation> fourth = parse("checkstyle-result-build4.xml");
        assertEquals("Wrong number of annotations detected.", 0, fourth.size());

        Set<FileAnnotation> newAnnotationsInFourth = new IssueDifference(Sets.newHashSet(fourth), Sets.newHashSet(first)).getNewIssues();
        assertEquals("Wrong number of annotations detected.", 0, newAnnotationsInFourth.size());

        result = evaluator.evaluateBuildResult(logger, t, first, newAnnotationsInFourth);
        assertEquals("Wrong plug-in result: ", Result.SUCCESS, result);
    }

    /**
     * Parses a file with one fatal error.
     *
     * @throws IOException if the file could not be read
     * @see <a href="http://issues.jenkins-ci.org/browse/JENKINS-25511">Issue 25511</a>
     */
    @Test
    @WithoutJenkins
    public void issue25511() throws InvocationTargetException {
        Collection<FileAnnotation> annotations = parse("issue25511.xml");

        assertEquals("Wrong number of annotations detected.", 2, annotations.size());

        Iterator<FileAnnotation> iterator = annotations.iterator();

        FileAnnotation annotation = iterator.next();
        assertEquals("Wrong message text", "',' is not followed by whitespace.",
                annotation.getMessage());

        annotation = iterator.next();
        assertEquals("Wrong message text", "Type hint \"kEvent\" missing for $event at position 1", annotation.getMessage());
    }

    /**
     * Tests parsing of file with some warnings that are in the same line but different column.
     *
     * @throws InvocationTargetException Signals that an I/O exception has occurred
     * @see <a href="http://issues.jenkins-ci.org/browse/JENKINS-19122">Issue 19122</a>
     */
    @Test
    @WithoutJenkins
    public void testColumnPositions() throws InvocationTargetException {
        Collection<FileAnnotation> annotations = parse("issue19122.xml");

        assertEquals("Wrong number of annotations detected.", 58, annotations.size());

        ParserResult withoutDuplicates = new ParserResult(annotations);
        assertEquals("Wrong number of annotations detected.", 58, withoutDuplicates.getNumberOfAnnotations());
    }

    /**
     * Tests parsing of a file with Scala style warnings.
     *
     * @throws InvocationTargetException Signals that an I/O exception has occurred
     * @see <a href="http://www.scalastyle.org">Scala Style Homepage</a>
     * @see <a href="http://issues.jenkins-ci.org/browse/JENKINS-17287">Issue 17287</a>
     */
    @Test
    @WithoutJenkins
    public void testParsingOfScalaStyleFormat() throws InvocationTargetException {
        Collection<FileAnnotation> annotations = parse("scalastyle-output.xml");

        assertEquals("Wrong number of annotations detected.", 2, annotations.size());
    }

    /**
     * Tests parsing of a simple Checkstyle file.
     *
     * @throws InvocationTargetException Signals that an I/O exception has occurred
     */
    @Test
    @WithoutJenkins
    public void analyseCheckStyleFile() throws InvocationTargetException {
        CheckStyleRules.getInstance().initialize();

        Collection<FileAnnotation> annotations = parse("checkstyle.xml");

        MavenModule module = new MavenModule();
        module.addAnnotations(annotations);

        assertEquals("Wrong number of annotations detected.", 6, module.getNumberOfAnnotations());
        Collection<WorkspaceFile> files = module.getFiles();
        assertEquals("Wrong number of files detected.", 1, files.size());
        WorkspaceFile file = files.iterator().next();
        assertEquals("CsharpNamespaceDetector.java not detected.", "X:/Build/Results/jobs/Maven/workspace/tasks/src/main/java/hudson/plugins/tasks/parser/CsharpNamespaceDetector.java", file.getName());
        assertEquals("Wrong number of annotations detected.", 6, file.getNumberOfAnnotations());

        Iterator<FileAnnotation> iterator = file.getAnnotations().iterator();

        boolean hasChecked = false;
        while (iterator.hasNext()) {
            FileAnnotation annotation = iterator.next();
            assertTrue("Annotations is of wrong type.", annotation instanceof Warning);
            Warning warning = (Warning) annotation;
            assertEquals("Wrong number of line ranges detected.", 1, warning.getLineRanges().size());
            if (warning.getPrimaryLineNumber() == 22) {
                assertEquals("Wrong category detected.", "Design", warning.getCategory());
                assertEquals("Wrong type detected.", "DesignForExtensionCheck", warning.getType());
                assertEquals("Wrong priority detected.", Priority.HIGH, warning.getPriority());
                assertTrue("Wrong description detected.", warning.getToolTip().contains(
                        "finds classes that are designed for extension"));
                assertEquals(
                        "Wrong message detected.",
                        "Die Methode 'detectPackageName' ist nicht fr Vererbung entworfen - muss abstract, final oder leer sein.",
                        warning.getMessage());
                hasChecked = true;
            }
        }
        assertTrue("Warning is not in checkstyle.xml file.", hasChecked);
    }

    private Collection<FileAnnotation> parse(final String fileName) throws InvocationTargetException {
        Collection<FileAnnotation> annotations;
        InputStream inputStream = null;
        try {
            inputStream = CheckStyleParserTest.class.getResourceAsStream(fileName);

            annotations = new CheckStyleParser().parse(inputStream, "empty");
        }
        finally {
            IOUtils.closeQuietly(inputStream);
        }
        return annotations;
    }
    
    @Test
    @Issue("SECURITY-656")
    public void testXxe() throws Exception {
        String xxeInUserContentLink = j.getURL() + "userContent/xxe.xml";
        String oobInUserContentLink = j.getURL() + "userContent/oob.xml";
        String triggerLink = j.getURL() + "triggerMe";
        
        String xxeFile = this.getClass().getResource("testXxe-xxe.xml").getFile();
        String xxeFileContent = FileUtils.readFileToString(new java.io.File(xxeFile), StandardCharsets.UTF_8);
        String adaptedXxeFileContent = xxeFileContent.replace("$OOB_LINK$", oobInUserContentLink);
        
        String oobFile = this.getClass().getResource("testXxe-oob.xml").getFile();
        String oobFileContent = FileUtils.readFileToString(new java.io.File(oobFile), StandardCharsets.UTF_8);
        String adaptedOobFileContent = oobFileContent.replace("$TARGET_URL$", triggerLink);
        
        java.io.File userContentDir = new java.io.File(j.jenkins.getRootDir(), "userContent");
        FileUtils.writeStringToFile(new java.io.File(userContentDir, "xxe.xml"), adaptedXxeFileContent);
        FileUtils.writeStringToFile(new File(userContentDir, "oob.xml"), adaptedOobFileContent);
        
        FreeStyleProject project = j.createFreeStyleProject();
        Shell copyToWorkspace = new Shell("curl \"" + xxeInUserContentLink + "\" > xxe.xml");
        project.getBuildersList().add(copyToWorkspace);
        
        CheckStylePublisher publisher = new CheckStylePublisher();
        publisher.setPattern("xxe.xml");
        project.getPublishersList().add(publisher);
        
        assertEquals(Result.SUCCESS, project.scheduleBuild2(0).get().getResult());
        
        YouCannotTriggerMe urlHandler = j.jenkins.getExtensionList(UnprotectedRootAction.class).get(YouCannotTriggerMe.class);
        assertEquals(urlHandler.triggerCount, 0);
    }
    
    @TestExtension("testXxe")
    public static class YouCannotTriggerMe implements UnprotectedRootAction {
        private int triggerCount = 0;
        
        @Override
        public String getIconFileName() {
            return null;
        }
        
        @Override
        public String getDisplayName() {
            return null;
        }
        
        @Override
        public String getUrlName() {
            return "triggerMe";
        }
        
        public HttpResponse doIndex() {
            triggerCount++;
            return HttpResponses.plainText("triggered");
        }
    }
}
