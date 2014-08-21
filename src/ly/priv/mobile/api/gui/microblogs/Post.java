package ly.priv.mobile.api.gui.microblogs;

import java.util.Date;

/**
 * Class for entity Post
 * 
 * @author Ivan Metla e-mail: metlaivan@gmail.com
 * 
 */
public class Post {
	private String mName;
	private String mNic;
	private Date mTime;
	private String mMessage;
	private String mUrlAvatar;

	public Post(String name, String nic, Date time, String mess,
			String urlAvatar) {
		super();
		this.mName = name;
		this.mNic = nic;
		this.mTime = time;
		this.mMessage = mess;
		this.mUrlAvatar = urlAvatar;
	}

	public Post() {
		super();
		this.mName = "";
		this.mNic = "";
		this.mTime = null;
		this.mMessage = "";
		this.mUrlAvatar = "";
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		this.mName = name;
	}

	public String getNic() {
		return mNic;
	}

	public void setNic(String nic) {
		this.mNic = nic;
	}

	public Date getTime() {
		return mTime;
	}

	public void setTime(Date time) {
		this.mTime = time;
	}

	public String getMessage() {
		return mMessage;
	}

	public void setMessage(String message) {
		this.mMessage = message;
	}

	public String getUrlAvatar() {
		return mUrlAvatar;
	}

	public void setUrlAvatar(String urlAvatar) {
		this.mUrlAvatar = urlAvatar;
	}
}
