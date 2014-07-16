package ly.priv.mobile;

import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.api.services.gmail.model.Message;

public class EmailThreadObject implements Parcelable{
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

	public EmailThreadObject(){
		
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
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		
	}
	
	
}