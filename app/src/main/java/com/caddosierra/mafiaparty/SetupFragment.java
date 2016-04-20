package com.caddosierra.mafiaparty;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.caddosierra.mafiaparty.game.Role;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SetupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SetupFragment extends Fragment {

    private SetupAdapter adapter;
    private ArrayList<Setup> setups;

    public SetupFragment() {

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SetupFragment.
     */
    public static SetupFragment newInstance() {
        SetupFragment fragment = new SetupFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_select_setup, container, false);

        ListView setupListView = (ListView) v.findViewById(R.id.select_setup_list_view);

        ArrayList<Role> items = new ArrayList<>();
        items.add(new Role("Mafia", 2));
        items.add(new Role("Serial Killer", 0));
        items.add(new Role("Doctor", 1));
        items.add(new Role("Detective", 1));

        ArrayList<Role> items2 = new ArrayList<>();
        items2.add(new Role("Mafia", 2));
        items2.add(new Role("Serial Killer", 6));
        items2.add(new Role("Doctor", 100));
        items2.add(new Role("Detective", 1));
        items2.add(new Role("Your Mom", 1));
        items2.add(new Role("Werewolf", 1));
        items2.add(new Role("Deadly Assassin X Malcolm", 1));

        setups = new ArrayList<>();
        setups.add(new Setup("Default", items));
        setups.add(new Setup("Default Part 2", items2));

        adapter = new SetupAdapter(getActivity(), R.layout.setup_item, setups);
        setupListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        return v;
    }

}
