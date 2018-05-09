
# Installation
Add this to your root build.gradle file under repositories

```groovy
allprojects {
    repositories {
        maven {
            url "https://dl.bintray.com/dangngocduc/AndroidLibrary"
        }
    }
}
```

Add this to your app level build.gradle as dependency

```groovy
    compile 'com.android.leso:leso:1.1.7'
    annotationProcessor 'com.android.leso:processor:1.2.0'
```
with kotlin
```groovy
    apply plugin: 'kotlin-kapt'
    ....
    dependencies {
        compile 'com.android.leso:leso:1.1.7'
        kapt 'com.android.leso:processor:1.2.0'
    }
```

# Implementation
### Step 1 : Create ViewHolder for RecycleView
```java
@ViewHolder(layout = R.layout.row_simple_title2, data = Title2::class)
class ViewHolderSimple2(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bindData(context: Context, title: Title2) {
        itemView.title.text = title.titlte
        itemView.subTitle.text = title.subTitle
    }
}
```

```java
@ViewHolder(layout = R.layout.row_simple_title, data = Title::class)
class ViewHolderSimpleTitle(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bindData(context: Context, title: Title) {

        itemView.title.text = title.titlte
    }
}
```

### Step 2 : Create Adapter RecycleView
```java
@AdapterRecycleView(viewholders = [(ViewHolderSimpleTitle::class), (ViewHolderSimple2::class)])
class HomeAdapter(var context :Context) : HomeAdapter_Builder<Any>(context)  {
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is ViewHolderSimpleTitle -> {
                holder.bindData(context, mDatas[position] as Title)
            }
            is ViewHolderSimple2 -> {
                holder.bindData(context, mDatas[position] as Title2)
            }
        }

    }
}
```
