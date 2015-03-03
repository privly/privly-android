package ly.priv.mobile.gui.drawer;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ly.priv.mobile.R;
import ly.priv.mobile.utils.LobsterTextView;

public class NavDrawerAdapter extends BaseAdapter {
    ArrayList<NavDrawerItem> navDrawerItems;
    LayoutInflater inflater;
    private String LOGTAG = getClass().getSimpleName();

    public NavDrawerAdapter(Activity activity, ArrayList<NavDrawerItem> navDrawerItems) {
        inflater = (LayoutInflater) activity.getLayoutInflater();
        this.navDrawerItems = navDrawerItems;
    }

    @Override
    public int getItemViewType(int position) {
        return navDrawerItems.get(position).getType();
    }

    @Override
    public int getViewTypeCount() {
        return NavDrawerItemType.ITEM_TYPE_COUNT;
    }

    @Override
    public int getCount() {
        return navDrawerItems.size();
    }

    @Override
    public Object getItem(int position) {
        return navDrawerItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean isEnabled(int position) {
        if (getItemViewType(position) == NavDrawerItemType.HEADER)
            return false;
        else
            return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Object object = navDrawerItems.get(position).getObject();
        switch (getItemViewType(position)) {
            case NavDrawerItemType.HEADER:
                Header header = (Header) object;
                ViewHolderHeader viewHolderHeader = null;
                if (convertView == null) {
                    viewHolderHeader = new ViewHolderHeader();
                    convertView = inflater.inflate(R.layout.list_item_drawer_header, parent, false);
                    viewHolderHeader.headerText = (LobsterTextView) convertView.findViewById(R.id.headerTextView);
                    convertView.setTag(viewHolderHeader);
                } else {
                    viewHolderHeader = (ViewHolderHeader) convertView.getTag();
                }
                viewHolderHeader.headerText.setText(header.getHeaderText());
                break;
            case NavDrawerItemType.PRIVLY_APPLICATION:
                PrivlyApplication application = (PrivlyApplication) object;
                ViewHolderPrivlyApp viewHolder = null;
                if (convertView == null) {
                    viewHolder = new ViewHolderPrivlyApp();
                    convertView = inflater.inflate(R.layout.list_item_drawer_privly_application, parent, false);
                    viewHolder.appName = (TextView) convertView.findViewById(R.id.app_name);
                    viewHolder.appIcon = (ImageView) convertView.findViewById(R.id.app_icon);
                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolderPrivlyApp) convertView.getTag();
                }
                viewHolder.appName.setText(application.getName());
                viewHolder.appIcon.setImageResource(application.getIconResId());
                break;
        }
        return convertView;
    }

    private static class ViewHolderPrivlyApp {
        TextView appName;
        ImageView appIcon;
    }

    private static class ViewHolderHeader {
        LobsterTextView headerText;
    }
}
