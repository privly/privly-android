package ly.priv.mobile.gui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import ly.priv.mobile.R;
import ly.priv.mobile.utils.ConstantValues;

public class SharePrivlyURLFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_share_privly_url, container, false);
        getActivity().setTitle(getResources().getString(R.string.share_privly_url));
        TextView urlTextView = (TextView) rootView.findViewById(R.id.url_text_view);
        final String mPrivlyUrl = getArguments().getString(ConstantValues.PRIVLY_URL_KEY);
        urlTextView.setText(mPrivlyUrl);
        Linkify linkify = new Linkify();
        linkify.addLinks(urlTextView, Linkify.ALL);
        Button shareButton = (Button) rootView.findViewById(R.id.share_url_btn);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, mPrivlyUrl);
                try {
                    startActivity(Intent.createChooser(intent,
                            getString(R.string.share_privly_url)));
                } catch (android.content.ActivityNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
        });
        return rootView;
    }
}
