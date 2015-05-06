package hudson.plugins.checkstyle.parser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.Iterables;

import static org.junit.Assert.*;

import hudson.plugins.analysis.ast.Ast;
import hudson.plugins.analysis.util.ContextHashCode;
import hudson.plugins.analysis.util.Singleton;
import hudson.plugins.analysis.util.model.FileAnnotation;

/**
 * Test cases for the new warnings detector.
 *
 * @author Ullrich Hafner
 * @author Christian M�stl
 */
@SuppressWarnings("PMD.ExcessivePublicCount")
public class NewWarningDetectorTest {
    private static final String PACKAGE_DECLARATION = "PackageDeclaration";
    private static final String REDUNDANT_MODIFIER = "RedundantModifier";
    private static final String FINAL_CLASS = "FinalClass";
    private static final String INTERFACE_IS_TYPE = "InterfaceIsType";
    private static final String EXPLICIT_INITIALIZATION = "ExplicitInitialization";
    private static final String PACKAGE_NAME = "PackageName";
    private static final String NEED_BRACES = "NeedBraces";
    private static final String METHOD_NAME = "MethodName";

    private static final String METHOD_AST_FOLDERNAME = "MethodAst";
    private static final String ENVIRONMENT_AST_FOLDERNAME = "EnvironmentAst";
    private static final String FILE_AST_FOLDERNAME = "FileAst";
    private static final String CLASS_AST_FOLDERNAME = "ClassAst";
    private static final String METHOD_OR_CLASS_AST_FOLDERNAME = "MethodOrClassAst";
    private static final String INSTANCEVARIABLE_AST_FOLDERNAME = "InstancevariableAst";
    private static final String NAME_PACKAGE_AST_FOLDERNAME = "NamePackageAst";

    // Compare Refactorings from Martin Fowler (http://refactoring.com/catalog/)
    private static final String REFACTORING_NEWLINE = "_Newline";
    private static final String REFACTORING_RENAME = "_Rename";
    private static final String REFACTORING_EXTRACT_METHOD = "_ExtractMethod";
    private static final String REFACTORING_INLINE_METHOD = "_InlineMethod";
    private static final String REFACTORING_PULL_UP_METHOD = "_PullUpMethod";
    private static final String REFACTORING_PUSH_DOWN_METHOD = "_PushDownMethod";
    private static final String REFACTORING_EXTRACT_CONSTANT = "_ExtractConstant";

    /**
     * FIXME: In the end this should report no new warnings...
     */
    @Test @Ignore("Identifies the problematic ast creation...")
    public void shouldIdentifyTabWarnings() {
        String file = "Cobertura";
        String affectedClass = "MavenCoberturaPublisher";

        Set<FileAnnotation> affectedPrevious = parse("before/", file, affectedClass);
        assertEquals("Wrong Number of annotations found: ", 329, affectedPrevious.size());

        Set<FileAnnotation> affectedCurrent = parse("after/", file, affectedClass);
        assertEquals("Wrong Number of annotations found: ", 331, affectedCurrent.size());

        affectedCurrent.removeAll(affectedPrevious);
        assertEquals("Wrong Number of annotations found: ", 123, affectedCurrent.size());

        Set<String> hashes = new HashSet<String>();
        HashMap<String, FileAnnotation> map = new HashMap<String, FileAnnotation>();
        for (FileAnnotation warning : affectedCurrent) {
            Ast ast = getAst(file + ".java", warning, "", false);
            String key = ast.getDigest() + warning.getType();
            if (!hashes.add(key)) {
                System.out.println("-----------------------");
                System.out.println("Old: " + map.get(key));
                System.out.println("-----------------------");
                System.out.println("Now: " + warning);
                System.out.println("-----------------------");
                String digest = ast.getDigest();
                System.out.println(digest);

                ast = getAst(file + ".java", map.get(key), "", false);
                digest = ast.getDigest();
                System.out.println(digest);
            }
            map.put(key, warning);
        }
        assertEquals("Wrong Number of hashes found: ", 123, hashes.size());
    }

