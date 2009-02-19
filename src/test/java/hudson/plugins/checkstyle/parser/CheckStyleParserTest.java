package hudson.plugins.checkstyle.parser;

import static org.junit.Assert.*;
import hudson.plugins.checkstyle.util.model.FileAnnotation;
import hudson.plugins.checkstyle.util.model.MavenModule;
import hudson.plugins.checkstyle.util.model.Priority;
import hudson.plugins.checkstyle.util.model.WorkspaceFile;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Iterator;

import org.junit.Test;

/**
 *  Tests the extraction of Checkstyle analysis results.
 */
public class CheckStyleParserTest {
    /**
     * Tests parsing of a simple Checkstyle file.
     *
     * @throws InvocationTargetException Signals that an I/O exception has occurred
     */
    @Test
    public void analyseCheckStyleFile() throws InvocationTargetException {
        InputStream inputStream = CheckStyleParserTest.class.getResourceAsStream("checkstyle.xml");

        Collection<FileAnnotation> annotations = new CheckStyleParser().parse(inputStream, "empty");

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
            Warning warning = (Warning)annotation;
            assertEquals("Wrong number of line ranges detected.", 1, warning.getLineRanges().size());
            if (warning.getPrimaryLineNumber() == 22) {
                assertEquals("Wrong category detected.", "Design", warning.getCategory());
                assertEquals("Wrong type detected.", "DesignForExtensionCheck", warning.getType());
                assertEquals("Wrong priority detected.", Priority.HIGH, warning.getPriority());
                assertTrue("Wrong description detected.", warning.getToolTip().contains(
                        "Checks that classes are designed for extension."));
                assertEquals(
                        "Wrong message detected.",
                        "Die Methode 'detectPackageName' ist nicht fr Vererbung entworfen - muss abstract, final oder leer sein.",
                        warning.getMessage());
                hasChecked = true;
            }
        }
        assertTrue("Warning is not in checkstyle.xml file.", hasChecked);
    }
}


/* Copyright (c) Avaloq Evolution AG */