package com.leso;

import com.annotation.AdapterRecycleview;
import com.annotation.Viewholder;

import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;
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
            AdapterRecycleview adapter = element.getAnnotation(AdapterRecycleview.class);

            StringBuilder builder = new StringBuilder();
            builder.append("package  com.leso.adapter.adapter;\n\n");
            builder.append("import com.leso.adapter.info.*;\n");
            builder.append("import android.content.Context;\n");
            builder.append("import android.support.v7.widget.RecyclerView;\n");
            builder.append("import android.util.SparseIntArray;\n");
            builder.append("import android.view.LayoutInflater;\n");
            builder.append("import android.view.ViewGroup;\n\n");
            builder.append("import java.util.ArrayList;\n\n");
            builder.append("/**\n * Created by @leso.\n */\n");
            builder.append("public class  " + element.getSimpleName().toString() + "_Builder<E> extends RecyclerView.Adapter {\n");
            builder.append("    protected ArrayList<E> mDatas = new ArrayList<E>();\n\n");
            builder.append("    private Context mContext;\n");
            builder.append("    private LayoutInflater mLayoutInflater;\n");
            builder.append("    private static ArrayList<com.leso.adapter.info.IViewHolderInfo> list = new ArrayList<>();\n");
            builder.append("    static {\n");
            try {
              Class[] clas =   adapter.viewholder();
            } catch (MirroredTypesException e) {
                for (TypeMirror type : e.getTypeMirrors()) {

                    builder.append("        list.add(new "+  ((DeclaredType)type).asElement().getSimpleName()+"Info());\n");
                }
            }
            builder.append("    }\n");

            builder.append("    public void addDatas(ArrayList<E> data) {\n        mDatas.addAll(data);\n      notifyItemRangeInserted(mDatas.size() - data.size(), data.size());\n     }\n\n");
            builder.append("    public void addData(E data) {\n");
            builder.append("        mDatas.add(data);\n");
            builder.append("        notifyItemInserted(mDatas.size() - 1);\n");
            builder.append("    }\n\n");
            builder.append("    public " + element.getSimpleName().toString() + "_Builder(Context context) {\n");
            builder.append("        mContext = context;\n");
            builder.append("        mLayoutInflater = LayoutInflater.from(mContext);\n");
            builder.append("    }\n\n");
            builder.append("    @Override\n");
            builder.append("    public int getItemViewType(int position) {\n");
            builder.append("        for (int i = 0; i < list.size(); i++) {\n");
            builder.append("            if (list.get(i).invalidData(mDatas.get(position))) {\n");
            builder.append("                 return i;\n");
            builder.append("            }\n");
            builder.append("        }\n");
            builder.append("        throw new ClassCastException(\"Dont have Viewholder for Object at : \" + position);\n");
            builder.append("    }\n\n");
            builder.append("    @Override\n");
            builder.append("    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {\n");
            builder.append("        return list.get(viewType).getViewHolder(parent, mLayoutInflater);\n");
            builder.append("    }\n\n");
            builder.append("    @Override\n");
            builder.append("    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {\n");
            builder.append("        list.get(getItemViewType(position)).bindData(mContext, mDatas.get(position), holder);\n");
            builder.append("    }\n\n");
            builder.append("    @Override\n");
            builder.append("    public int getItemCount() {\n");
            builder.append("        return mDatas.size();\n");
            builder.append("    }\n");
            builder.append("}");


            JavaFileObject javaFileObject = null;
            try {
                javaFileObject = processingEnv.getFiler().createSourceFile("com.leso.adapter.adapter." + element.getSimpleName().toString() + "_Builder");
                Writer writer = javaFileObject.openWriter();
                writer.write(builder.toString());
                writer.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    private void genViewHolder(RoundEnvironment roundEnv) {
        Set<? extends Element> annotationViewHolder = roundEnv.getElementsAnnotatedWith(Viewholder.class);
        Set<TypeElement> types = ElementFilter.typesIn(annotationViewHolder);

        StringBuilder builder = new StringBuilder();

        builder.append("package  com.leso.adapter.info;\n");
        builder.append("import android.support.v7.widget.RecyclerView;\n");
        builder.append("import android.view.LayoutInflater;\n");
        builder.append("import android.content.Context;\n");
        builder.append("import android.view.ViewGroup;\n\n");

        builder.append("/**\n * Created by @leso.\n */\n");
        builder.append("public interface IViewHolderInfo<X extends RecyclerView.ViewHolder> {\n\n");
        builder.append("    boolean invalidData(Object object);\n\n");
        builder.append("    int getLayout();\n\n");
        builder.append("    X getViewHolder(ViewGroup parent, LayoutInflater inflater);\n\n");
        builder.append("    void bindData(Context context, Object data, X  vh);\n\n");
        builder.append("}");
        JavaFileObject javaFileObject = null;
        try {
            javaFileObject = processingEnv.getFiler().createSourceFile("com.leso.adapter.info.IViewHolderInfo");
            Writer writer = javaFileObject.openWriter();
            writer.write(builder.toString());
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        for (TypeElement element : types) {

            builder = new StringBuilder();
            builder.append("package  com.leso.adapter.info;\n\n");

            Viewholder holder = element.getAnnotation(Viewholder.class);
            try {
                String pkg = holder.data().getSimpleName();
            } catch (MirroredTypeException e) {
                builder.append("import " + e.getTypeMirror().toString() + ";\n");
            }
            builder.append("import " + element.getQualifiedName()+";\n");
            builder.append("import  android.view.LayoutInflater;\n");
            builder.append("import android.view.View;\n");
            builder.append("import android.view.ViewGroup;\n");
            builder.append("import android.content.Context;\n");
            try {
                String pkg = holder.data().getCanonicalName();
            } catch (MirroredTypeException e) {


                builder.append("public class  " + element.getSimpleName().toString() + "Info implements IViewHolderInfo<" + element.getSimpleName().toString()+"> {\n");
                builder.append("    @Override\n");
                builder.append("    public  boolean invalidData(Object object) {\n");
                builder.append("        if (object instanceof " + e.getTypeMirror().toString()+") {\n");
                builder.append("            return true;\n");
                builder.append("        } else {\n");
                builder.append("            return false;\n");
                builder.append("            }\n");
                builder.append("    }\n\n");
                builder.append("    @Override\n");
                builder.append("    public int getLayout() {\n");
                String x = String.format("%#010x", holder.layout());
                builder.append("        return " + x +";\n");
                builder.append("    }\n\n");
                builder.append("    @Override\n");
                builder.append("    public " + element.getSimpleName().toString() + " getViewHolder(ViewGroup parent, LayoutInflater inflater) {\n");
                builder.append("        View view = inflater.inflate(getLayout(), parent, false);\n");
                builder.append("        " + element.getSimpleName().toString() + " vh = new " + element.getSimpleName().toString() +" (view);\n");
                builder.append("        return vh;\n");
                builder.append("    }\n\n");
                builder.append("    @Override\n");
                builder.append("    public void bindData(Context context, Object data, " + element.getSimpleName().toString()+" vh){\n");
                builder.append("        vh.bindData(context, ("+ e.getTypeMirror().toString()+" ) data);\n");
                builder.append("    }\n\n");
                builder.append("}");
            }



            javaFileObject = null;
            try {
                javaFileObject = processingEnv.getFiler().createSourceFile("com.leso.adapter.info." + element.getSimpleName().toString()+"Info");
                Writer writer = javaFileObject.openWriter();
                writer.write(builder.toString());
                writer.close();

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

    public void genClasApdater(TypeElement element) {
        StringBuilder builder = new StringBuilder();
        AdapterRecycleview data = element.getAnnotation(AdapterRecycleview.class);

        builder.append("package  com.leso.adapter;\n\n");
        builder.append("import android.support.v7.widget.RecyclerView;\n");
        builder.append("import android.content.Context;\n");
        try {
            int x = data.viewholder().length;

        } catch (MirroredTypesException e) {
            for (int i = 0; i < e.getTypeMirrors().size(); i++) {
                builder.append("import " + e.getTypeMirrors().get(i).toString() + ";\n");
            }
        }

        builder.append("import android.view.LayoutInflater;\n\n\n");
        builder.append("public class Adapter_" + element.getSimpleName().toString() + "  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {\n");
        builder.append("    private Context mContext;\n");
        builder.append("    private LayoutInflater mlayoutInflater;\n");
        builder.append("    public Adapter_" + element.getSimpleName().toString() + "(Context context) {\n");
        builder.append("        mContext = context;\n");
        builder.append("        mlayoutInflater = LayoutInflater.from(mContext);\n");

        builder.append("    }\n");

        builder.append("}");

        List<VariableElement> list = ElementFilter.fieldsIn(element.getEnclosedElements());


        for (VariableElement var : list) {
        }


        JavaFileObject javaFileObject = null;
        try {
            javaFileObject = processingEnv.getFiler().createSourceFile("com.leso.adapter.Adapter_" + element.getSimpleName().toString());
            Writer writer = javaFileObject.openWriter();
            writer.write(builder.toString());
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
