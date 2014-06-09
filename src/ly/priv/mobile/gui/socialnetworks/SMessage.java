package ly.priv.mobile.gui.socialnetworks;

import java.io.Serializable;

import ly.priv.mobile.Utilities;

import com.google.gson.annotations.SerializedName;

/**
 * Class for entity Message
 * 
 * Annotation is used for generate list of messages from json via Gson library.
 * 
 * @author Ivan Metla e-mail: metlaivan@gmail.com
 *
 */
/**
 * @author Ivan Metla e-mail: metlaivan@gmail.com
 *
 */
public class SMessage implements Serializable {
	private static final long serialVersionUID = 1L;
	@SerializedName("message")
	private String mMessage;
	@SerializedName("created_time")
	private String mTime;
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
	}

	/**
	 * Constructor for SMessage
	 * @param mMessage - message 
	 * @param mTime - time when created message
	 * @param mUrlToAvatar - url to avatar
	 */
	public SMessage(String mMessage, String mTime, String mUrlToAvatar
			) {
		super();
		this.mMessage = mMessage;
		this.mTime = mTime;
	}

	/**
	 * Get message
	 * 
	 * @return message
	 */
	public String getMessage() {
		return mMessage;
	}

	/**
	 * Set message
	 * @param mMessage
	 */
	public void setMessage(String mMessage) {
		this.mMessage = mMessage;
	}

	
	/**
	 * Get time
	 * @return time
	 */
	public String getTime() {
		return Utilities.getTime(mTime);
	}

	/**
	 * Set time
	 * @param mTime
	 */
	public void setTime(String mTime) {
		this.mTime = mTime;
	}

	/**
	 * Get url to avatar from message
	 * @return
	 */
	public String getUrlToAvatar() {
		return mFrom.mPicture.mData.mUrl;
	}


	/**
	 * Get id from message
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
		return "SMessage [mMessage=" + mMessage + ", mTime=" + mTime
				+ ", from=" + mFrom + "]";
	}

}