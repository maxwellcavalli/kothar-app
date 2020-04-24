package br.com.kotar.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.com.kotar.core.exception.InvalidTokenException;
import br.com.kotar.core.helper.response.ResponseHelper;
import br.com.kotar.core.service.BaseCrudService;
import br.com.kotar.domain.helper.MenuHelper;
import br.com.kotar.domain.security.Usuario;
import br.com.kotar.web.controller.base.BaseLoggedUserController;
import br.com.kotar.web.service.AcessosService;

@RestController
@RequestMapping(value = { "/api/secure/acessos" })
public class AcessosController extends BaseLoggedUserController<Usuario> {

	//@formatter:off
	@Autowired AcessosService acessosService;
	//@formatter:on

	@Override
	public BaseCrudService<Usuario> getService() {
		return null;
	}

	@Override
	protected Usuario onSave(Usuario domain) throws Exception {
		throw new NotImplementedException();

	}

	@Override
	protected Usuario onUpdate(Usuario domain) throws Exception {
		throw new NotImplementedException();
	}

	@RequestMapping(value = "/menus", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseHelper<List<MenuHelper>>> getMenus() {
		ResponseHelper<List<MenuHelper>> _ret = new ResponseHelper<>(new ArrayList<>());

		try {
			Usuario usuario = getLoggedUser();
			List<MenuHelper> _l = acessosService.getMenus(usuario);
			_ret = new ResponseHelper<List<MenuHelper>>(_l);
		} catch (InvalidTokenException e) {

		} catch (Exception e) {
			String message = ExceptionUtils.getRootCauseMessage(e);
			ResponseHelper<List<MenuHelper>> helper = new ResponseHelper<>(message);
			return new ResponseEntity<ResponseHelper<List<MenuHelper>>>(helper, HttpStatus.EXPECTATION_FAILED);
		}

		return new ResponseEntity<ResponseHelper<List<MenuHelper>>>(_ret, HttpStatus.OK);
	}

}
