package ly.priv.mobile;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.api.services.gmail.model.Message;

import java.util.List;

public class EmailThreadObject implements Parcelable {
	String mailSnippet;
	String mailCount;
	String mailTime;
	String mailSender;
	List<Message> messages;
	String Id;

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public EmailThreadObject() {

	}

	public EmailThreadObject(Parcel in) {
		this.mailCount = in.readString();
		this.mailSender = in.readString();
		this.mailSnippet = in.readString();
		this.mailTime = in.readString();
		this.Id = in.readString();
		in.readList(messages, null);
	}

	public EmailThreadObject(String mailSnippet, String mailCount,
			String mailTime, String mailSender, String Id,
			List<Message> messages) {
		this.mailSnippet = mailSnippet;
		this.mailCount = mailCount;
		this.mailSender = mailSender;
		this.mailTime = mailTime;
		this.Id = Id;
		this.messages = messages;
	}

	public List<Message> getMessages() {
		return messages;
	}

	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}

	public String getMailSnippet() {
		return mailSnippet;
	}

	public void setMailSnippet(String mailSnippet) {
		this.mailSnippet = mailSnippet;
	}

	public String getMailCount() {
		return mailCount;
	}

	public void setMailCount(String mailCount) {
		this.mailCount = mailCount;
	}

	public String getMailTime() {
		return mailTime;
	}

	public void setMailTime(String mailTime) {
		this.mailTime = mailTime;
	}

	public String getMailSender() {
		return mailSender;
	}

	public void setMailSender(String mailSender) {
		this.mailSender = mailSender;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mailCount);
		dest.writeString(mailSender);
		dest.writeString(mailSnippet);
		dest.writeString(mailTime);
		dest.writeString(Id);
		dest.writeList(messages);
	}

	public static final Parcelable.Creator<EmailThreadObject> Creator = new Parcelable.Creator<EmailThreadObject>() {

		@Override
		public EmailThreadObject createFromParcel(Parcel source) {
			return new EmailThreadObject(source);
		}

		@Override
		public EmailThreadObject[] newArray(int size) {
			return new EmailThreadObject[size];
		}

	};

}