/*
 * Copyright 2016 Realm Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package intermercato.com.keygenerator.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashSet;

import java.util.Set;

import intermercato.com.keygenerator.R;
import intermercato.com.keygenerator.models.CustomerKey;
import io.realm.OrderedRealmCollection;

import io.realm.RealmRecyclerViewAdapter;


public class mValidateRecyclerViewAdapter extends RealmRecyclerViewAdapter<CustomerKey,CustomerKeyHolder > {

    private BankOnClickCallBack listener;
    private boolean inDeletionMode = false;
    private Set<Integer> countersToDelete = new HashSet<>();
    private ConstraintLayout.LayoutParams params;
    private double maxWeightVal;
    private int bankWidth;
    private int bankHeight;
    private boolean showHideBankArrow = false;
    private Context context;


    public mValidateRecyclerViewAdapter(OrderedRealmCollection<CustomerKey> data, Context c) {
        super(data, true);
        // setMaxWeightVal();

        // Only set this if the model class has a primary key that is also a integer or long.
        // In that case, {@code getItemId(int)} must also be overridden to return the key.
        // See https://developer.android.com/reference/android/support/v7/widget/RecyclerView.Adapter.html#hasStableIds()
        // See https://developer.android.com/reference/android/support/v7/widget/RecyclerView.Adapter.html#getItemId(int)
        setHasStableIds(false);
        context = c;

    }

    public void setLayoutParamsForHolder(ConstraintLayout.LayoutParams p, int w, int h) {
        params = p;
        bankWidth = w;
        bankHeight = h;
    }

    public void setClickListener(BankOnClickCallBack l) {
        listener = l;

    }

    public void setShowBankArrow(boolean s) {
        showHideBankArrow = s;
    }

    void enableDeletionMode(boolean enabled) {
        inDeletionMode = enabled;
        if (!enabled) {
            countersToDelete.clear();
        }
        notifyDataSetChanged();
    }



    @Override
    public CustomerKeyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d("ADAPTER", "onCreateViewHolder ");
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.customerkey_row, parent, false);
        //itemView.setLayoutParams(params);

        return new CustomerKeyHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerKeyHolder holder, int position) {
        Log.d("ADAPTER", "onBindViewHolder ");
        CustomerKey ck = getItem(position);
        Log.d("ADAPTER", "ck "+ck.getScaleId() );

        holder.itemView.setOnClickListener(v -> {
            listener.onClick(ck.getScaleId());
        });

        holder.bind(getItem(position));
    }

  /*  @Override
    public void onBindViewHolder(BankHolder holder, int position) {
        final Bank b = getItem(position);
        holder.bind(b, maxWeightVal, bankWidth, bankHeight);
        //Log.d("ADAPTER", "onBindViewHolder " + b.isActive());
        if (showHideBankArrow)
            holder.arrow.setVisibility(b.isActive() == true ? View.VISIBLE : View.INVISIBLE);

        if(b.isActive() && b.getTotalweight()>0){
            holder.activePlate.setBackgroundColor(context.getResources().getColor(R.color.bg_10_transparent*//*,context.getTheme()*//*));
            setColor(holder.holder);
        }else {
            holder.activePlate.setBackgroundColor(context.getResources().getColor(R.color.bg_transparent*//*,context.getTheme()*//*));
        }

        holder.itemView.setLayoutParams(params);

        holder.itemView.setOnClickListener(v -> {

            listener.onClick(b.getId());

            if (showHideBankArrow) {
                for (Bank a : getData()) {

                    //Log.d("ADAPTER", "in  ---> " + a.isActive());
                    if (b.getId().equals(a.getId())) {
                        holder.arrow.setVisibility(View.VISIBLE);
                        dataHandler(a, true);
                    } else {
                        dataHandler(a, false);
                    }
                }
                notifyItemChanged(position);
            }
        });

    }*/



    @Override
    public long getItemId(int index) {
        //noinspection ConstantConditions
        return 0;//getItem(index).getId();
    }


    //holder.data = obj;
    //final String itemId = obj.getId();
    //noinspection ConstantConditions
    //holder.ttitle.setText(obj.getBankName());
    //holder.deletedCheckBox.setChecked(countersToDelete.contains(itemId));
/*        if (inDeletionMode) {
            holder.deletedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        countersToDelete.add(itemId);
                    } else {
                        countersToDelete.remove(itemId);
                    }
                }
            });
        } else {holder.deletedCheckBox.setOnCheckedChangeListener(null);
        }*/
    //holder.deletedCheckBox.setVisibility(inDeletionMode ? View.VISIBLE : View.GONE);


}
