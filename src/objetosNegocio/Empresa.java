//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package objetosNegocio;

public class Empresa {
    private String idEmpresa;
    private String nombre;
    private String ruc;
    private String direccion;
    private String telefono;
    private String email;

    public Empresa() {
    }

    public Empresa(String idEmpresa, String nombre, String ruc, String direccion, String telefono, String email) {
        this.idEmpresa = idEmpresa;
        this.nombre = nombre;
        this.ruc = ruc;
        this.direccion = direccion;
        this.telefono = telefono;
        this.email = email;
    }

    public Empresa(String idEmpresa) {
        this(idEmpresa, (String)null, (String)null, (String)null, (String)null, (String)null);
    }

    public String getIdEmpresa() {
        return this.idEmpresa;
    }

    public void setIdEmpresa(String idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getNombre() {
        return this.nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRuc() {
        return this.ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getDireccion() {
        return this.direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return this.telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (this.getClass() != obj.getClass()) {
            return false;
        } else {
            Empresa other = (Empresa)obj;
            if (this.idEmpresa == null) {
                if (other.idEmpresa != null) {
                    return false;
                }
            } else if (!this.idEmpresa.equals(other.idEmpresa)) {
                return false;
            }

            return true;
        }
    }

    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + (this.idEmpresa != null ? this.idEmpresa.hashCode() : 0);
        return hash;
    }

    public String toString() {
        return this.idEmpresa + " - " + this.nombre + " - " + this.direccion;
    }
}
