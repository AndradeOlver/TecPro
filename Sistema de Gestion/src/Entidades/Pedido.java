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
    private static int contadorGlobal = 200000;
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

    public Pedido( String fechaEmision, String fechaRecepcion, String estado, String tipoVenta, String fechaLimitePago, double deudaPendiente,Cliente cliente) {
       this.codigo = contadorGlobal++;
       this.deudaPendiente = 0.0;
       this.fechaEmision = fechaEmision;
       setFechaRecepcion(fechaRecepcion);
       setEstado(estado);
       setTipoVenta(tipoVenta);
       setFechaLimitePago(fechaLimitePago);
       setCliente(cliente);
       this.detallesVenta = new ArrayList<>();
       this.abonos = new ArrayList<>();
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
        if (esFechaMayor(this.fechaEmision, fechaRecepcion)) {
            this.fechaRecepcion = fechaRecepcion;
        } else {
            throw new IllegalArgumentException("Error: La fecha de recepción debe ser posterior a la fecha de emisión.");
        }
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        if (estado.equals("Activo") || estado.equals("Pendiente") || estado.equals("Entregado") || estado.equals("Cancelado")) {
            this.estado = estado;
        } else {
            throw new IllegalArgumentException("Error: Estado inválido. Solo se permite 'Activo', 'Pendiente', 'Entregado' o 'Cancelado'.");
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
        if (esFechaMayor(this.fechaEmision, fechaLimitePago)) {
            this.fechaLimitePago = fechaLimitePago;
        } else {
            throw new IllegalArgumentException("Error: La fecha límite de pago debe ser posterior a la fecha de emisión.");
        }
    }

    public double getDeudaPendiente() {
        return deudaPendiente;
    }

    public void setDeudaPendiente(double deudaPendiente) {
        if (deudaPendiente >= 0) {
            this.deudaPendiente = deudaPendiente;
        } else {
            throw new IllegalArgumentException("Error: La deuda pendiente debe ser mayor a 0.");
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
    
    private boolean esFechaMayor(String fechaBase, String fechaAComparar) {
        if (fechaBase == null || fechaAComparar == null) return false;
        try {
            LocalDate fecha1 = LocalDate.parse(fechaBase);
            LocalDate fecha2 = LocalDate.parse(fechaAComparar);
            return fecha2.isAfter(fecha1);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Error: Formato de fecha inválido. Utilice el formato AAAA-MM-DD.");
        }
    }
    
    public void agregarDetalleVenta(DetallePedido detalle){
        
        if (detalle != null) {
            this.detallesVenta.add(detalle);
            this.calcularTotalDeuda(); 
        }
        
    }
    public void registrarPago(PagoAbono abono) {
    if (abono != null) {
        this.abonos.add(abono);
        this.calcularTotalDeuda(); 
        this.verificarDeuda(); 
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

    

   
    
    
}
