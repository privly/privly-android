package ly.priv.mobile.gui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ly.priv.mobile.R;
import ly.priv.mobile.utils.ConstantValues;

public class SharePrivlyURLFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_share_privly_url, container, false);
        TextView urlTextView = (TextView) rootView.findViewById(R.id.url_text_view);
        urlTextView.setText(getArguments().getString(ConstantValues.PRIVLY_URL_KEY));
        Linkify linkify = new Linkify();
        linkify.addLinks(urlTextView, Linkify.ALL);
        return rootView;
    }
}
