package br.com.kotar.core.helper.email;

import java.io.Serializable;

public class EmailHelper implements Serializable {

	private static final long serialVersionUID = 1359391853344609585L;

	private String assunto;
	private String corpo;
	private String para;
	private String nomeEnvio;
	private String emailEnvio;
	private String caminhoImagem;
	
	public EmailHelper() {
		super();
	}

	public EmailHelper(String assunto, String corpo, String para) {
		super();
		this.assunto = assunto;
		this.corpo = corpo;
		this.para = para;
	}
	
	public EmailHelper(String assunto, String corpo, String para, String caminhoImagem) {
		super();
		this.assunto = assunto;
		this.corpo = corpo;
		this.para = para;
		this.caminhoImagem = caminhoImagem;
	}

	public String getPara() {
		return para;
	}

	public void setPara(String para) {
		this.para = para;
	}

	public String getDe() {
		return emailEnvio;
	}

	public void setDe(String de) {
		this.emailEnvio = de;
	}

	public String getAssunto() {
		return assunto;
	}

	public void setAssunto(String assunto) {
		this.assunto = assunto;
	}

	public String getCorpo() {
		return corpo;
	}

	public void setCorpo(String corpo) {
		this.corpo = corpo;
	}
	
	public static String getCorpoBase(){
		StringBuilder email = new StringBuilder();
		email.append("<html>																																	");
		email.append("	<body>       ");
		email.append("		<table align=\"center\" width=\"800px\">");
		email.append("			<tr align=\"center\">"); 
		email.append("				<td align=\"center\">");
		email.append("					<img src='cid:image' />");                                            
		email.append("				</td>");
		email.append("			</tr>");
		email.append("		</table>");
		email.append("      <table align=\"center\" width=\"800px\">");
		email.append("			<tr align=\"left\"> ");
		email.append("				<td align=\"left\">");
		email.append("					<div style=\"padding: 	5px 20px;\">");                                                                                                              
//		email.append("						<div style=\"font-weight: bold; margin: 20px 0;\">:P_TITULO</div>");                                                                                    
		email.append("");						                                                                                                                                
		email.append("						:P_CONTEUDO");                                                                                                                     
		email.append("");			                                                                                                                                          
		email.append("						<div style=\"margin: 20px 0;\" />");                                                                                                          
		email.append("");						                                                                                                                                
		email.append("						<p>Atenciosamente,</p>");                                                                                                          
		email.append("						<p>Suporte Kothar.</p>");                                                                                                    
		email.append("");						                                                                                                                                
		email.append("					</div>");     						
		email.append("				</td>");                                                                                                                              
		email.append("			</tr>");
		email.append("		</table>");
		email.append("	</body>");                                                                                                                                 
		email.append("</html>");                                                                                                                                   
	
		return email.toString();
	}

	public String getCaminhoImagem() {
		return caminhoImagem;
	}

	public void setCaminhoImagem(String caminhoImagem) {
		this.caminhoImagem = caminhoImagem;
	}

	public String getNomeEnvio() {
		return nomeEnvio;
	}

	public void setNomeEnvio(String nomeEnvio) {
		this.nomeEnvio = nomeEnvio;
	}

}
