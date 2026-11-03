//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package objetosNegocio;

public class Bus {
    private String idBus;
    private String placa;
    private String numeroChasis;
    private String numeroMotor;
    private String marca;
    private int anioFabricacion;
    private int capacidad;
    private String estado;
    private Empresa empresa;

    public Bus() {
    }

    public Bus(String idBus, String placa, String numeroChasis, String numeroMotor, String marca, int anioFabricacion, int capacidad, String estado, Empresa empresa) {
        this.idBus = idBus;
        this.placa = placa;
        this.numeroChasis = numeroChasis;
        this.numeroMotor = numeroMotor;
        this.marca = marca;
        this.anioFabricacion = anioFabricacion;
        this.capacidad = capacidad;
        this.estado = estado;
        this.empresa = empresa;
    }

    public Bus(String idBus) {
        this(idBus, (String)null, (String)null, (String)null, (String)null, 0, 0, (String)null, (Empresa)null);
    }

    public String getIdBus() {
        return this.idBus;
    }

    public void setIdBus(String idBus) {
        this.idBus = idBus;
    }

    public String getPlaca() {
        return this.placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getNumeroChasis() {
        return this.numeroChasis;
    }

    public void setNumeroChasis(String numeroChasis) {
        this.numeroChasis = numeroChasis;
    }

    public String getNumeroMotor() {
        return this.numeroMotor;
    }

    public void setNumeroMotor(String numeroMotor) {
        this.numeroMotor = numeroMotor;
    }

    public String getMarca() {
        return this.marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public int getAnioFabricacion() {
        return this.anioFabricacion;
    }

    public void setAnioFabricacion(int anioFabricacion) {
        this.anioFabricacion = anioFabricacion;
    }

    public int getCapacidad() {
        return this.capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
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

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (this.getClass() != obj.getClass()) {
            return false;
        } else {
            Bus other = (Bus)obj;
            if (this.idBus == null) {
                if (other.idBus != null) {
                    return false;
                }
            } else if (!this.idBus.equals(other.idBus)) {
                return false;
            }

            return true;
        }
    }

    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + (this.idBus != null ? this.idBus.hashCode() : 0);
        return hash;
    }

    public String toString() {
        return this.idBus + " - " + this.placa + " - " + this.marca;
    }
}
