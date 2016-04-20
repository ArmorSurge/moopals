package com.caddosierra.mafiaparty.game;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.caddosierra.mafiaparty.R;

import java.util.ArrayList;

public class ItemAdapter extends ArrayAdapter<Role>
{
    private ArrayList<Role> items;

    public ItemAdapter(Context context, int viewResourceId, ArrayList<Role> items)
    {
        super(context, viewResourceId, items);
        this.items = items;
    }

    public View getView(final int position, View convertView, ViewGroup parent)
    {
        View v = convertView;

        if(v == null)
        {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.list_item, null);
        }

        Role i = items.get(position);

        if(i != null)
        {
            TextView roleLabel = (TextView) v.findViewById(R.id.role_label);
            Button add = (Button) v.findViewById(R.id.add);

            TextView amountLabel = (TextView) v.findViewById(R.id.amount);
            Button subtract = (Button) v.findViewById(R.id.subtract);

            if(roleLabel != null)
            {
                roleLabel.setText(i.getRole());
            }
            if(amountLabel != null)
            {
                amountLabel.setText("" + i.getAmount());
            }
            if(add != null)
            {
                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        items.get(position).setAmount(items.get(position).getAmount() + 1);
                        NewGame.setItems(items);
                    }
                });
            }
            if(subtract != null)
            {
                subtract.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(items.get(position).getAmount() > 0) {
                            items.get(position).setAmount(items.get(position).getAmount() - 1);
                            NewGame.setItems(items);
                        }
                    }
                });
            }
        }

        return v;
    }
}
