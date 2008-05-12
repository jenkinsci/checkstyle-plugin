package hudson.plugins.checkstyle.parser;

import static org.junit.Assert.*;
import hudson.plugins.checkstyle.util.model.FileAnnotation;
import hudson.plugins.checkstyle.util.model.MavenModule;
import hudson.plugins.checkstyle.util.model.Priority;
import hudson.plugins.checkstyle.util.model.WorkspaceFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;

import org.junit.Test;
import org.xml.sax.SAXException;

/**
 *  Tests the extraction of Checkstyle analysis results.
 */
public class CheckStyleParserTest {
    /**
     * Tests parsing of a simple Checkstyle file.
     *
     * @throws IOException Signals that an I/O exception has occurred
     * @throws SAXException Signals that an Parser exception has occurred
     */
    @Test
    public void analyseCheckStyleFile() throws IOException, SAXException {
        InputStream inputStream = CheckStyleParserTest.class.getResourceAsStream("checkstyle.xml");

        MavenModule module = new CheckStyleParser().parse(inputStream, "empty");

        assertEquals("Wrong number of annotations detected.", 7, module.getNumberOfAnnotations());
        Collection<WorkspaceFile> files = module.getFiles();
        assertEquals("Wrong number of files detected.", 2, files.size());
        Iterator<WorkspaceFile> iterator = files.iterator();
        WorkspaceFile file = iterator.next();
        assertEquals("package.html not detected.", "C:/Build/Results/jobs/Maven/workspace/tasks/src/main/java/hudson/plugins/tasks/util/model/package.html", file.getName());

        assertEquals("Wrong number of annotations detected.", 1, file.getNumberOfAnnotations());
        FileAnnotation annotation = file.getAnnotations().iterator().next();
        assertTrue("Annotations is of wrong type.", annotation instanceof Warning);
        Warning warning = (Warning)annotation;
        assertEquals("Wrong number of line ranges detected.", 1, warning.getLineRanges().size());
        assertEquals("Wrong line detected.", 0, warning.getLineRanges().iterator().next().getStart());
        assertEquals("Wrong line detected.", 0, warning.getPrimaryLineNumber());
        assertEquals("Wrong category detected.", "javadoc", warning.getCategory());
        assertEquals("Wrong type detected.", "PackageHtmlCheck", warning.getType());
        assertEquals("Wrong priority detected.", Priority.HIGH, warning.getPriority());
        assertEquals("Wrong message detected.", "Fehlende Package-Dokumentation.", warning.getMessage());

        file = iterator.next();
        assertEquals("CsharpNamespaceDetector.java not detected.", "C:/Build/Results/jobs/Maven/workspace/tasks/src/main/java/hudson/plugins/tasks/parser/CsharpNamespaceDetector.java", file.getName());
        assertEquals("Wrong number of annotations detected.", 6, file.getNumberOfAnnotations());
    }
}


/* Copyright (c) Avaloq Evolution AG */