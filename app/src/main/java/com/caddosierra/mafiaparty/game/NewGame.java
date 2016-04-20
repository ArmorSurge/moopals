package com.caddosierra.mafiaparty.game;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.caddosierra.mafiaparty.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;


public class NewGame extends AppCompatActivity {
    private static TextView players;
    private static ItemAdapter adapter;
    private static ArrayList<Role> items;
    private static int playerCount;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean darkTheme = preferences.getBoolean("pref_theme", false);
        if(darkTheme)
            setTheme(R.style.AppThemeDark);
        super.onCreate(savedInstanceState);
//        //Toast.makeText(this, "New game", Toast.LENGTH_SHORT).show();
        setContentView(R.layout.new_game);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(darkTheme)
            toolbar.setPopupTheme(R.style.DarkPopup);

        players = (TextView) findViewById(R.id.players);
        ListView listView = (ListView) findViewById(R.id.listView);

        View v = getLayoutInflater().inflate(R.layout.add_item, null);
        listView.addFooterView(v, null, false);

        items = new ArrayList<>();
        items.add(new Role("Mafia", 2));
        items.add(new Role("Serial Killer", 0));
        items.add(new Role("Doctor", 1));
        items.add(new Role("Detective", 1));
        items.add(new Role("Civilian", 4));

        adapter = new ItemAdapter(this, R.layout.list_item, items);

        listView.setAdapter(adapter);

        setItems(items);

        registerForContextMenu(listView);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        ArrayList<Role> placeHolder = savedInstanceState.getParcelableArrayList("PAL");

        if(placeHolder != null) {
            items.clear();
            for (int x = 0; x < placeHolder.size(); x++) {
                items.add(placeHolder.get(x));
            }
            setItems(items);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        outState.putParcelableArrayList("PAL", items);
        super.onSaveInstanceState(outState);
    }

    public void addItem(View v)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Create New Role")
                .setMessage("What is the name of this role?");
        final EditText input = new EditText(this);
        input.setSelection(input.getText().length());
        input.setSelectAllOnFocus(true);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Role itemAdded = new Role(input.getText().toString(), 1);
                items.add(itemAdded);
                setItems(items);
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }
        });

        AlertDialog alertToShow = alert.create();
        alertToShow.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        alertToShow.show();
    }

    public void startDisplay(View view)
    {
        Boolean isPossible = true;

        if (playerCount < 3 )
        {
            isPossible = false;
            Snackbar.make(findViewById(R.id.sbPosition), "At least 3 players are required.", Snackbar.LENGTH_LONG).show();
        }

        if(isPossible)
        {
            ArrayList<String> values = new ArrayList<>();

            int itemsSize = items.size();
            int itemsAmount;
            for(int x = 0; x < itemsSize; x++) {
                itemsAmount = items.get(x).getAmount();
                for(int y = 0; y < itemsAmount; y++)
                    values.add(items.get(x).getRole());
            }

            Collections.shuffle(values, new Random());

            Intent intent = new Intent(this, ShowCards.class);
            intent.putStringArrayListExtra("ArrayList", values);
            startActivity(intent);
        }
    }

    public static void setItems(ArrayList<Role> mItems) {
        items = mItems;
        playerCount = 0;
        for (int x = 0; x < items.size(); x++)
            playerCount = playerCount + items.get(x).getAmount();

        players.setText("" + playerCount);

        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.action_clear)
        {
            final ArrayList<Role> placeHolderItems = new ArrayList<>();

            for (int x = 0; x < items.size(); x++)
                placeHolderItems.add(items.get(x));

            items.clear();
            setItems(items);

            Snackbar.make(findViewById(R.id.sbPosition), "Setup cleared!", Snackbar.LENGTH_LONG)
                    .setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            items.clear();
                            for (int x = 0; x < placeHolderItems.size(); x++)
                                items.add(placeHolderItems.get(x));
                            setItems(items);
                            Toast.makeText(v.getContext(), "Undone.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .show();
            return true;
        }
        else if (id == R.id.action_reset)
        {
            final ArrayList<Role> placeHolderItems = new ArrayList<>();

            for (int x = 0; x < items.size(); x++)
                placeHolderItems.add(items.get(x));

            items.clear();
            items.add(new Role("Mafia", 2));
            items.add(new Role("Serial Killer", 0));
            items.add(new Role("Doctor", 1));
            items.add(new Role("Detective", 1));
            items.add(new Role("Civilian", 4));

            setItems(items);

            Snackbar.make(findViewById(R.id.sbPosition), "Setup reset!", Snackbar.LENGTH_LONG)
                    .setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            items.clear();
                            for (int x = 0; x < placeHolderItems.size(); x++)
                                items.add(placeHolderItems.get(x));
                            setItems(items);
                            Toast.makeText(v.getContext(), "Undone.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .show();
            return true;
        }
        else if(id == R.id.action_save)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Confirm Save");
            builder.setMessage("This will overwrite any previous save. Are you sure?");

            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    saveSetup();
                }
            });

            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Snackbar.make(findViewById(R.id.sbPosition), "Cancelled.",
                            Snackbar.LENGTH_SHORT).show();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();
            return true;
        }
        else if(id == R.id.action_load)
        {
            final ArrayList<Role> placeHolderItems = new ArrayList<>();

            for (int x = 0; x < items.size(); x++)
                placeHolderItems.add(items.get(x));

            items.clear();
            SharedPreferences sp = this.getPreferences(Context.MODE_PRIVATE);

            int arrayAmount = sp.getInt("arrayAmount", 0);

            for (int x = 0; x < arrayAmount; x++)
                items.add(new Role(sp.getString("Role_" + x, null), sp.getInt("Amt_" + x, 0)));

            setItems(items);

            Snackbar.make(findViewById(R.id.sbPosition), "Setup loaded!", Snackbar.LENGTH_LONG)
                    .setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            items.clear();
                            for (int x = 0; x < placeHolderItems.size(); x++)
                                items.add(placeHolderItems.get(x));
                            setItems(items);
                            Toast.makeText(v.getContext(), "Undone.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .show();
            return true;
        }
        else if(id == R.id.home)
        {
            NavUtils.navigateUpFromSameTask(this);
            finish();
            items = null;
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int index = info.position;

        switch (item.getItemId()) {
            case R.id.edit:
                editItem(index);
                return true;
            case R.id.delete:
                items.remove(index);
                setItems(items);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void saveSetup()
    {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("arrayAmount", items.size());

        for(int x = 0; x < items.size(); x++)
        {
            editor.putString("Role_" + x, items.get(x).getRole());
            editor.putInt("Amt_" + x, items.get(x).getAmount());
        }

        editor.apply();

        Snackbar.make(findViewById(R.id.sbPosition), "Current setup saved!", Snackbar.LENGTH_SHORT).show();
    }

    private void editItem(final int index)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Change Role Name");
        alert.setMessage("What is the name of this role?");
        final EditText input = new EditText(this);
        input.setText(items.get(index).getRole());
        input.setSelection(input.getText().length());
        input.setSelectAllOnFocus(true);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                items.get(index).setRole(input.getText().toString());
                adapter.notifyDataSetChanged();
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }
        });

        AlertDialog alertToShow = alert.create();
        alertToShow.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        alertToShow.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        items = null;
        finish();
    }
}
