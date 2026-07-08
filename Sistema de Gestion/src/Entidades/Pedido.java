/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entidades;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author equipo
 */
public class Pedido {
    
    private int codigo;
    private String fechaEmision;
    private String fechaRecepcion;
    private String estado;
    private String tipoVenta;
    private String fechaLimitePago;
    private double deudaPendiente;
    private Cliente cliente;
    private List<DetallePedido> detallesVenta;
    private List<PagoAbono> abonos;
    

   public Pedido(String fechaEmision, String fechaRecepcion, String estado, String tipoVenta, String fechaLimitePago, double deudaInicial, Cliente cliente) {
        this.fechaEmision = fechaEmision;
        this.fechaRecepcion = fechaRecepcion;
        this.tipoVenta = tipoVenta;
        this.fechaLimitePago = fechaLimitePago;
        this.cliente = cliente; 
        this.detallesVenta = new java.util.ArrayList<>();
        
        // Se asume el monto que viene del carrito y se evalúa automáticamente
        this.setDeudaPendiente(deudaInicial);
        
        // Solo asigna el estado ("Pendiente", etc.) si realmente hay deuda
        if (this.deudaPendiente > 0) {
            this.estado = estado;
        }
    }
    public Pedido(int codigo, String fechaEmision, String fechaRecepcion, String estado, String tipoVenta, String fechaLimitePago, double deudaPendiente, Cliente cliente) {
        this.codigo = codigo;
        this.fechaEmision = fechaEmision;
        this.fechaRecepcion = fechaRecepcion;
        this.estado = estado;
        this.tipoVenta = tipoVenta;
        this.fechaLimitePago = fechaLimitePago;
        this.deudaPendiente = deudaPendiente;
        this.cliente = cliente;
        this.detallesVenta = new ArrayList<>();
        this.abonos = new ArrayList<>();
    }

    public int getCodigo() {
        return codigo;
    }  

