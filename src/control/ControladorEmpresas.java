package control;

import excepciones.FachadaException;
import excepciones.Validador;
import fachadas.FachadaArchivos;
import interfaces.IFachada;
import java.util.List;
import java.util.regex.Pattern;
import objetosNegocio.Bus;
import objetosNegocio.Empresa;
import objetosNegocio.Viaje;

public class ControladorEmpresas {
    private IFachada fachada = FachadaArchivos.getInstance();

    public ResultadoOperacion registrarEmpresa(String idEmpresa, String nombre, String ruc, String direccion, String telefono, String email) {
        try {
            ResultadoOperacion validacion = this.validarDatosEmpresa(idEmpresa, nombre, ruc, direccion, telefono, email);
            if (!validacion.isExito()) {
                return validacion;
            } else {
                Empresa empresaExistente = this.fachada.obten(new Empresa(idEmpresa));
                if (empresaExistente != null) {
                    return new ResultadoOperacion(false, "Ya existe una empresa con ese ID");
                } else {
                    Empresa empresa = new Empresa(idEmpresa, nombre, ruc, direccion, telefono, email);
                    this.fachada.agrega(empresa);
                    return new ResultadoOperacion(true, "Empresa registrada exitosamente", empresa);
                }
            }
        } catch (FachadaException e) {
            return new ResultadoOperacion(false, "Error al registrar empresa: " + e.getMessage());
        }
    }

    public ResultadoOperacion actualizarEmpresa(String idEmpresa, String nombre, String ruc, String direccion, String telefono, String email) {
        try {
            Empresa empresa = this.fachada.obten(new Empresa(idEmpresa));
            if (empresa == null) {
                return new ResultadoOperacion(false, "Empresa no encontrada");
            } else {
                ResultadoOperacion validacion = this.validarDatosEmpresa(idEmpresa, nombre, ruc, direccion, telefono, email);
                if (!validacion.isExito()) {
                    return validacion;
                } else {
                    empresa.setNombre(nombre);
                    empresa.setRuc(ruc);
                    empresa.setDireccion(direccion);
                    empresa.setTelefono(telefono);
                    empresa.setEmail(email);
                    this.fachada.actualiza(empresa);
                    return new ResultadoOperacion(true, "Empresa actualizada exitosamente", empresa);
                }
            }
        } catch (FachadaException e) {
            return new ResultadoOperacion(false, "Error al actualizar empresa: " + e.getMessage());
        }
    }

    public ResultadoOperacion eliminarEmpresa(String idEmpresa) {
        try {
            Empresa empresa = this.fachada.obten(new Empresa(idEmpresa));
            if (empresa == null) {
                return new ResultadoOperacion(false, "Empresa no encontrada");
            } else {
                List<Bus> buses = this.fachada.consultaBusesPorEmpresa(idEmpresa);
                if (!buses.isEmpty()) {
                    return new ResultadoOperacion(false, String.format("No se puede eliminar la empresa porque tiene %d bus(es) asociado(s)", buses.size()));
                } else {
                    this.fachada.elimina(empresa);
                    return new ResultadoOperacion(true, "Empresa eliminada exitosamente");
                }
            }
        } catch (FachadaException e) {
            return new ResultadoOperacion(false, "Error al eliminar empresa: " + e.getMessage());
        }
    }

    public ResultadoOperacion consultarEmpresas() {
        try {
            List<Empresa> empresas = this.fachada.consultaEmpresas();
            return new ResultadoOperacion(true, "Consulta exitosa", empresas);
        } catch (FachadaException e) {
            return new ResultadoOperacion(false, "Error al consultar empresas: " + e.getMessage());
        }
    }

    public ResultadoOperacion obtenerEmpresa(String idEmpresa) {
        try {
            Empresa empresa = this.fachada.obten(new Empresa(idEmpresa));
            return empresa == null ? new ResultadoOperacion(false, "Empresa no encontrada") : new ResultadoOperacion(true, "Empresa encontrada", empresa);
        } catch (FachadaException e) {
            return new ResultadoOperacion(false, "Error al obtener empresa: " + e.getMessage());
        }
    }

