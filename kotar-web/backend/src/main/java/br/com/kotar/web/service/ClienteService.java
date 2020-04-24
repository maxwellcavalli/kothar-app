package br.com.kotar.web.service;

import java.util.List;
import java.util.Optional;

import br.com.kotar.core.exception.RecordNotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.kotar.core.repository.CrudRepository;
import br.com.kotar.core.service.BaseCrudService;
import br.com.kotar.core.util.formatter.CpfCnpjFormatter;
import br.com.kotar.domain.business.Cliente;
import br.com.kotar.domain.business.ClienteEndereco;
import br.com.kotar.domain.security.Usuario;
import br.com.kotar.web.repository.impl.ClienteDAOImpl;

@Service
public class ClienteService extends BaseCrudService<Cliente> {

    //@formatter:off
    @Autowired
    UsuarioService usuarioService;
    @Autowired
    ClienteDAOImpl clienteDAO;
    @Autowired
    ClienteEnderecoService clienteEnderecoService;
    //@formatter:on

    @Override
    public CrudRepository<Cliente> getRepository() {
        return clienteDAO;
    }

    public Page<Cliente> findByNomeOrCpfLikeIgnoreCase(String nome, String cpf, Usuario usuario,
                                                       Pageable pageable) throws Exception {

        return ((ClienteDAOImpl) getRepository()).findByNomeOrCpfLikeIgnoreCase(nome, cpf, usuario, pageable);
    }

    public Cliente saveOrUpdate(Cliente cliente) throws Exception {
        boolean isNew = cliente.getId() == null || cliente.getId().longValue() == 0;
        Usuario usuario = cliente.getUsuario();

        if (!isNew) {
            if (usuario == null) {
                Optional<Cliente> optionalCliente = findById(cliente.getId());

                if (!optionalCliente.isPresent()){
                    throw new RecordNotFound();
                }

                Cliente _cli = optionalCliente.get();
                usuario = _cli.getUsuario();
            }
        }

        usuario.setNome(cliente.getNome());
        usuario = usuarioService.saveOrUpdate(usuario);
        cliente.setUsuario(usuario);

        String cpf = cliente.getCpf();
        if (cpf != null && !cpf.isEmpty()) {
            cpf = CpfCnpjFormatter.cleanFormat(cpf);
            cliente.setCpf(cpf);
        }

        //salvar os enderecos do cliente
        List<ClienteEndereco> listEnderecos = cliente.getListEnderecos();
        cliente = getRepository().save(cliente);

        clienteEnderecoService.persistir(cliente, listEnderecos);

        return cliente;

    }

    public void delete(Cliente cliente) throws Exception {
        Usuario u = cliente.getUsuario();
        getRepository().deleteById(cliente.getId());
        usuarioService.delete(u);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public Cliente findByUsuario(Usuario usuario) throws Exception {

        Cliente cliente = ((ClienteDAOImpl) getRepository()).findByUsuario(usuario);

        if (cliente == null) {
            if (!usuario.isAdmin()) {
                throw new Exception(messages.get("cliente.not.found"));
            } else {
                return null;
            }
        } else {
            List<ClienteEndereco> listEnderecos = clienteEnderecoService.findByCliente(cliente);
            cliente.setListEnderecos(listEnderecos);
            return cliente;
        }
    }

    public boolean isExistsCliente(Usuario usuario) {
        Cliente cliente = ((ClienteDAOImpl) getRepository()).findByUsuario(usuario);
        return cliente != null;
    }


    @Override
    public Optional<Cliente> findById(Long id) {

        Optional<Cliente> clienteOptional = super.findById(id);
        if (!clienteOptional.isPresent()) {
            return Optional.empty();
        }

        Cliente cliente = clienteOptional.get();

        List<ClienteEndereco> listEnderecos = clienteEnderecoService.findByCliente(cliente);
        cliente.setListEnderecos(listEnderecos);

        return Optional.of(cliente);
    }

}
