//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package objetosNegocio;

import java.util.ArrayList;
import java.util.List;
import objetosServicio.Fecha;

public class Reserva {
    private String idReserva;
    private Pasajero pasajero;
    private Viaje viaje;
    private int cantidadAsientos;
    private Fecha fechaReserva;
    private String estado;
    private Empresa empresa;
    private List<String> numerosAsientos;
    private double precioTotal;
    private String metodoPago;
    private int ninosBrazos;

    public Reserva() {
        this.numerosAsientos = new ArrayList();
    }

    public Reserva(String idReserva, Pasajero pasajero, Viaje viaje, int cantidadAsientos, Fecha fechaReserva, String estado, Empresa empresa, double precioTotal, String metodoPago, int ninosBrazos) {
        this.idReserva = idReserva;
        this.pasajero = pasajero;
        this.viaje = viaje;
        this.cantidadAsientos = cantidadAsientos;
        this.fechaReserva = fechaReserva;
        this.estado = estado;
        this.empresa = empresa;
        this.numerosAsientos = new ArrayList();
        this.precioTotal = precioTotal;
        this.metodoPago = metodoPago;
        this.ninosBrazos = ninosBrazos;
    }

    public Reserva(String idReserva) {
        this(idReserva, (Pasajero)null, (Viaje)null, 0, (Fecha)null, (String)null, (Empresa)null, (double)0.0F, (String)null, 0);
    }

    public void agregarAsiento(String numeroAsiento) {
        if (!this.numerosAsientos.contains(numeroAsiento)) {
            this.numerosAsientos.add(numeroAsiento);
        }

    }

    public void removerAsiento(String numeroAsiento) {
        this.numerosAsientos.remove(numeroAsiento);
    }

    public List<String> getNumerosAsientos() {
        return this.numerosAsientos;
    }

    public void setNumerosAsientos(List<String> numerosAsientos) {
        this.numerosAsientos = numerosAsientos;
    }

    public String getIdReserva() {
        return this.idReserva;
    }

    public void setIdReserva(String idReserva) {
        this.idReserva = idReserva;
    }

    public Pasajero getPasajero() {
        return this.pasajero;
    }

    public void setPasajero(Pasajero pasajero) {
        this.pasajero = pasajero;
    }

    public Viaje getViaje() {
        return this.viaje;
    }

    public void setViaje(Viaje viaje) {
        this.viaje = viaje;
    }

    public int getCantidadAsientos() {
        return this.cantidadAsientos;
    }

    public void setCantidadAsientos(int cantidadAsientos) {
        this.cantidadAsientos = cantidadAsientos;
    }

    public Fecha getFechaReserva() {
        return this.fechaReserva;
    }

    public void setFechaReserva(Fecha fechaReserva) {
        this.fechaReserva = fechaReserva;
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

    public double getPrecioTotal() {
        return this.precioTotal;
    }

    public void setPrecioTotal(double precioTotal) {
        this.precioTotal = precioTotal;
    }

    public String getMetodoPago() {
        return this.metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public int getNinosBrazos() {
        return this.ninosBrazos;
    }

    public void setNinosBrazos(int ninosBrazos) {
        this.ninosBrazos = ninosBrazos;
    }

    public boolean equals(Object obj) {
        if (obj != null && this.getClass() == obj.getClass()) {
            Reserva other = (Reserva)obj;
            return this.idReserva != null && this.idReserva.equals(other.idReserva);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return this.idReserva != null ? this.idReserva.hashCode() : 0;
    }

    public String toString() {
        String nombrePasajero = this.pasajero != null && this.pasajero.getNombre() != null ? this.pasajero.getNombre() : "N/A";
        if (this.viaje != null && this.viaje.getIdViaje() != null) {
            this.viaje.getIdViaje();
        } else {
            String var10000 = "N/A";
        }

        if (this.numerosAsientos.isEmpty()) {
            String var4 = "Sin especificar";
        } else {
            String.join(", ", this.numerosAsientos);
        }

        return this.idReserva + " - " + nombrePasajero + " - $" + this.precioTotal + " - " + this.estado;
    }
}
