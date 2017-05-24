
package com.hxsn.witwork.beans;

public class ChatMsgEntity
{
	public static boolean isSingle = true;
	public static int PICTURE = 11;
	public static int VOICE = 13;
	public static int TEXT = 14;
	
	private static final String TAG = ChatMsgEntity.class.getSimpleName();
	
	private String name;
	private String id;
	private String date;
	private String headUrl;
	private String text;

	private boolean isComMeg = true;
	
	private int Type = 0;
	
	private int voiceleng ;
	
	
	public ChatMsgEntity()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public String getHeadUrl() {
		return headUrl;
	}

	public void setHeadUrl(String headUrl) {
		this.headUrl = headUrl;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getDate()
	{
		return date;
	}

	public void setDate(String date)
	{
		this.date = date;
	}

	public String getText()
	{
		return text;
	}

	public void setText(String text)
	{
		this.text = text;
	}

	public boolean getMsgType()
	{
		return isComMeg;
	}

	public void setMsgType(boolean isComMsg)
	{
		isComMeg = isComMsg;
	}
	
	public int getVoiceleng()
	{
		return voiceleng;
	}

	public void setVoiceleng(int voiceleng)
	{
		this.voiceleng = voiceleng;
	}

	public int getType()
	{
		return Type;
	}

	public void setType(int type)
	{
		Type = type;
	}

	public static boolean isSingle()
	{
		return isSingle;
	}

	public static void setSingle(boolean isSingle)
	{
		ChatMsgEntity.isSingle = isSingle;
	}

	
}
