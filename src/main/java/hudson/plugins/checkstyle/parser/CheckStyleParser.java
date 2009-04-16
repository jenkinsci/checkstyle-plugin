package hudson.plugins.checkstyle.parser;

import hudson.plugins.checkstyle.util.AbstractAnnotationParser;
import hudson.plugins.checkstyle.util.JavaPackageDetector;
import hudson.plugins.checkstyle.util.model.FileAnnotation;
import hudson.plugins.checkstyle.util.model.Priority;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.digester.Digester;
import org.apache.commons.lang.StringUtils;
import org.xml.sax.SAXException;

/**
 * A parser for Checkstyle XML files.
 *
 * @author Ulli Hafner
 */
public class CheckStyleParser extends AbstractAnnotationParser {
    /** Unique identifier of this class. */
    private static final long serialVersionUID = -8705621875291182458L;

    /**
     * Creates a new instance of {@link CheckStyleParser}.
     */
    public CheckStyleParser() {
        super(StringUtils.EMPTY);
    }

    /**
     * Creates a new instance of {@link CheckStyleParser}.
     *
     * @param defaultEncoding
     *            the default encoding to be used when reading and parsing files
     */
    public CheckStyleParser(final String defaultEncoding) {
        super(defaultEncoding);
    }

    /** {@inheritDoc} */
    @Override
    public Collection<FileAnnotation> parse(final InputStream file, final String moduleName) throws InvocationTargetException {
        try {
            Digester digester = new Digester();
            digester.setValidating(false);
            digester.setClassLoader(CheckStyleParser.class.getClassLoader());

            String rootXPath = "checkstyle";
            digester.addObjectCreate(rootXPath, CheckStyle.class);
            digester.addSetProperties(rootXPath);

            String fileXPath = "checkstyle/file";
            digester.addObjectCreate(fileXPath, hudson.plugins.checkstyle.parser.File.class);
            digester.addSetProperties(fileXPath);
            digester.addSetNext(fileXPath, "addFile", hudson.plugins.checkstyle.parser.File.class.getName());

            String bugXPath = "checkstyle/file/error";
            digester.addObjectCreate(bugXPath, Error.class);
            digester.addSetProperties(bugXPath);
            digester.addSetNext(bugXPath, "addError", Error.class.getName());

            CheckStyle module;
            module = (CheckStyle)digester.parse(new InputStreamReader(file, "UTF-8"));
            if (module == null) {
                throw new SAXException("Input stream is not a Checkstyle file.");
            }

            return convert(module, moduleName);
        }
        catch (IOException exception) {
            throw new InvocationTargetException(exception);
        }
        catch (SAXException exception) {
            throw new InvocationTargetException(exception);
        }
    }

    /**
     * Converts the internal structure to the annotations API.
     *
     * @param collection
     *            the internal maven module
     * @param moduleName
     *            name of the maven module
     * @return a maven module of the annotations API
     */
    private Collection<FileAnnotation> convert(final CheckStyle collection, final String moduleName) {
        ArrayList<FileAnnotation> annotations = new ArrayList<FileAnnotation>();

        for (hudson.plugins.checkstyle.parser.File file : collection.getFiles()) {
            if (isValidWarning(file)) {
                String packageName = new JavaPackageDetector().detectPackageName(file.getName());
                for (Error error : file.getErrors()) {
                    Priority priority;
                    if ("error".equalsIgnoreCase(error.getSeverity())) {
                        priority = Priority.HIGH;
                    }
                    else if ("warning".equalsIgnoreCase(error.getSeverity())) {
                        priority = Priority.NORMAL;
                    }
                    else if ("info".equalsIgnoreCase(error.getSeverity())) {
                        priority = Priority.LOW;
                    }
                    else {
                        continue; // ignore
                    }
                    String source = error.getSource();
                    String type = StringUtils.substringAfterLast(source, ".");
                    String category = StringUtils.substringAfterLast(StringUtils.substringBeforeLast(source, "."), ".");

                    Warning warning = new Warning(priority, error.getMessage(), StringUtils.capitalize(category),
                            type, error.getLine(), error.getLine());
                    warning.setModuleName(moduleName);
                    warning.setFileName(file.getName());
                    warning.setPackageName(packageName);

                    try {
                        warning.setContextHashCode(createContextHashCode(file.getName(), error.getLine()));
                    }
                    catch (IOException exception) {
                        // ignore and continue
                    }
                    annotations.add(warning);
                }
            }
        }
        return annotations;
    }

    /**
     * Returns <code>true</code> if this warning is valid or <code>false</code>
     * if the warning can't be processed by the checkstyle plug-in.
     *
     * @param file the file to check
     * @return <code>true</code> if this warning is valid
     */
    private boolean isValidWarning(final hudson.plugins.checkstyle.parser.File file) {
        return !file.getName().endsWith("package.html");
    }
}

