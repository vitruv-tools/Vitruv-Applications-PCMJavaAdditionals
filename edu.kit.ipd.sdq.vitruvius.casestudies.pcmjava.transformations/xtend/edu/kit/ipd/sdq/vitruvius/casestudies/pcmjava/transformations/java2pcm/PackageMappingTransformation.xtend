package edu.kit.ipd.sdq.vitruvius.casestudies.pcmjava.transformations.java2pcm

import edu.kit.ipd.sdq.vitruvius.casestudies.pcmjava.transformations.EObjectMappingTransformation
import edu.kit.ipd.sdq.vitruvius.framework.contracts.datatypes.CorrespondenceInstance
import org.eclipse.emf.ecore.EAttribute
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.EReference
import org.eclipse.emf.ecore.util.EcoreUtil
import org.emftext.language.java.containers.Package
import de.uka.ipd.sdq.pcm.repository.Repository
import de.uka.ipd.sdq.pcm.repository.RepositoryFactory
import edu.kit.ipd.sdq.vitruvius.framework.meta.correspondence.EObjectCorrespondence
import edu.kit.ipd.sdq.vitruvius.framework.meta.correspondence.CorrespondenceFactory
import de.uka.ipd.sdq.pcm.repository.BasicComponent
import org.apache.log4j.Logger

class PackageMappingTransformation extends EObjectMappingTransformation {
	
	private static val Logger logger = Logger.getLogger(PackageMappingTransformation.simpleName)
	
	private var boolean correspondenceRepositoryAlreadyExists;
	private var Repository repository
	
	override getClassOfMappedEObject() {
		return Package
	}
	
	/**
	 * override setCorrespondenceInstance:
	 * Check whether there already exists a repository in the correspondences.
	 * If yes set correspondenceRepositoryAlreadyExists to true otherwise to false.
	 * If a repository exists we do not have to create a new one in addEObject
	 */
	override void setCorrespondenceInstance(CorrespondenceInstance correspondenceInstance) {
		super.setCorrespondenceInstance(correspondenceInstance)
		val repositorys = correspondenceInstance.getAllEObjectsInCorrespondencesWithType(Repository)
		if(null == repositorys || 0 == repositorys.size){
			correspondenceRepositoryAlreadyExists = false
		}else{
			if(1 != repositorys.size){
				logger.warn("more than one repositorys exists in correspondence model. Should not happen. " + repositorys)	
			}
			repository = repositorys.iterator.next
			correspondenceRepositoryAlreadyExists = true
		}		
	}
	
	/**
	 * when a package is added there following possibilities exists:
	 * i) it is a package corresponding to a basic component --> create PCM basic component
	 * ii) it is a package corresponding to a composite component --> create PCM composite component
	 * iii) it is a package corresponding to a system --> create PCM system  
	 * iv) it is the root package and the package where all interfaces and datatypes should be stored --> create PCM repository
	 * v) none of the above --> do nothing
	 * 
	 * Case iv) occurs when no package and no repository exist yet--> c
	 * 			an be determined automatically (see correspondenceRepositoryAlreadyExists)
	 * Whether it is case i), ii) or iii) can not be decided automatically --> ask user
	 * 
	 * Since we do not have no interface to ask the user we currently do the following:
	 * We assume a new package (which is not the root package) corresponds to a PCM basic component (case i)).
	 * Packages that are not architectural relevant can not be created.
	 * Packages that represent a system or a composite component have to be created using the PCM
	 */
	override addEObject(EObject eObject) {
		val Package jaMoPPPackage = eObject as Package
		if(!correspondenceRepositoryAlreadyExists){
			// iv) first package is created --> it is the corresponding package to the repository
			repository = RepositoryFactory.eINSTANCE.createRepository
			repository.setEntityName(jaMoPPPackage.name)
			var EObjectCorrespondence repo2Package = CorrespondenceFactory.eINSTANCE.createEObjectCorrespondence
			repo2Package.setElementA(repository)
			repo2Package.setElementB(jaMoPPPackage)
			correspondenceInstance.addSameTypeCorrespondence(repo2Package) 
			correspondenceRepositoryAlreadyExists = true
			return repository.toArray
		}
		// case i)
		var BasicComponent basicComponent = RepositoryFactory.eINSTANCE.createBasicComponent
		basicComponent.setEntityName(jaMoPPPackage.name)
		basicComponent.setRepository__RepositoryComponent(repository)
		repository.components__Repository.add(basicComponent)
		var EObjectCorrespondence basicComponent2Package = CorrespondenceFactory.eINSTANCE.createEObjectCorrespondence
		basicComponent2Package.setElementA(basicComponent)
		basicComponent2Package.setElementB(jaMoPPPackage)
		basicComponent2Package.setParent(correspondenceInstance.claimUniqueOrNullCorrespondenceForEObject(repository))
		correspondenceInstance.addSameTypeCorrespondence(basicComponent2Package)
		return basicComponent.toArray
	}
	
