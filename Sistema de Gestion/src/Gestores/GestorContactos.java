/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Gestores;
import Entidades.Cliente;
import Entidades.Proveedor;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author equipo
 */
public class GestorContactos {
    private List<Cliente> listaClientes;
    private List<Proveedor> listaProveedores;
    public GestorContactos() {
        // Al nacer el gestor, se preparan las listas vacías
        this.listaClientes = new ArrayList<>();
        this.listaProveedores = new ArrayList<>();
        
        cargarDatosDePrueba(); 
    }
    public void registrarCliente(Cliente nuevoCliente) {
        if (nuevoCliente == null) {
            throw new IllegalArgumentException("Error: No se puede registrar un cliente nulo.");
        }

        // Lógica de seguridad: Evitar duplicados por Código
        for (Cliente c : listaClientes) {
            if (c.getCodigo() == nuevoCliente.getCodigo()) {
                throw new IllegalArgumentException("Error: Ya existe un cliente registrado con el código " + nuevoCliente.getCodigo());
            }
        }

        // Si el bucle termina sin lanzar excepciones, el cliente es válido y se guarda
        this.listaClientes.add(nuevoCliente);
    }
    public Cliente buscarClientePorCodigo(int codigoBuscado) {
        // Lógica de búsqueda secuencial
        for (Cliente c : listaClientes) {
            if (c.getCodigo() == codigoBuscado) {
                return c; // Objeto encontrado, se retorna inmediatamente
            }
        }
        return null; // Si el bucle termina, no existe
    }
    
    public List<Cliente> buscarClientePorNombre(String textoBusqueda) {
        // Lógica de filtro: Retorna una sub-lista con todas las coincidencias parciales
        List<Cliente> resultados = new ArrayList<>();
        
        for (Cliente c : listaClientes) {
            // Convertimos todo a minúsculas para que la búsqueda no sea sensible a mayúsculas
            if (c.getNombre().toLowerCase().contains(textoBusqueda.toLowerCase())) {
                resultados.add(c);
            }
        }
        return resultados;
    }
    
    //proveedores
    
    public void registrarProveedor(Proveedor nuevoProveedor) {
        if (nuevoProveedor == null) {
            throw new IllegalArgumentException("Error: No se puede registrar un proveedor nulo.");
        }

        // Lógica de seguridad: Evitar duplicados por RUC
        for (Proveedor p : listaProveedores) {
            if (p.getRuc().equals(nuevoProveedor.getRuc())) {
                throw new IllegalArgumentException("Error: Ya existe un proveedor registrado con el RUC " + nuevoProveedor.getRuc());
            }
        }

        this.listaProveedores.add(nuevoProveedor);
    }

    public Proveedor buscarProveedorPorRuc(String rucBuscado) {
        for (Proveedor p : listaProveedores) {
            if (p.getRuc().equals(rucBuscado)) {
                return p;
            }
        }
        return null;
    }
    // ==========================================
    // MÉTODO DE SIMULACIÓN (DATOS DE PRUEBA)
    // ==========================================

    private void cargarDatosDePrueba() {
        // Estos clientes y proveedores se crearán solos cada vez que inicies el programa.
        // Te ahorrarán el trabajo de escribirlos a mano para probar tus tablas visuales.
        
        try {
            registrarCliente(new Cliente(1001, "Juan Perez", "Av. Principal 123", "juan@mail.com", "987654321"));
            registrarCliente(new Cliente(1002, "Maria Gomez", "Calle Las Flores 45", "maria@mail.com", "912345678"));
            
            registrarProveedor(new Proveedor("20123456789", "Distribuidora Central S.A.", "Carlos Ruiz", "998877665", "ventas@central.com"));
        } catch (Exception e) {
            System.out.println("Error cargando semillas: " + e.getMessage());
        }
    }
    
    // Método para que tu interfaz gráfica (el DataGridView/Tabla) pueda leer todos los clientes y mostrarlos
    public List<Cliente> obtenerTodosLosClientes() {
        return this.listaClientes;
    }
    public void actualizarCliente(Cliente clienteModificado) {
    for (int i = 0; i < listaClientes.size(); i++) {
        if (listaClientes.get(i).getCodigo() == clienteModificado.getCodigo()) {
            listaClientes.set(i, clienteModificado);
            return; // Termina la búsqueda una vez actualizado
        }
    }
}
    
    
}
