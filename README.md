#  Adapter RecycleView Annotation
Tạo Adapter cho RecycleView với annotation 
### Import 
Trong file gradle của module phần _repositories_ thêm maven của thư viện như sau : 
```groovy 
android {
...
    repositories {
           maven {
                url 'https://dl.bintray.com/dangngocduc/AndroidLibrary'
        }
   } 
   
 }
```

Trong  _dependencies_ của module thêm 2 dòng sau :
```groovy
annotationProcessor 'com.android.leso:processor:1.0.8'
compile 'com.android.leso:annotation:1.0.2'
```
### Cách dùng
Để tự động gen code cho Adapter cần 2 thông class :  1 là thông tin của Apdater, 2 là thông tin của các ViewHolder
#### Adapter info 
Để có thể gen code thì Adapter cần biết có những loại row nào --> tương ứng với các ViewHolder bạn phải định nghĩa.
```java
import com.annotation.AdapterRecycleview;
	...
@AdapterRecycleview(viewholder = {AddressBookItemViewHolder.class})
public class AddressBookAdapter extends AddressBookAdapter_Builder<AddressData> {
...
}
```
Như ví dụ trên Adapter chỉ có 1 loại row là _AddressBookItemViewHolder_ --> nên chỉ cần khai báo 1 ViewHolder trong annotation .
__AddressBookAdapter_Builder__ là Adapter thư viện gen ra, nó sẽ có dạng : <Ten class của ban >_Builder 

#### ViewHolder Info
1 ViewHolder sẽ cần phải có 2 thông tin cơ bản :
- layout của nó là gì ?
- Dữ liệu đầu vào thuộc loại nào
 
__chú ý__ : Viewholder bắt buộc phải có 1 method dạng : 
```java
public void bind(Context context, String content)
```
để tu viện có thể bind Data vào , nếu muốn custom bạn có thể extend lại phần bindData của Adapter do thư viện gen ra.
```java
import com.annotation.Viewholder;
		...
@Viewholder(layout = R.layout.row_hybris_address_book , data = AddressData.class)
public class AddressBookItemViewHolder extends RecyclerView.ViewHolder {
	@Bind(R.id.text_info)
    TextView mTextInfo;
    .....
	public ViewHolderTextView(View itemView) {
		super(itemView);
		ButterKnife.bind(this, view);
	}

	public void bindData(Context context, AddressData data) {
		        mTextInfo.setText(String.format("%s - %s", data.fullName, data.phone));
		}
}
```
### Sử dụng 
Với 1 AdapterInfo ( như trên là AddressBookAdapter.class) thư viện sẽ tự gen ra 1 Adapter có dạng : AddressBookAdapter_Builder với 1 số method cơ bản mà bạn hay phải code lặp lại như __getItemViewType__ , __onCreateViewHolder__, __onBindViewHolder__, __getItemCount__ Phần còn lại trong class AddressBookAdapter là phần code xử lý logic ...
```java
        AddressBookAdapter mAdapter;
        ...
        mRecyclerView.setAdapter(mAdapter);
```
dưới đây là class mà thư viện tự động gen : 
```java
package  com.leso.adapter.adapter;

import com.leso.adapter.info.*;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by @leso.
 */
public class  AddressBookAdapter_Builder<E> extends RecyclerView.Adapter {
    protected ArrayList<E> mDatas = new ArrayList<E>();

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private static ArrayList<com.leso.adapter.info.IViewHolderInfo> list = new ArrayList<>();
    static {
        list.add(new AddressBookItemViewHolderInfo());
    }
    public void addDatas(ArrayList<E> data) {
        mDatas.addAll(data);
      notifyItemRangeInserted(mDatas.size() - data.size(), data.size());
     }

    public void addData(E data) {
        mDatas.add(data);
        notifyItemInserted(mDatas.size() - 1);
    }

    public AddressBookAdapter_Builder(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getItemViewType(int position) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).invalidData(mDatas.get(position))) {
                 return i;
            }
        }
        throw new ClassCastException("Dont have Viewholder for Object at : " + position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return list.get(viewType).getViewHolder(parent, mLayoutInflater);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        list.get(getItemViewType(position)).bindData(mContext, mDatas.get(position), holder);
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }
}
```

