/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Gestores;
import Entidades.PagoAbono;
import Entidades.Pedido;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author equipo
 */
public class GestorVentas {
    // 1. La "Base de Datos" en memoria para el historial comercial
    private List<Pedido> historialPedidos;

    public GestorVentas() {
        this.historialPedidos = new ArrayList<>();
        cargarDatosDePrueba();
    }

    // ==========================================
    // LÓGICA DE GESTIÓN DE PEDIDOS
    // ==========================================

    public void registrarPedido(Pedido nuevoPedido) {
        if (nuevoPedido == null) {
            throw new IllegalArgumentException("Error: No se puede registrar un pedido nulo.");
        }

        // Lógica de seguridad: Evitar duplicidad de facturas/pedidos
        for (Pedido p : historialPedidos) {
            if (p.getCodigo() == nuevoPedido.getCodigo()) {
                throw new IllegalArgumentException("Error: Ya existe un pedido registrado con el código " + nuevoPedido.getCodigo());
            }
        }
        
        // El pedido ya debe venir con sus detalles (productos) cargados desde la interfaz
        this.historialPedidos.add(nuevoPedido);
    }

    public Pedido buscarPedidoPorCodigo(int codigoBuscado) {
        for (Pedido p : historialPedidos) {
            if (p.getCodigo() == codigoBuscado) {
                return p;
            }
        }
        return null;
    }

    public List<Pedido> obtenerPedidosPorCliente(int codigoCliente) {
        // Retorna todo el historial de un cliente específico
        List<Pedido> resultados = new ArrayList<>();
        
        for (Pedido p : historialPedidos) {
            if (p.getCliente().getCodigo() == codigoCliente) {
                resultados.add(p);
            }
        }
        return resultados;
    }

    // ==========================================
    // LÓGICA DE FILTRADO (REGLA DE NEGOCIO: MESES)
    // ==========================================

    public List<Pedido> filtrarPedidosPorMes(String mesAnioBuscado) {
        // Lógica para cumplir la regla: "Todas las tablas deben dividirse en meses"
        // El formato esperado de 'mesAnioBuscado' debería ser "YYYY-MM" (Ej. "2026-05")
        List<Pedido> resultados = new ArrayList<>();
        
        for (Pedido p : historialPedidos) {
            // Extraemos los primeros 7 caracteres de la fecha de emisión (AAAA-MM)
            if (p.getFechaEmision() != null && p.getFechaEmision().startsWith(mesAnioBuscado)) {
                resultados.add(p);
            }
        }
        return resultados;
    }

    // ==========================================
    // LÓGICA FINANCIERA (RECAUDACIÓN)
    // ==========================================

    public void registrarPagoMonto(PagoAbono nuevoAbono) {
        if (nuevoAbono == null) {
            throw new IllegalArgumentException("Error: El abono no puede ser nulo.");
        }

        // 1. Verificamos que el pedido asociado al abono realmente exista en el sistema
        Pedido pedidoAsociado = buscarPedidoPorCodigo(nuevoAbono.getPedido().getCodigo());
        
        if (pedidoAsociado == null) {
            throw new IllegalArgumentException("Error: El pedido al que intenta abonar no existe.");
        }

        // 2. Ejecutamos la lógica de cobro. El método procesarPago() del abono 
        // internamente mandará a descontar la deuda en la clase Pedido.
        nuevoAbono.procesarPago();
    }

    public double calcularDeudaGlobal() {
        // Lógica de auditoría: Suma absolutamente todo el dinero que está en la calle (cuánto nos deben)
        double deudaTotalEmpresa = 0.0;
        
        for (Pedido p : historialPedidos) {
            if (p.getEstado().equals("Activo") || p.getEstado().equals("Pendiente")) {
                deudaTotalEmpresa += p.getDeudaPendiente();
            }
        }
        return deudaTotalEmpresa;
    }

    // ==========================================
    // MÉTODO DE SIMULACIÓN (DATOS DE PRUEBA)
    // ==========================================

    private void cargarDatosDePrueba() {
        try {
            // Para probar esta sección en tus ventanas visuales, necesitarás que 
            // este gestor trabaje en equipo con el GestorContactos en el Main.
            // Por ahora dejamos la estructura lista para recibir los pedidos.
            
        } catch (Exception e) {
            System.out.println("Error cargando semillas de ventas: " + e.getMessage());
        }
    }
    
    // Método para llenar la tabla principal de Ventas en el Formulario
    public List<Pedido> obtenerHistorialCompleto() {
        return this.historialPedidos;
    }
    
}
