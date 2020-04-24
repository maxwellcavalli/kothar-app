package br.com.kotar.web.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.kotar.core.component.Messages;
import br.com.kotar.domain.helper.MenuHelper;
import br.com.kotar.domain.security.Usuario;

@Service
public class AcessosService {
	
	@Autowired Messages messages;
	
	public List<MenuHelper> getMenus(Usuario usuario) throws Exception {
		boolean admin = usuario.isAdmin();
		return getMenus(admin);
	}
	
	private List<MenuHelper> getMenus(boolean admin){
		List<MenuHelper> _l = new ArrayList<>();
		_l.add(new MenuHelper(messages.get("menu.home"), "/dashboard", "home", true));
		_l.add(new MenuHelper(messages.get("menu.requests"), "/modules/budget/resposta/resposta-list", "email", "menu_resposta_cotacao", true));
		_l.add(new MenuHelper(messages.get("menu.my.budgets"), "/modules/budget/cotacao/cotacao-list", "assignment", "menu_cotacao", true));
		_l.add(new MenuHelper(messages.get("menu.suppliers"), "/modules/general/fornecedor/fornecedor-list", "domain", "menu_fornecedor", true));
		
		MenuHelper _reports = new MenuHelper(messages.get("menu.reports"), "description", true);
		
		MenuHelper _supplier = new MenuHelper(messages.get("menu.reports.supplier"), true);
		_supplier.getChildren().add(new MenuHelper(messages.get("menu.reports.best.clients"), "/modules/reports/melhores-clientes-fornecedor", "", "menu_melhores_clientes_fornecedor", true));
		_reports.getChildren().add(_supplier);
		
		_l.add(_reports);
		
		if (admin){
			MenuHelper _settings = new MenuHelper(messages.get("menu.settings"), "settings", true);
			
			MenuHelper _produto = new MenuHelper(messages.get("menu.product"), true);
			_produto.getChildren().add(new MenuHelper(messages.get("menu.category"), "/modules/general/categoria-produto/categoria-produto-list", "", "menu_categoria", true));
			_produto.getChildren().add(new MenuHelper(messages.get("menu.product.group"), "/modules/general/grupo-produto/grupo-produto-list", "", "menu_categoria", true));
			_produto.getChildren().add(new MenuHelper(messages.get("menu.product"), "/modules/general/produto/produto-list", "", "menu_produto", true));
			_settings.getChildren().add(_produto);
			
			MenuHelper _endereco = new MenuHelper(messages.get("menu.address"), true);
			_endereco.getChildren().add(new MenuHelper(messages.get("menu.state"), "/modules/general/estado/estado-list", "", "menu_estado", true));
			_endereco.getChildren().add(new MenuHelper(messages.get("menu.city"), "/modules/general/cidade/cidade-list", "", "menu_cidade", true));
			_endereco.getChildren().add(new MenuHelper(messages.get("menu.district"), "/modules/general/bairro/bairro-list", "", "menu_bairro", true));
			_endereco.getChildren().add(new MenuHelper(messages.get("menu.postal.code"), "/modules/general/cep/cep-list", "", "menu_cep", true));
			_settings.getChildren().add(_endereco);
			
			_settings.getChildren().add(new MenuHelper(messages.get("menu.client"), "/modules/general/cliente/cliente-list", "", "menu_cliente", true));
	
			MenuHelper _seguranca = new MenuHelper(messages.get("menu.security"), true);
			_seguranca.getChildren().add(new MenuHelper(messages.get("menu.user"), "/modules/security/usuario/usuario-list", "", "menu_usuario", true));
			_seguranca.getChildren().add(new MenuHelper(messages.get("menu.integration.pendings"), "/modules/security/pendencia-integracao/pendencia-integracao-list", "", "menu_pendencia_integracao", true));
			_settings.getChildren().add(_seguranca);
			
			_l.add(_settings);
		}
				
		//internal pages - invisible
		_l.add(new MenuHelper("RespostaFormComponent", "/modules/budget/resposta/resposta-form", "", "", false));
		_l.add(new MenuHelper("CotacaoFormComponent", "/modules/budget/cotacao/cotacao-form", "", "", false));
		_l.add(new MenuHelper("CotacaoConsultaComponent", "/modules/budget/cotacao/cotacao-consulta", "", "", false));
		_l.add(new MenuHelper("CotacaoProdutosComponent", "/modules/budget/cotacao/cotacao-produtos", "", "", false));		
		_l.add(new MenuHelper("RespostaFormComponent", "/modules/budget/resposta/resposta-form", "", "", false));
		_l.add(new MenuHelper("ProfileModule", "/profile", "", "", false));
		
		
		if (admin){
			//admin
			_l.add(new MenuHelper("BairroFormComponent", "/modules/general/bairro/bairro-form", "", "", false));
			_l.add(new MenuHelper("CategoriaProdutoFormComponent", "/modules/general/categoria-produto/categoria-produto-form", "", "", false));
			_l.add(new MenuHelper("CepFormComponent", "/modules/general/cep/cep-form", "", "", false));
			_l.add(new MenuHelper("CidadeFormComponent", "/modules/general/cidade/cidade-form", "", "", false));
			_l.add(new MenuHelper("ClienteFormComponent", "/modules/general/cliente/cliente-form", "", "", false));
			_l.add(new MenuHelper("EstadoFormComponent", "/modules/general/estado/estado-form", "", "", false));
			_l.add(new MenuHelper("FornecedorFormComponent", "/modules/general/fornecedor/fornecedor-form", "", "", false));
			_l.add(new MenuHelper("GrupoProdutoFormComponent", "/modules/general/grupo-produto/grupo-produto-form", "", "", false));
			_l.add(new MenuHelper("ProdutoFormComponent", "/modules/general/produto/produto-form", "", "", false));
			
			_l.add(new MenuHelper("PendenciaIntegracaoFormComponent", "/modules/security/pendencia-integracao/pendencia-integracao-form", "", "", false));
			_l.add(new MenuHelper("UsuarioFormComponent", "/modules/security/usuario/usuario-form", "", "", false));
		}
		
		return _l;
	}
	
}
