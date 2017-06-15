package com.leso;

import com.annotation.AdapterRecycleview;
import com.annotation.Viewholder;
import com.leso.demo.annotation.AdapterRecycleviewGener;
import com.leso.demo.annotation.IViewHolderInfoGenner;
import com.leso.demo.annotation.ViewholderInfoGener;

import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.util.ElementFilter;
import javax.tools.JavaFileObject;

@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class LesoProcessor extends AbstractProcessor {

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        for (Class<? extends Annotation> annotation : getSupportedAnnotations()) {
            types.add(annotation.getCanonicalName());
        }
        return types;
    }

    private Set<Class<? extends Annotation>> getSupportedAnnotations() {
        Set<Class<? extends Annotation>> annotations = new LinkedHashSet<>();

        annotations.add(AdapterRecycleview.class);
        annotations.add(Viewholder.class);

        return annotations;
    }

    private void genAdapterRecycleView(RoundEnvironment roundEnv) {
        Set<? extends Element> annotationViewHolder = roundEnv.getElementsAnnotatedWith(AdapterRecycleview.class);
        Set<TypeElement> types = ElementFilter.typesIn(annotationViewHolder);
        for (TypeElement element : types) {

            try {
                AdapterRecycleviewGener.getJavaFile(element).writeTo(processingEnv.getFiler());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    private void genViewHolder(RoundEnvironment roundEnv) {
        try {
            IViewHolderInfoGenner.generate().writeTo(processingEnv.getFiler());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Set<? extends Element> annotationViewHolder = roundEnv.getElementsAnnotatedWith(Viewholder.class);
        Set<TypeElement> types = ElementFilter.typesIn(annotationViewHolder);

        for (TypeElement element : types) {
            try {
                ViewholderInfoGener.getJavaFile(element).writeTo(processingEnv.getFiler());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        genViewHolder(roundEnv);
        genAdapterRecycleView(roundEnv);

        return true;

    }

}
