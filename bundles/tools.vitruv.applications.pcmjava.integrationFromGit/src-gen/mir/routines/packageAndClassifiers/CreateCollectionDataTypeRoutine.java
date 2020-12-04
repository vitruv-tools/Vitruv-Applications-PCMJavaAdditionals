package mir.routines.packageAndClassifiers;

import java.io.IOException;
import mir.routines.packageAndClassifiers.RoutinesFacade;
import org.eclipse.xtext.xbase.lib.Extension;
import org.emftext.language.java.containers.CompilationUnit;
import org.palladiosimulator.pcm.repository.CollectionDataType;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;

@SuppressWarnings("all")
public class CreateCollectionDataTypeRoutine extends AbstractRepairRoutineRealization {
  private CreateCollectionDataTypeRoutine.ActionUserExecution userExecution;
  
  private static class ActionUserExecution extends AbstractRepairRoutineRealization.UserExecution {
    public ActionUserExecution(final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy) {
      super(reactionExecutionState);
    }
    
    public void updatePcmCollectionDataTypeElement(final org.emftext.language.java.classifiers.Class javaClass, final CompilationUnit compilationUnit, final CollectionDataType pcmCollectionDataType) {
      pcmCollectionDataType.setEntityName(javaClass.getName());
    }
    
    public void callRoutine1(final org.emftext.language.java.classifiers.Class javaClass, final CompilationUnit compilationUnit, final CollectionDataType pcmCollectionDataType, @Extension final RoutinesFacade _routinesFacade) {
      _routinesFacade.addDataTypeCorrespondence(javaClass, compilationUnit, pcmCollectionDataType);
      _routinesFacade.addDataTypeInRepository(pcmCollectionDataType);
    }
  }
  
  public CreateCollectionDataTypeRoutine(final RoutinesFacade routinesFacade, final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy, final org.emftext.language.java.classifiers.Class javaClass, final CompilationUnit compilationUnit) {
    super(routinesFacade, reactionExecutionState, calledBy);
    this.userExecution = new mir.routines.packageAndClassifiers.CreateCollectionDataTypeRoutine.ActionUserExecution(getExecutionState(), this);
    this.javaClass = javaClass;this.compilationUnit = compilationUnit;
  }
  
  private org.emftext.language.java.classifiers.Class javaClass;
  
  private CompilationUnit compilationUnit;
  
  protected boolean executeRoutine() throws IOException {
    getLogger().debug("Called routine CreateCollectionDataTypeRoutine with input:");
    getLogger().debug("   javaClass: " + this.javaClass);
    getLogger().debug("   compilationUnit: " + this.compilationUnit);
    
    org.palladiosimulator.pcm.repository.CollectionDataType pcmCollectionDataType = org.palladiosimulator.pcm.repository.impl.RepositoryFactoryImpl.eINSTANCE.createCollectionDataType();
    notifyObjectCreated(pcmCollectionDataType);
    userExecution.updatePcmCollectionDataTypeElement(javaClass, compilationUnit, pcmCollectionDataType);
    
    userExecution.callRoutine1(javaClass, compilationUnit, pcmCollectionDataType, this.getRoutinesFacade());
    
    postprocessElements();
    
    return true;
  }
}
