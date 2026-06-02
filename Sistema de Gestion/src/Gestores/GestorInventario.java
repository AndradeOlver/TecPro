/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Gestores;


import Entidades.MovimientosKardex;
import Entidades.OrdenCompra;
import Entidades.Producto;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author equipo
 */
public class GestorInventario {
    // 1. Las "Bases de Datos" en memoria
    private List<Producto> catalogoProductos;
    private List<OrdenCompra> historialCompras;
    private List<MovimientosKardex> bitacoraKardex;

    public GestorInventario() {
        // Inicializamos las colecciones al nacer el gestor
        this.catalogoProductos = new ArrayList<>();
        this.historialCompras = new ArrayList<>();
        this.bitacoraKardex = new ArrayList<>();
        
        cargarDatosDePrueba(); 
    }

    // ==========================================
    // LÓGICA DE GESTIÓN DE PRODUCTOS
    // ==========================================

    public void registrarProducto(Producto nuevoProducto) {
        if (nuevoProducto == null) {
            throw new IllegalArgumentException("Error: No se puede registrar un producto nulo.");
        }

        // Validación de lógica de negocio: Evitar IDs duplicados
        for (Producto p : catalogoProductos) {
            if (p.getId() == nuevoProducto.getId()) {
                throw new IllegalArgumentException("Error: Ya existe un producto registrado con el ID " + nuevoProducto.getId());
            }
        }
        
        this.catalogoProductos.add(nuevoProducto);
    }

    public Producto buscarProductoPorId(int idBuscado) {
        // Búsqueda secuencial exacta
        for (Producto p : catalogoProductos) {
            if (p.getId() == idBuscado) {
                return p;
            }
        }
        return null;
    }
    
    public List<Producto> buscarProductoPorDescripcion(String textoBusqueda) {
        // Lógica para cumplir tu requerimiento visual: búsqueda ambigua
        List<Producto> resultados = new ArrayList<>();
        
        for (Producto p : catalogoProductos) {
            if (p.getDescripcion().toLowerCase().contains(textoBusqueda.toLowerCase())) {
                resultados.add(p);
            }
        }
        return resultados;
    }

    // ==========================================
    // LÓGICA DE ÓRDENES DE COMPRA E INVENTARIO
    // ==========================================

    public void registrarOrdenCompra(OrdenCompra nuevaOrden) {
        if (nuevaOrden == null) {
            throw new IllegalArgumentException("Error: La orden de compra no puede ser nula.");
        }

        // Evitar duplicados por código de orden
        for (OrdenCompra oc : historialCompras) {
            if (oc.getCodigo() == nuevaOrden.getCodigo()) {
                throw new IllegalArgumentException("Error: La orden con código " + nuevaOrden.getCodigo() + " ya existe.");
            }
        }

        this.historialCompras.add(nuevaOrden);
    }

    public void procesarEntradaAlmacen(OrdenCompra orden) {
        // 1. Buscamos la orden en nuestra lista para asegurar que existe
        OrdenCompra ordenGuardada = null;
        for (OrdenCompra oc : historialCompras) {
            if (oc.getCodigo() == orden.getCodigo()) {
                ordenGuardada = oc;
                break;
            }
        }

        if (ordenGuardada == null) {
            throw new IllegalArgumentException("Error: La orden de compra no está registrada en el sistema.");
        }

        // 2. Disparamos la lógica interna de la orden (que ya programaste en la entidad)
        // Esto confirmará el estado y ejecutará el promedio ponderado de los productos
        ordenGuardada.confirmarIngreso();
        ordenGuardada.procesarEntradaAlmacen();
        
        // Nota Arquitectónica: En una versión con Base de Datos, aquí llamarías
        // a un método para guardar los nuevos MovimientosKardex generados en SQL.
    }

    // ==========================================
    // LÓGICA DE LECTURA DEL KARDEX
    // ==========================================

    public List<MovimientosKardex> obtenerKardexPorProducto(int idProducto) {
        // Filtra la bitácora global para devolver solo la historia de un producto específico
        List<MovimientosKardex> historialProducto = new ArrayList<>();
        
        for (MovimientosKardex mov : bitacoraKardex) {
            if (mov.getProducto().getId() == idProducto) {
                historialProducto.add(mov);
            }
        }
        
        return historialProducto;
    }

    // ==========================================
    // MÉTODO DE SIMULACIÓN (DATOS DE PRUEBA)
    // ==========================================

    private void cargarDatosDePrueba() {
        try {
            // Instanciamos productos con un stock base para que no arranquen en 0
            Producto p1 = new Producto("Laptop Dell Inspiron", 2500.00, 2000.00, 10);
            Producto p2 = new Producto("Mouse Inalámbrico Logitech", 80.00, 50.00, 50);
            Producto p3 = new Producto("Monitor LG 24 Pulgadas", 600.00, 450.00, 15);
            
            registrarProducto(p1);
            registrarProducto(p2);
            registrarProducto(p3);
            
        } catch (Exception e) {
            System.out.println("Error cargando semillas de inventario: " + e.getMessage());
        }
    }
    
    // Métodos de acceso general para pintar las tablas en los Forms  
    public List<Producto> obtenerCatalogoCompleto() {
        return this.catalogoProductos;
    }
    
    public List<OrdenCompra> obtenerHistorialCompras() {
        return this.historialCompras;
    }
    
    
    
}
