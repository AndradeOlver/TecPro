    package Gestores;

    import DAO.LoteProductoDAO;
    import DAO.MovimientosKardexDAO;
    import DAO.OrdenCompraDAO;
    import DAO.ProductoDAO;
import Entidades.OrdenCompra;
    import Entidades.Producto;
    import java.util.List;

    public class GestorInventario {
        private ProductoDAO productoDAO;
        private OrdenCompraDAO ordenCompraDAO;
        private LoteProductoDAO loteProductoDAO;
        private MovimientosKardexDAO kardexDAO; 

        public GestorInventario() {
        this.productoDAO = new ProductoDAO();
        this.ordenCompraDAO = new DAO.OrdenCompraDAO();
        this.loteProductoDAO = new DAO.LoteProductoDAO();
        this.kardexDAO = new DAO.MovimientosKardexDAO();
        }

        

        public void registrarProducto(Producto nuevoProducto) {
            if (nuevoProducto == null) {
                throw new IllegalArgumentException("Error: No se puede registrar un producto nulo.");
            }

            if (productoDAO.buscarPorId(nuevoProducto.getId()) != null) {
                throw new IllegalArgumentException("Error: Ya existe un producto registrado con el ID " + nuevoProducto.getId());
            }

            boolean exito = productoDAO.registrar(nuevoProducto);
            if (!exito) {
                throw new IllegalArgumentException("Error: Hubo un problema al guardar el producto en la Base de Datos.");
            }
        }

        public Producto buscarProductoPorId(int idBuscado) {
            return productoDAO.buscarPorId(idBuscado);
        }

        public List<Producto> buscarProductoPorDescripcion(String textoBusqueda) {
            return productoDAO.buscarPorDescripcion(textoBusqueda);
        }

        public List<Producto> obtenerTodosLosProductos() {
            return productoDAO.obtenerTodos();
        }

        public void actualizarProducto(Producto productoModificado) {
            if (productoModificado == null) {
                throw new IllegalArgumentException("Error: El producto a actualizar es nulo.");
            }

            boolean exito = productoDAO.actualizar(productoModificado);
            if (!exito) {
                throw new IllegalArgumentException("Error: No se pudo actualizar el producto en la Base de Datos.");
            }
        }

        // Método listo para cuando conectemos compras/ventas y necesitemos mover el inventario
        public void modificarStock(int idProducto, int cantidadVariacion) {
            Producto p = productoDAO.buscarPorId(idProducto);
            if (p != null) {
                int nuevoStock = p.getStock() + cantidadVariacion;
                if (nuevoStock < 0) {
                    throw new IllegalArgumentException("Error: El stock no puede quedar en negativo.");
                }
                p.setStock(nuevoStock);
                productoDAO.actualizar(p);
            } else {
                throw new IllegalArgumentException("Error: Producto no encontrado para modificar stock.");
            }
        }
        public java.util.List<Entidades.OrdenCompra> obtenerHistorialCompras() {
        return ordenCompraDAO.obtenerTodos();
    }

    public void registrarOrdenCompra(Entidades.OrdenCompra orden) {
        boolean exito = ordenCompraDAO.registrar(orden);
        if (exito) {
            loteProductoDAO.registrarLotes(orden.getCodigo(), orden.getLotes());
        } else {
            throw new RuntimeException("Error crítico: Falló la creación de la orden en la BD.");
        }
    }

    public void avanzarEstadoOrden(int codigo) {
        Entidades.OrdenCompra orden = ordenCompraDAO.buscarPorCodigo(codigo);
        if(orden != null) {
            orden.avanzarEstado();
            // Ejecuta tu regla de negocio (Solicitada -> Pendiente -> Procesada)
            ordenCompraDAO.actualizarEstado(codigo, orden.getEstado());
            
            // CORRECCIÓN: Si el nuevo estado es "Procesada", se ingresan los productos al Kardex
            if (orden.getEstado().equals("Procesada")) {
                this.procesarStockKardex(codigo);
            }
        }
    }

    public void cancelarOrden(int codigo) {
        ordenCompraDAO.actualizarEstado(codigo, "Cancelada");
    }
    public OrdenCompra obtenerDetallesDeOrden(int codigoOrden) {
        // 1. Buscamos la cabecera de la orden
        OrdenCompra orden = ordenCompraDAO.buscarPorCodigo(codigoOrden);
        
        if (orden != null) {
            // 2. Le inyectamos la lista de productos asociados a ese código
            orden.getLotes().addAll(loteProductoDAO.obtenerLotesPorOrden(codigoOrden));
        }
        
        return orden;
    }

  public void procesarStockKardex(int codigoOrden) {
    OrdenCompra orden = ordenCompraDAO.buscarPorCodigo(codigoOrden);
    if (orden == null) return;
    orden.getLotes().addAll(loteProductoDAO.obtenerLotesPorOrden(codigoOrden));

    List<Entidades.MovimientosKardex> movimientosGenerados = orden.procesarEntradaAlmacen();

    // Iniciamos la transacción controlada
    try (java.sql.Connection con = DAO.ConexionSQL.probarConexion()) {
        con.setAutoCommit(false);
        
        try {
            for (Entidades.MovimientosKardex mk : movimientosGenerados) {
                // Registro en el Kardex
                boolean kOK = kardexDAO.registrarTransaccional(con, mk);
                if (!kOK) throw new java.sql.SQLException("Error al insertar registro en Kardex para producto ID: " + mk.getProducto().getId());

                // Actualización del Producto
                boolean pOK = productoDAO.actualizarTransaccional(con, mk.getProducto());
                if (!pOK) throw new java.sql.SQLException("Error al actualizar stock/precio del producto ID: " + mk.getProducto().getId());
            }
            
            // Si el bucle termina con éxito para todos los productos, se aprueba la transacción
            con.commit();
            
        } catch (Exception e) {
            // Si falla en CUALQUIER iteración, retrocedemos todo el lote
            con.rollback();
            throw new RuntimeException("Fallo al procesar el inventario. Se han revertido los cambios: " + e.getMessage());
        }
    } catch (java.sql.SQLException e) {
        System.err.println("Error de base de datos en procesarStockKardex: " + e.getMessage());
    }
}

    public java.util.List<Entidades.MovimientosKardex> obtenerKardexPorProducto(int idProducto) {
        return kardexDAO.obtenerPorProducto(idProducto);
    }
    public void eliminarOrdenesCanceladas() {
        boolean exito = ordenCompraDAO.eliminarCanceladas();
        if (!exito) {
            throw new RuntimeException("Error crítico: No se pudieron eliminar las órdenes de la base de datos.");
        }
    }
    public void eliminarProducto(int id) {
        boolean exito = productoDAO.eliminar(id);
        if (!exito) {
            throw new RuntimeException("Acción denegada: Este producto no se puede eliminar porque cuenta con stock en el Kardex o historial de transacciones asociadas.");
        }
    }
}