    private Set<FileAnnotation> parse(final String folder, final String file, final String affectedClass) {
        Set<FileAnnotation> affected = new HashSet<FileAnnotation>();
        for (FileAnnotation warning : parse(folder + file + ".xml")) {
            if (warning.getFileName().contains(affectedClass)) {
                affected.add(warning);
            }
        }
        return affected;
    }

    /**
     * Verifies that the insertion of a new line above the warning does produce a different hashCode.
     */
    @Test
    public void testInsertLineAboveWarning() {
        FileAnnotation beforeWarning = readWarning("before/" + METHOD_AST_FOLDERNAME + "/InsertLine.xml");
        FileAnnotation afterWarning = readWarning("after/" + METHOD_AST_FOLDERNAME + "/InsertLine.xml");

        verifyWarning(beforeWarning, "Javadoc", 7, "InsertLine.java");
        verifyWarning(afterWarning, "Javadoc", 8, "InsertLine.java");

        int beforeCode = createHashCode("before/" + METHOD_AST_FOLDERNAME + "/InsertLine.java", 7);
        int afterCode = createHashCode("after/" + METHOD_AST_FOLDERNAME + "/InsertLine.java", 8);

        assertNotEquals("Hash codes do not match: ", beforeCode, afterCode);
    }

    /**
     * Verifies that the insertion of a new line before the package declaration does not change the hash code.
     */
    @Test
    public void testInsertLineBeforePackage() {
        FileAnnotation beforeWarning = readWarning("before/" + METHOD_AST_FOLDERNAME + "/InsertLine.xml");
        FileAnnotation afterWarning = readWarning("after/" + METHOD_AST_FOLDERNAME + "/InsertLine2.xml");

        verifyWarning(beforeWarning, "Javadoc", 7, "InsertLine.java");
        verifyWarning(afterWarning, "Javadoc", 8, "InsertLine.java");

        int beforeCode = createHashCode("before/" + METHOD_AST_FOLDERNAME + "/InsertLine.java", 7);
        int afterCode = createHashCode("after/" + METHOD_AST_FOLDERNAME + "/InsertLine2.java", 8);

        assertEquals("Hash codes do not match: ", beforeCode, afterCode);
    }

    @SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
    private int createHashCode(final String fileName, final int line) {
        try {
            ContextHashCode hashCode = new ContextHashCode();
            return hashCode.create(getTempFileName(fileName), line, "UTF-8");
        }
        catch (IOException e) {
            throw new RuntimeException("Can't read Java file " + fileName, e);
        }
    }

    private String getTempFileName(final String fileName) {
        File warnings = createCopyInTemp(fileName);
        return warnings.getAbsolutePath();
    }

    private File createCopyInTemp(final String fileName) {
        try {
            File warnings = File.createTempFile("warnings", ".java");
            warnings.deleteOnExit();

            FileUtils.copyInputStreamToFile(NewWarningDetectorTest.class.getResourceAsStream(fileName), warnings);
            return warnings;
        }
        catch (IOException cause) {
            throw new IllegalArgumentException(cause);
        }
    }

    private void verifyWarning(final FileAnnotation before, final String category, final int line, final String fileName) {
        assertEquals("Wrong category", category, before.getCategory());
        assertEquals("Wrong line", line, before.getPrimaryLineNumber());
        assertEquals("Wrong line", fileName, FilenameUtils.getName(before.getFileName()));
    }

    @SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
    private FileAnnotation readWarning(final String fileName) {
        try {
            CheckStyleParser parser = new CheckStyleParser();
            Collection<FileAnnotation> warnings = null;
            warnings = parser.parse(read(fileName), "-");

            return Singleton.get(warnings);
        }
        catch (InvocationTargetException e) {
            throw new RuntimeException("Can't read CheckStyle file " + fileName, e);
        }
    }

