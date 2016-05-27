package hudson.plugins.checkstyle.rules;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests the class {@link CheckStyleRules}.
 *
 * @author Ulli Hafner
 */
public class CheckStyleRulesTest {
    /**
     * Test whether we could parse the Checkstyle rule meta data.
     */
    @Test
    public void checkRuleLoader() {
        CheckStyleRules reader = CheckStyleRules.getInstance();
        reader.initialize();

        assertEquals("Wrong number of rules detected.", 163, reader.getRules().size());
        assertNotNull("No EmptyBlock rule found.", reader.getRule("EmptyBlock"));
        assertEquals("Description for EmptyBlock found.", "<p> Checks for empty blocks. </p>", reader.getRule("EmptyBlock").getDescription());
        assertNotSame("No description for AnnotationUseStyle found.", Rule.UNDEFINED_DESCRIPTION, reader.getRule("AnnotationUseStyle").getDescription());
        assertNotSame("No description for AnnotationUseStyle found.", Rule.UNDEFINED_DESCRIPTION, reader.getDescription("AnnotationUseStyle"));
        assertSame("No default text available for undefined rule.", Rule.UNDEFINED_DESCRIPTION, reader.getRule("Undefined").getDescription());

        for (Rule rule : reader.getRules()) {
            assertNotSame("Rule " + rule.getName() + " has no description.", Rule.UNDEFINED_DESCRIPTION, rule.getDescription());
        }
    }
}
