package objetosNegocio;

import objetosServicio.Fecha;

/** Reporte que un conductor envía al administrador (no puede viajar, falla mecánica, queja, etc.). */
public class Novedad {
    public static final String ABIERTA = "ABIERTA";
    public static final String EN_REVISION = "EN_REVISION";
    public static final String RESUELTA = "RESUELTA";

    private String idNovedad;
    private String idConductor;
    private String idViaje;
    private String tipo;
    private String asunto;
    private String mensaje;
    private Fecha fecha;
    private String hora;
    private String estado;
    private String respuesta;
    private String idRevisor;

    public String getIdNovedad() { return idNovedad; }
    public void setIdNovedad(String idNovedad) { this.idNovedad = idNovedad; }
    public String getIdConductor() { return idConductor; }
    public void setIdConductor(String idConductor) { this.idConductor = idConductor; }
    public String getIdViaje() { return idViaje; }
    public void setIdViaje(String idViaje) { this.idViaje = idViaje; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getAsunto() { return asunto; }
    public void setAsunto(String asunto) { this.asunto = asunto; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public Fecha getFecha() { return fecha; }
    public void setFecha(Fecha fecha) { this.fecha = fecha; }
    public String getHora() { return hora; }
    public void setHora(String hora) { this.hora = hora; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getRespuesta() { return respuesta; }
    public void setRespuesta(String respuesta) { this.respuesta = respuesta; }
    public String getIdRevisor() { return idRevisor; }
    public void setIdRevisor(String idRevisor) { this.idRevisor = idRevisor; }
}
