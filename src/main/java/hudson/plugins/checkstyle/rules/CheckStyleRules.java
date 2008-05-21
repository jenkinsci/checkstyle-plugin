package hudson.plugins.checkstyle.rules;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.digester.Digester;
import org.apache.commons.lang.StringUtils;
import org.xml.sax.SAXException;

/**
 * Reads the meta data of the Checkstyle rules from the DocBook files of the Checkstyle distribution.
 *
 * @author Ulli Hafner
 */
public final class CheckStyleRules {
    /** Mapping of rule names to rules. */
    private final Map<String, Rule> rulesByName = new HashMap<String, Rule>();
    /** Singleton instance. */
    private static final CheckStyleRules INSTANCE = new CheckStyleRules();

    /**
     * Returns the singleton instance.
     *
     * @return the singleton instance
     */
    public static CheckStyleRules getInstance() {
        return INSTANCE;
    }

    /**
     * Creates the singleton instance.
     */
    private CheckStyleRules() {
        initialize();
    }

    /**
     * Initializes the rules.
     */
    private void initialize() {
        try {
            String[] ruleFiles = new String[] {"blocks", "coding", "design", "duplicates", "header", "imports",
                    "j2ee", "javadoc", "metrics", "misc", "modifier", "naming", "reporting", "sizes", "whitespace"};
            for (int i = 0; i < ruleFiles.length; i++) {
                InputStream inputStream = CheckStyleRules.class.getResourceAsStream("config_" + ruleFiles[i] + ".xml");
                Digester digester = createDigester();
                List<Rule> rules = new ArrayList<Rule>();
                digester.push(rules);
                digester.parse(inputStream);
                for (Rule rule : rules) {
                    rulesByName.put(rule.getName(), rule);
                }
            }
        }
        catch (ParserConfigurationException exception) {
            Logger.getLogger(CheckStyleRules.class.getName()).log(Level.SEVERE, "Can't initialize checkstyle rules.", exception);
        }
        catch (IOException exception) {
            Logger.getLogger(CheckStyleRules.class.getName()).log(Level.SEVERE, "Can't initialize checkstyle rules.", exception);
        }
        catch (SAXException exception) {
            Logger.getLogger(CheckStyleRules.class.getName()).log(Level.SEVERE, "Can't initialize checkstyle rules.", exception);
        }
    }

    /**
     * Creates a new digester.
     *
     * @return the new digester.
     * @throws ParserConfigurationException
     */
    private Digester createDigester() throws ParserConfigurationException {
        Digester digester = new Digester();
        digester.setValidating(false);
        digester.setClassLoader(CheckStyleRules.class.getClassLoader());

        String section = "*/section";
        digester.addObjectCreate(section, Rule.class);
        digester.addSetProperties(section);
        digester.addSetNext(section, "add");

        String subSection = "*/section/subsection";
        digester.addObjectCreate(subSection, Topic.class);
        digester.addSetProperties(subSection);
        digester.addSetNext(subSection, "setDescription");
        digester.addRule(subSection, new TopicRule());
        return digester;
    }

    /**
     * Returns all Checkstyle rules.
     *
     * @return all Checkstyle rules
     */
    public Collection<Rule> getRules() {
        return Collections.unmodifiableCollection(rulesByName.values());
    }

    /**
     * Returns the Checkstyle rule with the specified name.
     *
     * @param name the name of the rule
     * @return the Checkstyle rule with the specified name.
     */
    public Rule getRule(final String name) {
        Rule rule = rulesByName.get(name);
        if (rule == null) {
            rule = rulesByName.get(StringUtils.removeEnd(name, "Check"));
        }
        if (rule == null) {
            return new Rule(name);
        }
        return rule;
    }

    /**
     * Returns the description of the Checkstyle rule with the specified name.
     *
     * @param name the name of the rule
     * @return the description for the specified rule .
     */
    public String getDescription(final String name) {
        return getRule(name).getDescription();
    }
}
