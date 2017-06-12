#  Adapter RecycleView Annotation
 Create Adapter for RecycleView with Annotation.
### setup 
in your file gradle  : 
```groovy 
android {
...
    repositories {
           maven {
                url 'https://dl.bintray.com/dangngocduc/maven'
        }
   } 
   
 }
```

add line below  to  _dependencies_
```groovy
compile 'android.dangngocduc:viewpagervertical:2.1.0'
```
### Usage
#### Create Adapter info 
```java
		import com.annotation.AdapterRecycleview;
		...
		@AdapterRecycleview(viewholder = {ViewHolderTextView.class})
		public class Home {}
```

```java
		import com.annotation.Viewholder;
		...
		@Viewholder(layout = R.layout.row_text , data = String.class)
		public class ViewHolderTextView extends RecyclerView.ViewHolder {
			public TextView mText;
			public ViewHolderTextView(View itemView) {
				super(itemView);
				mText = (TextView) itemView.findViewById(R.id.text_view);
			}

			public void bind(Context context, String content) {
				mText.setText(content);
			}
		}
```
### Using 
with a Adapter info (Home ), processor with auto gen a AdapterRecycleview Home_Builder (extends RecyclerView.Adapter)
```java
        Home_Builder mAdapter;
        ...
        mRecyclerView.setAdapter(mAdapter);
```
To custom Adapter, you can create Adapter which extend from  <Your Info>_Builder to customize.

