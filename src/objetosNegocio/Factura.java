//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package objetosNegocio;

import objetosServicio.Fecha;

public class Factura {
    private String idFactura;
    private Pasajero pasajero;
    private Reserva reserva;
    private double total;
    private Fecha fechaEmision;
    private String estado;
    private Empresa empresa;
    private String nombreEmpresaSnapshot;
    private String rucEmpresaSnapshot;
    private String direccionEmpresaSnapshot;
    private String nombreVendedor;
    private String datosPasajeroSnapshot;

    public Factura() {
    }

    public Factura(String idFactura, Pasajero pasajero, Reserva reserva, double total, Fecha fechaEmision, String estado, Empresa empresa) {
        this.idFactura = idFactura;
        this.pasajero = pasajero;
        this.reserva = reserva;
        this.total = total;
        this.fechaEmision = fechaEmision;
        this.estado = estado;
        this.empresa = empresa;
        this.nombreEmpresaSnapshot = "";
        this.rucEmpresaSnapshot = "";
        this.direccionEmpresaSnapshot = "";
        this.nombreVendedor = "";
        this.datosPasajeroSnapshot = "";
    }

    public Factura(String idFactura) {
        this(idFactura, (Pasajero)null, (Reserva)null, (double)0.0F, (Fecha)null, (String)null, (Empresa)null);
    }

    public String getIdFactura() {
        return this.idFactura;
    }

    public void setIdFactura(String idFactura) {
        this.idFactura = idFactura;
    }

    public Pasajero getPasajero() {
        return this.pasajero;
    }

    public void setPasajero(Pasajero pasajero) {
        this.pasajero = pasajero;
    }

    public Reserva getReserva() {
        return this.reserva;
    }

    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
    }

    public double getTotal() {
        return this.total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public Fecha getFechaEmision() {
        return this.fechaEmision;
    }

    public void setFechaEmision(Fecha fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public String getEstado() {
        return this.estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Empresa getEmpresa() {
        return this.empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public String getNombreEmpresaSnapshot() {
        return this.nombreEmpresaSnapshot;
    }

    public void setNombreEmpresaSnapshot(String nombreEmpresaSnapshot) {
        this.nombreEmpresaSnapshot = nombreEmpresaSnapshot;
    }

    public String getRucEmpresaSnapshot() {
        return this.rucEmpresaSnapshot;
    }

    public void setRucEmpresaSnapshot(String rucEmpresaSnapshot) {
        this.rucEmpresaSnapshot = rucEmpresaSnapshot;
    }

    public String getDireccionEmpresaSnapshot() {
        return this.direccionEmpresaSnapshot;
    }

    public void setDireccionEmpresaSnapshot(String direccionEmpresaSnapshot) {
        this.direccionEmpresaSnapshot = direccionEmpresaSnapshot;
    }

    public String getNombreVendedor() {
        return this.nombreVendedor;
    }

    public void setNombreVendedor(String nombreVendedor) {
        this.nombreVendedor = nombreVendedor;
    }

    public String getDatosPasajeroSnapshot() {
        return this.datosPasajeroSnapshot;
    }

    public void setDatosPasajeroSnapshot(String datosPasajeroSnapshot) {
        this.datosPasajeroSnapshot = datosPasajeroSnapshot;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (this.getClass() != obj.getClass()) {
            return false;
        } else {
            Factura other = (Factura)obj;
            if (this.idFactura == null) {
                if (other.idFactura != null) {
                    return false;
                }
            } else if (!this.idFactura.equals(other.idFactura)) {
                return false;
            }

            return true;
        }
    }

    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + (this.idFactura != null ? this.idFactura.hashCode() : 0);
        return hash;
    }

    public String toString() {
        String nombrePasajero = this.pasajero != null && this.pasajero.getNombre() != null ? this.pasajero.getNombre() : "N/A";
        return this.idFactura + " - Pasajero: " + nombrePasajero + " - Total: $" + this.total + " - " + this.estado;
    }
}
