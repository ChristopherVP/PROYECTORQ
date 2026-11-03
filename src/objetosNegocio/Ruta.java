//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package objetosNegocio;

public class Ruta {
    private String idRuta;
    private String origen;
    private String destino;
    private double distancia;
    private double precio;
    private Empresa empresa;
    private double tiempoEstimado;
    private String paradas;
    private int tiempoDetencion;
    private String advertencias;
    private String estado;

    public Ruta() {
    }

    public Ruta(String idRuta, String origen, String destino, double distancia, double precio, Empresa empresa, double tiempoEstimado, String paradas, int tiempoDetencion, String advertencias, String estado) {
        this.idRuta = idRuta;
        this.origen = origen;
        this.destino = destino;
        this.distancia = distancia;
        this.precio = precio;
        this.empresa = empresa;
        this.tiempoEstimado = tiempoEstimado;
        this.paradas = paradas;
        this.tiempoDetencion = tiempoDetencion;
        this.advertencias = advertencias;
        this.estado = estado;
    }

    public Ruta(String idRuta) {
        this(idRuta, (String)null, (String)null, (double)0.0F, (double)0.0F, (Empresa)null, (double)0.0F, "", 0, "", "ACTIVA");
    }

    public String getIdRuta() {
        return this.idRuta;
    }

    public void setIdRuta(String idRuta) {
        this.idRuta = idRuta;
    }

    public String getOrigen() {
        return this.origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getDestino() {
        return this.destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public double getDistancia() {
        return this.distancia;
    }

    public void setDistancia(double distancia) {
        this.distancia = distancia;
    }

    public double getPrecio() {
        return this.precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public Empresa getEmpresa() {
        return this.empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public double getTiempoEstimado() {
        return this.tiempoEstimado;
    }

    public void setTiempoEstimado(double tiempoEstimado) {
        this.tiempoEstimado = tiempoEstimado;
    }

    public String getParadas() {
        return this.paradas;
    }

    public void setParadas(String paradas) {
        this.paradas = paradas;
    }

    public int getTiempoDetencion() {
        return this.tiempoDetencion;
    }

    public void setTiempoDetencion(int tiempoDetencion) {
        this.tiempoDetencion = tiempoDetencion;
    }

    public String getAdvertencias() {
        return this.advertencias;
    }

    public void setAdvertencias(String advertencias) {
        this.advertencias = advertencias;
    }

    public String getEstado() {
        return this.estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public double calcularPrecioFinal(int edadPasajero) {
        double factor = (double)1.0F;
        if (edadPasajero < 18 || edadPasajero > 65) {
            factor = (double)0.5F;
        }

        return this.precio * factor;
    }

    public double calcularPrecioParcial(double kilometrosRecorrer) {
        return this.distancia <= (double)0.0F ? (double)0.0F : this.precio / this.distancia * kilometrosRecorrer;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (this.getClass() != obj.getClass()) {
            return false;
        } else {
            Ruta other = (Ruta)obj;
            if (this.idRuta == null) {
                if (other.idRuta != null) {
                    return false;
                }
            } else if (!this.idRuta.equals(other.idRuta)) {
                return false;
            }

            return true;
        }
    }

    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + (this.idRuta != null ? this.idRuta.hashCode() : 0);
        return hash;
    }

    public String toString() {
        return this.idRuta + " - " + this.origen + " a " + this.destino + " - " + this.distancia + "km - $" + this.precio;
    }
}
