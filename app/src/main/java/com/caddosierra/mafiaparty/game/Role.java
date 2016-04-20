package com.caddosierra.mafiaparty.game;


import android.os.Parcel;
import android.os.Parcelable;


public class Role implements Parcelable {
    private String role;
    private int amount;

    public Role(String role, int amount)
    {
        this.role = role;
        this.amount = amount;
    }

    public void setRole(String role)
    {
        this.role = role;
    }

    public void setAmount(int amount)
    {
        this.amount = amount;
    }

    public String getRole()
    {
        return role;
    }

    public int getAmount()
    {
        return amount;
    }

    public Role(Parcel in) {
        super();
        readFromParcel(in);
    }

    public static final Parcelable.Creator<Role> CREATOR = new Parcelable.Creator<Role>() {
        public Role createFromParcel(Parcel in) {
            return new Role(in);
        }

        public Role[] newArray(int size) {

            return new Role[size];
        }

    };

    /**
     * Describe the kinds of special objects contained in this Parcelable's
     * marshalled representation.
     *
     * @return a bitmask indicating the set of special object types marshalled
     * by the Parcelable.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(role);
        dest.writeInt(amount);
    }

    public void readFromParcel(Parcel in)
    {
        role = in.readString();
        amount = in.readInt();
    }

}
