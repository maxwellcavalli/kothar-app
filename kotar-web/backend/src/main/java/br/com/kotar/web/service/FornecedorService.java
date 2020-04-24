package br.com.kotar.web.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import br.com.kotar.core.exception.RecordNotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.kotar.core.repository.CrudRepository;
import br.com.kotar.core.service.BaseCrudService;
import br.com.kotar.core.util.formatter.CpfCnpjFormatter;
import br.com.kotar.domain.business.Cep;
import br.com.kotar.domain.business.Cliente;
import br.com.kotar.domain.business.ClienteFornecedor;
import br.com.kotar.domain.business.EnderecoComplemento;
import br.com.kotar.domain.business.Fornecedor;
import br.com.kotar.domain.business.FornecedorGrupoProduto;
import br.com.kotar.domain.business.GrupoProduto;
import br.com.kotar.domain.helper.AvaliacaoFornecedorHelper;
import br.com.kotar.domain.helper.MelhoresClientesFornecedorHelper;
import br.com.kotar.domain.security.Usuario;
import br.com.kotar.web.repository.impl.FornecedorDAOImpl;

@Service
public class FornecedorService extends BaseCrudService<Fornecedor> {

	//@formatter:off
	//@Autowired FornecedorRepository fornecedorRepository;
	@Autowired UsuarioService usuarioService;
	@Autowired FornecedorGrupoProdutoService fornecedorGrupoProdutoService;
	@Autowired ClienteService clienteService;
	@Autowired ClienteFornecedorService clienteFornecedorService;
	@Autowired FornecedorDAOImpl fornecedorDAO;
	
	@Autowired GeoService geoService;
	
	//@formatter:on

	@Override
	public CrudRepository<Fornecedor> getRepository() {
		return fornecedorDAO;
	}
	

	public Fornecedor saveOrUpdate(Fornecedor fornecedor, Usuario usuario) throws Exception {
		List<GrupoProduto> grupos = fornecedor.getGrupos();
		if (grupos == null) {
			grupos = new ArrayList<>();
		}

		if (!usuario.isAdmin() && !usuario.isLoginCliente()) {
			throw new Exception(messages.get("fornecedor.not.agent.login"));
		}
		
		Cep cep = fornecedor.getCep();
		EnderecoComplemento enderecoComplemento = fornecedor.getEnderecoComplemento();
		if (enderecoComplemento.getNumero() == null){
			enderecoComplemento.setNumero("");
		}
		
		boolean hasChanged = false;
		if (enderecoComplemento.getLatitude() == null || enderecoComplemento.getLongitude() == null || 
				(enderecoComplemento.getLatitude().doubleValue() == 0.0 && enderecoComplemento.getLongitude().doubleValue() == 0.0)) {

			Optional<Fornecedor> fornecedorOptional = findById(fornecedor.getId());
			Fornecedor _fornecedorOld = null;
			if (fornecedorOptional.isPresent()){
				 _fornecedorOld = fornecedorOptional.get();
			}

			if (_fornecedorOld != null){
				Cep _cepOld = _fornecedorOld.getCep();
				EnderecoComplemento _enderecoComplementoOld = _fornecedorOld.getEnderecoComplemento();
				if (_enderecoComplementoOld.getNumero() == null){
					_enderecoComplementoOld.setNumero("");
				}
				
				if (_cepOld.getId().longValue() != cep.getId().longValue() ||
					!enderecoComplemento.getNumero().equals(_enderecoComplementoOld.getNumero())){
					
					hasChanged = true;
				}
			} else {
				hasChanged = true;
			}
		} else {	
			hasChanged = true;
		}
		
		if (hasChanged){
			enderecoComplemento = geoService.geoReferenciar(cep, enderecoComplemento);
			fornecedor.setEnderecoComplemento(enderecoComplemento);
		}
		
		List<ClienteFornecedor> clientes = fornecedor.getClientes();

		String cpfCnpj = fornecedor.getCpfCnpj();
		cpfCnpj = CpfCnpjFormatter.cleanFormat(cpfCnpj);
		fornecedor.setCpfCnpj(cpfCnpj);
		
		if (fornecedor.getUuid() == null || fornecedor.getUuid().isEmpty()){
			String uuid = UUID.randomUUID().toString().substring(0, 30);
			fornecedor.setUuid(uuid);
		}
		
		fornecedor = getRepository().save(fornecedor);

		fornecedorGrupoProdutoService.saveOrUpate(fornecedor, grupos);

		if (!usuario.isAdmin()) {
			Cliente cliente = clienteService.findByUsuario(usuario);
			clienteFornecedorService.saveOrUpdate(fornecedor, cliente);
		} else {
			clienteFornecedorService.saveOrUpdate(fornecedor, clientes);
		}

		return fornecedor;
	}

	public void delete(Fornecedor fornecedor, Usuario usuario) throws Exception {
		Cliente cliente = clienteService.findByUsuario(usuario);
		if (cliente == null) {
			throw new Exception(messages.get("fornecedor.not.agent.login"));
		}

		fornecedorGrupoProdutoService.delete(fornecedor);
		clienteFornecedorService.delete(fornecedor, cliente);
		getRepository().deleteById(fornecedor.getId());
	}

	public Optional<Fornecedor> findById(Long id, Usuario usuario) throws Exception {
		Optional<Fornecedor> fornecedorOptional = getRepository().findById(id);

		if (!fornecedorOptional.isPresent()){
			return Optional.empty();
		}

		Fornecedor fornecedor = fornecedorOptional.get();
		List<FornecedorGrupoProduto> list = fornecedorGrupoProdutoService.findByFornecedor(fornecedor);
		List<GrupoProduto> listGrupo = new ArrayList<>();

		for (FornecedorGrupoProduto f : list) {
			listGrupo.add(f.getGrupoProduto());
		}

		fornecedor.setGrupos(listGrupo);

		// carregar o cliente do fornecedor
		if (usuario.isAdmin()) {
			List<ClienteFornecedor> clientes = clienteFornecedorService.findByFornecedor(fornecedor);
			fornecedor.setClientes(clientes);
		}

		return Optional.of(fornecedor);
	}

	public List<Fornecedor> findByCliente(Cliente cliente) throws Exception {
		return ((FornecedorDAOImpl)getRepository()).findByCliente(cliente);
	}
	
	public Fornecedor findByUuid(String uuid) throws Exception {
		return ((FornecedorDAOImpl)getRepository()).findByUuid(uuid);
	}

	public Page<Fornecedor> findByNomeOrCpfCnpjLikeIgnoreCase(String nome, String cpfCnpj, Usuario usuario, Pageable pageable) throws Exception {
		return fornecedorDAO.findByNomeOrCpfCnpjLikeIgnoreCase(nome, cpfCnpj, usuario, pageable);
	}

	public List<Fornecedor> findByGrupoProduto(GrupoProduto grupoProduto) throws Exception {
		return fornecedorDAO.findByGrupoProduto(grupoProduto);
	}
	
	public List<AvaliacaoFornecedorHelper> findAvaliacaoFornecedor(Fornecedor fornecedor) throws Exception {
		return ((FornecedorDAOImpl)getRepository()).findAvaliacaoFornecedor(fornecedor);
	}
	
	public List<MelhoresClientesFornecedorHelper> findMelhoresClientes(Fornecedor fornecedor) throws Exception {
		return ((FornecedorDAOImpl)getRepository()).findMelhoresClientes(fornecedor);
	}
}
