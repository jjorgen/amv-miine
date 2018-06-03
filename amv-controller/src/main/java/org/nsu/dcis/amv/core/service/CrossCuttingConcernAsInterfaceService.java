package org.nsu.dcis.amv.core.service;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.MethodRepresentation;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.extend.CompilationUnitWrapper;
import org.apache.log4j.Logger;
import org.nsu.dcis.amv.common.AspectMiningByCategory;
import org.nsu.dcis.amv.core.domain.CodeCloneResult;
import org.nsu.dcis.amv.core.exception.AspectCloneException;
import org.nsu.dcis.amv.core.instrumentation.AmvConfigurationInstrumentation;
import org.nsu.dcis.amv.core.util.CodeCloneMiningResult;
import org.nsu.dcis.amv.core.util.CodeCloneStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by jorgej2 on 5/21/2018.
 */
@Service
public class CrossCuttingConcernAsInterfaceService
{
    private Logger log = Logger.getLogger(getClass().getName());

    @Autowired
    AmvConfigurationInstrumentation amvConfigurationInstrumentation;

    @Autowired
    private CodeCloneMiningService codeCloneMiningService;

    public List<AspectMiningByCategory> getCrossCuttingConcerns() {
        List<AspectMiningByCategory> aspectMiningByCategoryList = new ArrayList();

        log.info("Mining for cross cutting concern as interface");

        CodeCloneMiningResult codeCloneMiningResult = getCodeCloneMiningResult();

        CodeCloneStatistics codeCloneStatistics = codeCloneMiningResult.getCodeCloneStatistics();
        log.info("Code Clone Statistics: " + codeCloneStatistics);
        aspectMiningByCategoryList.add(getBeforeAdviceCrossCuttingConcernAsInterfaceCandidates(codeCloneMiningResult.getBeforeAdviceCandidates(), getAllInterfaces()));
        aspectMiningByCategoryList.add(getAfterAdviceCrossCuttingConcernAsInterfaceCandidates(codeCloneMiningResult.getAfterAdviceCandidates(), getAllInterfaces()));
//        aspectMiningByCategoryList.add(getAroundAdviceCandidatesAsInterfaceCandidates(codeCloneMiningResult));
//        aspectMiningByCategoryList.add(getClonesAsInterface(codeCloneMiningResult));
        log.info("Returning aspectMiningByCategoryList: " + aspectMiningByCategoryList);
        return aspectMiningByCategoryList;
    }

    public CodeCloneMiningResult getCodeCloneMiningResult() {
        return codeCloneMiningService.getCodeCloneMiningResults("C:/work/0_NSU/CH/ifa",
                amvConfigurationInstrumentation.getExcludedDirectoryList(),
                amvConfigurationInstrumentation.getFileExtensions());
    }

    public AspectMiningByCategory getClonesAsInterface(CodeCloneMiningResult codeCloneMiningResult) {
        int codeCloneWithComminInterface = 0;
        List<CodeCloneResult> codeClones = codeCloneMiningResult.getClone();
        log.info("**********************************************************************************");
        log.info("The Clone Count was: " + codeCloneMiningResult.getClone().size());
        log.info("**********************************************************************************");
        for (CodeCloneResult CodeCloneResult : codeClones) {
            NodeList<ClassOrInterfaceType> firstMethodImplements = enclosingClassImplements(CodeCloneResult.getMethodPair().getFirst());
            NodeList<ClassOrInterfaceType> secondMethodImplements = enclosingClassImplements(CodeCloneResult.getMethodPair().getSecond());
            Set<String> commonInterfaceNames = getInterfacesInCommon(firstMethodImplements, secondMethodImplements);
            if (commonInterfaceNames.size() > 0) {
                ++codeCloneWithComminInterface;
                log.info("First Method Name:  " + CodeCloneResult.getMethodPair().getFirst().getFullMethodName());
                log.info("Second Method Name: " + CodeCloneResult.getMethodPair().getSecond().getFullMethodName());
                log.info("With common interface(s) found: " + commonInterfaceNames);
                log.info("**********************************************************************************");
            }
        }
        log.info("**********************************************************************************");
        log.info("The number of Code Clones with common interface was: " + codeCloneWithComminInterface);
        log.info("**********************************************************************************");
        return new AspectMiningByCategory("Cross Cutting Concern As Interface, Clone",
                codeCloneWithComminInterface,0,0);
    }

