/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entidades;

import java.util.List;
import java.util.ArrayList;
/**
 *
 * @author equipo
 */
public class Cliente {
    private int codigo;
    private String nombre;
    private String direccion;
    private String correo;
    private String numeroContacto;
    private List<Pedido> pedidos;

    public Cliente(int codigo, String nombre, String direccion, String correo, String numeroContacto) {
      setCodigo(codigo);
        setNombre(nombre);
        setDireccion(direccion);
        setCorreo(correo);
        setNumeroContacto(numeroContacto);
        this.pedidos = new ArrayList<>();
    }
    
    public int getCodigo() {
        return codigo;
    }
    
    public void setCodigo(int codigo) {
       if (codigo > 0) {
            this.codigo = codigo;
        } else {
            throw new IllegalArgumentException("Error: El código del cliente debe ser mayor a 0.");
        }
    }

    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
         if (nombre != null && !nombre.isBlank()) {
            this.nombre = nombre;
        } else {
            throw new IllegalArgumentException("Error: El nombre no puede estar vacío.");
        }
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
      if (direccion != null && !direccion.isBlank()) {
            this.direccion = direccion;
        } else {
            throw new IllegalArgumentException("Error: La dirección no puede estar vacía.");
        }
    }

    public String getCorreo() {
        
        return correo;
    }
    
    public void setCorreo(String correo) {
      if (correo != null && correo.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) { //validacion mas completa
            this.correo = correo;
        } else {
            throw new IllegalArgumentException("Error: Formato de correo electrónico inválido.");
        }
    }

    public String getNumeroContacto() {
        return numeroContacto;
    }
    
    public void setNumeroContacto(String numeroContacto) {
      if (numeroContacto != null && numeroContacto.matches("\\d{9}")) {
            this.numeroContacto = numeroContacto;
        } else {
            throw new IllegalArgumentException("Error: El número de contacto debe contener exactamente 9 dígitos numéricos.");
        }
    }

    public List<Pedido> getPedidos() {
        return pedidos;
    }
    
   public void llenarPedidos(List<Pedido> pedidosActualizado) {
        if (pedidosActualizado != null) {
            
            this.pedidos.clear(); 
            
            this.pedidos.addAll(pedidosActualizado); 
        } else {
            throw new IllegalArgumentException("Error: La lista de pedidos entrante no puede ser nula.");
        }
    }

   public double calcularDeudaHistorica() {
        double deudaTotal = 0.0;
      
        for (Pedido pedido : this.pedidos) {
            deudaTotal += pedido.getDeudaPendiente();
        }

        return deudaTotal;
    }
    
 
}
