package ly.priv.mobile.gui.socialnetworks;

import java.io.Serializable;
import java.util.ArrayList;

public class SUser implements Serializable {
	private static final long serialVersionUID = 1L;
	private String mUserName;
	private String mUrlToAvatar;
	private String mTime;
	private String mLastUserMess;
	private String mDialogId;

	public SUser() {
		this.mUserName = "";
		this.mLastUserMess = "";
		this.mTime = "";

	}

	/**
	 * @param mUserName
	 * @param mUrlToAvatar
	 * @param mTime
	 * @param mLastUserMess
	 * @param mDialogId
	 */
	public SUser(String mUserName, String mUrlToAvatar, String mTime,
			String mLastUserMess, String mDialogId) {
		super();
		this.mUserName = mUserName;
		this.mUrlToAvatar = mUrlToAvatar;
		this.mTime = mTime;
		this.mLastUserMess = mLastUserMess;
		this.mDialogId = mDialogId;
	}

	public String getLastUserMess() {
		return mLastUserMess;
	}

	public void setLastUserMess(String mLastUserMess) {
		this.mLastUserMess = mLastUserMess;
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

	public String getDialogId() {
		return mDialogId;
	}

	public void setDialogId(String mDialogId) {
		this.mDialogId = mDialogId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SUser [mUserName=" + mUserName + ", mUrlToAvatar="
				+ mUrlToAvatar + ", mTime=" + mTime + ", mLastUserMess="
				+ mLastUserMess + ", mDialogId=" + mDialogId + "]";
	}

}