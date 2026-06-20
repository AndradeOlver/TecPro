/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entidades;

/**
 *
 * @author equipo
 */
public class Producto {
    private static int contadorGlobal = 100000;
    private int id;
    private String descripcion;
    private Double precioVentaBase;
    private Double precioCompra;
    private Integer stock;
    
    // 1. CONSTRUCTOR PARA CREAR PRODUCTOS NUEVOS DESDE LA INTERFAZ
    public Producto(String descripcion, double precioVentaBase, double precioCompra, int stock) {
        this.id = contadorGlobal++;
        setDescripcion(descripcion);
        setPrecioCompra(precioCompra); // IMPORTANTE: Setear compra primero para la validación
        setPrecioVentaBase(precioVentaBase);
        setStock(stock);
    }

    // 2. CONSTRUCTOR PARA PRODUCTOS VACÍOS (Borradores)
    public Producto(String descripcion) {
        this.id = contadorGlobal++;
        setDescripcion(descripcion);
        this.precioVentaBase = null;
        this.precioCompra = null;
        this.stock = null;
    }

    // 3. ¡NUEVO! CONSTRUCTOR PARA EL DAO (Carga datos desde SQL Server)
    public Producto(int id, String descripcion, Double precioVentaBase, Double precioCompra, Integer stock) {
        this.id = id; // Usamos el ID real de la base de datos
        setDescripcion(descripcion);
        
        // Asignación directa para datos que ya vienen validados de la BD
        this.precioCompra = precioCompra;
        this.precioVentaBase = precioVentaBase;
        this.stock = stock;
        this.stock = stock;
    }


    public int getId() {
        return id;
    }

   

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        if (descripcion != null && !descripcion.isBlank()) {
            this.descripcion = descripcion;
        } else {
            throw new IllegalArgumentException("Error: La descripción del producto no puede estar vacía.");
        }
    }

    public Double getPrecioVentaBase() {
        return precioVentaBase;
    }

    public void setPrecioVentaBase(Double precioVentaBase) {
        // 1. Permitimos nulos para cuando el producto recién se crea sin precios
        if (precioVentaBase == null) {
            this.precioVentaBase = null;
            return;
        }
        
        // 2. Validación matemática básica
        if (precioVentaBase <= 0) {
            throw new IllegalArgumentException("Error: El precio de venta base debe ser mayor a 0.");
        }
        
        // 3. LA REGLA DE NEGOCIO (Blindaje interno)
        if (this.precioCompra != null && precioVentaBase <= this.precioCompra) {
            throw new IllegalArgumentException("Error: El precio de venta (S/ " + precioVentaBase + 
                                               ") debe ser mayor al precio de compra actual (S/ " + this.precioCompra + ").");
        }

        this.precioVentaBase = precioVentaBase;
    }

    public Double getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(Double precioCompra) {
        if ( precioCompra ==null||precioCompra > 0 ) {
            this.precioCompra = precioCompra;
        } else {
            throw new IllegalArgumentException("Error: El precio de compra debe ser mayor a 0.");
        }
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        if ( stock ==null||stock >= 0 ) {
            this.stock = stock;
        } else {
            throw new IllegalArgumentException("Error: El stock debe ser mayor o igual a 0.");
        }
    }
    
    public void actualizarStock(int cantidad, String tipoMovimiento) {
        if(this.stock==null) this.stock=0;
        
        // Validamos que no nos envíen cantidades negativas por error
        if (cantidad <= 0) {
            throw new IllegalArgumentException("Error: La cantidad a mover debe ser mayor a 0.");
        }

        if (tipoMovimiento.equals("entrada")) {
            // Camino 1: Ingreso de mercadería (Sumamos)
            this.setStock(this.stock + cantidad);
            
        } else if (tipoMovimiento.equals("salida")) {
            // Camino 2: Venta de mercadería (Restamos con Guardia de Seguridad)
            if (this.stock >= cantidad) {
                this.setStock(this.stock - cantidad);
            } else {
                throw new IllegalStateException("Error: Stock insuficiente para realizar la salida. Stock actual: " + this.stock);
            }
        } else {
            throw new IllegalArgumentException("Error: El tipo de movimiento debe ser 'entrada' o 'salida'.");
        }
    }
    
    public void recalcularCostoPromedio(int cantidadNueva, double precioNuevo) {
        if(this.precioCompra==null) this.precioCompra=0.0;
        if(this.stock==null) this.stock=0;
        // Validación básica
        if (cantidadNueva <= 0 || precioNuevo < 0) {
            throw new IllegalArgumentException("Error: Cantidad y precio nuevo deben ser válidos.");
        }

       
        double valorInventarioActual = this.stock * this.precioCompra;

        // Paso 2: Tu dinero nuevo
        double valorIngresoNuevo = cantidadNueva * precioNuevo;

        // Paso 3: La suma de cantidades físicas
        int nuevoStockTotal = this.stock + cantidadNueva;

        // Paso 4: La fórmula del Promedio Ponderado
        double nuevoCostoPromedio = (valorInventarioActual + valorIngresoNuevo) / nuevoStockTotal;

        // Actualizamos la variable de la clase
        this.setPrecioCompra(nuevoCostoPromedio);
    }
    
    @Override
    public String toString() {
        // Esto es lo que verá el usuario en el JComboBox de forma elegante
        return this.descripcion;
    }
    
}
