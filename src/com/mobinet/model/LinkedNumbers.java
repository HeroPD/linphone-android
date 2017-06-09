/**
 * 
 */
package com.mobinet.model;

import java.util.List;
/**
 * @author Showtime
 *
 */
public class LinkedNumbers extends BaseModel{
	
	public int total;
	public List<NumberListItem> numberList;
	
	public class NumberListItem{
		public String date;
		public String direction;
		public int id ;
		public String number ; 
		public int status;
		public String subject ;
		public String text;
		public boolean visible;
		public String errorMessage;
		public int totalNewMessage;
	}

}