    public AspectMiningByCategory getBeforeAdviceCrossCuttingConcernAsInterfaceCandidates(
           List<CodeCloneResult> beforeAdviceCandidates, List<CompilationUnitWrapper> allInterfaces) {
        int beforeAdviceCandidatesWithComonInterface = getCommonInterfaceCount(beforeAdviceCandidates, allInterfaces);
        return new AspectMiningByCategory("Cross Cutting Concern As Interface, Before Advice",
                beforeAdviceCandidatesWithComonInterface,0,0);
    }

    public AspectMiningByCategory getAfterAdviceCrossCuttingConcernAsInterfaceCandidates(
            List<CodeCloneResult> beforeAdviceCandidates, List<CompilationUnitWrapper> allInterfaces) {
        int beforeAdviceCandidatesWithCommonInterface = getCommonInterfaceCount(beforeAdviceCandidates, allInterfaces);
        return new AspectMiningByCategory("Cross Cutting Concern As Interface, After Advice",
                beforeAdviceCandidatesWithCommonInterface,0,0);
//
//        int afterAdviceCandidatesWithComminInterface = 0;
//        List<CodeCloneResult> afterAdviceCandidates = codeCloneMiningResult.getAfterAdviceCandidates();
//        log.info("**********************************************************************************");
//        log.info("The After Advice Candidate Count was: " + codeCloneMiningResult.getAfterAdviceCandidates().size());
//        log.info("**********************************************************************************");
//
////        int beforeAdviceCandidatesWithComonInterface = getCommonInterfaceCount(beforeAdviceCandidates, allInterfaces);
////        return new AspectMiningByCategory("Cross Cutting Concern As Interface, Before Advice",
////                beforeAdviceCandidatesWithComonInterface,0,0);
//
//
//        for (CodeCloneResult codeCloneResult : afterAdviceCandidates) {
//            NodeList<ClassOrInterfaceType> firstMethodImplements = enclosingClassImplements(codeCloneResult.getMethodPair().getFirst());
//            NodeList<ClassOrInterfaceType> secondMethodImplements = enclosingClassImplements(codeCloneResult.getMethodPair().getSecond());
//            Set<String> commonInterfaceNames = getInterfacesInCommon(firstMethodImplements, secondMethodImplements);
//            if (commonInterfaceNames.size() > 0) {
//                ++afterAdviceCandidatesWithComminInterface;
//                log.info("First Method Name:  " + codeCloneResult.getMethodPair().getFirst().getFullMethodName());
//                log.info("Second Method Name: " + codeCloneResult.getMethodPair().getSecond().getFullMethodName());
//                log.info("With common interface(s) found: " + commonInterfaceNames);
//                log.info("**********************************************************************************");
//            }
//        }
//        log.info("**********************************************************************************");
//        log.info("The number of After Advice Candidates with common interface was: " + afterAdviceCandidatesWithComminInterface);
//        log.info("**********************************************************************************");
//        return new AspectMiningByCategory("Cross Cutting Concern As Interface, After Advice",
//                afterAdviceCandidatesWithComminInterface,0,0);
    }

    private int getCommonInterfaceCount(List<CodeCloneResult> adviceCandidates,
                                        List<CompilationUnitWrapper> allInterfaces) {
        int adviceCandidatesWithCommonInterface = 0;
        for (CodeCloneResult codeCloneResult : adviceCandidates) {
            Set<String> commonInterfaceNames = getInterfacesInCommon(
                enclosingClassImplements(codeCloneResult.getMethodPair().getFirst()),
                enclosingClassImplements(codeCloneResult.getMethodPair().getSecond()));

            CompilationUnitWrapper implementingInterface =
                getImplementingInterface(codeCloneResult.getMethodPair().getFirst().getMethodName(),
                codeCloneResult.getMethodPair().getSecond().getMethodName(),
                getImplementingInterfaceCandidates(commonInterfaceNames, allInterfaces));

            if (implementingInterface != null) {
                ++adviceCandidatesWithCommonInterface;
            }
        }
        return adviceCandidatesWithCommonInterface;
    }


    private List getImplementingInterfaceCandidates(Set<String> commonInterfaceNames, List<CompilationUnitWrapper> allInterfaces) {
        List implementingInterfaceCandidates = new ArrayList<CompilationUnitWrapper>();
        for (String commonInterfaceName : commonInterfaceNames) {
            for (CompilationUnitWrapper  compilationUnitWrapper : allInterfaces) {
                log.info("Class Or Interface Name: " + compilationUnitWrapper.getClassOrInterfaceName());
                if (commonInterfaceName.equals(compilationUnitWrapper.getClassOrInterfaceName())) {
                    implementingInterfaceCandidates.add(compilationUnitWrapper);
                }
            }
        }
        return implementingInterfaceCandidates;
    }

