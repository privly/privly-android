package ly.priv.mobile.gui.socialnetworks;

import java.io.Serializable;

import ly.priv.mobile.Utilities;

import com.google.gson.annotations.SerializedName;

public class SMessage implements Serializable {
	private static final long serialVersionUID = 1L;
	@SerializedName("message")
	private String mMessage;
	@SerializedName("created_time")
	private String mTime;
	private String mUrlToAvatar;
	@SerializedName("from")
	private From mFrom;

	private class From {
		@SerializedName("id")
		private String mID;
		@SerializedName("picture")
		private Picture mPicture;

		private class Picture {
			@SerializedName("data")
			private Data mData;

			private class Data {
				@SerializedName("url")
				private String mUrl;

				/*
				 * (non-Javadoc)
				 * 
				 * @see java.lang.Object#toString()
				 */
				@Override
				public String toString() {
					return "Data [mUrl=" + mUrl + "]";
				}

			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see java.lang.Object#toString()
			 */
			@Override
			public String toString() {
				return "Picture [mdata=" + mData + "]";
			}

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "From [mID=" + mID + ", mPicture=" + mPicture + "]";
		}

	}

	public SMessage() {
		this.mMessage = "";
		this.mTime = "";
		this.mUrlToAvatar = "";
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
		this.mMessage = mMessage;
		this.mTime = mTime;
		this.mUrlToAvatar = mUrlToAvatar;
	}

	public String getMessage() {
		return mMessage;
	}

	public void setMessage(String mMessage) {
		this.mMessage = mMessage;
	}

	public String getTime() {
		return Utilities.getTime(mTime);
	}

	public void setTime(String mTime) {
		this.mTime = mTime;
	}

	public String getUrlToAvatar() {
		return mFrom.mPicture.mData.mUrl;
	}

	public void setUrlToAvatar(String mUrlToAvatar) {
		this.mUrlToAvatar = mUrlToAvatar;
	}

	
	/**
	 * @return the mId
	 */
	public String getId() {
		return mFrom.mID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SMessage [mMessage="
				+ mMessage + ", mTime=" + mTime + ", mUrlToAvatar="
				+ mUrlToAvatar + ", from=" + mFrom + "]";
	}

}