    public ResultadoOperacion obtenerEstadisticasEmpresa(String idEmpresa) {
        try {
            Empresa empresa = this.fachada.obten(new Empresa(idEmpresa));
            if (empresa == null) {
                return new ResultadoOperacion(false, "Empresa no encontrada");
            } else {
                List<Bus> buses = this.fachada.consultaBusesPorEmpresa(idEmpresa);
                int busesActivos = 0;
                int busesInactivos = 0;
                int busesMantenimiento = 0;

                for(Bus bus : buses) {
                    if ("ACTIVO".equalsIgnoreCase(bus.getEstado())) {
                        ++busesActivos;
                    } else if ("INACTIVO".equalsIgnoreCase(bus.getEstado())) {
                        ++busesInactivos;
                    } else if ("MANTENIMIENTO".equalsIgnoreCase(bus.getEstado())) {
                        ++busesMantenimiento;
                    }
                }

                List<Viaje> todosViajes = this.fachada.consultaViajes();
                int viajesProgramados = 0;
                int viajesCompletados = 0;

                for(Viaje viaje : todosViajes) {
                    if (viaje.getBus() != null && viaje.getBus().getEmpresa() != null && viaje.getBus().getEmpresa().getIdEmpresa().equals(idEmpresa)) {
                        if ("Programado".equals(viaje.getEstado())) {
                            ++viajesProgramados;
                        } else if ("Completado".equals(viaje.getEstado())) {
                            ++viajesCompletados;
                        }
                    }
                }

                EstadisticasEmpresa estadisticas = new EstadisticasEmpresa(empresa, buses.size(), busesActivos, busesInactivos, busesMantenimiento, viajesProgramados, viajesCompletados);
                return new ResultadoOperacion(true, "Estadísticas obtenidas", estadisticas);
            }
        } catch (FachadaException e) {
            return new ResultadoOperacion(false, "Error al obtener estadísticas: " + e.getMessage());
        }
    }

    private ResultadoOperacion validarDatosEmpresa(String idEmpresa, String nombre, String ruc, String direccion, String telefono, String email) {
        if (idEmpresa != null && !idEmpresa.trim().isEmpty()) {
            if (nombre != null && !nombre.trim().isEmpty()) {
                if (nombre.length() < 3) {
                    return new ResultadoOperacion(false, "El nombre debe tener al menos 3 caracteres");
                } else if (!Validador.validarRuc(ruc)) {
                    return new ResultadoOperacion(false, Validador.getMensajeErrorRuc());
                } else if (direccion != null && !direccion.trim().isEmpty()) {
                    if (!this.validarTelefono(telefono)) {
                        return new ResultadoOperacion(false, "El teléfono debe tener entre 7 y 15 dígitos");
                    } else {
                        return !this.validarEmail(email) ? new ResultadoOperacion(false, "El email es inválido") : new ResultadoOperacion(true, "Validación exitosa");
                    }
                } else {
                    return new ResultadoOperacion(false, "La dirección es obligatoria");
                }
            } else {
                return new ResultadoOperacion(false, "El nombre es obligatorio");
            }
        } else {
            return new ResultadoOperacion(false, "El ID de la empresa es obligatorio");
        }
    }

    private boolean validarEmail(String email) {
        if (email != null && !email.trim().isEmpty()) {
            String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
            return Pattern.matches(regex, email);
        } else {
            return false;
        }
    }

    private boolean validarTelefono(String telefono) {
        return telefono != null && !telefono.trim().isEmpty() ? telefono.matches("\\d{7,15}") : false;
    }

    public static class EstadisticasEmpresa {
        private Empresa empresa;
        private int totalBuses;
        private int busesActivos;
        private int busesInactivos;
        private int busesMantenimiento;
        private int viajesProgramados;
        private int viajesCompletados;

        public EstadisticasEmpresa(Empresa empresa, int totalBuses, int busesActivos, int busesInactivos, int busesMantenimiento, int viajesProgramados, int viajesCompletados) {
            this.empresa = empresa;
            this.totalBuses = totalBuses;
            this.busesActivos = busesActivos;
            this.busesInactivos = busesInactivos;
            this.busesMantenimiento = busesMantenimiento;
            this.viajesProgramados = viajesProgramados;
            this.viajesCompletados = viajesCompletados;
        }

        public Empresa getEmpresa() {
            return this.empresa;
        }

        public int getTotalBuses() {
            return this.totalBuses;
        }

        public int getBusesActivos() {
            return this.busesActivos;
        }

        public int getBusesInactivos() {
            return this.busesInactivos;
        }

        public int getBusesMantenimiento() {
            return this.busesMantenimiento;
        }

        public int getViajesProgramados() {
            return this.viajesProgramados;
        }

        public int getViajesCompletados() {
            return this.viajesCompletados;
        }

        public double getPorcentajeBusesActivos() {
            return this.totalBuses > 0 ? (double)this.busesActivos * (double)100.0F / (double)this.totalBuses : (double)0.0F;
        }
    }
}
