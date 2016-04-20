package com.caddosierra.mafiaparty;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class RulesFragment extends Fragment {


    public RulesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.rules, container, false);
        TextView t = (TextView) v.findViewById(R.id.html_text);
        t.setText(Html.fromHtml(getString(R.string.rules_html)));
        t.setMovementMethod(LinkMovementMethod.getInstance());
        return v;
    }
}
