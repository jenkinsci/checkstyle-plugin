package hudson.plugins.checkstyle.parser;

import hudson.FilePath;
import hudson.FilePath.FileCallable;
import hudson.plugins.checkstyle.Messages;
import hudson.plugins.checkstyle.util.MavenModuleDetector;
import hudson.plugins.checkstyle.util.model.JavaProject;
import hudson.plugins.checkstyle.util.model.MavenModule;
import hudson.remoting.VirtualChannel;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.tools.ant.types.FileSet;
import org.xml.sax.SAXException;

/**
 * Parses the PMD files that match the specified pattern and creates a
 * corresponding Java project with a collection of annotations.
 *
 * @author Ulli Hafner
 */
public class PmdCollector implements FileCallable<JavaProject> {
    /** Generated ID. */
    private static final long serialVersionUID = -6415863872891783891L;
    /** Logger. */
    private final transient PrintStream logger;
    /** Ant file-set pattern to scan for PMD files. */
    private final String filePattern;

    /**
     * Creates a new instance of <code>PmdCollector</code>.
     *
     * @param listener
     *            the Logger
     * @param filePattern
     *            ant file-set pattern to scan for PMD files
     */
    public PmdCollector(final PrintStream listener, final String filePattern) {
        logger = listener;
        this.filePattern = filePattern;
    }

    /**
     * Logs the specified message.
     *
     * @param message the message
     */
    protected void log(final String message) {
        if (logger != null) {
            logger.println("[PMD] " + message);
        }
    }

    /** {@inheritDoc} */
    public JavaProject invoke(final File workspace, final VirtualChannel channel) throws IOException {
        String[] pmdFiles = findPmdFiles(workspace);
        JavaProject project = new JavaProject();

        if (pmdFiles.length == 0) {
            project.setError("No pmd report files were found. Configuration error?");
            return project;
        }

        try {
            MavenModuleDetector detector = new MavenModuleDetector();
            int duplicateModuleCounter = 1;
            for (String file : pmdFiles) {
                File pmdFile = new File(workspace, file);

                String moduleName = detector.guessModuleName(pmdFile.getAbsolutePath());
                if (project.containsModule(moduleName)) {
                    moduleName += "-" + duplicateModuleCounter++;
                }
                MavenModule module = new MavenModule(moduleName);

                if (!pmdFile.canRead()) {
                    String message = Messages.Checkstyle_CheckstyleCollector_Error_NoPermission(pmdFile);
                    log(message);
                    module.setError(message);
                    continue;
                }
                if (new FilePath(pmdFile).length() <= 0) {
                    String message = Messages.Checkstyle_CheckstyleCollector_Error_EmptyFile(pmdFile);
                    log(message);
                    module.setError(message);
                    continue;
                }

                module = parseFile(workspace, pmdFile, module);
                project.addModule(module);
            }
        }
        catch (InterruptedException exception) {
            log("Parsing has been canceled.");
        }
        return project;
    }

    /**
     * Parses the specified PMD file and maps all warnings to a
     * corresponding annotation. If the file could not be parsed then an empty
     * module with an error message is returned.
     *
     * @param workspace
     *            the root of the workspace
     * @param pmdFile
     *            the file to parse
     * @param emptyModule
     *            an empty module with the guessed module name
     * @return the created module
     * @throws InterruptedException
     */
    private MavenModule parseFile(final File workspace, final File pmdFile, final MavenModule emptyModule) throws InterruptedException {
        Exception exception = null;
        MavenModule module = emptyModule;
        try {
            FilePath filePath = new FilePath(pmdFile);
            CheckStyleParser pmdParser = new CheckStyleParser();
            module = pmdParser.parse(filePath.read(), emptyModule.getName());
            log("Successfully parsed PMD file " + pmdFile + " of module "
                    + module.getName() + " with " + module.getNumberOfAnnotations() + " warnings.");
        }
        catch (IOException e) {
            exception = e;
        }
        catch (SAXException e) {
            exception = e;
        }
        if (exception != null) {
            String errorMessage = Messages.Checkstyle_CheckstyleCollector_Error_Exception(pmdFile)
                    + "\n\n" + ExceptionUtils.getStackTrace(exception);
            log(errorMessage);
            module.setError(errorMessage);
        }
        return module;
    }

    /**
     * Returns an array with the filenames of the PMD files that have been
     * found in the workspace.
     *
     * @param workspaceRoot
     *            root directory of the workspace
     * @return the filenames of the PMD files
     */
    private String[] findPmdFiles(final File workspaceRoot) {
        FileSet fileSet = new FileSet();
        org.apache.tools.ant.Project project = new org.apache.tools.ant.Project();
        fileSet.setProject(project);
        fileSet.setDir(workspaceRoot);
        fileSet.setIncludes(filePattern);

        return fileSet.getDirectoryScanner(project).getIncludedFiles();
    }
}