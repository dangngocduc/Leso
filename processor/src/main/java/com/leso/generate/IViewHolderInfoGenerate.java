package com.leso.generate;

import com.leso.C;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import javax.lang.model.element.Modifier;

/**
 * Created by DANGNGOCDUC on 6/14/2017.
 */

public class IViewHolderInfoGenerate {

    public static JavaFile generate() {
        MethodSpec invalidData = MethodSpec.methodBuilder("invalidData")
                .returns(boolean.class)
                .addParameter(TypeName.OBJECT, "object")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .build();

        MethodSpec getLayout = MethodSpec.methodBuilder("getLayout")
                .returns(int.class)
                .addModifiers(Modifier.PUBLIC,  Modifier.ABSTRACT)
                .build();

        ClassName viewgroup = ClassName.get("android.view", "ViewGroup");

        ClassName layoutInflater = ClassName.get("android.view", "LayoutInflater");

        MethodSpec getViewHolder = MethodSpec.methodBuilder("getViewHolder")
                .returns(TypeVariableName.get("T"))
                .addParameter(viewgroup, "parent")
                .addParameter(layoutInflater, "inflater")
                .addModifiers(Modifier.PUBLIC,  Modifier.ABSTRACT)
                .build();

        ClassName context = ClassName.get("android.content", "Context");
        ClassName hoverboard = ClassName.get("android.support.v7.widget", "RecyclerView","ViewHolder");
        TypeVariableName genneric = TypeVariableName.get("T", hoverboard);

        MethodSpec bindData = MethodSpec.methodBuilder("bindData")
                .returns(void.class)
                .addModifiers(Modifier.PUBLIC,  Modifier.ABSTRACT)
                .addParameter(context, "context")
                .addParameter(TypeName.OBJECT, "object")
                .addParameter(genneric, "vh")
                .build();




        TypeSpec IViewHolderInfo = TypeSpec.interfaceBuilder("IViewHolderInfo")
                .addTypeVariable(genneric)
                .addModifiers(Modifier.PUBLIC)
                .addMethod(invalidData)
                .addMethod(getLayout)
                .addJavadoc(C.author)
                .addJavadoc("\n")
                .addMethod(getViewHolder)
                .build();

        JavaFile javaFile = JavaFile.builder("com.leso.adapter.info", IViewHolderInfo)
                .build();
       return javaFile;

    }
}