    private InputStream read(final String fileName) {
        return NewWarningDetectorTest.class.getResourceAsStream(fileName);
    }

    /**
     * Verifies that the ast calculates the same hashcode. (inclusive Refactoring: Newline).
     */
    @Test
    public void testFinalClassWithNewLines() {
        checkThatHashesMatching(FINAL_CLASS, REFACTORING_NEWLINE);
    }

    /**
     * Verifies that the ast calculates the same hashcode.
     */
    @Test
    public void testNeedBracesWithNewLines() {
        checkThatHashesMatching(NEED_BRACES, REFACTORING_NEWLINE);
    }

    /**
     * Verifies that the ast calculates the same hashcode.
     */
    @Test
    public void testInterfaceIsTypeWithNewLines() {
        checkThatHashesMatching(INTERFACE_IS_TYPE, REFACTORING_NEWLINE);
    }

    /**
     * Verifies that the ast calculates the same hashcode.
     */
    @Test
    public void testExplicitInitializationWithNewLines() {
        checkThatHashesMatching(EXPLICIT_INITIALIZATION, REFACTORING_NEWLINE);
    }

    /**
     * Verifies that the ast calculates the same hashcode.
     */
    @Test
    public void testMethodNameWithNewLines() {
        checkThatHashesMatching(METHOD_NAME, REFACTORING_NEWLINE);
    }

    /**
     * Verifies that the ast calculates the same hashcode.
     */
    @Test
    public void testRedundantModifierWithNewLines() {
        checkThatHashesMatching(REDUNDANT_MODIFIER, REFACTORING_NEWLINE);
    }

    /**
     * Verifies that the ast calculates the same hashcode.
     */
    @Test
    public void testPackageNameWithNewLines() {
        checkThatHashesMatching(PACKAGE_NAME, REFACTORING_NEWLINE);
    }

    /**
     * Verifies that the ast calculates the same hashcode.
     */
    @Test
    public void testFinalClassWithRename() {
        checkThatHashesMatching(FINAL_CLASS, REFACTORING_RENAME);
    }

    /**
     * Verifies that the ast calculates the same hashcode.
     */
    @Test
    public void testNeedBracesWithRename() {
        checkThatHashesMatching(NEED_BRACES, REFACTORING_RENAME);
    }

    /**
     * Verifies that the ast calculates the same hashcode.
     */
    @Test
    public void testInterfaceIsTypeWithRename() {
        checkThatHashesMatching(INTERFACE_IS_TYPE, REFACTORING_RENAME);
    }

    /**
     * Verifies that the ast calculates the same hashcode.
     */
    @Test
    public void testExplicitInitializationWithRename() {
        checkThatHashesMatching(EXPLICIT_INITIALIZATION, REFACTORING_RENAME);
    }

    /**
     * Verifies that the ast calculates the same hashcode.
     */
    @Test
    public void testMethodNameWithRename() {
        checkThatHashesMatching(METHOD_NAME, REFACTORING_RENAME);
    }

    /**
     * Verifies that the ast calculates NOT the same hashcode.
     */
    @Test
    public void testPackageNameWithRenameHaveNotTheSameHashcode() {
        checkThatHashesNotMatching(PACKAGE_NAME, REFACTORING_RENAME);
    }

    /**
     * Verifies that the ast calculates the same hashcode.
     */
    @Test
    public void testNeedBracesWithExtractMethod() {
        checkThatHashesMatching(NEED_BRACES, "NeedBraces1", REFACTORING_EXTRACT_METHOD, true);
    }

    /**
     * Verifies that the ast calculates NOT the same hashcode.
     */
    @Test
    public void testNeedBracesWithExtractMethodHaveNotTheSameHashcode() {
        checkThatHashesMatching(NEED_BRACES, "NeedBraces2", REFACTORING_EXTRACT_METHOD, false);
    }

