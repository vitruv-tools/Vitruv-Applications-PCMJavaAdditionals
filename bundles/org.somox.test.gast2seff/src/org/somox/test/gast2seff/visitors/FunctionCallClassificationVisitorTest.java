package org.somox.test.gast2seff.visitors;

import java.util.BitSet;
import java.util.List;
import java.util.Map;

import org.emftext.language.java.commons.Commentable;
import org.emftext.language.java.statements.Condition;
import org.emftext.language.java.statements.DoWhileLoop;
import org.emftext.language.java.statements.ForEachLoop;
import org.emftext.language.java.statements.ForLoop;
import org.emftext.language.java.statements.SwitchCase;
import org.emftext.language.java.statements.TryBlock;
import org.emftext.language.java.statements.WhileLoop;
import org.junit.Test;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.somox.gast2seff.visitors.BasicFunctionClassificationStrategy;
import org.somox.gast2seff.visitors.FunctionCallClassificationVisitor;
import org.somox.gast2seff.visitors.FunctionCallClassificationVisitor.FunctionCallType;
import org.somox.gast2seff.visitors.IFunctionClassificationStrategy;
import org.somox.gast2seff.visitors.MethodCallFinder;

public class FunctionCallClassificationVisitorTest extends JaMoPP2SEFFBaseTest {

    @Test
    public void testExternalSimpleMethodAnnotations() {
        this.testSimpleMethodAnnotation(REQUIRED_COMPONENT_NAME, TEST_DO_EXTERNAL_CALL, 1, FunctionCallType.EXTERNAL);
    }

    @Test
    public void testLibrarySimpleMethodAnnotation() {
        this.testSimpleMethodAnnotation(REQUIRED_COMPONENT_NAME, TEST_DO_LIBRARY_CALL, 1, FunctionCallType.LIBRARY);
    }

    @Test
    public void testInternalSimpleMethodAnnotation() {
        this.testSimpleMethodAnnotation(REQUIRED_COMPONENT_NAME, TEST_DO_INTERNAL_CALL, 1, FunctionCallType.INTERNAL);
    }

    @Test
    public void testDoExternalCallViaInterface() {
        final String methodName = getTestMethodName();
        this.executeTest(methodName, Commentable.class, FunctionCallType.EXTERNAL);
    }

    @Test
    public void testDoExternalCallWithSimpleParametersAndReturnTypeViaInterface() {
        final String methodName = getTestMethodName();
        this.executeTest(methodName, Commentable.class, FunctionCallType.EXTERNAL);
    }

    @Test
    public void testConditionWithExternalCallInIf() {
        final String methodName = getTestMethodName();
        this.testConditionMethod(methodName, FunctionCallType.EXTERNAL, FunctionCallType.INTERNAL);
    }

    @Test
    public void testConditionWithExternalCallInElse() {
        final String methodName = getTestMethodName();
        this.testConditionMethod(methodName, FunctionCallType.INTERNAL, FunctionCallType.EXTERNAL);
    }

    @Test
    public void testConditionWithExternalCallInIfAndElse() {
        final String methodName = getTestMethodName();
        this.testConditionMethod(methodName, FunctionCallType.EXTERNAL);
    }

    // @Test
    public void testConditionWithExternalCallInCondition() {
        final String methodName = getTestMethodName();
        this.testConditionMethod(methodName, FunctionCallType.EXTERNAL, FunctionCallType.INTERNAL);
    }

    // @Test
    public void testConditionWithLibraryCallInCondition() {
        final String methodName = getTestMethodName();
        this.testConditionMethod(methodName, FunctionCallType.LIBRARY, FunctionCallType.INTERNAL);
    }

    @Test
    public void testForLoopWithExternalCall() {
        this.testForLoopMethod(getTestMethodName(), FunctionCallType.EXTERNAL);
    }

    @Test
    public void testForLoopWithInternalCall() {
        this.testForLoopMethod(getTestMethodName(), FunctionCallType.INTERNAL);
    }

    @Test
    public void testForLoopWithInternalLibraryAndExternalCall() {
        this.testForLoopMethod(getTestMethodName(), FunctionCallType.INTERNAL, FunctionCallType.LIBRARY,
                FunctionCallType.EXTERNAL);
    }

    @Test
    public void testWhileLoopWithExternalCall() {
        this.executeTest(getTestMethodName(), WhileLoop.class, FunctionCallType.EXTERNAL);
    }

    @Test
    public void testDoWhileLoopWithExternalCall() {
        this.executeTest(getTestMethodName(), DoWhileLoop.class, FunctionCallType.EXTERNAL);
    }

