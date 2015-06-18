package ly.priv.mobile.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class LobsterTextView extends TextView {

    public LobsterTextView(Context context) {
        super(context);
        setLobster();
    }

    public LobsterTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLobster();
    }

    public LobsterTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLobster();
    }

    private void setLobster() {
        if (!isInEditMode()) {
            Typeface typeFace = Typeface.createFromAsset(getContext()
                    .getAssets(), "fonts/Lobster.ttf");
            setTypeface(typeFace);
        }
    }
}