    /**
     * Verifies that the ast calculates NOT the same hashcode.
     */
    @Test
    public void testFinalClassWithExtractMethodHaveNotTheSameHashcode() {
        checkThatHashesMatching(FINAL_CLASS, "FinalClass1", REFACTORING_EXTRACT_METHOD, false);
    }

    /**
     * Verifies that the ast calculates NOT the same hashcode.
     */
    @Test
    public void testPackageDeclarationWithExtractMethodHaveNotTheSameHashcode() {
        checkThatHashesMatching(PACKAGE_DECLARATION, "PackageDeclaration1", REFACTORING_EXTRACT_METHOD, false);
    }

    /**
     * Verifies that the ast calculates the same hashcode.
     */
    @Test
    public void testExplicitInitializationWithExtractMethod() {
        checkThatHashesMatching(EXPLICIT_INITIALIZATION, "ExplicitInitialization2", REFACTORING_EXTRACT_METHOD, true);
    }

    /**
     * Verifies that the ast calculates the same hashcode.
     */
    @Test
    public void testMethodNameWithExtractMethod() {
        checkThatHashesMatching(METHOD_NAME, "MethodName2", REFACTORING_EXTRACT_METHOD, true);
    }

    /**
     * Verifies that the ast calculates NOT the same hashcode.
     */
    @Test
    public void testMethodNameWithExtractMethodHaveNotTheSameHashcode() {
        checkThatHashesMatching(METHOD_NAME, "MethodName2", "MethodName3", REFACTORING_EXTRACT_METHOD, false);
    }

    /**
     * Verifies that the ast calculates the same hashcode.
     */
    @Test
    public void testPackageNameWithExtractMethod() {
        checkThatHashesMatching(PACKAGE_NAME, "PackageName", "PackageName2", REFACTORING_EXTRACT_METHOD, true);
    }

    /**
     * Verifies that the ast calculates the same hashcode.
     */
    @Test
    public void testMethodNameWithPullUpMethod() {
        checkThatHashesMatching(METHOD_NAME, "MethodName1SubclassA", "MethodName1Superclass",
                REFACTORING_PULL_UP_METHOD, true);
    }

    /**
     * Verifies that the ast calculates the same hashcode.
     */
    @Test
    public void testNeedBracesWithPullUpMethod() {
        checkThatHashesMatching(NEED_BRACES, "NeedBraces3SubclassA", "NeedBraces3Superclass",
                REFACTORING_PULL_UP_METHOD, true);
    }

    /**
     * Verifies that the ast calculates NOT the same hashcode.
     */
    @Test
    public void testNeedBracesWithPullUpMethodHaveNotTheSameHashcode() {
        checkThatHashesMatching(NEED_BRACES, "NeedBraces4SubclassA", "NeedBraces4Superclass",
                REFACTORING_PULL_UP_METHOD, false);
    }

    /**
     * Verifies that the ast calculates NOT the same hashcode.
     */
    @Test
    public void testFinalClassWithPullUpMethodHaveNotTheSameHashcode() {
        checkThatHashesMatching(FINAL_CLASS, "FinalClass2SubclassA", REFACTORING_PULL_UP_METHOD, false);
    }

    /**
     * Verifies that the ast calculates NOT the same hashcode.
     */
    @Test
    public void testPackageDeclarationWithPullUpMethodHaveNotTheSameHashcode() {
        checkThatHashesMatching(PACKAGE_DECLARATION, "PackageDeclarationSubclass", REFACTORING_PULL_UP_METHOD, false);
    }

    /**
     * Verifies that the ast calculates the same hashcode.
     */
    @Test
    public void testExplicitInitializationWithPullUpMethod() {
        checkThatHashesMatching(EXPLICIT_INITIALIZATION, "ExplicitInitialization1Subclass", REFACTORING_PULL_UP_METHOD,
                true);
    }

