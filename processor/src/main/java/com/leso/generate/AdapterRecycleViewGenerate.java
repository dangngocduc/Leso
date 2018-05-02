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

import java.util.ArrayList;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 * Created by DANGNGOCDUC on 6/14/2017.
 */

public class AdapterRecycleViewGenerate {

    public static JavaFile getJavaFile(TypeElement element, ProcessingEnvironment processingEnvironment) {
        AdapterRecycleView adapter = element.getAnnotation(AdapterRecycleView.class);
        ClassName classNameParent = ClassName.get("android.support.v7.widget", "RecyclerView", "Adapter");
        ClassName classNameViewHolder = ClassName.get("android.support.v7.widget", "RecyclerView", "ViewHolder");
        ClassName classNameContext =  ClassName.get("android.content", "Context" );
        ClassName ClassNameLayoutInflater =  ClassName.get("android.view", "LayoutInflater" );
        ClassName classNameViewGroup =  ClassName.get("android.view", "ViewGroup" );
        ClassName classNameViewOnClick =  ClassName.get("android.view.View","OnClickListener" );
        ClassName classNameLesoViewHolder =  ClassName.get("com.leso.viewholder", "LesoViewHolder" );

        ClassName classNameIViewHolderInfo =  ClassName.get("com.leso.adapter.info", "IViewHolderInfo" );
        TypeVariableName T = TypeVariableName.get("T");
        ClassName list = ClassName.get("java.util", "ArrayList");
        TypeName listOfT= ParameterizedTypeName.get(list, T);
        TypeName listOfIViewHolderInfo= ParameterizedTypeName.get(list, classNameIViewHolderInfo);

        CodeBlock.Builder builder = CodeBlock.builder().add("mListViewHolderInfo = new ArrayList<>();\n");
        ArrayList<MethodSpec> listMethods = new ArrayList<>();

        try {
            Class[] clas =   adapter.viewholders();
        } catch (MirroredTypesException e) {
            for (TypeMirror type : e.getTypeMirrors()) {

                Element enclosingElement =    ((DeclaredType)type).asElement().getEnclosingElement();
                while ( ElementKind.PACKAGE != enclosingElement.getKind() ) {
                    enclosingElement = enclosingElement.getEnclosingElement();
                }

                builder.add("mListViewHolderInfo.add(new "+ ((TypeElement) ((DeclaredType) type).asElement()).getQualifiedName()+ "Info());\n");


                TypeMirror parrent = processingEnvironment.getTypeUtils().directSupertypes(type).get(0);
                PackageElement pkgparrent = processingEnvironment.getElementUtils().getPackageOf(processingEnvironment.getTypeUtils().asElement(parrent));

                PackageElement pkgCurrent = processingEnvironment.getElementUtils().getPackageOf(processingEnvironment.getTypeUtils().asElement(type));



                processingEnvironment.getMessager().printMessage(Diagnostic.Kind.NOTE, "Classname " + pkgparrent.getQualifiedName()+ processingEnvironment.getTypeUtils().asElement(parrent).getSimpleName().toString());

                if (( pkgparrent.getQualifiedName()+"."+ processingEnvironment.getTypeUtils().asElement(parrent).getSimpleName().toString())
                            .contentEquals("com.leso.viewholder.LesoViewHolder")) {
                    } else {

                        ClassName classNameForViewHolder = ClassName.get(pkgCurrent.getQualifiedName().toString()
                                , processingEnvironment.getTypeUtils().asElement(type).getSimpleName().toString());

                        MethodSpec.Builder builderMethod =  MethodSpec.methodBuilder("bindDataFor")
                                .returns(void.class)
                                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)

                                .addParameter(classNameForViewHolder, "holder")
                                .addParameter(int.class, "position");
                        listMethods.add(builderMethod.build());
                    }

            }
        }

