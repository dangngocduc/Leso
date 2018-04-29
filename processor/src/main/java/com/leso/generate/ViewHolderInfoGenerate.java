package com.leso.generate;

import com.annotation.ViewHolder;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;

/**
 * Created by DANGNGOCDUC on 6/15/2017.
 */

public class ViewHolderInfoGenerate {

    public static JavaFile getJavaFile(TypeElement element) {

        ViewHolder holder = element.getAnnotation(ViewHolder.class);

        ClassName context =  ClassName.get("android.content", "Context" );
        ClassName layoutInflater =  ClassName.get("android.view", "LayoutInflater" );
        ClassName viewgroup =  ClassName.get("android.view", "ViewGroup" );
        ClassName view =  ClassName.get("android.view", "View" );
        ClassName iviewholderInfo = ClassName.get("com.leso.adapter.info", "IViewHolderInfo");
        Element enclosingElement = element.getEnclosingElement();
        while ( ElementKind.PACKAGE != enclosingElement.getKind() ) {
            enclosingElement = enclosingElement.getEnclosingElement();
        }
        PackageElement packageElement = (PackageElement) enclosingElement;
        ClassName viewhoder = ClassName.get(packageElement.getQualifiedName().toString(), element.getSimpleName().toString());

        TypeName genneric = ParameterizedTypeName.get(iviewholderInfo, viewhoder );

        MethodSpec invalidData = MethodSpec.methodBuilder("invalidData")
                .returns(boolean.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(TypeName.OBJECT, "object")
                .beginControlFlow("if (object instanceof "+ getData(holder)+")")
                .addStatement("return true")
                .endControlFlow()
                .beginControlFlow("else")
                .addStatement(" return false")
                .endControlFlow()
                .addAnnotation(Override.class)
                .build();

        MethodSpec getLayout = MethodSpec.methodBuilder("getLayout")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(int.class)
                .addStatement("return " + String.format("%#010x", holder.layout()))
                .build();

        MethodSpec getViewHolder = MethodSpec.methodBuilder("getViewHolder")
                .returns(viewhoder)
                .addParameter(viewgroup, "parent")
                .addParameter(layoutInflater, "inflater")
                .addStatement("$T view = inflater.inflate(getLayout(), parent, false)", view)
                .addStatement("$T vh = new $T(view)", viewhoder, viewhoder)
                .addStatement("return vh")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .build();

        MethodSpec bindData = MethodSpec.methodBuilder("bindData")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(void.class)
                .addParameter(context, "context")
                .addParameter(TypeName.OBJECT, "data")
                .addParameter(viewhoder, "vh")
                .addStatement("vh.bindData(context, ("+getData(holder)+" ) data)")
                .build();

        TypeVariableName T = TypeVariableName.get("T", viewhoder);



        TypeSpec viewholderinfo = TypeSpec.classBuilder(element.getSimpleName().toString()+"Info")
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(genneric)
                .addMethod(invalidData)
                .addMethod(getLayout)
                .addMethod(getViewHolder)
                .build();




        return JavaFile.builder(packageElement.getQualifiedName().toString(), viewholderinfo).build();


    }

    private static String getData(ViewHolder holder) {
        try {
            return holder.data().getCanonicalName();
        } catch (MirroredTypeException e) {
            return e.getTypeMirror().toString();
        }
    }

}