    /**
     * Verifies that the ast calculates the same hashcode.
     */
    @Test
    public void testPackageNameWithPullUpMethod() {
        checkThatHashesMatching(PACKAGE_NAME, "PackageName1Subclass", REFACTORING_PULL_UP_METHOD, true);
    }

    /**
     * Verifies that the ast calculates the same hashcode.
     */
    @Test
    public void testMethodNameWithPushDownMethod() {
        checkThatHashesMatching(METHOD_NAME, "MethodName4Superclass", "MethodName4Subclass",
                REFACTORING_PUSH_DOWN_METHOD, true);
    }

    /**
     * Verifies that the ast calculates the same hashcode.
     */
    @Test
    public void testNeedBracesWithPushDownMethod() {
        checkThatHashesMatching(NEED_BRACES, "NeedBraces5Superclass", "NeedBraces5Subclass",
                REFACTORING_PUSH_DOWN_METHOD, true);
    }

    /**
     * Verifies that the ast calculates NOT the same hashcode.
     */
    @Test
    public void testNeedBracesWithPushDownMethodHaveNotTheSameHashcode() {
        checkThatHashesMatching(NEED_BRACES, "NeedBraces6Superclass", "NeedBraces6Subclass",
                REFACTORING_PUSH_DOWN_METHOD, false);
    }

    /**
     * Verifies that the ast calculates NOT the same hashcode.
     */
    @Test
    public void testFinalClassWithPushDownMethodHaveNotTheSameHashcode() {
        checkThatHashesMatching(FINAL_CLASS, "FinalClass3Subclass", REFACTORING_PUSH_DOWN_METHOD, false);
    }

    /**
     * Verifies that the ast calculates NOT the same hashcode.
     */
    @Test
    public void testPackageDeclarationWithPushDownMethodHaveNotTheSameHashcode() {
        checkThatHashesMatching(PACKAGE_DECLARATION, "PackageDeclaration2Subclass", REFACTORING_PUSH_DOWN_METHOD, false);
    }

    /**
     * Verifies that the ast calculates the same hashcode.
     */
    @Test
    public void testExplicitInitializationWithPushDownMethod() {
        checkThatHashesMatching(EXPLICIT_INITIALIZATION, "ExplicitInitialization3Subclass",
                REFACTORING_PUSH_DOWN_METHOD, true);
    }

    /**
     * Verifies that the ast calculates the same hashcode.
     */
    @Test
    public void testPackageNameWithPushDownMethod() {
        checkThatHashesMatching(PACKAGE_NAME, "PackageName3Superclass", REFACTORING_PUSH_DOWN_METHOD, true);
    }

    /**
     * Verifies that the ast calculates NOT the same hashcode.
     */
    @Test
    public void testFinalClassWithInlineMethodHaveNotTheSameHashcode() {
        checkThatHashesMatching(FINAL_CLASS, "FinalClass4", REFACTORING_INLINE_METHOD, false);
    }

    /**
     * Verifies that the ast calculates the same hashcode.
     */
    @Test
    public void testNeedBracesWithInlineMethod() {
        checkThatHashesMatching(NEED_BRACES, "NeedBraces7", REFACTORING_INLINE_METHOD, true);
    }

    /**
     * Verifies that the ast calculates NOT the same hashcode.
     */
    @Test
    public void testNeedBracesWithInlineMethodHaveNotTheSameHashcode() {
        checkThatHashesMatching(NEED_BRACES, "NeedBraces8", REFACTORING_INLINE_METHOD, false);
    }

    /**
     * Verifies that the ast calculates NOT the same hashcode.
     */
    @Test
    public void testPackageDeclarationWithInlineMethod() {
        checkThatHashesMatching(PACKAGE_DECLARATION, "PackageDeclaration3", REFACTORING_INLINE_METHOD, false);
    }

