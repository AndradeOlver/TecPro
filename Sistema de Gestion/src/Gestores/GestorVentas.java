/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Gestores;
import DAO.PagoAbonoDAO;
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
    private PagoAbonoDAO pagoAbonoDAO;

    public GestorVentas() {
    this.pedidoDAO = new PedidoDAO();
    this.detallePedidoDAO = new DetallePedidoDAO();
    this.gestorInventario = new GestorInventario();
    this.pagoAbonoDAO = new PagoAbonoDAO(); // <-- CORRECCIÓN: Inicialización que faltaba
}

    public void registrarVenta(Pedido nuevoPedido) {
    if (nuevoPedido == null || nuevoPedido.getDetalles() == null || nuevoPedido.getDetalles().isEmpty()) {
        throw new IllegalArgumentException("Error: Datos de pedido inválidos o sin productos.");
    }

    // 1. Abrimos la conexión AQUÍ para controlar toda la transacción
    try (java.sql.Connection con = DAO.ConexionSQL.probarConexion()) {
        
        // Apagamos el guardado automático
        con.setAutoCommit(false); 
        
        try {
            // 2. Guardamos Cabecera
            boolean pedidoGuardado = pedidoDAO.registrarTransaccional(con, nuevoPedido);
            if (!pedidoGuardado) throw new java.sql.SQLException("Fallo interno al crear cabecera del pedido.");

            // 3. Guardamos Detalles
            boolean detallesGuardados = detallePedidoDAO.registrarDetallesTransaccional(con, nuevoPedido.getCodigo(), nuevoPedido.getDetalles());
            if (!detallesGuardados) throw new java.sql.SQLException("Fallo interno al guardar los productos del pedido.");

            // 4. Si todo salió perfecto, CONFIRMAMOS los datos en el disco duro
            con.commit();
            
        } catch (Exception e) {
            // 5. Si CUALQUIER COSA falla, REVERTIMOS TODO y no quedan datos fantasma
            con.rollback();
            throw new RuntimeException("Error crítico. Transacción revertida: " + e.getMessage());
        }
    } catch (java.sql.SQLException e) {
        throw new RuntimeException("Error de conexión a la base de datos: " + e.getMessage());
    }

    // 6. Lógica de inventario (en memoria/posterior a la transacción)
    if (nuevoPedido.getEstado().equalsIgnoreCase("Entregado")) {
        // En lugar de hacer un bucle modificando el stock a medias, llamamos al Kardex completo
        gestorInventario.procesarSalidaKardex(nuevoPedido);
    }
}

    public void registrarPagoAbono(PagoAbono abono) {
    if (abono.getMontoAbonado() <= 0) {
        throw new IllegalArgumentException("Error: El monto a abonar debe ser mayor a cero.");
    }

    double deudaActual = abono.getPedido().getDeudaPendiente();
    double nuevaDeuda = deudaActual - abono.getMontoAbonado();
    
    if (nuevaDeuda < 0) {
        throw new IllegalArgumentException("Error: El abono supera la deuda actual.");
    }

    // 1. Calculamos el nuevo estado de forma LOCAL (sin alterar el objeto real todavía)
    String nuevoEstado = abono.getPedido().getEstado(); 
    if (nuevaDeuda <= 0) {
        nuevoEstado = "Abonada";
    }

    // 2. Iniciamos el bloque transaccional con la base de datos
    try (java.sql.Connection con = DAO.ConexionSQL.probarConexion()) {
        con.setAutoCommit(false); // Desactivamos el guardado automático
        
        try {
            // A. Intentamos actualizar la cabecera del pedido en la BD
            boolean pedidoActualizado = pedidoDAO.actualizarEstadoYDeudaTransaccional(con, abono.getPedido().getCodigo(), nuevoEstado, nuevaDeuda);
            if (!pedidoActualizado) throw new java.sql.SQLException("Fallo al actualizar estado y deuda en el Pedido.");
            
            // B. Intentamos insertar el recibo de abono en la BD
            boolean pagoRegistrado = pagoAbonoDAO.registrarTransaccional(con, abono);
            if (!pagoRegistrado) throw new java.sql.SQLException("Fallo al insertar el registro de PagoAbono.");
            
            // C. Si ambas operaciones en la BD fueron exitosas, guardamos los cambios de forma permanente
            con.commit();
            
            // 3. ¡ÉXITO EN DISCO! Ahora procedemos a modificar de forma segura la memoria RAM
            abono.getPedido().setDeudaPendiente(nuevaDeuda);
            
        } catch (Exception e) {
            // Si algo falla a mitad de camino, cancelamos todo en la BD
            con.rollback();
            // La memoria RAM se queda intacta con los valores originales, evitando inconsistencias visuales
            throw new RuntimeException("No se pudo registrar el abono. Operación revertida: " + e.getMessage());
        }
    } catch (java.sql.SQLException e) {
        throw new RuntimeException("Error de comunicación con el servidor de base de datos: " + e.getMessage());
    }
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
                // Reemplazamos el bucle viejo por la transacción de Kardex
                gestorInventario.procesarSalidaKardex(pedido);
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
    public void eliminarPedidosCancelados() {
    boolean exito = pedidoDAO.eliminarCancelados();
    if (!exito) {
        throw new RuntimeException("Error crítico: No se pudieron limpiar los pedidos cancelados.");
    }
}
}