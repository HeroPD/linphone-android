package com.mobinet.model;

import java.util.List;

public class PromData{

	public int result_code;
	public String message_mn;
	public String message_en;
	public List<data> promotions;
	public String hash;
	public String type;
	public int count;
	
	public class data{
		public int Id ;
		public String title_mn;
		public String title_en;
		public String picture;
		public String description_mn;
		public String description_en;
	}
	
}
