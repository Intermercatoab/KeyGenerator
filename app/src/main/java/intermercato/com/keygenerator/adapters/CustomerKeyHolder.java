package intermercato.com.keygenerator.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import intermercato.com.keygenerator.R;
import intermercato.com.keygenerator.models.CustomerKey;

public class CustomerKeyHolder extends RecyclerView.ViewHolder {

    public TextView scaleid,created,customerkey,key;

    public CustomerKeyHolder(View v) {
        super(v);
        scaleid = v.findViewById(R.id.txt_scaleid);
        key = v.findViewById(R.id.txt_key);
        customerkey = v.findViewById(R.id.txt_customerkey);
        created = v.findViewById(R.id.txt_created);
    }

    public void bind(CustomerKey customerKey){

        created.setText("Created "+customerKey.getCreated() );
        scaleid.setText("Scale ID: "+customerKey.getScaleId() );
        customerkey.setText("CustomerKey "+customerKey.getCustomerKey() );
        key.setText("Key "+customerKey.getKey() );


    }
}
