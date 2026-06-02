/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entidades;

import java.util.Objects;

/**
 *
 * @author equipo
 */
public class LoteProducto {
    private static int contadorGlobal = 600000;
    private int codigo;
    private int cantidadIngresada;
    private double precioCompraIndividual;
    private OrdenCompra ordenCompra;
    private Producto producto;
    
 public LoteProducto( int cantidadIngresada, double precioCompraIndividual, Producto producto) {
        this.codigo = contadorGlobal++;
        setCantidadIngresada(cantidadIngresada);
        setPrecioCompraIndividual(precioCompraIndividual);
        setProducto(producto);
    }
    public int getCodigo() { return codigo; }
   

    public int getCantidadIngresada() {
        return cantidadIngresada;
    }

    public void setCantidadIngresada(int cantidadIngresada) {
        if (cantidadIngresada > 0) {
            this.cantidadIngresada = cantidadIngresada;
        } else {
            throw new IllegalArgumentException("Error: La cantidad ingresada del lote debe ser mayor a 0.");
        }
    }

    public double getPrecioCompraIndividual() {
        return precioCompraIndividual;
    }

    public void setPrecioCompraIndividual(double precioCompraIndividual) {
      
        if (precioCompraIndividual >= 0) {
            this.precioCompraIndividual = precioCompraIndividual;
        } else {
            throw new IllegalArgumentException("Error: El precio de compra individual debe ser mayor a 0.");
        }
    }

    public OrdenCompra getOrdenCompra() {
        return ordenCompra;
    }

    public void setOrdenCompra(OrdenCompra ordenCompra) {
        this.ordenCompra = ordenCompra;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        if (producto != null) {
            this.producto = producto;
        } else {
            throw new IllegalArgumentException("Error: El lote debe estar asociado a un producto válido (no nulo).");
        }
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        LoteProducto otroLote = (LoteProducto) obj;
        return this.codigo == otroLote.codigo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }
    
}
