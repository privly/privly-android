package ly.priv.mobile.gui.microblogs;

import java.io.Serializable;

public class Post implements Serializable {
	private static final long serialVersionUID = 1L;
	private String mName;
	private String mNic;
	private String mTime;
	private String mMess;
	private String mUrlAvatar;

	/**
	 * @param mName
	 * @param mNic
	 * @param mTime
	 * @param mMess
	 * @param mUrlAvatar
	 */
	public Post(String mName, String mNic, String mTime, String mMess,
			String mUrlAvatar) {
		super();
		this.mName = mName;
		this.mNic = mNic;
		this.mTime = mTime;
		this.mMess = mMess;
		this.mUrlAvatar = mUrlAvatar;
	}

	public Post() {
		super();
		this.mName = "";
		this.mNic = "";
		this.mTime = "";
		this.mMess = "";
		this.mUrlAvatar = "";
	}

	public String getName() {
		return mName;
	}

	public void setName(String mName) {
		this.mName = mName;
	}

	public String getNic() {
		return mNic;
	}

	public void setNic(String mNic) {
		this.mNic = mNic;
	}

	public String getTime() {
		return mTime;
	}

	public void setTime(String mTime) {
		this.mTime = mTime;
	}

	public String getMess() {
		return mMess;
	}

	public void setMess(String mMess) {
		this.mMess = mMess;
	}

	public String getUrlAvatar() {
		return mUrlAvatar;
	}

	public void setUrlAvatar(String mUrlAvatar) {
		this.mUrlAvatar = mUrlAvatar;
	}

}
