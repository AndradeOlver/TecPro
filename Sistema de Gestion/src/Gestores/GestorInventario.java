    package Gestores;

    import DAO.LoteProductoDAO;
    import DAO.MovimientosKardexDAO;
    import DAO.OrdenCompraDAO;
    import DAO.ProductoDAO;
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
            orden.avanzarEstado(); // Ejecuta tu regla de negocio (Solicitada -> Pendiente -> Procesada)
            ordenCompraDAO.actualizarEstado(codigo, orden.getEstado());
        }
    }

    public void cancelarOrden(int codigo) {
        ordenCompraDAO.actualizarEstado(codigo, "Cancelada");
    }

    public void procesarStockKardex(int codigoOrden) {
        // 1. Buscamos la orden y sus productos (lotes) de la BD
        Entidades.OrdenCompra orden = ordenCompraDAO.buscarPorCodigo(codigoOrden);
        if (orden == null) return;
        
        orden.getLotes().addAll(loteProductoDAO.obtenerLotesPorOrden(codigoOrden));

        // 2. Ejecutamos tu propio método matemático de Entidades.OrdenCompra
        java.util.List<Entidades.MovimientosKardex> movimientosGenerados = orden.procesarEntradaAlmacen();

        // 3. Guardamos los resultados del Kardex y actualizamos los precios/stock de los Productos
        for (Entidades.MovimientosKardex mk : movimientosGenerados) {
            kardexDAO.registrar(mk);
            productoDAO.actualizar(mk.getProducto());
        }
    }

    public java.util.List<Entidades.MovimientosKardex> obtenerKardexPorProducto(int idProducto) {
        return kardexDAO.obtenerPorProducto(idProducto);
    }
    }