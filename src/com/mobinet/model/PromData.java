package com.mobinet.model;

import java.util.List;

public class PromData extends BaseModel{
	
	public List<data> Promotions;
	
	public class data{
		public int Id ;
		public Boolean IsCorp;
		public String Title ;
		public String Picture;
		public String Date;
		public String Text;
	}
	
}
