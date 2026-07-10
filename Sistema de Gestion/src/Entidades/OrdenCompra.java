/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entidades;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author equipo
 */
public class OrdenCompra {
    private static int contadorGlobal = 300000;
    private int codigo;
    private String fechaIngreso;
    private String estado;
    private Proveedor proveedor;
    private List<LoteProducto> lotes;
    
    
    public OrdenCompra( String fechaIngreso, String estado, Proveedor proveedor) {
        // Validaciones desde la instanciación
        this.codigo = contadorGlobal++;
        setCodigo(codigo);
        setFechaIngreso(fechaIngreso);
        setEstado(estado);
        setProveedor(proveedor);
        this.lotes = new ArrayList<>(); 
    }
    public OrdenCompra(int codigo, String fechaIngreso, String estado, Proveedor proveedor) {
        this.codigo = codigo;
        this.fechaIngreso = fechaIngreso;
        this.estado = estado;
        this.proveedor = proveedor;
        this.lotes = new ArrayList<>();
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        if (codigo > 0) {
            this.codigo = codigo;
        } else {
            throw new IllegalArgumentException("Error: El código de la orden de compra debe ser mayor a 0.");
        }
    }

    public String getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(String fechaIngreso) {

        if (fechaIngreso != null && esFechaValida(fechaIngreso)) {
            this.fechaIngreso = fechaIngreso;
        } else {
            throw new IllegalArgumentException("Error: Formato de fecha inválido. Utilice el formato AAAA-MM-DD.");
        }
    }
    public String getEstado() {
        return this.estado;
    }
    
    public void setEstado(String estado) {
        if (estado.equals("Solicitada") || estado.equals("Pendiente") || 
            estado.equals("Procesada") || estado.equals("Cancelada")) {
            this.estado = estado;
        } else {
            throw new IllegalArgumentException("Error: Estado de orden de compra inválido.");
        }
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
       if (proveedor != null) {
            this.proveedor = proveedor;
        } else {
            throw new IllegalArgumentException("Error: La orden de compra debe tener un proveedor válido asignado (no nulo).");
        }
    }

    public List<LoteProducto> getLotes() {
        return lotes;
    }
    
    
    private boolean esFechaValida(String fecha) {
        try {
            LocalDate.parse(fecha);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
    public void avanzarEstado() {
        if (this.estado.equals("Solicitada")) {
            this.setEstado("Pendiente");
        } else if (this.estado.equals("Pendiente")) {
            this.setEstado("Procesada");
        } else {
            throw new IllegalStateException("La orden no puede avanzar. Estado actual: " + this.estado);
        }
    }

    public void cancelarOrden() {
        this.setEstado("Cancelada");
    }
    public void agregarLote(LoteProducto lote) {
        if (lote != null) {
            this.lotes.add(lote);
        }
    }
 
    public List<MovimientosKardex> procesarEntradaAlmacen() {
        // Seguridad: No podemos procesar el almacén si la orden no ha sido confirmada primero
        if (!this.estado.equals("Procesada")) {
            throw new IllegalStateException("Error: Primero debes confirmar el ingreso de la orden.");
        }

        if (this.lotes == null || this.lotes.isEmpty()) {
            throw new IllegalStateException("Error: No se puede procesar una orden sin productos.");
        }

        // 1. Creamos la "cesta" para guardar los movimientos generados
        List<MovimientosKardex> movimientosGenerados = new ArrayList<>();

        // RECORREMOS CADA LOTE (El efecto dominó)
        for (LoteProducto lote : this.lotes) {
            Producto productoDelLote = lote.getProducto(); 
            
            if (productoDelLote != null) {
                // Actualizamos costos y stock
                
                // 1. Llamamos a Recalcular Costo Promedio (Necesita cantidad y precio del lote)
                productoDelLote.recalcularCostoPromedio(lote.getCantidadIngresada(), lote.getPrecioCompraIndividual());
                
                // 2. Llamamos a Actualizar Stock (Suma la cantidad física al almacén)
                productoDelLote.actualizarStock(lote.getCantidadIngresada(), "entrada");
                
                // Creamos el registro ("el papel")
                MovimientosKardex nuevoMovimiento = new MovimientosKardex(
                    this.fechaIngreso,                      
                    "entrada",                              
                    lote.getCantidadIngresada(),            
                    lote.getPrecioCompraIndividual(),       
                    productoDelLote.getStock(),             
                    productoDelLote.getPrecioCompra(),      
                    productoDelLote                        
                );
                
                // 2. Metemos el registro en nuestra cesta
                movimientosGenerados.add(nuevoMovimiento);
            }
        }
        
        // 3. Devolvemos la cesta llena de registros
        return movimientosGenerados;
    }
    // NUEVO MÉTODO: Calcula el costo total de la orden de compra sumando sus lotes
    public double getTotalOrden() {
        double total = 0.0;
        if (this.lotes != null) {
            for (LoteProducto lote : this.lotes) {
                total += (lote.getCantidadIngresada() * lote.getPrecioCompraIndividual());
            }
        }
        return total;
    }
    
}
