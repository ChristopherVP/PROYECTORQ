package objetosNegocio;

import objetosServicio.Fecha;

/** Asiento contable de la billetera de un pasajero (recarga, compra o reembolso). */
public class Movimiento {
    public static final String RECARGA = "RECARGA";
    public static final String COMPRA = "COMPRA";
    public static final String REEMBOLSO = "REEMBOLSO";

    public static final String METODO_TARJETA = "TARJETA";
    public static final String METODO_EFECTIVO = "EFECTIVO";
    public static final String METODO_BILLETERA = "BILLETERA";

    public static final String PENDIENTE = "PENDIENTE";
    public static final String APROBADA = "APROBADA";
    public static final String RECHAZADA = "RECHAZADA";

    private String idMovimiento;
    private String idPasajero;
    private String tipo;
    private double monto;
    private String metodo;
    private String estado;
    private String referencia;
    private Fecha fecha;
    private String hora;
    private String idRevisor;
    private String motivo;

    public String getIdMovimiento() { return idMovimiento; }
    public void setIdMovimiento(String idMovimiento) { this.idMovimiento = idMovimiento; }
    public String getIdPasajero() { return idPasajero; }
    public void setIdPasajero(String idPasajero) { this.idPasajero = idPasajero; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }
    public String getMetodo() { return metodo; }
    public void setMetodo(String metodo) { this.metodo = metodo; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getReferencia() { return referencia; }
    public void setReferencia(String referencia) { this.referencia = referencia; }
    public Fecha getFecha() { return fecha; }
    public void setFecha(Fecha fecha) { this.fecha = fecha; }
    public String getHora() { return hora; }
    public void setHora(String hora) { this.hora = hora; }
    public String getIdRevisor() { return idRevisor; }
    public void setIdRevisor(String idRevisor) { this.idRevisor = idRevisor; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public boolean esRecargaPendiente() {
        return RECARGA.equals(tipo) && PENDIENTE.equals(estado);
    }

    @Override
    public String toString() {
        return "Movimiento{" + idMovimiento + ", " + tipo + ", " + monto + ", " + estado + "}";
    }
}
