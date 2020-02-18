package cn.cheney.xrouter.compiler;


import com.google.auto.service.AutoService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import cn.cheney.xrouter.annotation.Param;
import cn.cheney.xrouter.annotation.XRoute;
import cn.cheney.xrouter.compiler.generator.ModuleClassGenerator;
import cn.cheney.xrouter.compiler.generator.ParamClassGenerator;
import cn.cheney.xrouter.compiler.util.CheckUtil;
import cn.cheney.xrouter.compiler.util.Logger;
import cn.cheney.xrouter.compiler.util.TypeUtils;

@AutoService(Processor.class)
public class XRouterProcessor extends AbstractProcessor {

    private Holder holder;
    private Map<String, ModuleClassGenerator> moduleMap = new HashMap<>();
    private Map<TypeElement, List<Element>> paramsMap = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        holder = new Holder();
        holder.messager = processingEnv.getMessager();
        holder.elementUtils = processingEnv.getElementUtils();
        holder.filer = processingEnv.getFiler();
        holder.types = processingEnv.getTypeUtils();
        holder.typeUtils = new TypeUtils(holder.types, holder.elementUtils);
        Logger.init(holder.messager);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        HashSet<String> supportTypes = new LinkedHashSet<>();
        supportTypes.add(XRoute.class.getCanonicalName());
        supportTypes.add(Param.class.getCanonicalName());
        return supportTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }


    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        processRoute(roundEnvironment);
        processParams(roundEnvironment);
        return true;
    }


    private boolean checkParamRepeat(List<Element> elementList, Element element) {
        String targetName = getParamName((VariableElement) element);
        if (null == targetName) {
            return false;
        }
        for (Element elementInList : elementList) {
            String str = getParamName((VariableElement) elementInList);
            if (targetName.equals(str)) {
                return true;
            }
        }
        return false;
    }


    private String getParamName(VariableElement variableElement) {
        if (null == variableElement) {
            return null;
        }
        Param param = variableElement.getAnnotation(Param.class);
        if (null == param) {
            return null;
        }
        return param.name().isEmpty() ? variableElement.getSimpleName().toString()
                : param.name();
    }

    private void processParams(RoundEnvironment roundEnvironment) {
        Set<? extends Element> elementSet = roundEnvironment.getElementsAnnotatedWith(Param.class);
        if (null == elementSet || elementSet.isEmpty()) {
            return;
        }
        for (Element element : elementSet) {
            VariableElement variableElement = (VariableElement) element;
            TypeElement activityClass = (TypeElement) variableElement.getEnclosingElement();
            String notAllowModifier = CheckUtil.checkModifiers(variableElement.getModifiers());
            if (null != notAllowModifier) {
                Logger.e("The inject fields CAN NOT BE '" + notAllowModifier
                        + "'!!! please check field["
                        + variableElement.getSimpleName()
                        + "] in class ["
                        + activityClass.getQualifiedName() + "]");
                continue;
            }
            List<Element> paramsElements = paramsMap.get(activityClass);
            if (null == paramsElements) {
                paramsElements = new ArrayList<>();
                paramsMap.put(activityClass, paramsElements);
            }
            if (checkParamRepeat(paramsElements, element)) {
                Logger.e("The  inject  fields Repeat !!! please check field["
                        + element.getSimpleName()
                        + "] in class ["
                        + activityClass.getQualifiedName() + "]");
                return;
            }
            paramsElements.add(element);
        }
        if (paramsMap.isEmpty()) {
            return;
        }
        for (Map.Entry<TypeElement, List<Element>> entry : paramsMap.entrySet()) {
            TypeElement typeElement = entry.getKey();
            List<Element> paramsElements = entry.getValue();
            if (null == typeElement || null == paramsElements || paramsElements.isEmpty()) {
                continue;
            }
            ParamClassGenerator paramClassGenerator = new ParamClassGenerator(typeElement);
            for (Element paramElement : paramsElements) {
                paramClassGenerator.generateSeg(holder, (VariableElement) paramElement,
                        paramElement.getAnnotation(Param.class));
            }
            paramClassGenerator.generateJavaFile(holder.filer);
        }
    }


    private void processRoute(RoundEnvironment roundEnvironment) {
        Set<? extends Element> elementSet = roundEnvironment.getElementsAnnotatedWith(XRoute.class);
        if (null == elementSet || elementSet.isEmpty()) {
            return;
        }
        for (Element element : elementSet) {
            TypeElement typeElement = (TypeElement) element;
            XRoute route = element.getAnnotation(XRoute.class);
            String module = route.module();
            String path = route.path();
            ModuleClassGenerator groupClassGenerator;
            if (!moduleMap.containsKey(module)) {
                groupClassGenerator = new ModuleClassGenerator(module);
                moduleMap.put(module, groupClassGenerator);
            } else {
                groupClassGenerator = moduleMap.get(route.module());
            }
            groupClassGenerator.generateSeg(holder, typeElement, module, path);
        }
        generateFile();
    }


    private void generateFile() {
        for (Map.Entry<String, ModuleClassGenerator> entry : moduleMap.entrySet()) {
            ModuleClassGenerator groupClassGenerator = entry.getValue();
            groupClassGenerator.generateJavaFile(holder.filer);
        }
    }


    public static class Holder {
        public Messager messager;
        public Elements elementUtils;
        public Types types;
        public Filer filer;
        public TypeUtils typeUtils;
    }


}

