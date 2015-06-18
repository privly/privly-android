package ly.priv.mobile.api.gui.socialnetworks;

import java.io.Serializable;

/**
 * Class for entity SUser
 * 
 * @author Ivan Metla e-mail: metlaivan@gmail.com
 * 
 */
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
	 * Constructor for SUser
	 * 
	 * @param mUserName
	 *            - uusername from which the messages was received
	 * @param mUrlToAvatar
	 *            url to avatar of user from which the messages was received
	 * @param mTime
	 *            - time when the user received last message
	 * @param mLastUserMess
	 *            - last message
	 * @param mDialogId
	 *            - id of dialog
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