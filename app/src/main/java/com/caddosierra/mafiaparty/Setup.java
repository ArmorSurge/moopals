package com.caddosierra.mafiaparty;

import android.os.Parcel;
import android.os.Parcelable;

import com.caddosierra.mafiaparty.game.Role;

import java.util.ArrayList;

public class Setup implements Parcelable{
    private String title;
    private String rolesDescription = "";
    private int players = 0;
    private ArrayList<Role> roles;

    public Setup(String title, ArrayList<Role> roles)
    {
        this.title = title;
        this.roles = roles;

        rolesDescription = roles.get(0).getRole();
        int i = roles.size();
        for(int x = 0; x < i; x++)
        {
            players = players + roles.get(x).getAmount();
        }
        for(int x = 1; x < i; x++)
        {
            rolesDescription = rolesDescription + ", " + roles.get(x).getRole();
        }
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getTitle()
    {
        return title;
    }

    public String getRolesDescription()
    {
        return rolesDescription;
    }

    public int getPlayers()
    {
        return players;
    }

    public ArrayList<Role> getRolesArrayList()
    {
        return roles;
    }


    protected Setup(Parcel in) {

    }

    public static final Creator<Setup> CREATOR = new Creator<Setup>() {
        @Override
        public Setup createFromParcel(Parcel in) {
            return new Setup(in);
        }

        @Override
        public Setup[] newArray(int size) {
            return new Setup[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
    }
}
