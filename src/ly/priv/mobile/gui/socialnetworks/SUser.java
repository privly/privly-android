package ly.priv.mobile.gui.socialnetworks;

import java.io.Serializable;
import java.util.ArrayList;

public class SUser implements Serializable {
	private static final long serialVersionUID = 1L;
	private String mLastUserMess;
	private ArrayList<SMessage> mListUserMess;
	private String mTime;
	private String mUrlToAvatar;
	private String mUserName;

	public SUser() {
		this.mUserName = "";
		this.mLastUserMess = "";
		this.mTime = "";
		this.mListUserMess = null;
	}

	public SUser(String mUserName, String mLastUserMess, String mTime,
			String mUrlToAvatar, ArrayList<SMessage> mListUserMess) {
		super();
		this.mLastUserMess = mLastUserMess;
		this.mListUserMess = mListUserMess;
		this.mTime = mTime;
		this.mUrlToAvatar = mUrlToAvatar;
		this.mUserName = mUserName;
	}

	public String getLastUserMess() {
		return mLastUserMess;
	}

	public void setLastUserMess(String mLastUserMess) {
		this.mLastUserMess = mLastUserMess;
	}

	public ArrayList<SMessage> getListUserMess() {
		return mListUserMess;
	}

	public void setListUserMess(ArrayList<SMessage> mListUserMess) {
		this.mListUserMess = mListUserMess;
	}

	public String getTime() {
		return mTime;
	}

	public void setTime(String mTime) {
		this.mTime = mTime;
	}

	public String getUrlToAvatar() {
		return mUrlToAvatar;
	}

	public void setUrlToAvatar(String mUrlToAvatar) {
		this.mUrlToAvatar = mUrlToAvatar;
	}

	public String getUserName() {
		return mUserName;
	}

	public void setUserName(String mUserName) {
		this.mUserName = mUserName;
	}

}