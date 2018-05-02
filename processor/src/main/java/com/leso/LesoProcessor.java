package com.leso;

import com.annotation.AdapterRecycleView;
import com.annotation.ViewHolder;
import com.leso.generate.AdapterRecycleViewGenerate;
import com.leso.generate.IViewHolderInfoGenerate;
import com.leso.generate.ViewHolderInfoGenerate;


import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;

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

        annotations.add(AdapterRecycleView.class);
        annotations.add(ViewHolder.class);

        return annotations;
    }

    private void genAdapterRecycleView(RoundEnvironment roundEnv) {
        Set<? extends Element> annotationViewHolder = roundEnv.getElementsAnnotatedWith(AdapterRecycleView.class);
        Set<TypeElement> types = ElementFilter.typesIn(annotationViewHolder);
        for (TypeElement element : types) {

            try {
                AdapterRecycleViewGenerate.getJavaFile(element, processingEnv).writeTo(processingEnv.getFiler());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    private void genViewHolder(RoundEnvironment roundEnv) {
        try {
            IViewHolderInfoGenerate.generate().writeTo(processingEnv.getFiler());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Set<? extends Element> annotationViewHolder = roundEnv.getElementsAnnotatedWith(ViewHolder.class);
        Set<TypeElement> types = ElementFilter.typesIn(annotationViewHolder);

        for (TypeElement element : types) {
            try {
                ViewHolderInfoGenerate.getJavaFile(element).writeTo(processingEnv.getFiler());
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
