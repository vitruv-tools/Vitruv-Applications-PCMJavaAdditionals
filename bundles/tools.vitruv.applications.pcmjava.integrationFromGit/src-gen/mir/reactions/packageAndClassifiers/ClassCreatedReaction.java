package mir.reactions.packageAndClassifiers;

import mir.routines.packageAndClassifiers.RoutinesFacade;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.xbase.lib.Extension;
import org.emftext.language.java.containers.CompilationUnit;
import tools.vitruv.applications.util.temporary.java.JavaContainerAndClassifierUtil;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractReactionRealization;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;
import tools.vitruv.framework.change.echange.EChange;
import tools.vitruv.framework.change.echange.feature.reference.InsertEReference;

@SuppressWarnings("all")
public class ClassCreatedReaction extends AbstractReactionRealization {
  private InsertEReference<CompilationUnit, org.emftext.language.java.classifiers.Class> insertChange;
  
  private int currentlyMatchedChange;
  
  public ClassCreatedReaction(final RoutinesFacade routinesFacade) {
    super(routinesFacade);
  }
  
  public void executeReaction(final EChange change) {
    if (!checkPrecondition(change)) {
    	return;
    }
    org.emftext.language.java.containers.CompilationUnit affectedEObject = insertChange.getAffectedEObject();
    EReference affectedFeature = insertChange.getAffectedFeature();
    org.emftext.language.java.classifiers.Class newValue = insertChange.getNewValue();
    int index = insertChange.getIndex();
    				
    getLogger().trace("Passed complete precondition check of Reaction " + this.getClass().getName());
    				
    mir.reactions.packageAndClassifiers.ClassCreatedReaction.ActionUserExecution userExecution = new mir.reactions.packageAndClassifiers.ClassCreatedReaction.ActionUserExecution(this.executionState, this);
    userExecution.callRoutine1(insertChange, affectedEObject, affectedFeature, newValue, index, this.getRoutinesFacade());
    
    resetChanges();
  }
  
  private void resetChanges() {
    insertChange = null;
    currentlyMatchedChange = 0;
  }
  
  public boolean checkPrecondition(final EChange change) {
    if (currentlyMatchedChange == 0) {
    	if (!matchInsertChange(change)) {
    		resetChanges();
    		return false;
    	} else {
    		currentlyMatchedChange++;
    	}
    }
    
    return true;
  }
  
  private boolean matchInsertChange(final EChange change) {
    if (change instanceof InsertEReference<?, ?>) {
    	InsertEReference<org.emftext.language.java.containers.CompilationUnit, org.emftext.language.java.classifiers.Class> _localTypedChange = (InsertEReference<org.emftext.language.java.containers.CompilationUnit, org.emftext.language.java.classifiers.Class>) change;
    	if (!(_localTypedChange.getAffectedEObject() instanceof org.emftext.language.java.containers.CompilationUnit)) {
    		return false;
    	}
    	if (!_localTypedChange.getAffectedFeature().getName().equals("classifiers")) {
    		return false;
    	}
    	if (!(_localTypedChange.getNewValue() instanceof org.emftext.language.java.classifiers.Class)) {
    		return false;
    	}
    	this.insertChange = (InsertEReference<org.emftext.language.java.containers.CompilationUnit, org.emftext.language.java.classifiers.Class>) change;
    	return true;
    }
    
    return false;
  }
  
  private static class ActionUserExecution extends AbstractRepairRoutineRealization.UserExecution {
    public ActionUserExecution(final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy) {
      super(reactionExecutionState);
    }
    
    public void callRoutine1(final InsertEReference insertChange, final CompilationUnit affectedEObject, final EReference affectedFeature, final org.emftext.language.java.classifiers.Class newValue, final int index, @Extension final RoutinesFacade _routinesFacade) {
      final org.emftext.language.java.containers.Package javaPackage = JavaContainerAndClassifierUtil.getContainingPackageFromCorrespondenceModel(newValue, 
        this.correspondenceModel);
      _routinesFacade.classMapping(newValue, affectedEObject, javaPackage);
    }
  }
}
