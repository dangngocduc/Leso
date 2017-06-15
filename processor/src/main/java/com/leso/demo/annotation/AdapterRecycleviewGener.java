package com.leso.demo.annotation;

import com.annotation.AdapterRecycleview;
import com.squareup.javapoet.AnnotationSpec;
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

public class AdapterRecycleviewGener {

    public static JavaFile getJavaFile(TypeElement element) {
        AdapterRecycleview adapter = element.getAnnotation(AdapterRecycleview.class);
        ClassName supper = ClassName.get("android.support.v7.widget", "RecyclerView", "Adapter");
        ClassName viewholder = ClassName.get("android.support.v7.widget", "RecyclerView", "ViewHolder");

        ClassName context =  ClassName.get("android.content", "Context" );
        ClassName layoutInflater =  ClassName.get("android.view", "LayoutInflater" );
        ClassName viewgroup =  ClassName.get("android.view", "ViewGroup" );
        ClassName IViewHolderInfo =  ClassName.get("com.leso.adapter.info", "IViewHolderInfo" );
        TypeVariableName T = TypeVariableName.get("T");
        ClassName list = ClassName.get("java.util", "ArrayList");
        ClassName arrayList = ClassName.get("java.util", "ArrayList");
        TypeName listOfT= ParameterizedTypeName.get(list, T);
        TypeName listOfIViewHolderInfo= ParameterizedTypeName.get(list, IViewHolderInfo);

        CodeBlock.Builder builder = CodeBlock.builder().add("mlistViewHolderInfo = new ArrayList<>();\n");

        try {
            Class[] clas =   adapter.viewholder();
        } catch (MirroredTypesException e) {
            for (TypeMirror type : e.getTypeMirrors()) {

                Element enclosingElement =    ((DeclaredType)type).asElement().getEnclosingElement();
                while ( ElementKind.PACKAGE != enclosingElement.getKind() ) {
                    enclosingElement = enclosingElement.getEnclosingElement();
                }
                PackageElement packageElement = (PackageElement) enclosingElement;

               // ClassName typeInfo =  ClassName.get(packageElement.getQualifiedName().toString(), ((TypeElement) ((DeclaredType) type).asElement()).getQualifiedName()+ "Info" );

                builder.add("mlistViewHolderInfo.add(new "+ ((TypeElement) ((DeclaredType) type).asElement()).getQualifiedName()+ "Info());\n");
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
                .addParameter(context, "context")
                .addModifiers(Modifier.PUBLIC)
                .addStatement("mContext = context")
                .addStatement("mLayoutInflater = LayoutInflater.from(mContext)")
                .build();
        MethodSpec getItemViewType = MethodSpec.methodBuilder("getItemViewType")
                .addParameter(TypeName.INT, "position")
                .addModifiers(Modifier.PUBLIC)
                .returns(int.class)
                .beginControlFlow("for (int i = 0; i < mlistViewHolderInfo.size(); i++)")
                .beginControlFlow("if (mlistViewHolderInfo.get(i).invalidData(mDatas.get(position)))")
                .addStatement(" return i")
                .endControlFlow()
                .endControlFlow()
                .addStatement("throw new ClassCastException(\"Dont have Viewholder for Object at : \" + position)")
                .addAnnotation(Override.class)
                .build();

        MethodSpec onCreateViewHolder = MethodSpec.methodBuilder("onCreateViewHolder")
                .addParameter(viewgroup, "parent")
                .addParameter(TypeName.INT, "viewType")
                .addModifiers(Modifier.PUBLIC)
                .returns(viewholder)
                .addStatement("return mlistViewHolderInfo.get(viewType).getViewHolder(parent, mLayoutInflater)")
                .addAnnotation(Override.class)
                .build();

        MethodSpec onBindViewHolder = MethodSpec.methodBuilder("onBindViewHolder")
                .addParameter(viewholder, "holder")
                .addParameter(TypeName.INT, "position")
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addStatement("mlistViewHolderInfo.get(getItemViewType(position)).bindData(mContext, mDatas.get(position), holder)")
                .addAnnotation(Override.class)
                .build();

        MethodSpec getItemCount = MethodSpec.methodBuilder("getItemCount")
                .addModifiers(Modifier.PUBLIC)
                .returns(int.class)
                .addStatement("return mDatas.size()")
                .addAnnotation(Override.class)
                .build();


        TypeSpec recycleviewAdapter = TypeSpec.classBuilder(element.getSimpleName().toString() + "_Builder")
                .addModifiers(Modifier.PUBLIC)
                .addTypeVariable(T)
                .superclass(supper)
                .addField(FieldSpec.builder(listOfT, "mDatas", Modifier.PROTECTED).initializer("new  $T()", listOfT).build())
                .addField(FieldSpec.builder(context, "mContext", Modifier.PROTECTED).build())
                .addField(FieldSpec.builder(layoutInflater, "mLayoutInflater", Modifier.PROTECTED).build())
                .addField(FieldSpec.builder(listOfIViewHolderInfo, "mlistViewHolderInfo", Modifier.PRIVATE).build())
                .addInitializerBlock(builder.build())
                .addMethod(constructor)
                .addMethod(addDatas)
                .addMethod(addData)
                .addMethod(getItemViewType)
                .addMethod(onCreateViewHolder)
                .addMethod(onBindViewHolder)
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
