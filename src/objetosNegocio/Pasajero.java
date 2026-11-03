//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package objetosNegocio;

import java.util.Calendar;
import java.util.Objects;
import objetosServicio.Fecha;

public class Pasajero {
    private String idPasajero;
    private String nombre;
    private String cedula;
    private String telefono;
    private String email;
    private String direccion;
    private Empresa empresa;
    private Fecha fechaNacimiento;
    private boolean discapacidad;

    public Pasajero() {
    }

    public Pasajero(String idPasajero) {
        this.idPasajero = idPasajero;
    }

    public Pasajero(String idPasajero, String nombre, String cedula, String telefono, String email, String direccion, Empresa empresa, Fecha fechaNacimiento, boolean discapacidad) {
        this.idPasajero = idPasajero;
        this.nombre = nombre;
        this.cedula = cedula;
        this.telefono = telefono;
        this.email = email;
        this.direccion = direccion;
        this.empresa = empresa;
        this.fechaNacimiento = fechaNacimiento;
        this.discapacidad = discapacidad;
    }

    public String getIdPasajero() {
        return this.idPasajero;
    }

    public void setIdPasajero(String idPasajero) {
        this.idPasajero = idPasajero;
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

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDireccion() {
        return this.direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Empresa getEmpresa() {
        return this.empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public Fecha getFechaNacimiento() {
        return this.fechaNacimiento;
    }

    public void setFechaNacimiento(Fecha fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public boolean isDiscapacidad() {
        return this.discapacidad;
    }

    public void setDiscapacidad(boolean discapacidad) {
        this.discapacidad = discapacidad;
    }

    public int calcularEdad() {
        if (this.fechaNacimiento == null) {
            return 0;
        } else {
            Calendar hoy = Calendar.getInstance();
            int edad = hoy.get(1) - this.fechaNacimiento.getAnio();
            if (hoy.get(2) + 1 < this.fechaNacimiento.getMes() || hoy.get(2) + 1 == this.fechaNacimiento.getMes() && hoy.get(5) < this.fechaNacimiento.getDia()) {
                --edad;
            }

            return Math.max(0, edad);
        }
    }

    public String getTipoPasajero() {
        if (this.discapacidad) {
            return "DISCAPACITADO";
        } else {
            int edad = this.calcularEdad();
            if (edad < 12) {
                return "NIÑO";
            } else if (edad < 18) {
                return "ADOLESCENTE";
            } else {
                return edad > 65 ? "TERCERA_EDAD" : "ADULTO";
            }
        }
    }

    public String toString() {
        return this.nombre + " (" + this.cedula + ")";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            Pasajero pasajero = (Pasajero)o;
            return Objects.equals(this.idPasajero, pasajero.idPasajero);
        } else {
            return false;
        }
    }
}
