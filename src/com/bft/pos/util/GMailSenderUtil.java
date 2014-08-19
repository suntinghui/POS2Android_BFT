package com.bft.pos.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Security;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import android.util.Log;

 
public class GMailSenderUtil extends javax.mail.Authenticator {    
    private Session session;    
 
    static {    
        Security.addProvider(new JSSEProvider()); 
        Log.i("jsseprovider: %@", "jsseprovider");
    }   
	
    public GMailSenderUtil() {    
        Properties props = new Properties();    
        props.setProperty("mail.transport.protocol", "smtp");   
        Log.e("maihost:", getMailHost());
        Log.e("getMailUsernameFrom:", getMailUsernameFrom());
        Log.e("getMailPassWordFrom:", getMailPassWordFrom());
        Log.e("getMailUserNameTo:", getMailUserNameTo());
        props.setProperty("mail.host", getMailHost());    
        props.put("mail.smtp.auth", "true");    
        props.put("mail.smtp.port", "465");    
        props.put("mail.smtp.socketFactory.port", "465");    
        props.put("mail.smtp.socketFactory.class",    
                "javax.net.ssl.SSLSocketFactory");    
        props.put("mail.smtp.socketFactory.fallback", "false");    
        props.setProperty("mail.smtp.quitwait", "false");    
 
        session = Session.getDefaultInstance(props, this);    
    }    
 
    protected PasswordAuthentication getPasswordAuthentication() {  
        return new PasswordAuthentication(getMailUsernameFrom(), getMailPassWordFrom());    
    }    
 
    public synchronized void sendMail(String subject, String body) throws Exception {    
        try{ 
        MimeMessage message = new MimeMessage(session);    
        DataHandler handler = new DataHandler(new ByteArrayDataSource(body.getBytes(), "text/plain"));    
        message.setSender(new InternetAddress(getMailUsernameFrom()));    
        message.setSubject(subject);    
        message.setDataHandler(handler);    
        if (getMailUserNameTo().indexOf(',') > 0)    
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(getMailUserNameTo()));    
        else   
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(getMailUserNameTo()));    
        Transport.send(message);    
        }catch(Exception e){ 
 
        } 
    }    
 
    public class ByteArrayDataSource implements DataSource {    
        private byte[] data;    
        private String type;    
 
        public ByteArrayDataSource(byte[] data, String type) {    
            super();    
            this.data = data;    
            this.type = type;    
        }    
 
        public ByteArrayDataSource(byte[] data) {    
            super();    
            this.data = data;    
        }    
 
        public void setType(String type) {    
            this.type = type;    
        }    
 
        public String getContentType() {    
            if (type == null)    
                return "application/octet-stream";    
            else   
                return type;    
        }    
 
        public InputStream getInputStream() throws IOException {    
            return new ByteArrayInputStream(data);    
        }    
 
        public String getName() {    
            return "ByteArrayDataSource";    
        }    
 
        public OutputStream getOutputStream() throws IOException {    
            throw new IOException("Not Supported");    
        }    
    } 
    
    public String getMailHost(){
    	return "smtp.163.com";
    }
	public String getMailUsernameFrom(){
		return "lkoa_app@163.com";
	}
	public String getMailPassWordFrom(){
		return "LKOA_123";
	}
	public String getMailUserNameTo(){
		return "tinghuisun@163.com";
	}
	
}   

