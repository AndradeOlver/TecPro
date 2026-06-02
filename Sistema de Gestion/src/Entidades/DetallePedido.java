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
public class DetallePedido {
    private static int contadorGlobal = 500000;
    private int codigo;
    private int cantidadVendida;
    private double precioVentaCongelado;
    private Pedido pedido;
    private Producto producto;
    
    public DetallePedido(int cantidadVendida, double precioVentaCongelado, Producto producto) {
        
        this.codigo = contadorGlobal++;
        
        setCantidadVendida(cantidadVendida);
        
        setProducto(producto);
   
        if (precioVentaCongelado >= 0) {
            this.precioVentaCongelado = precioVentaCongelado;
        } else {
            throw new IllegalArgumentException("Error: El precio de venta congelado no puede ser negativo.");
        }
    }
    public int getCodigo() { return codigo; }
    

    public int getCantidadVendida() {
        return cantidadVendida;
    }

    public void setCantidadVendida(int cantidadVendida) {
        if (cantidadVendida > 0) {
            this.cantidadVendida = cantidadVendida;
        } else {
            throw new IllegalArgumentException("Error: La cantidad vendida debe ser mayor a 0.");
        }
    }

    public double getPrecioVentaCongelado() {
        return precioVentaCongelado;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        if(producto!=null){
        this.producto = producto;}
        else {
            throw new IllegalArgumentException("Error: Debe ingresar un producto.");
        }
            
    }
    
    
   @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        DetallePedido otroDetalle = (DetallePedido) obj;
        // Ahora Java buscará y eliminará basándose ÚNICAMENTE en tu código único
        return this.codigo == otroDetalle.codigo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }
    
}

