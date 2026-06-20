/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Gestores;

import DAO.ProveedorDAO;
import Entidades.Cliente;
import Entidades.Proveedor;
import DAO.ClienteDAO;
import java.util.List;
/**
 *
 * @author equipo
 */
public class GestorContactos {
    
    // Ahora dependemos de los DAO en lugar de ArrayList
    private ClienteDAO clienteDAO;
    private ProveedorDAO proveedorDAO;

    public GestorContactos() {
        this.clienteDAO = new ClienteDAO();
        this.proveedorDAO = new ProveedorDAO();
    }

    // ==========================================
    // LÓGICA DE CLIENTES
    // ==========================================

    public void registrarCliente(Cliente nuevoCliente) {
        if (nuevoCliente == null) {
            throw new IllegalArgumentException("Error: No se puede registrar un cliente nulo.");
        }

        // Regla de Negocio: Evitar duplicados consultando a la BD
        if (clienteDAO.buscarPorCodigo(nuevoCliente.getCodigo()) != null) {
            throw new IllegalArgumentException("Error: Ya existe un cliente registrado con el código " + nuevoCliente.getCodigo());
        }

        // Si es válido, lo mandamos a la base de datos
        boolean exito = clienteDAO.registrar(nuevoCliente);
        if (!exito) {
            throw new IllegalArgumentException("Error: Hubo un problema al guardar el cliente en la Base de Datos.");
        }
    }

    public Cliente buscarClientePorCodigo(int codigoBuscado) {
        // Pide a la BD directamente el cliente
        return clienteDAO.buscarPorCodigo(codigoBuscado);
    }
    
    public List<Cliente> buscarClientePorNombre(String textoBusqueda) {
        // Pide a la BD la lista filtrada
        return clienteDAO.buscarPorNombre(textoBusqueda);
    }
    
    public List<Cliente> obtenerTodosLosClientes() {
        return clienteDAO.obtenerTodos();
    }

    public void actualizarCliente(Cliente clienteModificado) {
        if (clienteModificado == null) {
            throw new IllegalArgumentException("Error: El cliente a actualizar es nulo.");
        }
        
        boolean exito = clienteDAO.actualizar(clienteModificado);
        if (!exito) {
            throw new IllegalArgumentException("Error: No se pudo actualizar el cliente en la Base de Datos.");
        }
    }

    // ==========================================
    // LÓGICA DE PROVEEDORES
    // ==========================================
    
    public void registrarProveedor(Proveedor nuevoProveedor) {
        if (nuevoProveedor == null) {
            throw new IllegalArgumentException("Error: No se puede registrar un proveedor nulo.");
        }

        // Regla de Negocio: Evitar duplicados consultando a la BD
        if (proveedorDAO.buscarPorRuc(nuevoProveedor.getRuc()) != null) {
            throw new IllegalArgumentException("Error: Ya existe un proveedor registrado con el RUC " + nuevoProveedor.getRuc());
        }

        boolean exito = proveedorDAO.registrar(nuevoProveedor);
        if (!exito) {
            throw new IllegalArgumentException("Error: Hubo un problema al guardar el proveedor en la Base de Datos.");
        }
    }

    public Proveedor buscarProveedorPorRuc(String rucBuscado) {
        return proveedorDAO.buscarPorRuc(rucBuscado);
    }
    
    public List<Proveedor> buscarProveedorPorNombre(String textoBusqueda) {
        return proveedorDAO.buscarPorNombre(textoBusqueda);
    }

    public List<Proveedor> obtenerTodosLosProveedores() {
        return proveedorDAO.obtenerTodos();
    }

    public void actualizarProveedor(Proveedor proveedorModificado) {
        if (proveedorModificado == null) {
            throw new IllegalArgumentException("Error: El proveedor a actualizar es nulo.");
        }
        
        boolean exito = proveedorDAO.actualizar(proveedorModificado);
        if (!exito) {
            throw new IllegalArgumentException("Error: No se pudo actualizar el proveedor en la Base de Datos.");
        }
    }
}
