package hudson.plugins.checkstyle.util.model;

import java.io.Serializable;

// CHECKSTYLE:OFF
/** Backward compatibility.*/
@SuppressWarnings("unused")
public class MavenModule implements Serializable {
    /** Unique identifier of this class. */
    private static final long serialVersionUID = 5467122420572804130L;

    private String name;
    private String annotations;
    private String packageMapping;
    private boolean handleFiles;
}

