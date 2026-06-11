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
public class MovimientosKardex {
    private static int contadorGlobal = 100000;
    private int idMovimiento;
    private String fechaMovimiento;
    private String tipoMovimiento;
    private int cantidadFisica;
    private double valorUnitario;
    private int saldoCantidadActual;
    private double saldoCostoPromedio;
    private Producto producto;
    
    public MovimientosKardex(String fechaMovimiento, String tipoMovimiento, int cantidadFisica, double valorUnitario, int saldoCantidadActual, double saldoCostoPromedio, Producto producto) {
        this.idMovimiento = contadorGlobal++;
        setFechaMovimiento(fechaMovimiento);
        setTipoMovimiento(tipoMovimiento);
        setCantidadFisica(cantidadFisica);
        setValorUnitario(valorUnitario);
        setSaldoCantidadActual(saldoCantidadActual);
        setSaldoCostoPromedio(saldoCostoPromedio);
        setProducto(producto);
    }
    public int getIdMovimiento() { 
        return idMovimiento; 
    }

    

    public String getFechaMovimiento() {
        return fechaMovimiento;
    }

    public void setFechaMovimiento(String fechaMovimiento) {
        if (fechaMovimiento != null && esFechaValida(fechaMovimiento)) {
            this.fechaMovimiento = fechaMovimiento;
        } else {
            throw new IllegalArgumentException("Error: Formato de fecha inválido. Utilice el formato AAAA-MM-DD.");
        }
    }

    public String getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(String tipoMovimiento) {
        
        if (tipoMovimiento != null && (tipoMovimiento.equals("entrada") || tipoMovimiento.equals("salida"))) {
            this.tipoMovimiento = tipoMovimiento;
        } else {
            throw new IllegalArgumentException("Error: Tipo de movimiento inválido. Solo se permite 'entrada' o 'salida'.");
        }
    }

    public int getCantidadFisica() {
        return cantidadFisica;
    }

    public void setCantidadFisica(int cantidadFisica) {
        if (cantidadFisica > 0) {
            this.cantidadFisica = cantidadFisica;
        } else {
            throw new IllegalArgumentException("Error: La cantidad física debe ser mayor a 0.");
        }
    }

    public double getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(double valorUnitario) {
        if (valorUnitario > 0) {
            this.valorUnitario = valorUnitario;
        } else {
            throw new IllegalArgumentException("Error: El valor unitario debe ser mayor a 0.");
        }
    }

    public int getSaldoCantidadActual() {
        return saldoCantidadActual;
    }

    public void setSaldoCantidadActual(int saldoCantidadActual) {
        if (saldoCantidadActual >= 0) {
            this.saldoCantidadActual = saldoCantidadActual;
        } else {
            throw new IllegalArgumentException("Error: El saldo de cantidad actual debe ser mayor a 0.");
        }
    }

    public double getSaldoCostoPromedio() {
        return saldoCostoPromedio;
    }

    public void setSaldoCostoPromedio(double saldoCostoPromedio) {
        if (saldoCostoPromedio >= 0) {
            this.saldoCostoPromedio = saldoCostoPromedio;
        } else {
            throw new IllegalArgumentException("Error: El saldo de costo promedio debe ser mayor a 0.");
        }
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        if (producto != null) {
            this.producto = producto;
        } else {
            throw new IllegalArgumentException("Error: El movimiento de Kardex debe estar asociado a un producto válido (no nulo).");
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
    
    public String registrarTransaccion() {
       
        double valorTotalMovimiento = this.cantidadFisica * this.valorUnitario;
        
      
        
        String registro = String.format(
            "Transacción Registrada [%s] | Tipo: %s | Cantidad: %d | Total Movimiento: S/ %.2f | Stock Restante: %d | Costo Promedio: S/ %.2f",
            this.fechaMovimiento,
            this.tipoMovimiento.toUpperCase(),
            this.cantidadFisica,
            valorTotalMovimiento,
            this.saldoCantidadActual, // Nota: Si cambias esto a int, puedes usar %d en lugar de %.0f
            this.saldoCostoPromedio
        );
        
        return registro;
    }
    // Nuevo método: Calcula el total de dinero de este movimiento específico
    public double getTotalMovimiento() {
        return this.cantidadFisica * this.valorUnitario;
}

    // Nuevo método: Calcula cuánto vale todo el inventario restante
    public double getValorTotalSaldo() {
        return this.saldoCantidadActual * this.saldoCostoPromedio;
}
}