	/**
	 * When a package is removed all classes in the packages are removed as well.
	 * Hence, we remove all corresponding objects (which theoretically could be the whole repository if the main
	 * package is removed.
	 */
	override removeEObject(EObject eObject) {
		val Package jaMoPPPackage = eObject as Package
		try{
			val correspondingObjects = correspondenceInstance.claimCorrespondingEObjects(jaMoPPPackage)
			for(correspondingObject : correspondingObjects){
				EcoreUtil.remove(correspondingObject)
				correspondenceInstance.removeAllDependingCorrespondences(correspondingObject)
			}
		}catch(RuntimeException rte){
			logger.info(rte)
		}
		return null
	}
	
	override updateEAttribute(EObject eObject, EAttribute affectedAttribute, Object newValue) {
		// we do not have any corresponding features because the name of the Component corresponds to the 
		// name of the class not the interface
		/*val Package jaMoPPPackage = eObject as Package
		val correspondingEObjects = correspondenceInstance.claimCorrespondingEObjects(jaMoPPPackage)
		val EStructuralFeature esf = featureCorrespondenceMap.claimValueForKey(affectedAttribute)
		for(correspondingEObject : correspondingEObjects){
			correspondingEObject.eSet(esf, newValue)
		}
		return correspondingEObjects.iterator.next*/
		return null
	}
	
	/**
	 * not needed for package
	 */
	override updateEReference(EObject eObject, EReference affectedEReference, Object newValue) {
//		throw new RuntimeException("updateEReference should not be called for " + 
//			PackageMappingTransformation.simpleName + " eObject: " + eObject
//		);
	}
	
	/**
	 * not needed for package
	 */
	override updateEContainmentReference(EObject eObject, EReference afffectedEReference, Object newValue) {
//		throw new RuntimeException("updateEContainmentReference should not be called for " + 
//			PackageMappingTransformation.simpleName + " eObject: " + eObject
//		);
	}
	
	override setCorrespondenceForFeatures() {
		// we do not have any corresponding features because the name of the Component corresponds to the 
		// name of the class not the interface
		/*var packageNameAttribute = ContainersFactory.eINSTANCE.createPackage.eClass.getEAllAttributes.filter[attribute|
			attribute.name.equalsIgnoreCase(JaMoPPPCMNamespace.JAMOPP_ATTRIBUTE_NAME)].iterator.next
		var opInterfaceNameAttribute = RepositoryFactory.eINSTANCE.createOperationInterface.eClass.getEAllAttributes.filter[attribute|
			attribute.name.equalsIgnoreCase(JaMoPPPCMNamespace.PCM_ATTRIBUTE_ENTITY_NAME)].iterator.next
		featureCorrespondenceMap.put(packageNameAttribute, opInterfaceNameAttribute)*/
	}
	
}