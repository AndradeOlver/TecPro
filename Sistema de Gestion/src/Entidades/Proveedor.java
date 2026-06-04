/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entidades;

/**
 *
 * @author equipo
 */
public class Proveedor {
    private String ruc;
    private String razonSocial;
    private String contacto;
    private String direccion;
    private String numeroContacto;
    private String correoContacto;
    public Proveedor(String ruc, String razonSocial,String direccion,String contacto, String numeroContacto, String correoContacto) {
        
        setRuc(ruc);
        setRazonSocial(razonSocial);
        setContacto(contacto);
        setDireccion(direccion);
        setNumeroContacto(numeroContacto);
        setCorreoContacto(correoContacto);
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        
        if (ruc != null && ruc.matches("\\d{11}")) {
            this.ruc = ruc;
        } else {
            throw new IllegalArgumentException("Error: El RUC debe contener exactamente 11 dígitos numéricos.");
        }
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        
        if (razonSocial != null && !razonSocial.isBlank()) {
            this.razonSocial = razonSocial;
        } else {
            throw new IllegalArgumentException("Error: La Razón Social no puede estar vacía.");
        }
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        
        if (contacto != null && !contacto.isBlank()) {
            this.contacto = contacto;
        } else {
            throw new IllegalArgumentException("Error: El nombre del contacto no puede estar vacío.");
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

    public String getCorreoContacto() {
        return correoContacto;
    }

    public void setCorreoContacto(String correoContacto) {
        
        if (correoContacto != null) {
            this.correoContacto = correoContacto;
        } else {
            throw new IllegalArgumentException("Error: El correo de contacto no puede ser nulo.");
        }
    }
    
}