    @Test
    public void testForEachLoopWithExternalCall() {
        this.executeTest(getTestMethodName(), ForEachLoop.class, FunctionCallType.EXTERNAL);
    }

    @Test
    public void testTryBlockWithExternalCallInTry() {
        this.testTryBlock(getTestMethodName(), FunctionCallType.EXTERNAL);
    }

    @Test
    public void testTryBlockWithExternalCallInCatch() {
        this.testTryBlock(getTestMethodName(), FunctionCallType.EXTERNAL);
    }

    @Test
    public void testTryBlockWithExternalCallInFinally() {
        this.testTryBlock(getTestMethodName(), FunctionCallType.EXTERNAL);
    }

    @Test
    public void testTryBlockWithInternalCallInTryLibraryCallInCatchAndExternalCallInFinally() {
        this.testTryBlock(getTestMethodName(), FunctionCallType.INTERNAL, FunctionCallType.LIBRARY,
                FunctionCallType.EXTERNAL);
    }

    @Test
    public void testSwitchCaseWithExternalCallInFirstCase() {
        this.testSwitchCaseMethod(getTestMethodName(), FunctionCallType.EXTERNAL);
    }

    public void testSwitchCaseWithExternalCallInFirstAndSecondCase() {
        this.testSwitchCaseMethod(getTestMethodName(), FunctionCallType.EXTERNAL);
    }

    @Test
    public void testSwitchCaseWithInternalCallInCaseAndExternalCallDefault() {
        this.testSwitchCaseMethod(getTestMethodName(), FunctionCallType.INTERNAL, FunctionCallType.EXTERNAL);
    }

    @Test
    public void testSimpleStatement() {
        this.executeTest(getTestMethodName(), WhileLoop.class, FunctionCallType.INTERNAL);
    }

    private void testTryBlock(final String methodName, final FunctionCallType... expectedFunctionCallTypes) {
        this.executeTest(methodName, TryBlock.class, expectedFunctionCallTypes);
    }

    private void testForLoopMethod(final String methodName, final FunctionCallType... expectedFunctionCallTypes) {
        this.executeTest(methodName, ForLoop.class, expectedFunctionCallTypes);
    }

    private void testSwitchCaseMethod(final String methodName, final FunctionCallType... expectedFunctionCallTypes) {
        this.executeTest(methodName, SwitchCase.class, expectedFunctionCallTypes);
    }

    private void testConditionMethod(final String methodName, final FunctionCallType... expectedTypesForCondition) {
        this.executeTest(methodName, Condition.class, expectedTypesForCondition);
    }

    private void executeTest(final String methodName, final Class<?> resultClassType,
            final FunctionCallType... expectedTypes) {
        final MethodFunctionCallClassificationVisitorPair pair = this
                .initializeComponentMethodAndFunctionCallClassificationVisitor(REQUIRED_COMPONENT_NAME, methodName);

        // do the test
        pair.functionCallClassificationVisitor.doSwitch(pair.method);
        final Map<Commentable, List<BitSet>> annotations = pair.functionCallClassificationVisitor.getAnnotations();

        assertBitSetsForType(annotations, resultClassType, expectedTypes);
    }

    private void testSimpleMethodAnnotation(final String componentName, final String methodName, final int expectedSize,
            final FunctionCallType expectedFuctionCallType) {
        // initialize
        final MethodFunctionCallClassificationVisitorPair pair = this
                .initializeComponentMethodAndFunctionCallClassificationVisitor(componentName, methodName);

        // test the switch
        pair.functionCallClassificationVisitor.doSwitch(pair.method);
        final Map<Commentable, List<BitSet>> annotations = pair.functionCallClassificationVisitor.getAnnotations();

        // assert: annotations should contain only one statement that is an external call
        assertBitSetsForType(annotations, Commentable.class, expectedFuctionCallType);
    }

    private MethodFunctionCallClassificationVisitorPair initializeComponentMethodAndFunctionCallClassificationVisitor(
            final String componentName, final String methodName) {
        final MethodFunctionCallClassificationVisitorPair pair = new MethodFunctionCallClassificationVisitorPair();
        final BasicComponent basicComponent = (BasicComponent) this.findComponentInPCMRepo(componentName);
        final MethodCallFinder methodCallFinder = new MethodCallFinder();
        final IFunctionClassificationStrategy strategy = new BasicFunctionClassificationStrategy(
                this.sourceCodeDecorator, basicComponent, compilationUnits, methodCallFinder);
        pair.functionCallClassificationVisitor = new FunctionCallClassificationVisitor(strategy, methodCallFinder);
        pair.method = this.findMethodInClassifier(methodName, componentName + "Impl");
        return pair;
    }

}
