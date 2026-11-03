//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package objetosNegocio;

public class Asiento {
    public static final String DISPONIBLE = "Disponible";
    public static final String RESERVADO = "Reservado";
    public static final String OCUPADO = "Ocupado";
    private String idAsiento;
    private int numeroAsiento;
    private String estado;
    private String idViaje;
    private String idReserva;
    private String ubicacion;
    private String liberadoEn;

    public Asiento() {
        this.estado = "Disponible";
    }

    public Asiento(String idAsiento, int numeroAsiento, String idViaje, String ubicacion) {
        this.idAsiento = idAsiento;
        this.numeroAsiento = numeroAsiento;
        this.idViaje = idViaje;
        this.ubicacion = ubicacion;
        this.estado = "Disponible";
    }

    public Asiento(String idAsiento, int numeroAsiento, String estado, String idViaje, String idReserva, String ubicacion) {
        this.idAsiento = idAsiento;
        this.numeroAsiento = numeroAsiento;
        this.estado = estado;
        this.idViaje = idViaje;
        this.idReserva = idReserva;
        this.ubicacion = ubicacion;
    }

    public boolean reservar(String idReserva) {
        if (this.estado.equals("Disponible")) {
            this.estado = "Reservado";
            this.idReserva = idReserva;
            return true;
        } else {
            return false;
        }
    }

    public boolean marcarOcupado() {
        if (this.estado.equals("Reservado")) {
            this.estado = "Ocupado";
            return true;
        } else {
            return false;
        }
    }

    public void liberar() {
        this.estado = "Disponible";
        this.idReserva = null;
    }

    public boolean estaDisponible() {
        return this.estado.equals("Disponible");
    }

    public String getIdAsiento() {
        return this.idAsiento;
    }

    public void setIdAsiento(String idAsiento) {
        this.idAsiento = idAsiento;
    }

    public int getNumeroAsiento() {
        return this.numeroAsiento;
    }

    public void setNumeroAsiento(int numeroAsiento) {
        this.numeroAsiento = numeroAsiento;
    }

    public String getEstado() {
        return this.estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getIdViaje() {
        return this.idViaje;
    }

    public void setIdViaje(String idViaje) {
        this.idViaje = idViaje;
    }

    public String getIdReserva() {
        return this.idReserva;
    }

    public void setIdReserva(String idReserva) {
        this.idReserva = idReserva;
    }

    public String getUbicacion() {
        return this.ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getLiberadoEn() {
        return this.liberadoEn;
    }

    public void setLiberadoEn(String liberadoEn) {
        this.liberadoEn = liberadoEn;
    }

    public String toString() {
        String info = "Asiento #" + this.numeroAsiento + " - " + this.estado + " - " + this.ubicacion;
        if (this.idReserva != null && !this.idReserva.isEmpty()) {
            info = info + " (Reserva: " + this.idReserva + ")";
        }

        return info;
    }

    public boolean equals(Object obj) {
        if (obj != null && this.getClass() == obj.getClass()) {
            Asiento other = (Asiento)obj;
            return this.idAsiento != null && this.idAsiento.equals(other.idAsiento);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return this.idAsiento != null ? this.idAsiento.hashCode() : 0;
    }
}