    /**
     * Verifies that the ast calculates the same hashcode.
     */
    @Test
    public void testExplicitInitializationWithInlineMethod() {
        checkThatHashesMatching(EXPLICIT_INITIALIZATION, "ExplicitInitialization4", REFACTORING_INLINE_METHOD, true);
    }

    /**
     * Verifies that the ast calculates the same hashcode.
     */
    @Test
    public void testMethodNameWithInlineMethod() {
        checkThatHashesMatching(METHOD_NAME, "MethodName5", REFACTORING_INLINE_METHOD, true);
    }

    /**
     * Verifies that the ast calculates NOT the same hashcode.
     */
    @Test
    public void testMethodNameWithInlineMethodHaveNotTheSameHashcode() {
        checkThatHashesMatching(METHOD_NAME, "MethodName6", REFACTORING_INLINE_METHOD, false);
    }

    /**
     * Verifies that the ast calculates the same hashcode.
     */
    @Test
    public void testPackageNameWithInlineMethod() {
        checkThatHashesMatching(PACKAGE_NAME, "PackageName4", REFACTORING_INLINE_METHOD, true);
    }


    /**
     * Verifies that the ast calculates the same hashcode.
     */
    @Test
    public void testMethodNameWithExtractConstantAsAVariable() {
        checkThatHashesMatching(METHOD_NAME, "MethodName7", REFACTORING_EXTRACT_CONSTANT, true);
    }

    /**
     * Verifies that the ast calculates the same hashcode.
     */
    @Test
    public void testMethodNameWithExtractConstantInSysout() {
        checkThatHashesMatching(METHOD_NAME, "MethodName8", REFACTORING_EXTRACT_CONSTANT, true);
    }

    /**
     * Verifies that the ast calculates the same hashcode.
     */
    @Test
    public void testPackageNameWithExtractConstant() {
        checkThatHashesMatching(PACKAGE_NAME, "PackageName5", REFACTORING_EXTRACT_CONSTANT, true);
    }

    /**
     * Verifies that the ast calculates NOT the same hashcode.
     */
    @Test
    public void testFinalClassWithExtractConstantHaveNotTheSameHashcode() {
        checkThatHashesMatching(FINAL_CLASS, "FinalClass5", REFACTORING_EXTRACT_CONSTANT, false);
    }

    /**
     * Verifies that the ast calculates the same hashcode.
     */
    @Test
    public void testNeedBracesWithExtractConstant() {
        checkThatHashesMatching(NEED_BRACES, "NeedBraces9", REFACTORING_EXTRACT_CONSTANT, true);
    }

    /**
     * Verifies that the ast calculates NOT the same hashcode.
     */
    @Test
    public void testNeedBracesWithExtractConstantHaveNotTheSameHashcode() {
        checkThatHashesMatching(NEED_BRACES, "NeedBraces10", REFACTORING_EXTRACT_CONSTANT, false);
    }

    /**
     * Verifies that the ast calculates NOT the same hashcode.
     */
    @Test
    public void testPackageDeclarationWithExtractConstantHaveNotTheSameHashcode() {
        checkThatHashesMatching(PACKAGE_DECLARATION, "PackageDeclaration4", REFACTORING_EXTRACT_CONSTANT, false);
    }

    /**
     * Verifies that the ast calculates the same hashcode.
     */
    @Test
    public void testExplicitInitializationWithExtractConstant() {
        checkThatHashesMatching(EXPLICIT_INITIALIZATION, "ExplicitInitialization5", REFACTORING_EXTRACT_CONSTANT, true);
    }

    /**
     * Shows that a previous file has the same warnings like the current file, if refactorings were realised. It means
     * that the same hashcode is calculated. (The warnings wasn't changed!)
     */
    @Test
    public void testFileWithManyWarningsHasSameHashcode() {
        evaluateHashes("MoreWarningsInClass", "MoreWarningsInClass_Refactored", 5, 5, 5);
    }

