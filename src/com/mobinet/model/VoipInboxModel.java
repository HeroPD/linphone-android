package com.mobinet.model;

import java.util.List;

public class VoipInboxModel extends BaseModel{
	
	public int smscount;
	public List<InboxMessageListItem> message;
	
	public class InboxMessageListItem{
		public String callednumber;
		public String date;
		public String sms ;
		public int smsid ; 
		public int status;
		public int type ;
		public boolean visible;
	}

}
