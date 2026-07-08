/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entidades;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 *
 * @author equipo
 */
public class PagoAbono {
    
    private int codigoRecibo;
    private String fechaAbono;
    private double montoAbonado;
    private Pedido pedido;
    
   public PagoAbono( String fechaAbono, double montoAbonado, Pedido pedido) {
        
        setFechaAbono(fechaAbono);
        setMontoAbonado(montoAbonado);
        setPedido(pedido);
    }

    public int getCodigoRecibo() {
        return codigoRecibo;
    }
    
   

    public String getFechaAbono() {
        return fechaAbono;
    }
    // NUEVO: Método para que el DAO inyecte el ID generado por SQL Server
    public void setCodigoRecibo(int codigoRecibo) {
        this.codigoRecibo = codigoRecibo;
    }

    public void setFechaAbono(String fechaAbono) {
       
        if (fechaAbono != null && esFechaValida(fechaAbono)) {
            this.fechaAbono = fechaAbono;
        } else {
            throw new IllegalArgumentException("Error: Formato de fecha inválido. Utilice el formato AAAA-MM-DD.");
        }
    }

    public double getMontoAbonado() {
        return montoAbonado;
    }

    public void setMontoAbonado(double montoAbonado) {
        if (montoAbonado > 0) {
            this.montoAbonado = montoAbonado;
        } else {
            throw new IllegalArgumentException("Error: El monto abonado debe ser mayor a 0.");
        }
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        if (pedido != null) {
            this.pedido = pedido;
        } else {
            throw new IllegalArgumentException("Error: El pago debe estar asociado a un pedido válido.");
        }
    }


    private boolean esFechaValida(String fecha) {
        try {
            LocalDate.parse(fecha);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
    
    public String procesarPago() {
        // 1. Verificación de seguridad antes de procesar el dinero
        if (this.pedido == null) {
            throw new IllegalStateException("Error: No se puede procesar un pago sin una factura/pedido asociado.");
        }
        
        if (this.pedido.getEstado().equals("Cancelado")) {
            throw new IllegalStateException("Error: Este pedido ya está pagado en su totalidad.");
        }

        // CORRECCIÓN: Se envía exclusivamente el monto decimal, no el comprobante entero
        this.pedido.registrarPago(this.montoAbonado);

        // 3. Generamos el comprobante o huella digital de la transacción
        String recibo = String.format(
            "========================================\n" +
            "           RECIBO DE PAGO #%s\n" +
            "========================================\n" +
            "Fecha: %s\n" +
            "Pedido Asociado: %d\n" +
            "Monto Abonado: S/ %.2f\n" +
            "Deuda Restante del Pedido: S/ %.2f\n" +
            "Estado Actual del Pedido: %s\n" +
            "========================================",
            this.codigoRecibo,
            this.fechaAbono,
            this.pedido.getCodigo(),
            this.montoAbonado,
            this.pedido.getDeudaPendiente(),
            this.pedido.getEstado()
        );
        return recibo;
    }
}
