//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package control;

import excepciones.PersistenciaException;
import java.util.List;
import objetosNegocio.Conductor;
import objetosNegocio.Empresa;
import persistencia.Conductores;
import persistencia.Empresas;

public class ControladorConductores {
    private Conductores persistencia = new Conductores("conductores.dat");
    private Empresas persistenciaEmpresas = new Empresas("empresas.dat");

    public ResultadoOperacion agregarConductor(String nombre, String cedula, String telefono, String licencia, String idEmpresa) {
        try {
            for(Conductor c : this.persistencia.lista()) {
                if (c.getNombre().equalsIgnoreCase(nombre)) {
                    return new ResultadoOperacion(false, "El nombre del conductor ya está registrado.");
                }
            }

            Empresa empresa = this.persistenciaEmpresas.obten(new Empresa(idEmpresa));
            if (empresa == null) {
                return new ResultadoOperacion(false, "La empresa seleccionada no existe.");
            } else {
                Conductor conductor = new Conductor();
                conductor.setNombre(nombre);
                conductor.setCedula(cedula);
                conductor.setTelefono(telefono);
                conductor.setLicencia(licencia);
                conductor.setEmpresa(empresa);
                conductor.setEstado("ACTIVO");
                this.persistencia.agrega(conductor);
                return new ResultadoOperacion(true, "Conductor agregado exitosamente.", conductor);
            }
        } catch (PersistenciaException e) {
            return new ResultadoOperacion(false, "Error al agregar conductor: " + e.getMessage());
        }
    }

    public ResultadoOperacion actualizarConductor(String idConductor, String nombre, String cedula, String telefono, String licencia, String idEmpresa, String estado) {
        try {
            for(Conductor c : this.persistencia.lista()) {
                if (c.getNombre().equalsIgnoreCase(nombre) && !c.getIdConductor().equals(idConductor)) {
                    return new ResultadoOperacion(false, "El nombre del conductor ya está registrado.");
                }
            }

            Conductor conductor = this.persistencia.obten(new Conductor(idConductor));
            if (conductor == null) {
                return new ResultadoOperacion(false, "El conductor no existe.");
            } else {
                Empresa empresa = this.persistenciaEmpresas.obten(new Empresa(idEmpresa));
                if (empresa == null) {
                    return new ResultadoOperacion(false, "La empresa seleccionada no existe.");
                } else {
                    conductor.setNombre(nombre);
                    conductor.setCedula(cedula);
                    conductor.setTelefono(telefono);
                    conductor.setLicencia(licencia);
                    conductor.setEmpresa(empresa);
                    conductor.setEstado(estado);
                    this.persistencia.actualiza(conductor);
                    return new ResultadoOperacion(true, "Conductor actualizado exitosamente.", conductor);
                }
            }
        } catch (PersistenciaException e) {
            return new ResultadoOperacion(false, "Error al actualizar conductor: " + e.getMessage());
        }
    }

    public ResultadoOperacion eliminarConductor(String idConductor) {
        try {
            Conductor conductor = new Conductor(idConductor);
            this.persistencia.elimina(conductor);
            return new ResultadoOperacion(true, "Conductor eliminado exitosamente.");
        } catch (PersistenciaException e) {
            return new ResultadoOperacion(false, "Error al eliminar conductor: " + e.getMessage());
        }
    }

    public ResultadoOperacion listarConductores() {
        try {
            List<Conductor> lista = this.persistencia.lista();
            return new ResultadoOperacion(true, "Listado obtenido.", lista);
        } catch (PersistenciaException e) {
            return new ResultadoOperacion(false, "Error al listar conductores: " + e.getMessage());
        }
    }

    public Conductor buscarConductor(String id) throws PersistenciaException {
        return this.persistencia.obten(new Conductor(id));
    }
}
