//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package objetosNegocio;

import objetosServicio.Fecha;

public class Viaje {
    private String idViaje;
    private Bus bus;
    private Ruta ruta;
    private Fecha fechaSalida;
    private Fecha fechaLlegada;
    private int asientosDisponibles;
    private String estado;
    private Empresa empresa;
    private String horaSalida;
    private String horaLlegada;
    private double precio;
    private Conductor conductor1;
    private Conductor conductor2;

    public Viaje() {
    }

    public Viaje(String idViaje) {
        this.idViaje = idViaje;
    }

    public Viaje(String idViaje, Bus bus, Ruta ruta, Fecha fechaSalida, Fecha fechaLlegada, int asientosDisponibles, String estado, Empresa empresa, String horaSalida, String horaLlegada, double precio) {
        this.idViaje = idViaje;
        this.bus = bus;
        this.ruta = ruta;
        this.fechaSalida = fechaSalida;
        this.fechaLlegada = fechaLlegada;
        this.asientosDisponibles = asientosDisponibles;
        this.estado = estado;
        this.empresa = empresa;
        this.horaSalida = horaSalida;
        this.horaLlegada = horaLlegada;
        this.precio = precio;
    }

    public String getIdViaje() {
        return this.idViaje;
    }

    public void setIdViaje(String idViaje) {
        this.idViaje = idViaje;
    }

    public Bus getBus() {
        return this.bus;
    }

    public void setBus(Bus bus) {
        this.bus = bus;
    }

    public Ruta getRuta() {
        return this.ruta;
    }

    public void setRuta(Ruta ruta) {
        this.ruta = ruta;
    }

    public Fecha getFechaSalida() {
        return this.fechaSalida;
    }

    public void setFechaSalida(Fecha fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    public Fecha getFechaLlegada() {
        return this.fechaLlegada;
    }

    public void setFechaLlegada(Fecha fechaLlegada) {
        this.fechaLlegada = fechaLlegada;
    }

    public int getAsientosDisponibles() {
        return this.asientosDisponibles;
    }

    public void setAsientosDisponibles(int asientosDisponibles) {
        this.asientosDisponibles = asientosDisponibles;
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

    public String getHoraSalida() {
        return this.horaSalida;
    }

    public void setHoraSalida(String horaSalida) {
        this.horaSalida = horaSalida;
    }

    public String getHoraLlegada() {
        return this.horaLlegada;
    }

    public void setHoraLlegada(String horaLlegada) {
        this.horaLlegada = horaLlegada;
    }

    public double getPrecio() {
        return this.precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public Conductor getConductor1() {
        return this.conductor1;
    }

    public void setConductor1(Conductor conductor1) {
        this.conductor1 = conductor1;
    }

    public Conductor getConductor2() {
        return this.conductor2;
    }

    public void setConductor2(Conductor conductor2) {
        this.conductor2 = conductor2;
    }

    public String toString() {
        return this.idViaje;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj != null && this.getClass() == obj.getClass()) {
            Viaje viaje = (Viaje)obj;
            return this.idViaje != null ? this.idViaje.equals(viaje.idViaje) : viaje.idViaje == null;
        } else {
            return false;
        }
    }
}
