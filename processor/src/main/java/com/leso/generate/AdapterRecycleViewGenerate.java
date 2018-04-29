package com.leso.generate;

import com.annotation.AdapterRecycleView;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
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
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;

/**
 * Created by DANGNGOCDUC on 6/14/2017.
 */

public class AdapterRecycleViewGenerate {

    public static JavaFile getJavaFile(TypeElement element) {
        AdapterRecycleView adapter = element.getAnnotation(AdapterRecycleView.class);
        ClassName classNameParent = ClassName.get("android.support.v7.widget", "RecyclerView", "Adapter");
        ClassName classNameViewHolder = ClassName.get("android.support.v7.widget", "RecyclerView", "ViewHolder");
        ClassName classNameContext =  ClassName.get("android.content", "Context" );
        ClassName ClassNameLayoutInflater =  ClassName.get("android.view", "LayoutInflater" );
        ClassName classNameViewGroup =  ClassName.get("android.view", "ViewGroup" );
        ClassName classNameIViewHolderInfo =  ClassName.get("com.leso.adapter.info", "IViewHolderInfo" );
        TypeVariableName T = TypeVariableName.get("T");
        ClassName list = ClassName.get("java.util", "ArrayList");
        TypeName listOfT= ParameterizedTypeName.get(list, T);
        TypeName listOfIViewHolderInfo= ParameterizedTypeName.get(list, classNameIViewHolderInfo);

        CodeBlock.Builder builder = CodeBlock.builder().add("mListViewHolderInfo = new ArrayList<>();\n");

        try {
            Class[] clas =   adapter.viewholders();
        } catch (MirroredTypesException e) {
            for (TypeMirror type : e.getTypeMirrors()) {

                Element enclosingElement =    ((DeclaredType)type).asElement().getEnclosingElement();
                while ( ElementKind.PACKAGE != enclosingElement.getKind() ) {
                    enclosingElement = enclosingElement.getEnclosingElement();
                }

                builder.add("mListViewHolderInfo.add(new "+ ((TypeElement) ((DeclaredType) type).asElement()).getQualifiedName()+ "Info());\n");
            }
        }

        MethodSpec addDatas = MethodSpec.methodBuilder("addDatas")
                .returns(void.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(listOfT, "data")
                .addStatement("mDatas.addAll(data);")
                .addStatement("notifyItemRangeInserted(mDatas.size() - data.size(), data.size());")
                .build();

        MethodSpec addData = MethodSpec.methodBuilder("addData")
                .returns(void.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(T, "data")
                .addStatement("mDatas.add(data)")
                .addStatement("notifyItemInserted(mDatas.size() - 1)")
                .build();

        MethodSpec constructor = MethodSpec.constructorBuilder()
                .addParameter(classNameContext, "context")
                .addModifiers(Modifier.PUBLIC)
                .addStatement("mContext = context")
                .addStatement("mLayoutInflater = LayoutInflater.from(mContext)")
                .build();
        MethodSpec getItemViewType = MethodSpec.methodBuilder("getItemViewType")
                .addParameter(TypeName.INT, "position")
                .addModifiers(Modifier.PUBLIC)
                .returns(int.class)
                .beginControlFlow("for (int i = 0; i < mListViewHolderInfo.size(); i++)")
                .beginControlFlow("if (mListViewHolderInfo.get(i).invalidData(mDatas.get(position)))")
                .addStatement(" return i")
                .endControlFlow()
                .endControlFlow()
                .addStatement("throw new ClassCastException(\"Dont have Viewholder for Object at : \" + position)")
                .addAnnotation(Override.class)
                .build();

        MethodSpec onCreateViewHolder = MethodSpec.methodBuilder("onCreateViewHolder")
                .addParameter(classNameViewGroup, "parent")
                .addParameter(TypeName.INT, "viewType")
                .addModifiers(Modifier.PUBLIC)
                .returns(classNameViewHolder)
                .addStatement("return mListViewHolderInfo.get(viewType).getViewHolder(parent, mLayoutInflater)")
                .addAnnotation(Override.class)
                .build();

        MethodSpec onBindViewHolder = MethodSpec.methodBuilder("onBindViewHolder")
                .addParameter(classNameViewHolder, "holder")
                .addParameter(TypeName.INT, "position")
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addStatement("mListViewHolderInfo.get(getItemViewType(position)).bindData(mContext, mDatas.get(position), holder)")
                .addAnnotation(Override.class)
                .build();

        MethodSpec getItemCount = MethodSpec.methodBuilder("getItemCount")
                .addModifiers(Modifier.PUBLIC)
                .returns(int.class)
                .addStatement("return mDatas.size()")
                .addAnnotation(Override.class)
                .build();


        TypeSpec recycleviewAdapter = TypeSpec.classBuilder(element.getSimpleName().toString() + "_Builder")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addTypeVariable(T)
                .superclass(classNameParent)
                .addField(FieldSpec.builder(listOfT, "mDatas", Modifier.PROTECTED).initializer("new  $T()", listOfT).build())
                .addField(FieldSpec.builder(classNameContext, "mContext", Modifier.PROTECTED).build())
                .addField(FieldSpec.builder(ClassNameLayoutInflater, "mLayoutInflater", Modifier.PROTECTED).build())
                .addField(FieldSpec.builder(listOfIViewHolderInfo, "mListViewHolderInfo", Modifier.PRIVATE).build())
                .addInitializerBlock(builder.build())
                .addMethod(constructor)
                .addMethod(addDatas)
                .addMethod(addData)
                .addMethod(getItemViewType)
                .addMethod(onCreateViewHolder)
                .addMethod(getItemCount)
                .build();


            Element enclosingElement = element.getEnclosingElement();
            while ( ElementKind.PACKAGE != enclosingElement.getKind() ) {
                enclosingElement = enclosingElement.getEnclosingElement();
            }
            PackageElement packageElement = (PackageElement) enclosingElement;

        return JavaFile.builder(packageElement.getQualifiedName().toString(), recycleviewAdapter).build();

    }
}