        MethodSpec addDatas = MethodSpec.methodBuilder("addAll")
                .returns(void.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(listOfT, "data")
                .addStatement("mDatas.addAll(data);")
                .addStatement("notifyItemRangeInserted(mDatas.size() - data.size(), data.size());")
                .build();

        MethodSpec addData = MethodSpec.methodBuilder("add")
                .returns(void.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(T, "data")
                .addStatement("mDatas.add(data)")
                .addStatement("notifyItemInserted(mDatas.size() - 1)")
                .build();

        MethodSpec remove = MethodSpec.methodBuilder("remove")
                .returns(void.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(T, "data")
                .addStatement("int index = mDatas.indexOf(data)")
                .beginControlFlow("if (index > -1) ")
                .addStatement("mDatas.remove(index)")
                .addStatement("notifyItemInserted(mDatas.size() - 1)")
                .endControlFlow()
                .build();

        MethodSpec removeData = MethodSpec.methodBuilder("remove")
                .returns(void.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(int.class, "index")
                .beginControlFlow("if (index > -1) ")
                .addStatement("mDatas.remove(index)")
                .addStatement("notifyItemInserted(mDatas.size() - 1)")
                .endControlFlow()
                .build();

        MethodSpec get = MethodSpec.methodBuilder("get")
                .returns(T)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(int.class, "index")
                .addStatement("return mDatas.get(index)")
                .build();

        MethodSpec indexOf = MethodSpec.methodBuilder("indexOf")
                .returns(Integer.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(T, "data")
                .addStatement("return mDatas.indexOf(data)")
                .build();

        MethodSpec constructor = MethodSpec.constructorBuilder()
                .addParameter(classNameContext, "context")
                .addParameter(classNameViewOnClick, "onClick")
                .addModifiers(Modifier.PUBLIC)
                .addStatement("mContext = context")
                .addStatement("mViewOnClickCallBack = onClick")
                .addStatement("mLayoutInflater = LayoutInflater.from(mContext)")
                .build();
        MethodSpec getItemViewType = MethodSpec.methodBuilder("getItemViewType")
                .addParameter(int.class, "position")
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
                .addParameter(int.class, "viewType")
                .addModifiers(Modifier.PUBLIC)
                .returns(classNameViewHolder)
                .addStatement("return mListViewHolderInfo.get(viewType).getViewHolder(parent, mLayoutInflater)")
                .addAnnotation(Override.class)
                .build();

        MethodSpec.Builder onBindViewHolderBuilder = MethodSpec.methodBuilder("onBindViewHolder")
                .addParameter(classNameViewHolder, "holder")
                .addParameter(TypeName.INT, "position")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(void.class)
                .beginControlFlow("if (holder instanceof com.leso.viewholder.LesoViewHolder)")
                .addStatement("mListViewHolderInfo.get(getItemViewType(position)).bindData(mContext, mDatas.get(position), mViewOnClickCallBack, holder)")
                .endControlFlow();

        if (listMethods.size() > 0) {
            onBindViewHolderBuilder.beginControlFlow("else")
                    .addStatement("bindViewHolderNormal(holder, position)")
                    .endControlFlow();
        }

        MethodSpec onBindViewHolder = onBindViewHolderBuilder.build();

        MethodSpec getItemCount = MethodSpec.methodBuilder("getItemCount")
                .addModifiers(Modifier.PUBLIC)
                .returns(int.class)
                .addStatement("return mDatas.size()")
                .addAnnotation(Override.class)
                .build();


        TypeSpec.Builder recycleviewAdapterBuilder = TypeSpec.classBuilder(element.getSimpleName().toString() + "_Builder")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addTypeVariable(T)
                .superclass(classNameParent)
                .addField(FieldSpec.builder(listOfT, "mDatas", Modifier.PROTECTED).initializer("new  $T()", listOfT).build())
                .addField(FieldSpec.builder(classNameContext, "mContext", Modifier.PROTECTED).build())
                .addField(FieldSpec.builder(classNameViewOnClick, "mViewOnClickCallBack", Modifier.PROTECTED).build())
                .addField(FieldSpec.builder(ClassNameLayoutInflater, "mLayoutInflater", Modifier.PROTECTED).build())
                .addField(FieldSpec.builder(listOfIViewHolderInfo, "mListViewHolderInfo", Modifier.PRIVATE).build())
                .addInitializerBlock(builder.build())
                .addMethod(constructor)
                .addMethod(addDatas)
                .addMethod(addData)
                .addMethod(remove)
                .addMethod(removeData)
                .addMethod(get)
                .addMethod(indexOf)
                .addMethod(getItemViewType)
                .addMethod(onCreateViewHolder)
                .addMethod(onBindViewHolder)
                .addMethod(getItemCount);

        if (listMethods.size() > 0) {

            MethodSpec bindViewHolderNormal = MethodSpec.methodBuilder("bindViewHolderNormal")
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .returns(void.class)
                    .addParameter(classNameViewHolder, "holder")
                    .addParameter(int.class, "position")
                    .build();

            recycleviewAdapterBuilder.addMethod(bindViewHolderNormal);
        }


            Element enclosingElement = element.getEnclosingElement();
            while ( ElementKind.PACKAGE != enclosingElement.getKind() ) {
                enclosingElement = enclosingElement.getEnclosingElement();
            }
            PackageElement packageElement = (PackageElement) enclosingElement;

        return JavaFile.builder(packageElement.getQualifiedName().toString(), recycleviewAdapterBuilder.build()).build();

    }
}
