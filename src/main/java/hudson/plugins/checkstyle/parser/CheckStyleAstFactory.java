package hudson.plugins.checkstyle.parser;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import hudson.plugins.ast.factory.Ast;
import hudson.plugins.ast.specific.ClassAst;
import hudson.plugins.ast.specific.FileAst;
import hudson.plugins.ast.specific.FieldsAst;
import hudson.plugins.ast.specific.MethodAst;
import hudson.plugins.ast.specific.MethodOrClassAst;
import hudson.plugins.ast.specific.NamePackageAst;
import hudson.plugins.ast.specific.SurroundingElementsAst;

/**
 * Factory to pick the best AST implementation based on the type of the CheckStyle warning.
 *
 * @author Christian Möstl
 */
// CHECKSTYLE:COUPLING-OFF
public final class CheckStyleAstFactory {
    private static final String[] METHOD_AST = new String[]{"AnonInnerLength", "CovariantEquals", "EqualsAvoidNull",
            "EqualsHashCode", "HiddenField", "JavadocMethod", "MethodLength", "MethodName", "MethodParamPad",
            "MethodTypeParameterName", "ParameterAssignment", "ParameterName", "ParameterNumber", "SuperClone",
            "SuperFinalize", "ThrowsCount"};
    private static final String[] SURROUNDING_ELEMENTS_AST = new String[]{"ArrayTypeStyle", "AvoidNestedBlocks",
            "BooleanExpressionComplexity", "DefaultComesLast", "EmptyBlock", "EmptyForIteratorPad", "EmptyStatement",
            "FallThrough", "GenericWhitespace", "IllegalCatch", "IllegalThrows", "InnerAssignment", "JavaNCSS",
            "LeftCurly", "LocalFinalVariableName", "LocalVariableName", "MissingSwitchDefault",
            "ModifiedControlVariable", "MultipleVariableDeclarations", "NeedBraces", "NestedForDepth", "NestedIfDepth",
            "NestedTryDepth", "NoWhitespaceAfter", "NoWhitespaceBefore", "NPathComplexity", "OneStatementPerLine",
            "OperatorWrap", "ParenPad", "RightCurly", "SimplifyBooleanExpression", "SimplifyBooleanReturn",
            "StringLiteralEquality", "TypecastParenPad", "UnnecessaryParentheses", "UpperEll", "WhitespaceAfter",
            "WhitespaceAround"};
    private static final String[] FILE_AST = new String[]{"ClassDataAbstractionCoupling", "ClassFanOutComplexity",
            "FileLength", "IllegalImport", "InterfaceIsType", "OuterTypeFilename", "PackageDeclaration"};
    private static final String[] CLASS_AST = new String[]{"ClassTypeParameterName", "FinalClass",
            "HideUtilityClassConstructor", "InnerTypeLast", "JavadocType", "JUnitTestCase", "MultipleStringLiterals",
            "MutableException", "TypeName"};
    private static final String[] METHOD_OR_CLASS_AST = new String[]{"AnnotationUseStyle", "FileTabCharacter",
            "JavadocStyle", "MissingDeprecated", "ModifierOrder", "RedundantModifier", "VisibilityModifier"};
    private static final String[] FIELDS_AST = new String[]{"ConstantName", "ExplicitInitialization",
            "JavadocVariable", "MemberName", "StaticVariableName"};
    private static final String[] NAME_PACKAGE_AST = new String[]{"PackageName"};

    /**
     * Returns an instance of an {@link Ast} specific for the specified warning type.
     *
     * @param filename    the file name of the Java class that contains the warning
     * @param warningType the type of the warning
     * @param lineNumber  the primary line number of the warning
     * @return the specific ast
     */
    @SuppressWarnings("PMD.CyclomaticComplexity")
    public static Ast getInstance(final String filename, final String warningType, final int lineNumber) {
        Ast ast;
        String checkstyleModuleName = StringUtils.removeEnd(warningType, "Check");

        if (Arrays.asList(METHOD_AST).contains(checkstyleModuleName)) {
            ast = new MethodAst(filename, lineNumber);
        }
        else if (Arrays.asList(SURROUNDING_ELEMENTS_AST).contains(checkstyleModuleName)) {
            ast = new SurroundingElementsAst(filename, lineNumber, 3);
        }
        else if (Arrays.asList(FILE_AST).contains(checkstyleModuleName)) {
            ast = new FileAst(filename, lineNumber);
        }
        else if (Arrays.asList(CLASS_AST).contains(checkstyleModuleName)) {
            ast = new ClassAst(filename, lineNumber);
        }
        else if (Arrays.asList(METHOD_OR_CLASS_AST).contains(checkstyleModuleName)) {
            ast = new MethodOrClassAst(filename, lineNumber);
        }
        else if (Arrays.asList(FIELDS_AST).contains(checkstyleModuleName)) {
            ast = new FieldsAst(filename, lineNumber);
        }
        else if (Arrays.asList(NAME_PACKAGE_AST).contains(checkstyleModuleName)) {
            ast = new NamePackageAst(filename, lineNumber);
        }
        else {
            ast = new SurroundingElementsAst(filename, lineNumber, 3);
        }

        return ast;
    }

    static String[] getMethodAst() {
        return METHOD_AST;
    }

    static String[] getSurroundingElementsAst() {
        return SURROUNDING_ELEMENTS_AST;
    }

    static String[] getFileAst() {
        return FILE_AST;
    }

    static String[] getClassAst() {
        return CLASS_AST;
    }

    static String[] getMethodOrClassAst() {
        return METHOD_OR_CLASS_AST;
    }

    static String[] getNamePackageAst() {
        return NAME_PACKAGE_AST;
    }

    static String[] getFieldsAst() {
        return FIELDS_AST;
    }

    private CheckStyleAstFactory() {
        // prevents instantiation
    }
}