    /**
     * Shows that a previous file has more warnings than the current file, if warnings were fixed. Other new warnings
     * were added.
     */
    @Test
    public void testPreviousHasMoreWarningsThanCurrent() {
        evaluateHashes("MoreWarningsInClass2", "MoreWarningsInClass2_Refactored", 5, 4, 3);
    }

    /**
     * Shows that a previous file has less warnings than the current file, if the current files have additional
     * warnings. The difference in set theory is (current-warnings minus previous warnings), which are the new
     * warnings.
     */
    @Test
    public void testCurrentHasMoreWarningsThanPrevious() {
        evaluateHashes("MoreWarningsInClass", "MoreWarningsInClass1_Refactored", 5, 7, 5);
    }

    private void evaluateHashes(final String fileBefore, final String fileAfter, final int expectedPrev,
            final int expectedCur, final int intersection) {
        Set<String> hashSetPevious = calculateHashes(fileBefore, true);
        Set<String> hashSetCurrent = calculateHashes(fileAfter, false);

        Collection<String> intersections = CollectionUtils.intersection(hashSetCurrent, hashSetPevious);

        assertEquals("Not expected count of previous warnings", expectedPrev, hashSetPevious.size());
        assertEquals("Not expected count of current warnings", expectedCur, hashSetCurrent.size());
        assertEquals("The warnings aren't equal.", intersection, intersections.size());
    }

    private Set<String> calculateHashes(final String file, final boolean before) {
        Collection<FileAnnotation> annotations;
        String fileWithXmlExtension = file + ".xml";
        String fileWithJavaExtension = file + ".java";

        if (before) {
            annotations = parse("before/" + fileWithXmlExtension);
        }
        else {
            annotations = parse("after/" + fileWithXmlExtension);
        }

        Set<String> hashSet = new HashSet<String>();

        Ast ast;
        String hash;
        for (int i = 0; i < annotations.size(); i++) {
            ast = getAst(fileWithJavaExtension, Iterables.get(annotations, i), "", before);
            hash = ast.getDigest();
            hashSet.add(hash);
        }

        return hashSet;
    }

