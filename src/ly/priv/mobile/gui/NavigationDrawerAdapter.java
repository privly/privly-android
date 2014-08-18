package ly.priv.mobile.gui;

import java.util.ArrayList;

import ly.priv.mobile.R;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NavigationDrawerAdapter extends BaseAdapter {

	ArrayList<DrawerObject> drawerItemList = new ArrayList<DrawerObject>();
	Context mContext;

	public NavigationDrawerAdapter(Context context,
			ArrayList<DrawerObject> drawerItems) {
		super();
		this.mContext = context;
		this.drawerItemList = drawerItems;
	}

	@Override
	public int getCount() {
		return this.drawerItemList.size();
	}

	@Override
	public Object getItem(int position) {
		return this.drawerItemList.get(position);
	}

	public boolean isEnabled(int position) {
		if (getItemViewType(position) == 1)
			return false;
		// ViewType 1 corresponds to the section header. So we make this type
		// non clickable.
		else
			return true;
		// Every other type in this list is clickable.
	}

	// 2 types of views: section headers and other clickable items
	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		DrawerObject currentObject = drawerItemList.get(position);
		if (currentObject.getType().equalsIgnoreCase("header")) {
			NavHeaderHolder headerHolder;
			if (convertView == null) {
				ViewGroup viewGroup = (ViewGroup) ((LayoutInflater) mContext
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
						.inflate(R.layout.nav_header_layout, null);
				headerHolder = new NavHeaderHolder(
						(TextView) viewGroup.findViewById(R.id.textview1));
				viewGroup.setTag(headerHolder);
				view = viewGroup;
			} else {
				headerHolder = (NavHeaderHolder) convertView.getTag();
				view = convertView;
			}
			headerHolder.headerView.setText(currentObject.getSectionheader());
			headerHolder.headerView.setOnClickListener(null);
		} else if (currentObject.getType().equalsIgnoreCase("NavItem")) {
			NavItemHolder itemHolder;
			if (convertView == null) {
				ViewGroup viewGroup = (ViewGroup) ((LayoutInflater) mContext
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
						.inflate(R.layout.nav_item_layout, null);
				itemHolder = new NavItemHolder(
						(TextView) viewGroup.findViewById(R.id.textview2),
						(ImageView) viewGroup.findViewById(R.id.navIcon));
				viewGroup.setTag(itemHolder);
				view = viewGroup;
			} else {
				itemHolder = (NavItemHolder) convertView.getTag();
				view = convertView;
			}
			itemHolder.titleView.setText(currentObject.getTitle());
			itemHolder.appIcon.setImageDrawable(currentObject.getIcon());
		}
		return view;
	}

	// Holder view for Navigation section headers
	static class NavHeaderHolder {
		TextView headerView;

		public NavHeaderHolder(TextView mTextView) {
			this.headerView = mTextView;
		}
	}

	// Holder view for Navigation clickable items
	static class NavItemHolder {
		TextView titleView;
		ImageView appIcon;

		public NavItemHolder(TextView mTextView, ImageView mImageView) {
			this.titleView = mTextView;
			this.appIcon = mImageView;
		}
	}
}

// This class represents the items in Navigation Drawer
class DrawerObject {
	String type;
	String sectionheader;
	String title;
	Drawable icon;

	public DrawerObject() {

	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSectionheader() {
		return sectionheader;
	}

	public void setSectionheader(String sectionheader) {
		this.sectionheader = sectionheader;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

	public Drawable getIcon() {
		return icon;
	}
}
