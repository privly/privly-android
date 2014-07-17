package ly.priv.mobile;

import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.api.services.gmail.model.Message;

public class SingleEmailObject implements Parcelable{
	String mailSnippet;
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
	
	public SingleEmailObject(){
		
	}
	
	public SingleEmailObject(Parcel in){
		this.mailSender = in.readString();
		this.mailSnippet = in.readString();
		this.mailTime = in.readString();
		this.Id = in.readString();
		in.readList(messages, null);
	}

	public SingleEmailObject(String mailSnippet, String mailTime, String mailSender, String Id, List<Message> messages){
		this.mailSnippet = mailSnippet;
		this.mailSender = mailSender;
		this.mailTime =  mailTime;
		this.Id =  Id;
		this.messages =  messages;
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
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(mailSender);
		dest.writeString(mailSnippet);
		dest.writeString(mailTime);
		dest.writeString(Id);
		dest.writeList(messages);
	}
	
	public static final Parcelable.Creator<EmailThreadObject> Creator = new Parcelable.Creator<EmailThreadObject>() {

		@Override
		public EmailThreadObject createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new EmailThreadObject(source);
		}

		@Override
		public EmailThreadObject[] newArray(int size) {
			// TODO Auto-generated method stub
			return new EmailThreadObject[size];
		}
		
	};
	
}