    private Collection<FileAnnotation> parse(final String fileName) {
        InputStream inputStream = null;
        try {
            inputStream = NewWarningDetectorTest.class.getResourceAsStream(fileName);

            return new CheckStyleParser().parse(inputStream, "empty");
        }
        catch (InvocationTargetException exception) {
            throw new RuntimeException(exception);
        }
        finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    private String matchWarningTypeToFoldername(final String warningType) {
        String ordnerName = "";
        if (Arrays.asList(CheckStyleAstFactory.getClassAst()).contains(warningType)) {
            ordnerName = CLASS_AST_FOLDERNAME;
        }
        else if (Arrays.asList(CheckStyleAstFactory.getSurroundingElementsAst()).contains(warningType)) {
            ordnerName = ENVIRONMENT_AST_FOLDERNAME;
        }
        else if (Arrays.asList(CheckStyleAstFactory.getFileAst()).contains(warningType)) {
            ordnerName = FILE_AST_FOLDERNAME;
        }
        else if (Arrays.asList(CheckStyleAstFactory.getFieldsAst()).contains(warningType)) {
            ordnerName = INSTANCEVARIABLE_AST_FOLDERNAME;
        }
        else if (Arrays.asList(CheckStyleAstFactory.getMethodAst()).contains(warningType)) {
            ordnerName = METHOD_AST_FOLDERNAME;
        }
        else if (Arrays.asList(CheckStyleAstFactory.getMethodOrClassAst()).contains(warningType)) {
            ordnerName = METHOD_OR_CLASS_AST_FOLDERNAME;
        }
        else if (Arrays.asList(CheckStyleAstFactory.getNamePackageAst()).contains(warningType)) {
            ordnerName = NAME_PACKAGE_AST_FOLDERNAME;
        }

        return ordnerName;
    }

    /**
     * Use this method for calculating the hashcode, if the filename (inclusive file extension) before is equal after,
     * except that the after-filename has only(!) a postfix of the refactoring.
     *
     * @param warningType the warningType
     * @param refactoring the refactoring
     */
    private void checkThatHashesMatching(final String warningType, final String refactoring) {
        checkThatHashesMatching(warningType, warningType, warningType, refactoring, true);
    }

    private void checkThatHashesNotMatching(final String warningType, final String refactoring) {
        checkThatHashesMatching(warningType, warningType, warningType, refactoring, false);
    }

    /**
     * Use this method for calculating the hashcode, if the filename before is different as after, disregarded that the
     * after-filename has a postfix of the refactoring.
     *
     * @param warningType           the warningType
     * @param beforeClass           the name of the class before
     * @param afterClass            the name of the class after
     * @param refactoring           the refactoring
     * @param expectedEqualHashcode <code>true</code>, if the expected hashcode is equal, otherwise <code>false</code>.
     */
    private void checkThatHashesMatching(final String warningType, final String beforeClass, final String afterClass,
            final String refactoring, final boolean expectedEqualHashcode) {
        String foldername = matchWarningTypeToFoldername(warningType);
        long hashBefore = calcHashcode(beforeClass, foldername, true);
        long hashAfter = calcHashcode(afterClass + refactoring, foldername, false);

        if (expectedEqualHashcode) {
            compareHashcode(hashBefore, hashAfter);
        }
        else {
            compareHashcodeOnNonEquality(hashBefore, hashAfter);
        }
    }

    private void checkThatHashesMatching(final String warningType, final String fileName, final String refactoring,
            final boolean expectedEqualHashcode) {
        checkThatHashesMatching(warningType, fileName, fileName, refactoring, expectedEqualHashcode);
    }

    private long calcHashcode(final String filename, final String foldername, final boolean beforeRefactoring) {
        String javaFile = filename.concat(".java");
        String xmlFile = filename.concat(".xml");

        return calcHashcode(javaFile, foldername, xmlFile, beforeRefactoring);
    }

    private void compareHashcode(final long hashBefore, final long hashAfter) {
        assertNotNull("Hash code isn't not null", hashBefore);
        assertEquals("Hash codes don't match: ", hashBefore, hashAfter);
    }

    private void compareHashcodeOnNonEquality(final long hashBefore, final long hashAfter) {
        assertNotNull("Hash code isn't not null", hashBefore);
        assertNotNull("Hash code isn't not null", hashAfter);
        assertNotEquals("Hash codes aren't different: ", hashBefore, hashAfter);
    }

    private long calcHashcode(final String javaFile, final String foldername, final String xmlFile,
            final boolean before) {
        Ast ast = getAst(javaFile, xmlFile, foldername, before);
        return ast.getContextHashCode();
    }

    private Ast getAst(final String javaFile, final String xmlFile, final String foldername, final boolean before) {
        FileAnnotation warning = readWarning(calcCorrectPath(xmlFile, foldername, before));
        return CheckStyleAstFactory.getInstance(getTempFileName(calcCorrectPath(javaFile, foldername, before)),
                warning.getType(), warning.getPrimaryLineNumber());
    }

    private Ast getAst(final String javaFile, final FileAnnotation warning, final String foldername,
            final boolean before) {
        return CheckStyleAstFactory.getInstance(getTempFileName(calcCorrectPath(javaFile, foldername, before)),
                warning.getType(), warning.getPrimaryLineNumber());
    }

    private String calcCorrectPath(final String nameOfFile, final String foldername, final boolean before) {
        StringBuilder stringBuilder = new StringBuilder();
        if (before) {
            stringBuilder.append("before/");
        }
        else {
            stringBuilder.append("after/");
        }
        stringBuilder.append(foldername);
        stringBuilder.append('/');
        stringBuilder.append(nameOfFile);

        return stringBuilder.toString();
    }
}