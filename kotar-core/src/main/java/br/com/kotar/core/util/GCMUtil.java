package br.com.kotar.core.util;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Notification;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

public class GCMUtil {
	
	public static void sendMessage(Map<String, String> data, 
								   Notification notification, 
								   String googleServerKey, 
								   String pushTokenId) throws Exception {
		
		Result result = null;
		try {
			Sender sender = new Sender(googleServerKey);		
			
			//Message message 
			Message.Builder builder = new Message.Builder().timeToLive(30)
					.delayWhileIdle(true).notification(notification);

			if (data != null && data.size() > 0) {
				Set<String> keys = data.keySet();
				Iterator<String> it = keys.iterator();
				while (it.hasNext()) {
					String key = it.next();
					String value = data.get(key);
					builder.addData(key, value);
				}
			}

			Message message = builder.build();
			
			System.out.println("regId: " + pushTokenId);
			result = sender.send(message, pushTokenId, 1);
			
			int sucesso = result.getSuccess();
			
			System.out.println("Sucesso PUSH "+sucesso);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public static void main(String[] args) {
		System.setProperty("https.proxyHost", "172.23.3.254");
 	    System.setProperty("https.proxyPort", "8080");
		
 	    try {
 	    	
 	    	Notification notification = new Notification.Builder("iconexxzz").badge(1).body("corpobody").clickAction("acao123").color("colorers").sound("somzyz").title("Kothar - Nova Resposta").tag("tag xxx").build();
 	    	
 	    	GCMUtil.sendMessage(null, notification, "AIzaSyDhDyd4oyIT6F8S1k1MMfQfi9dKVSHxbWE", 
				"dUoRrCfwSJY:APA91bFON4V0fg4ayg0awAdIvjmr4PU-ER9OO8A8fjYljrKn6uyMXQwgv_tQFcl1txqoe7cHhJpEFu7a4aBFOUsikHAwlirKQCACHpvmA0u2jzSlUvFNvCe0C5NQsjVKjEoZW9Yn2uZS");
 	    }
 	    catch (Exception e) {
 	    	e.printStackTrace();
		}
	}
}