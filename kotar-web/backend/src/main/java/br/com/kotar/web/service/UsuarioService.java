package br.com.kotar.web.service;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.*;

import br.com.kotar.core.exception.RecordNotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

import br.com.kotar.core.helper.email.EmailHelper;
import br.com.kotar.core.repository.CrudRepository;
import br.com.kotar.core.service.BaseCrudService;
import br.com.kotar.core.util.Constants;
import br.com.kotar.core.util.DataUtil;
import br.com.kotar.core.util.StringUtil;
import br.com.kotar.core.util.http.HttpUtil;
import br.com.kotar.domain.business.Cliente;
import br.com.kotar.domain.security.LogUsuario;
import br.com.kotar.domain.security.Usuario;
import br.com.kotar.web.repository.UsuarioRepository;
import br.com.kotar.web.service.email.EmailService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class UsuarioService extends BaseCrudService<Usuario> {

	//@formatter:off
	@Autowired UsuarioRepository usuarioRepository;
	@Autowired Environment env;
	@Autowired ClienteService clienteService;
	@Autowired EmailService emailService;

	//@formatter:on
	
	static long PRAZO_LIMITE_TOKEN = 24;

	private static final JacksonFactory jacksonFactory = new JacksonFactory();

	@Override
	public CrudRepository<Usuario> getRepository() {
		return usuarioRepository;
	}

	public Usuario saveOrUpdate(Usuario usuario) throws Exception {
		boolean isNew = usuario.getId() == null || usuario.getId().longValue() == 0;

		String password = usuario.getPasswordLogin();
		if (password != null && !password.trim().isEmpty()) {
			password = encodePassword(password);
		}

		if (isNew) {
			String username = usuario.getUsername();
			List<Usuario> l = usuarioRepository.findByUsernameEqualsIgnoreCase(username);

			if (!l.isEmpty()) {
				throw new Exception(messages.get("usuario.username.duplicado"));
			}

			usuario.setPassword(password);
		} else {
			Optional<Usuario> usuarioOptional = findById(usuario.getId());

			if (usuarioOptional.isPresent()){
				throw new RecordNotFound();
			}

			Usuario old = usuarioOptional.get();
			String oldPassword = old.getPassword();
			// oldPassword = encodePassword(oldPassword);

			if (password != null && !password.trim().isEmpty()) {
				if (!password.equals(oldPassword)) {
					usuario.setPassword(password);
				} else {
					if (usuario.getPassword() == null) {
						usuario.setPassword(oldPassword);
					}
				}
			} else {
				usuario.setPassword(oldPassword);
			}
		}

		if (!StringUtil.temValor(usuario.getUsername())) {
			throw new Exception(messages.get("usuario.username.nao.preenchido"));
		}
		if (!StringUtil.temValor(usuario.getEmail())) {
			throw new Exception(messages.get("usuario.email.nao.preenchido"));
		}
		if (!StringUtil.temValor(usuario.getNome())) {
			throw new Exception(messages.get("usuario.nome.nao.preenchido"));
		}
		if (!StringUtil.temValor(usuario.getPassword())) {
			throw new Exception(messages.get("usuario.password.nao.preenchido"));
		}

		usuario.setUpdated(Calendar.getInstance().getTime());

		if (!isNew) {
			if (usuario.isAdmin()) {
				boolean hasCliente = clienteService.isExistsCliente(usuario);
				if (hasCliente) {
					usuario.setAdmin(false);
				}
			}
		}

		return usuarioRepository.save(usuario);
	}

	public Usuario validateUser(String username, String password) throws Exception {

		password = encodePassword(password);

		Usuario usuario = usuarioRepository.findByUsernameAndPassword(username, password);
		if (usuario == null) {
			throw new Exception(messages.get("login.invalido"));
		}

		if (usuario.getLogAcesso() == null) {
			usuario.setLogAcesso(new HashSet<>());
		}

		Cliente cliente = null;

		try {
			cliente = clienteService.findByUsuario(usuario);
		} catch (Exception e) {
		}

		usuario.setLoginCliente(cliente != null);

		usuario.setLastLogin(Calendar.getInstance().getTime());
		usuario.getLogAcesso().add(new LogUsuario(usuario, Calendar.getInstance().getTime()));
		usuarioRepository.save(usuario);

		String token = generateToken(usuario);
		usuario.setToken(token);

		return usuario;
	}

	public void delete(Usuario usuario) throws Exception {
		usuarioRepository.deleteById(usuario.getId());
	}

	private String encodePassword(String password) throws NoSuchAlgorithmException, NoSuchProviderException {
		MessageDigest mda = MessageDigest.getInstance("SHA-256");
		byte[] digesta = mda.digest(password.getBytes());
		return new String(Base64.getEncoder().encode(digesta));
	}

	public Usuario validateFacebookTokenUser(String idFacebook, String tokenFacebook, Usuario usuario) throws Exception {

		String url = "https://graph.facebook.com/me?locale=en_US&fields=name,email&access_token=" + tokenFacebook;

		String jsonResposta = HttpUtil.getHttpRequest(url);
		ObjectMapper mapper = new ObjectMapper();
		TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {
		};

		HashMap<String, Object> o = mapper.readValue(jsonResposta, typeRef);

		// JSONObject jsonObject = new JSONObject(jsonResposta);

		String idF = null;
		String nome = "", email = "";

		try {
			idF = (String) o.get("id");
			nome = (String) o.get("name");
			email = (String) o.get("email");
		} catch (Exception e) {
		}

		if (idF == null) {
			@SuppressWarnings("unchecked")
			Map<String, Object> error = (Map<String, Object>) o.get("error");
			if (error != null) {
				String message = (String) error.get("message");
				throw new Exception(message);
			} else {
				throw new Exception(messages.get("login.invalido"));
			}
		}

		if (StringUtil.temValor(nome)) {
			usuario.setNome(nome);
		}

		if (StringUtil.temValor(email)) {
			usuario.setEmail(email);
		}

		usuario.setUsername(idFacebook);
		usuario.setPasswordLogin(tokenFacebook);

		return usuario;
	}

	public Usuario validateGoogleTokenUser(String idGoogle, String tokenGoogle, Usuario usuario) throws Exception {

		HttpTransport httpTransport = null;
		if (Constants.HAS_PROXY == true) {
			Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(Constants.PROXY.getHostName(), Constants.PROXY.getPort()));
			httpTransport = new NetHttpTransport.Builder().setProxy(proxy).build();
		} else {
			httpTransport = new NetHttpTransport.Builder().build();// UrlFetchTransport.getDefaultInstance();
		}

		List<String> inssuers = new ArrayList<>();
		inssuers.add("https://accounts.google.com");
		inssuers.add("accounts.google.com");

		List<String> googleAppIds = new ArrayList<>();
		googleAppIds.add(env.getProperty("google.client.id.android"));
		googleAppIds.add(env.getProperty("google.client.id.ios"));

		GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(httpTransport, jacksonFactory).setAudience(googleAppIds)
				.setIssuers(inssuers).build();
		GoogleIdToken idToken = verifier.verify(tokenGoogle);

		if (idToken != null) {
			Payload payload = idToken.getPayload();

			// Print user identifier
			String userId = payload.getSubject();
			System.out.println("User ID: " + userId);

			// Get profile information from payload
			// String email = payload.getEmail();
			// boolean emailVerified =
			// Boolean.valueOf(payload.getEmailVerified());
			String nome = (String) payload.get("name");
			String email = payload.getEmail();
			// String pictureUrl = (String) payload.get("picture");
			// String locale = (String) payload.get("locale");
			// String familyName = (String) payload.get("family_name");
			// String givenName = (String) payload.get("given_name");

			// Use or store profile information
			// ...

			if (StringUtil.temValor(nome)) {
				usuario.setNome(nome);
			}

			if (StringUtil.temValor(email)) {
				usuario.setEmail(email);
			}

			usuario.setUsername(idGoogle);
			usuario.setPasswordLogin(tokenGoogle);

		} else {
			throw new Exception(messages.get("login.invalido"));
		}

		return usuario;
	}

	public String generateToken(Usuario usuario) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);

		// Usuario _u = new Usuario();
		// _u.setId(usuario.getId());
		// _u.setNome(usuario.getNome());

		// String userStr = mapper.writeValueAsString(_u);

		//@formatter:off
		String jwtToken = Jwts.builder()
				//.setSubject(userStr)
				.claim("roles", "user")
				.setIssuedAt(new Date())
				// .setExpiration(arg0)
				.signWith(SignatureAlgorithm.HS256, "secretkey").compact();
		//@formatter:on

		return jwtToken;

	}

	public Usuario findByEmail(String email) {
		return usuarioRepository.findByEmail(email);
	}

	public Usuario findByIdFacebook(String idFacebook) {
		return usuarioRepository.findByIdFacebook(idFacebook);
	}

	public Usuario findByIdGoogle(String idGoogle) {
		return usuarioRepository.findByIdGoogle(idGoogle);
	}
	
	public Usuario findByResetToken(String resetToken) throws Exception {
		Usuario usuario = usuarioRepository.findByResetToken(resetToken);
		if (usuario == null){
			throw new Exception(messages.get("reset.password.invalid.uid"));
		} 
		
		Date dataHoje = Calendar.getInstance().getTime();
		Date dateResetToken = usuario.getDateResetToken();
		Long dif = DataUtil.diferrenceBetweenTwoDatesInHours(dataHoje, dateResetToken);
		if (Math.abs(dif) > PRAZO_LIMITE_TOKEN) {
			throw new Exception(messages.get("reset.password.expired.uid"));
		}
		
		return usuario;
	}

	public void resetPassword(Usuario usuario) throws Exception {
		String resetToken = StringUtil.generateRandString(12);

		usuario.setResetToken(resetToken);
		usuario.setDateResetToken(Calendar.getInstance().getTime());

		super.saveOrUpdate(usuario);

		EmailHelper emailHelper = new EmailHelper();

		String corpo = EmailHelper.getCorpoBase();

		//@formatter:off
		corpo = corpo.replace(":P_CONTEUDO",
				" Olá, este email foi solicitado pelo APP Kothar para redefinir a senha de seu usuário.<br>" + 
						"<br>" + 
						"Clique no link abaixo para redefinir sua senha:<br>" + 
						"<a href=http://www.kothar.com.br/reset/" + resetToken+ ">" + 
						"http://www.kothar.com.br/reset/" + resetToken + "" + 
						"</a>");
		//@formatter:on

		emailHelper.setCorpo(corpo);

		emailHelper.setPara(usuario.getEmail());
		emailHelper.setAssunto("Kothar - Redefinição de Senha");
		emailHelper.setCaminhoImagem("/static/cabecalho_email_kothar.png");
		emailHelper.setNomeEnvio("Suporte Kothar");
		emailService.sendEmail(emailHelper);
	}

	public void trocarSenha(Usuario usuario) throws Exception {
		String password = usuario.getPassword();
		password = encodePassword(password);

		Optional<Usuario> usuarioOptional = findById(usuario.getId());

		if (!usuarioOptional.isPresent()){
			throw new RecordNotFound();
		}

		usuario = usuarioOptional.get();
		
		usuario.setResetToken(null);
		usuario.setDateResetToken(null);
		usuario.setPassword(password);

		super.saveOrUpdate(usuario);
	}

}
