//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package objetosNegocio;

import objetosServicio.Fecha;

public class Pago {
    private String idPago;
    private Usuario usuario;
    private Factura factura;
    private double monto;
    private Fecha fechaPago;
    private String metodo;

    public Pago() {
    }

    public Pago(String idPago, Usuario usuario, Factura factura, double monto, Fecha fechaPago, String metodo) {
        this.idPago = idPago;
        this.usuario = usuario;
        this.factura = factura;
        this.monto = monto;
        this.fechaPago = fechaPago;
        this.metodo = metodo;
    }

    public Pago(String idPago) {
        this(idPago, (Usuario)null, (Factura)null, (double)0.0F, (Fecha)null, (String)null);
    }

    public String getIdPago() {
        return this.idPago;
    }

    public void setIdPago(String idPago) {
        this.idPago = idPago;
    }

    public Usuario getUsuario() {
        return this.usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Factura getFactura() {
        return this.factura;
    }

    public void setFactura(Factura factura) {
        this.factura = factura;
    }

    public double getMonto() {
        return this.monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public Fecha getFechaPago() {
        return this.fechaPago;
    }

    public void setFechaPago(Fecha fechaPago) {
        this.fechaPago = fechaPago;
    }

    public String getMetodo() {
        return this.metodo;
    }

    public void setMetodo(String metodo) {
        this.metodo = metodo;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (this.getClass() != obj.getClass()) {
            return false;
        } else {
            Pago other = (Pago)obj;
            if (this.idPago == null) {
                if (other.idPago != null) {
                    return false;
                }
            } else if (!this.idPago.equals(other.idPago)) {
                return false;
            }

            return true;
        }
    }

    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + (this.idPago != null ? this.idPago.hashCode() : 0);
        return hash;
    }

    public String toString() {
        String nombreUsuario = this.usuario != null && this.usuario.getNombre() != null ? this.usuario.getNombre() : "N/A";
        return this.idPago + " - Usuario: " + nombreUsuario + " - Monto: $" + this.monto + " - " + this.metodo;
    }

    public String getMetodoPago() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getEstado() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
