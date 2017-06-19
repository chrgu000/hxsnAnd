package com.hxsn.hf.navigation;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.hxsn.hf.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;

public class messageservice extends Service
{


	private messagethread messagethread = null;


	private Intent messageintent = null;
	private PendingIntent messagependingintent = null;


	private int messagenotificationid = 1000;
	private Notification messagenotification = null;
	private NotificationManager messagenotificatiomanager = null;
	
	private String xiaoxi = "0";
	
	@Override
	public IBinder onBind(Intent intent)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public int onStartCommand(Intent intent, int flags, int startid)
	{

		messagenotification = new Notification();
		messagenotification.icon = R.drawable.ic_launcher;
		messagenotification.tickerText = "三省农场通知";
		messagenotification.defaults = Notification.DEFAULT_SOUND;
		messagenotification.flags = Notification.FLAG_AUTO_CANCEL;
		messagenotificatiomanager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		messageintent = new Intent(this, XiaoxiActivity.class);
		messageintent.setAction("android.intent.action.MAIN"); 
		messageintent.addCategory("android.intent.category.LAUNCHER"); 
		messageintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
		messagependingintent = PendingIntent.getActivity(this, 0,
					messageintent, 0);
		messagethread = new messagethread();
		messagethread.isrunning = true;
		messagethread.start();
		return super.onStartCommand(intent, flags, startid);
	}

	class messagethread extends Thread {
		public boolean isrunning = true;
		public void run() {
			while (true) {
				try {
					String xiaoxi = doNetWork();
					CharSequence cs = "新消息\", \"西瓜熟了,请及时采摘";
					if (xiaoxi.equals("2")) {
						Notification.Builder builder = new Notification.Builder(messageservice.this);
						builder.setContentIntent(messagependingintent);
						builder.setContentTitle("新消息");
						builder.setContentText("西瓜熟了,请及时采摘");

						messagenotificatiomanager.notify(messagenotificationid, messagenotification);
					}
					Thread.sleep(5000);
					
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}

	}

	@Override 
	public void onDestroy() {
		super.onDestroy(); 
		System.exit(0); 
		//messagethread.isrunning = false; 
		
	} 
	
	private String doNetWork() {
		// TODO Auto-generated method stub
		PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try
        {
            URL realUrl = new URL("http://192.168.12.33:8080/zhnc/zixun/pick.json");
            URLConnection conn = realUrl.openConnection();
            
            conn.setRequestProperty("Content-Type", "html/text");
            conn.setConnectTimeout(20*1000);
			conn.setReadTimeout(20*1000);
            conn.setDoOutput(false);
            conn.setDoInput(true);
            conn.connect();

            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null)
            {
            	if(result.equals("")){
            		result += line;
            	} else {
            		result += "\n" + line;
            	}
            }
            
            System.out.println("result: " + result);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (out != null)
                {
                    out.close();
                }
                if (in != null)
                {
                    in.close();
                }
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
            }
        }
        
        return result.equals("")? "0":result;
        
	}
}
