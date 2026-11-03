//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package objetosNegocio;

public class Conductor {
    private String idConductor;
    private String nombre;
    private String cedula;
    private String telefono;
    private String licencia;
    private String estado;
    private Empresa empresa;

    public Conductor() {
    }

    public Conductor(String idConductor) {
        this.idConductor = idConductor;
    }

    public Conductor(String idConductor, String nombre, String cedula, String telefono, String licencia, String estado, Empresa empresa) {
        this.idConductor = idConductor;
        this.nombre = nombre;
        this.cedula = cedula;
        this.telefono = telefono;
        this.licencia = licencia;
        this.estado = estado;
        this.empresa = empresa;
    }

    public String getIdConductor() {
        return this.idConductor;
    }

    public void setIdConductor(String idConductor) {
        this.idConductor = idConductor;
    }

    public String getNombre() {
        return this.nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCedula() {
        return this.cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getTelefono() {
        return this.telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getLicencia() {
        return this.licencia;
    }

    public void setLicencia(String licencia) {
        this.licencia = licencia;
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

    public String toString() {
        return this.nombre + " (Lic: " + this.licencia + ")";
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj != null && this.getClass() == obj.getClass()) {
            Conductor conductor = (Conductor)obj;
            return this.idConductor != null ? this.idConductor.equals(conductor.idConductor) : conductor.idConductor == null;
        } else {
            return false;
        }
    }
}
