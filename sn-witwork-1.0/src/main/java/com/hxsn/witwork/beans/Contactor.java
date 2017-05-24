package com.hxsn.witwork.beans;

public class Contactor {
	    String id;
	    String uheadpic;
	    String name;
	    String role;
	    boolean onduty;
	    String phone;
	    int remind = 0;
	    
		public Contactor() {
			super();
			// TODO Auto-generated constructor stub
		}

		public Contactor(String id, String uheadpic, String name, String role,
				boolean onduty, String phone) {
			super();
			this.id = id;
			this.uheadpic = uheadpic;
			this.name = name;
			this.role = role;
			this.onduty = onduty;
			this.phone = phone;
		}

		
		public int getRemind() {
			return remind;
		}

		public void setRemind(int remind) {
			this.remind = remind;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getUheadpic() {
			return uheadpic;
		}

		public void setUheadpic(String uheadpic) {
			this.uheadpic = uheadpic;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getRole() {
			return role;
		}

		public void setRole(String role) {
			this.role = role;
		}

		public boolean isOnduty() {
			return onduty;
		}

		public void setOnduty(boolean onduty) {
			this.onduty = onduty;
		}

		public String getPhone() {
			return phone;
		}

		public void setPhone(String phone) {
			this.phone = phone;
		}
		
	
		
}
