package br.com.kotar.web.service.email;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.imageio.ImageIO;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.kotar.core.helper.email.EmailHelper;
import br.com.kotar.core.util.StringUtil;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class EmailService {

	@Autowired
	private Environment env;
	
	public void sendEmail(EmailHelper emailHelper) throws Exception {
		
		emailHelper.setDe(env.getProperty("email.endereco.envio"));
		
		String dsServidor = env.getProperty("email.smtp");
		String dsPort =  env.getProperty("email.smtp.porta");
		String dsUsuario =  env.getProperty("email.username");
		String dsSenha =  env.getProperty("email.password");

		Properties props = this.loadApplicationProps(dsServidor, dsPort);
		KotharAuthenticator auth = new KotharAuthenticator(dsUsuario, dsSenha);
		Session sessao = Session.getInstance(props, auth);
		Message message = this.getNewMimeMessage(sessao, emailHelper);

		Transport.send(message);
	}

	private Properties loadApplicationProps(String servidor, String porta) {
		Properties props = new Properties();
		props.setProperty("mail.smtp.host", servidor);
		props.setProperty("mail.smtp.port", porta);
		props.setProperty("mail.smtp.ssl.trust", servidor);
		props.setProperty("mail.smtp.starttls.enable", "true");
		props.setProperty("mail.smtp.auth", "true");
		props.setProperty("mail.debug", "true");
		
		
		
		return props;
	}

	private MimeMessage getNewMimeMessage(Session sessao, EmailHelper emailHelper)
			throws Exception {
		
        MimeMultipart multipart = new MimeMultipart("related");

        // first part  (the html)
        BodyPart messageBodyPart = new MimeBodyPart();
        String htmlText = emailHelper.getCorpo();
        messageBodyPart.setContent(htmlText, "text/html; charset=UTF-8 ");
        messageBodyPart.setHeader("Content-Encoding", "UTF-8");
        multipart.addBodyPart(messageBodyPart);
        
        if (StringUtil.temValor(emailHelper.getCaminhoImagem())){
        	
        	File out = new File("tmp/"+emailHelper.getCaminhoImagem());
        	
        	if (!out.exists()) {
            	out.mkdirs();
            	BufferedImage buff = ImageIO.read(EmailService.class.getResourceAsStream(emailHelper.getCaminhoImagem()));
            	ImageIO.write(buff, "png", out);
        	}        	
        	
	        // second part (the image)
	        messageBodyPart = new MimeBodyPart();
	        DataSource fds = new FileDataSource(out);
	        messageBodyPart.setDataHandler(new DataHandler(fds));
	        messageBodyPart.setHeader("Content-ID","<image>");
	        multipart.addBodyPart(messageBodyPart);
        }
        
		MimeMessage message = new MimeMessage(sessao);
		String name = emailHelper.getNomeEnvio();
		InternetAddress address = new InternetAddress();
		address.setAddress(emailHelper.getPara());
		address.setPersonal(name);
		message.setFrom(new InternetAddress(emailHelper.getDe(), name));
		message.setSubject(emailHelper.getAssunto(), "utf-8");
		message.addRecipient(Message.RecipientType.TO,
				new InternetAddress(emailHelper.getPara()));
		message.setReplyTo(new InternetAddress[] { new InternetAddress(emailHelper.getDe()) });
		message.setSentDate(new Date());

        message.setContent(multipart);
        
		return message;
	}

	public class KotharAuthenticator extends Authenticator {
		private String email;
		private String senha;

		public KotharAuthenticator(String email, String senha) {
			this.email = email;
			this.senha = senha;
		}
		public javax.mail.PasswordAuthentication getPasswordAuthentication() {
			return new javax.mail.PasswordAuthentication(email, senha);
		}
	}

	public void sendEmail(String titulo, String corpo, String para)
			throws Exception {
		EmailHelper emailHelper = new EmailHelper(titulo, corpo, para);
		this.sendEmail(emailHelper);
	}

}