    private CompilationUnitWrapper getImplementingInterface(String firstMethod, String secondMethod, List<CompilationUnitWrapper> implementingInterfaceCandidates) {
        for (CompilationUnitWrapper compilationUnitWrapper : implementingInterfaceCandidates) {
            ArrayList<String> methodNames = compilationUnitWrapper.getMethodNames();
            if (methodNames.contains(firstMethod) && methodNames.contains(secondMethod)) {
                return compilationUnitWrapper;
            }
        }
        return null;
    }

    private String getImplementingInterface(String firstMethodName, String secondMethodName1, Set<String> commonInterfaceNames, List<CompilationUnitWrapper> allInterfaces) {
        return firstMethodName;
    }

    public AspectMiningByCategory getAroundAdviceCandidatesAsInterfaceCandidates(CodeCloneMiningResult codeCloneMiningResult) {
        int aroundAdviceCandidatesWithCommonInterface = 0;
//        List<CompilationUnitWrapper> allInterfaces = getAllInterfaces();

        log.info("**********************************************************************************");
        log.info("The Around Advice Count was: " + codeCloneMiningResult.getAroundAdviceCandidates().size());
        log.info("**********************************************************************************");
        List<CodeCloneResult> aroundAdviceCandidates = codeCloneMiningResult.getAroundAdviceCandidates();
        for (CodeCloneResult codeCloneResult : aroundAdviceCandidates) {
            NodeList<ClassOrInterfaceType> firstMethodImplements = enclosingClassImplements(codeCloneResult.getMethodPair().getFirst());
            NodeList<ClassOrInterfaceType> secondMethodImplements = enclosingClassImplements(codeCloneResult.getMethodPair().getSecond());
            Set<String> commonInterfaceNames = getInterfacesInCommon(firstMethodImplements, secondMethodImplements);
            if (commonInterfaceNames.size() > 0) {
                ++aroundAdviceCandidatesWithCommonInterface;
                log.info("First Method Name:  " + codeCloneResult.getMethodPair().getFirst().getFullMethodName());
                log.info("Second Method Name: " + codeCloneResult.getMethodPair().getSecond().getFullMethodName());
                log.info("With common interface(s) found: " + commonInterfaceNames);
                log.info("**********************************************************************************");
            }
        }
        log.info("**********************************************************************************");
        log.info("The number of After Advice Candidates with common interface was: " + aroundAdviceCandidatesWithCommonInterface);
        log.info("**********************************************************************************");
        return new AspectMiningByCategory("Cross Cutting Concern As Interface, Around Advice",
                aroundAdviceCandidatesWithCommonInterface,0,0);
    }

    private NodeList<ClassOrInterfaceType> enclosingClassImplements(MethodRepresentation firstMethodRepresentation) {
        NodeList<ClassOrInterfaceType> methodImplements = null;
        CompilationUnitWrapper compilationUnitWrapper = new CompilationUnitWrapper(firstMethodRepresentation.getFilePath());
        try {
            CompilationUnit compilationUnit = compilationUnitWrapper.getCompilationUnit(firstMethodRepresentation.getFilePath());
            NodeList<TypeDeclaration<?>> types = compilationUnit.getTypes();
            for (TypeDeclaration type : types) {
                if (type instanceof ClassOrInterfaceDeclaration && !((ClassOrInterfaceDeclaration) type).isInterface()) {
                    methodImplements = ((ClassOrInterfaceDeclaration) type).getImplements();
                }
            }
        } catch (IOException e) {
            throw new AspectCloneException("An error when attempting to extract interfaces for method", e);
        }
        return methodImplements;
    }

    private Set<String> getInterfacesInCommon(NodeList<ClassOrInterfaceType> firstMethodImplements,
                                              NodeList<ClassOrInterfaceType> secondMethodImplements) {
        Set<String> commonInterfaceNames = new HashSet<String>();
        for (ClassOrInterfaceType firstInterfaceType : firstMethodImplements) {
            String firstInterfaceName = firstInterfaceType.getName();
            for (ClassOrInterfaceType secondInterfaceType : secondMethodImplements) {
                String secondInterfaceName = secondInterfaceType.getName();
                if (secondInterfaceName.equals(firstInterfaceName)) {
                    commonInterfaceNames.add(firstInterfaceType.getName());
                }
            }
        }
        return commonInterfaceNames;
    }

    public List<CompilationUnitWrapper> getAllInterfaces() {
        List<CompilationUnitWrapper> allInterfaces = codeCloneMiningService.getAllInterfaces(
                "C:/work/0_NSU/CH/ifa",
                amvConfigurationInstrumentation.getExcludedDirectoryList(),
                amvConfigurationInstrumentation.getFileExtensions());
        return allInterfaces;
    }

}