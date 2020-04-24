package br.com.kotar.web.soap.endpoint;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import br.com.kotar.domain.business.Cliente;
import br.com.kotar.domain.business.Fornecedor;
import br.com.kotar.domain.security.Usuario;
import br.com.kotar.web.service.ClienteService;
import br.com.kotar.web.service.FornecedorService;
import br.com.kotar.web.soap.endpoint.base.BaseEndpoint;
import br.com.kotar.web.soap.schema.fornecedor.GetFornecedoresRequest;
import br.com.kotar.web.soap.schema.fornecedor.GetFornecedoresResponse;

@Endpoint
public class FornecedorEndpoint extends BaseEndpoint {

	//@formatter:off
	@Autowired FornecedorService fornecedorService;
	@Autowired ClienteService clienteService;
	
	//@formatter:on

	private static final String NAMESPACE_URI = "http://kotar.com.br/web/soap/schema/fornecedor";

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getFornecedoresRequest")
	@ResponsePayload
	public GetFornecedoresResponse getFornecedores(@RequestPayload GetFornecedoresRequest request) throws Exception {
		GetFornecedoresResponse response = new GetFornecedoresResponse();
		String token = request.getToken();

		Usuario usuario = validateToken(token);

		List<br.com.kotar.web.soap.schema.common.Fornecedor> _l = findFornecedores(usuario);

		response.getFornecedor().addAll(_l);
		return response;
	}

	private List<br.com.kotar.web.soap.schema.common.Fornecedor> findFornecedores(Usuario usuario) throws Exception {
		Cliente cliente = clienteService.findByUsuario(usuario);
		List<Fornecedor> list = fornecedorService.findByCliente(cliente);
		
		List<br.com.kotar.web.soap.schema.common.Fornecedor> ret = new ArrayList<>();
		
		list.forEach(el -> {
			br.com.kotar.web.soap.schema.common.Fornecedor _f = new br.com.kotar.web.soap.schema.common.Fornecedor();
			_f.setNome(el.getNome());
			_f.setCnpj(el.getCpfFormat());
			_f.setIdentificacao(el.getUuid());
			
			ret.add(_f);
		});
		
		return ret;
	}
}
