package objetosNegocio;

import objetosServicio.Fecha;
import objetosServicio.Seguridad;

/**
 * Cuenta de acceso al sistema. La contraseña y la respuesta de seguridad se guardan siempre como hash
 * (ver {@link Seguridad}); nunca en texto plano.
 */
public class Usuario {
    public static final String ESTADO_ACTIVO = "ACTIVO";
    public static final String ESTADO_INACTIVO = "INACTIVO";
    public static final String ESTADO_PENDIENTE = "PENDIENTE";

    private String idUsuario;
    private String login;
    private String nombre;
    private String cedula;
    private String contrasena;
    private String email;
    private String telefono;
    private String tipo;
    private Fecha fechaRegistro;
    private Empresa empresa;
    private String estado;
    private String preguntaSeguridad;
    private String respuestaSeguridad;
    private String idPerfil;
    private int intentosRecuperacion;
    private long bloqueoHasta;

    public Usuario() {
    }

    public Usuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getIdUsuario() { return idUsuario; }
    public void setIdUsuario(String idUsuario) { this.idUsuario = idUsuario; }
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }
    /** Devuelve el HASH almacenado, no la contraseña. */
    public String getContrasena() { return contrasena; }
    /** Recibe el HASH ya calculado (use {@link #definirContrasena(String)} para texto plano). */
    public void setContrasena(String contrasenaHash) { this.contrasena = contrasenaHash; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public Fecha getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(Fecha fechaRegistro) { this.fechaRegistro = fechaRegistro; }
    public Empresa getEmpresa() { return empresa; }
    public void setEmpresa(Empresa empresa) { this.empresa = empresa; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getPreguntaSeguridad() { return preguntaSeguridad; }
    public void setPreguntaSeguridad(String preguntaSeguridad) { this.preguntaSeguridad = preguntaSeguridad; }
    /** Devuelve el HASH de la respuesta normalizada. */
    public String getRespuestaSeguridad() { return respuestaSeguridad; }
    public void setRespuestaSeguridad(String respuestaHash) { this.respuestaSeguridad = respuestaHash; }
    /** Id de la ficha asociada (CNDxxx para conductores, PASxxx para pasajeros). */
    public String getIdPerfil() { return idPerfil; }
    public void setIdPerfil(String idPerfil) { this.idPerfil = idPerfil; }
    public int getIntentosRecuperacion() { return intentosRecuperacion; }
    public void setIntentosRecuperacion(int intentosRecuperacion) { this.intentosRecuperacion = intentosRecuperacion; }
    public long getBloqueoHasta() { return bloqueoHasta; }
    public void setBloqueoHasta(long bloqueoHasta) { this.bloqueoHasta = bloqueoHasta; }

    public void definirContrasena(String contrasenaPlana) {
        this.contrasena = Seguridad.hash(contrasenaPlana);
    }

    public void definirRespuestaSeguridad(String respuestaPlana) {
        this.respuestaSeguridad = Seguridad.hash(Seguridad.normalizarRespuesta(respuestaPlana));
    }

    public boolean validarContrasena(String contrasenaPlana) {
        return Seguridad.verificar(contrasenaPlana, this.contrasena);
    }

    public boolean validarRespuestaSeguridad(String respuestaPlana) {
        return Seguridad.verificar(Seguridad.normalizarRespuesta(respuestaPlana), this.respuestaSeguridad);
    }

    public boolean tienePreguntaSeguridad() {
        return preguntaSeguridad != null && !preguntaSeguridad.trim().isEmpty()
                && respuestaSeguridad != null && !respuestaSeguridad.trim().isEmpty();
    }

    public Rol getRol() {
        return Rol.desdeEtiqueta(tipo);
    }

    public boolean tieneEmpresa() {
        return empresa != null && empresa.getIdEmpresa() != null && !empresa.getIdEmpresa().trim().isEmpty();
    }

    /** Administrador sin empresa asignada: gestiona toda la plataforma. */
    public boolean esSuperAdministrador() {
        return getRol() == Rol.ADMINISTRADOR && !tieneEmpresa();
    }

    public boolean estaActivo() {
        return ESTADO_ACTIVO.equalsIgnoreCase(estado);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Usuario)) return false;
        Usuario otro = (Usuario) obj;
        return idUsuario != null && idUsuario.equals(otro.idUsuario);
    }

    @Override
    public int hashCode() {
        return idUsuario != null ? idUsuario.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Usuario{id=" + idUsuario + ", login=" + login + ", nombre=" + nombre + ", tipo=" + tipo + ", estado=" + estado + "}";
    }
}