    public String getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(String fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public String getFechaRecepcion() {
        return fechaRecepcion;
    }

    public void setFechaRecepcion(String fechaRecepcion) {
       // La validación ahora permite que sea el mismo día (mayor o igual)
        if (esFechaValidaPosteriorOIgual(this.fechaEmision, fechaRecepcion)) {
            this.fechaRecepcion = fechaRecepcion;
        } else {
            // El mensaje cambia: ya no es "debe ser posterior", sino "no puede ser anterior"
            throw new IllegalArgumentException("Error: La fecha de recepción no puede ser anterior a la fecha de emisión.");
        }
    }

    public String getEstado() {
        return this.estado;
        
    }

   public void setEstado(String estado) {
        // Bloqueo: No permitir asignar "Abonada" manualmente si aún hay deuda
        if (estado.equals("Abonada") && this.deudaPendiente > 0) {
            throw new IllegalArgumentException("Error: No se puede marcar como 'Abonada' si existe deuda pendiente.");
        }

        if (estado.equals("Pendiente") || estado.equals("Entregado") || 
            estado.equals("Abonada") || estado.equals("Cancelada")) {
            this.estado = estado;
        } else {
            throw new IllegalArgumentException("Error: Estado de pedido inválido.");
        }
    }

    public String getTipoVenta() {
        return tipoVenta;
    }

    public void setTipoVenta(String tipoVenta) {
        if (tipoVenta.equals("Contado") || tipoVenta.equals("Credito")) {
            this.tipoVenta = tipoVenta;
        } else {
            throw new IllegalArgumentException("Error: Tipo de venta inválido. Solo se permite 'Contado' o 'Credito'.");
        }
    }

    public String getFechaLimitePago() {
        return fechaLimitePago;
    }

    public void setFechaLimitePago(String fechaLimitePago) {
        // Cambiamos el validador para usar la regla "mayor o igual" (permite ventas al Contado el mismo día)
        if (esFechaValidaPosteriorOIgual(this.fechaEmision, fechaLimitePago)) {
            this.fechaLimitePago = fechaLimitePago;
        } else {
            // Actualizamos el mensaje por coherencia semántica
            throw new IllegalArgumentException("Error: La fecha límite de pago no puede ser anterior a la fecha de emisión.");
        }
    }

    public double getDeudaPendiente() {
        return deudaPendiente;
    }

   public void setDeudaPendiente(double deudaPendiente) {
        this.deudaPendiente = deudaPendiente;
        
        // Si el abono liquida la deuda o la venta es al contado (deuda 0)
        if (this.deudaPendiente <= 0) {
            this.deudaPendiente = 0.0;
            this.estado = "Abonada"; 
        }
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        if (cliente != null) {
            this.cliente = cliente;
        } else {
            throw new IllegalArgumentException("Error: El pedido debe pertenecer a un cliente válido (no nulo).");
        }
    }
    public List<DetallePedido> getDetalles() {
    return this.detallesVenta;
}
    public void setCodigo(int codigo) {
    this.codigo = codigo;
}
    
   
    
    public void agregarDetalleVenta(DetallePedido detalle){
        
        if (detalle != null) {
            this.detallesVenta.add(detalle);
            this.calcularTotalDeuda(); 
        }
        
    }
    public void registrarPago(double monto) {
        this.deudaPendiente -= monto;
        if (this.deudaPendiente <= 0) {
            this.deudaPendiente = 0;
            this.estado = "Abonada"; // Se asigna automáticamente al saldar la deuda
        }
    }
    
    
    
    public void calcularTotalDeuda(){
     double totalVenta = 0;
     double totalPagado = 0;

        
        for (DetallePedido detalle : this.detallesVenta) {
            totalVenta += (detalle.getCantidadVendida() * detalle.getPrecioVentaCongelado());
        }

       
        for (PagoAbono abono : this.abonos) {
            totalPagado += abono.getMontoAbonado();
        }

        
        double nuevaDeuda = totalVenta - totalPagado;

     
        if (nuevaDeuda < 0) {
            nuevaDeuda = 0;
        }

        this.setDeudaPendiente(nuevaDeuda);
    }
    
    
    public void eliminarDetalleVenta(DetallePedido detalle)
    {
      if (detalle != null && !this.detallesVenta.isEmpty()) {
            
            this.detallesVenta.remove(detalle);
            this.calcularTotalDeuda(); 
        }
    }
    
    private void verificarDeuda(){
        if (this.deudaPendiente == 0 && !this.detallesVenta.isEmpty()) {
            this.setEstado("Cancelado"); 
        }
    
    }
    public void avanzarEstado() {
        if (this.estado.equals("Pendiente")) {
            this.setEstado("Entregado");
        } else if (this.estado.equals("Activo")) {
            this.setEstado("Pendiente");
        } else {
            throw new IllegalStateException("El pedido no puede avanzar. Estado actual: " + this.estado);
        }
    }
    private boolean esFechaValidaPosteriorOIgual(String fechaEmision, String fechaRecepcion) {
        try {
            // 1. Transformamos el texto crudo en objetos de fecha reales
            java.time.LocalDate emision = java.time.LocalDate.parse(fechaEmision);
            java.time.LocalDate recepcion = java.time.LocalDate.parse(fechaRecepcion);
            
            // 2. La lógica de evaluación: 
            // Retorna TRUE si la recepción NO es anterior a la emisión.
            return !recepcion.isBefore(emision);
            
        } catch (java.time.format.DateTimeParseException e) {
            // Manejo estructural en caso de que el texto venga vacío o corrupto
            System.err.println("Error lógico: El formato de la fecha es inválido.");
            return false;
        }
    }
    // NUEVO MÉTODO: Calcula el total bruto del pedido sumando sus detalles
    public double getTotalPedido() {
        double total = 0.0;
        if (this.detallesVenta != null) {
            for (DetallePedido detalle : this.detallesVenta) {
                total += (detalle.getCantidadVendida() * detalle.getPrecioVentaCongelado());
            }
        }
        return total;
    }
    // MÉTODO CRÍTICO: Este método controla el ciclo de vida real del estado
    public void actualizarEstadoSegunPago() {
        if (this.deudaPendiente <= 0) {
            this.estado = "Abonada";
        } else {
            // Si tiene deuda, se queda como estaba o pasa a "Pendiente" (de entrega)
            if (this.estado.equals("Solicitada")) {
                this.estado = "Pendiente"; 
            }
        }
    }

    

   
    
    
}
