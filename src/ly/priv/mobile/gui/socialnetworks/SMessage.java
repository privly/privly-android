package ly.priv.mobile.gui.socialnetworks;

import java.io.Serializable;

public class SMessage implements Serializable {
	private static final long serialVersionUID = 1L;
	private boolean mIsMyMessage;
	private String mMessage;
	private String mTime;
	private String mUrlToAvatar;

	public SMessage() {
		this.mMessage = "";
		this.mTime = "";
		this.mUrlToAvatar = "";
		this.mIsMyMessage = true;
	}

	/**
	 * @param mIsMyMessage
	 * @param mMessage
	 * @param mTime
	 * @param mUrlToAvatar
	 */
	public SMessage(String mMessage, String mTime, String mUrlToAvatar,
			boolean mIsMyMessage) {
		super();
		this.mIsMyMessage = mIsMyMessage;
		this.mMessage = mMessage;
		this.mTime = mTime;
		this.mUrlToAvatar = mUrlToAvatar;
	}

	public boolean IsMyMessage() {
		return mIsMyMessage;
	}

	public void setIsMyMessage(boolean mIsMyMessage) {
		this.mIsMyMessage = mIsMyMessage;
	}

	public String getMessage() {
		return mMessage;
	}

	public void setMessage(String mMessage) {
		this.mMessage = mMessage;
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

}