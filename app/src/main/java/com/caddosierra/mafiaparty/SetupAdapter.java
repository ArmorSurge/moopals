package com.caddosierra.mafiaparty;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.caddosierra.mafiaparty.game.Setup;

import java.util.ArrayList;


public class SetupAdapter extends ArrayAdapter<Setup> {
    private ArrayList<Setup> setups;

    public SetupAdapter(Context context, int viewResourceId, ArrayList<Setup> setups)
    {
        super(context, viewResourceId, setups);
        this.setups = setups;
    }

    public View getView(final int position, View convertView, ViewGroup parent)
    {
        View v = convertView;

        if(v == null)
        {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.setup_item, null);
        }

        Setup i = setups.get(position);
        if(i != null)
        {
            TextView setupTitle = (TextView) v.findViewById(R.id.setup_title);
            TextView players = (TextView) v.findViewById(R.id.setup_player_amount);
            TextView description = (TextView) v.findViewById(R.id.setup_description);

            if(setupTitle != null)
            {
                setupTitle.setText(i.getTitle());
            }
            if(players != null)
            {
                players.setText("Players: " + i.getPlayers());
            }
            if(description != null)
            {
                description.setText(i.getRolesDescription());
            }
        }

        return v;
    }
}
