/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Gestores;
import Entidades.PagoAbono;
import Entidades.Pedido;
import java.util.ArrayList;
import java.util.List;
import DAO.PedidoDAO;
import DAO.DetallePedidoDAO;
import Entidades.Pedido;
import Entidades.DetallePedido;
/**
 *
 * @author equipo
 */
public class GestorVentas {
    
    private PedidoDAO pedidoDAO;
    private DetallePedidoDAO detallePedidoDAO;
    private GestorInventario gestorInventario; // Necesario para actualizar el stock

    public GestorVentas() {
        this.pedidoDAO = new PedidoDAO();
        this.detallePedidoDAO = new DetallePedidoDAO();
        this.gestorInventario = new GestorInventario(); // Conectamos con el módulo de inventario
    }

    public void registrarVenta(Pedido nuevoPedido) {
        if (nuevoPedido == null) {
            throw new IllegalArgumentException("Error: El pedido no puede ser nulo.");
        }
        
        if (nuevoPedido.getDetalles() == null || nuevoPedido.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("Error: No se puede registrar un pedido sin productos.");
        }

        // 1. Guardamos la cabecera del Pedido en SQL Server
        boolean pedidoGuardado = pedidoDAO.registrar(nuevoPedido);
        if (!pedidoGuardado) {
            throw new RuntimeException("Error crítico: Falló la creación del pedido en la base de datos.");
        }

        // 2. Guardamos todos los productos de ese pedido (Detalles) en SQL Server
        boolean detallesGuardados = detallePedidoDAO.registrarDetalles(nuevoPedido.getCodigo(), nuevoPedido.getDetalles());
        if (!detallesGuardados) {
            throw new RuntimeException("Error crítico: Falló la creación de los detalles del pedido.");
        }

        // 3. LA NUEVA LÓGICA DE INVENTARIO
        // Solo descontamos físicamente de la base de datos si el pedido ya fue entregado al cliente
        if (nuevoPedido.getEstado().equalsIgnoreCase("Entregado")) {
            for (DetallePedido dp : nuevoPedido.getDetalles()) {
                // Pasamos el ID del producto y la cantidad en NEGATIVO para restar
                gestorInventario.modificarStock(dp.getProducto().getId(), -dp.getCantidadVendida());
            }
        }
    }

    public void registrarPagoAbono(int codigoPedido, double montoAbonado, double deudaActual) {
        if (montoAbonado <= 0) {
            throw new IllegalArgumentException("Error: El monto a abonar debe ser mayor a cero.");
        }

        double nuevaDeuda = deudaActual - montoAbonado;
        if (nuevaDeuda < 0) {
            throw new IllegalArgumentException("Error: El abono supera la deuda actual.");
        }

        // Determinamos el nuevo estado basado en si la deuda llegó a 0
        String nuevoEstado = (nuevaDeuda == 0) ? "Entregado" : "Pendiente";

        // Actualizamos la base de datos
        boolean actualizado = pedidoDAO.actualizarEstadoYDeuda(codigoPedido, nuevoEstado, nuevaDeuda);
        
        if (!actualizado) {
            throw new RuntimeException("Error: No se pudo registrar el abono en la base de datos.");
        }
        
        // (Nota: Aquí en el futuro puedes agregar la llamada a PagoAbonoDAO para guardar el recibo físico)
    }
    
    public List<Pedido> obtenerHistorialPedidos() {
    return pedidoDAO.obtenerTodos();
    }
       public Pedido obtenerDetallesDePedido(int codigo) {
    Pedido pedido = pedidoDAO.buscarPorCodigo(codigo);
    if (pedido != null) {
        pedido.getDetalles().addAll(detallePedidoDAO.obtenerDetallesPorPedido(codigo));
    }
    return pedido;
    }
    public void avanzarEstadoPedido(int codigo) {
        // Usamos obtenerDetallesDePedido en lugar de buscarPorCodigo para traer la lista de productos
        Pedido pedido = obtenerDetallesDePedido(codigo); 
        
        if(pedido != null) {
            pedido.avanzarEstado(); // Cambia el estado internamente según tus reglas
            pedidoDAO.actualizarEstado(codigo, pedido.getEstado());
            
            // Si el nuevo estado tras avanzar es "Entregado", ejecutamos la salida de almacén
            if (pedido.getEstado().equalsIgnoreCase("Entregado")) {
                for (DetallePedido dp : pedido.getDetalles()) {
                    gestorInventario.modificarStock(dp.getProducto().getId(), -dp.getCantidadVendida());
                }
            }
        }
    }
    
    public void cancelarPedido(int codigo) {
        Pedido pedido = obtenerDetallesDePedido(codigo);
        
        if (pedido != null) {
            // Si el pedido ya había salido del almacén, devolvemos el stock
            if (pedido.getEstado().equalsIgnoreCase("Entregado")) {
                for (DetallePedido dp : pedido.getDetalles()) {
                    // Pasamos la cantidad en POSITIVO para que sume al inventario
                    gestorInventario.modificarStock(dp.getProducto().getId(), dp.getCantidadVendida()); 
                }
            }
            // Finalmente, actualizamos la base de datos marcándolo como Cancelado
            pedidoDAO.actualizarEstado(codigo, "Cancelado");
        }
    }
}