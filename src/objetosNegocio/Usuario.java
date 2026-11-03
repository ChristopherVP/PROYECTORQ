//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package objetosNegocio;

import objetosServicio.Fecha;

public class Usuario {
    private String idUsuario;
    private String nombre;
    private String cedula;
    private String contrasena;
    private String email;
    private String telefono;
    private String tipo;
    private Fecha fechaRegistro;
    private Empresa empresa;
    private String estado;

    public Usuario() {
    }

    public Usuario(String idUsuario, String nombre, String cedula, String contrasena, String email, String telefono, String tipo, Fecha fechaRegistro, Empresa empresa, String estado) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.cedula = cedula;
        this.contrasena = contrasena;
        this.email = email;
        this.telefono = telefono;
        this.tipo = tipo;
        this.fechaRegistro = fechaRegistro;
        this.empresa = empresa;
        this.estado = estado;
    }

    public Usuario(String idUsuario) {
        this(idUsuario, (String)null, (String)null, (String)null, (String)null, (String)null, (String)null, (Fecha)null, (Empresa)null, (String)null);
    }

    public String getIdUsuario() {
        return this.idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return this.nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCedula() {
        return this.cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getContrasena() {
        return this.contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return this.telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getTipo() {
        return this.tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Fecha getFechaRegistro() {
        return this.fechaRegistro;
    }

    public void setFechaRegistro(Fecha fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Empresa getEmpresa() {
        return this.empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public String getEstado() {
        return this.estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public boolean validarContrasena(String contrasena) {
        return this.contrasena != null && this.contrasena.equals(contrasena);
    }

    public boolean equals(Object obj) {
        if (obj != null && this.getClass() == obj.getClass()) {
            Usuario other = (Usuario)obj;
            return this.idUsuario != null && this.idUsuario.equals(other.idUsuario);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return this.idUsuario != null ? this.idUsuario.hashCode() : 0;
    }

    public String toString() {
        return this.idUsuario + " - " + this.nombre + " - " + this.email + " - " + this.tipo;
    }
}
