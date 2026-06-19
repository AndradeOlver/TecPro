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

    public OrdenCompra buscarOrdenPorCodigo(int codigo) {
        for (OrdenCompra oc : historialCompras) {
             if (oc.getCodigo() == codigo) return oc;
         }
         return null;
     }

    public void procesarStockKardex(int codigoOrden) {
        OrdenCompra orden = buscarOrdenPorCodigo(codigoOrden);
        if (orden != null && orden.getEstado().equals("Procesada")) {
            // Esto ejecuta la matemática y crea el historial del Kardex
            List<Entidades.MovimientosKardex> nuevosMovimientos = orden.procesarEntradaAlmacen();
            if (nuevosMovimientos != null && !nuevosMovimientos.isEmpty()) {
                this.bitacoraKardex.addAll(nuevosMovimientos);
            }
        }
    }

    public void avanzarEstadoOrden(int codigoOrden) {
        OrdenCompra orden = buscarOrdenPorCodigo(codigoOrden);
        if (orden == null) throw new IllegalArgumentException("Orden no encontrada.");
        
        orden.avanzarEstado(); 
        
        // Si al avanzar llegó a Procesada, automáticamente sube el stock
        if (orden.getEstado().equals("Procesada")) {
            procesarStockKardex(codigoOrden);
        }
    }

    public void cancelarOrden(int codigoOrden) {
        OrdenCompra orden = buscarOrdenPorCodigo(codigoOrden);
        if (orden != null) {
            orden.cancelarOrden();
        }
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
            // 1. Instanciamos el producto (como ya lo tenías)
            // Ojo: Lo dejamos con stock final de 8, porque simularemos que entraron 10 y se vendieron 2.
            Producto p1 = new Producto("Laptop Dell Inspiron", 2500.00, 2000.00, 8); 
            registrarProducto(p1);

            // ========================================================
            // 2. SIMULACIÓN DE KARDEX PARA LA LAPTOP (p1)
            // ========================================================
        
            // Movimiento A: El inventario inicial (Entraron 10 laptops el 1 de junio)
            Entidades.MovimientosKardex mov1 = new Entidades.MovimientosKardex(
                "2026-06-01",     // Fecha
                "entrada",        // Tipo
                10,               // Cantidad que ingresa
                2000.00,          // Costo unitario de compra
                10,               // Saldo de cantidad en ese momento
                2000.00,          // Saldo de costo promedio en ese momento
                p1                // El producto al que pertenece
            );
            this.bitacoraKardex.add(mov1); // Lo guardamos en el archivero

            // Movimiento B: Una venta (Salieron 2 laptops al día siguiente)
            Entidades.MovimientosKardex mov2 = new Entidades.MovimientosKardex(
                "2026-06-02",     // Fecha
                "salida",         // Tipo
                2,                // Cantidad que sale
                2000.00,          // Salen costeados al promedio que teníamos (2000)
                8,                // Saldo de cantidad restante (10 - 2 = 8)
                2000.00,          // El costo promedio no cambia en las salidas
                p1                // El producto al que pertenece
            );  
            this.bitacoraKardex.add(mov2); // Lo guardamos en el archivero


            // 3. Tus otros productos de prueba para que la tabla principal no se vea vacía
            Producto p2 = new Producto("Mouse Inalámbrico Logitech", 80.00, 50.00, 50);
            Producto p3 = new Producto("Monitor LG 24 Pulgadas", 600.00, 450.00, 15);
            registrarProducto(p2);
            registrarProducto(p3);
        
        } catch (Exception e) {
        System.out.println("Error cargando semillas de inventario: " + e.getMessage());
        }
    }
    
    // Métodos de acceso general para pintar las tablas en los Forms  
    public List<Producto> obtenerCatalogoCompleto() {
        return new ArrayList<>(this.catalogoProductos);
    }
   
    
    public List<OrdenCompra> obtenerHistorialCompras() {
        return new ArrayList<>(this.historialCompras);
    }
    public void actualizarProducto(Producto productoModificado) {
        if (productoModificado == null) {
            throw new IllegalArgumentException("Error: El producto a actualizar no puede ser nulo.");
        }

        boolean encontrado = false;
        
        // Usamos un bucle clásico para tener acceso al índice (i)
        for (int i = 0; i < catalogoProductos.size(); i++) {
            // Buscamos cuál de los productos viejos tiene el mismo ID que el nuevo
            if (catalogoProductos.get(i).getId() == productoModificado.getId()) {
                
                // Reemplazamos el objeto viejo por el nuevo en esa posición exacta
                catalogoProductos.set(i, productoModificado);
                encontrado = true;
                
                break; // Rompemos el ciclo porque ya lo actualizamos
            }
        }

        // Si terminó de revisar toda la lista y no lo encontró, lanzamos una alerta
        if (!encontrado) {
            throw new IllegalArgumentException("Error: No se encontró el producto con ID " + productoModificado.getId() + " en el sistema.");
        }
    }
    
    
    
}
