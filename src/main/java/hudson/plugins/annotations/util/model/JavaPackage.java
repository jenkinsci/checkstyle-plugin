package hudson.plugins.annotations.util.model;



/**
 * A serializable Java Bean class representing a Java package.
 *
 * @author Ulli Hafner
 */
public class JavaPackage extends AnnotationContainer {
    /** Unique identifier of this class. */
    private static final long serialVersionUID = 4034932648975191723L;
    /** Name of this package. */
    private String name; // NOPMD: backward compatibility

    /**
     * Creates a new instance of <code>JavaPackage</code>.
     *
     * @param packageName
     *            the name of this package
     */
    public JavaPackage(final String packageName) {
        super(packageName, Hierarchy.PACKAGE);
    }

    /**
     * Rebuilds the priorities mapping.
     *
     * @return the created object
     */
    @Override
    protected Object readResolve() {
        if (name != null) {
            setName(name);
        }
        setHierarchy(Hierarchy.PACKAGE);
        rebuildMappings();
        return this;
    }
}

