//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package control;

import excepciones.FachadaException;
import fachadas.FachadaArchivos;
import interfaces.IFachada;
import java.util.List;
import java.util.regex.Pattern;
import objetosNegocio.Bus;
import objetosNegocio.Empresa;
import objetosNegocio.Ruta;
import objetosNegocio.Usuario;
import objetosNegocio.Viaje;

public class ControladorPrincipal {
    private static ControladorPrincipal instancia;
    private IFachada fachada = FachadaArchivos.getInstance();
    private Usuario usuarioActual = null;

    private ControladorPrincipal() {
    }

    public static synchronized ControladorPrincipal getInstance() {
        if (instancia == null) {
            instancia = new ControladorPrincipal();
        }

        return instancia;
    }

    public ResultadoOperacion iniciarSesion(String idUsuario, String contrasena) {
        try {
            if (idUsuario != null && !idUsuario.trim().isEmpty()) {
                if (contrasena != null && !contrasena.trim().isEmpty()) {
                    Usuario usuario = this.fachada.obten(new Usuario(idUsuario.trim()));
                    if (usuario == null) {
                        return new ResultadoOperacion(false, "Usuario no encontrado");
                    } else if (!usuario.validarContrasena(contrasena)) {
                        return new ResultadoOperacion(false, "Contraseña incorrecta");
                    } else {
                        this.usuarioActual = usuario;
                        return new ResultadoOperacion(true, "Sesión iniciada correctamente", usuario);
                    }
                } else {
                    return new ResultadoOperacion(false, "Debe ingresar una contraseña");
                }
            } else {
                return new ResultadoOperacion(false, "Debe ingresar un ID de usuario");
            }
        } catch (FachadaException e) {
            return new ResultadoOperacion(false, "Error al iniciar sesión: " + e.getMessage());
        }
    }

    public void cerrarSesion() {
        this.usuarioActual = null;
    }

    public Usuario getUsuarioActual() {
        return this.usuarioActual;
    }

    public boolean haySesionActiva() {
        return this.usuarioActual != null;
    }

    public ResultadoOperacion agregarBus(String idBus, String placa, String numeroChasis, String numeroMotor, String marca, int anioFabricacion, int capacidad, String estado, String idEmpresa) {
        try {
            ResultadoOperacion validacion = this.validarDatosBus(idBus, placa, marca, capacidad, estado);
            if (!validacion.isExito()) {
                return validacion;
            } else {
                Bus busExistente = this.fachada.obten(new Bus(idBus));
                if (busExistente != null) {
                    return new ResultadoOperacion(false, "Ya existe un bus con ese ID");
                } else {
                    Bus bus = new Bus(idBus, placa, numeroChasis, numeroMotor, marca, anioFabricacion, capacidad, estado, (Empresa)null);
                    if (idEmpresa != null && !idEmpresa.trim().isEmpty()) {
                        Empresa empresa = this.fachada.obten(new Empresa(idEmpresa));
                        if (empresa == null) {
                            return new ResultadoOperacion(false, "La empresa especificada no existe");
                        }

                        bus.setEmpresa(empresa);
                    }

                    this.fachada.agrega(bus);
                    return new ResultadoOperacion(true, "Bus agregado exitosamente", bus);
                }
            }
        } catch (FachadaException e) {
            return new ResultadoOperacion(false, "Error al agregar bus: " + e.getMessage());
        }
    }

    public ResultadoOperacion actualizarBus(String idBus, String placa, String numeroChasis, String numeroMotor, String marca, int anioFabricacion, int capacidad, String estado, String idEmpresa) {
        try {
            Bus busExistente = this.fachada.obten(new Bus(idBus));
            if (busExistente == null) {
                return new ResultadoOperacion(false, "El bus no existe");
            } else {
                ResultadoOperacion validacion = this.validarDatosBus(idBus, placa, marca, capacidad, estado);
                if (!validacion.isExito()) {
                    return validacion;
                } else {
                    busExistente.setPlaca(placa);
                    busExistente.setNumeroChasis(numeroChasis);
                    busExistente.setNumeroMotor(numeroMotor);
                    busExistente.setMarca(marca);
                    busExistente.setAnioFabricacion(anioFabricacion);
                    busExistente.setCapacidad(capacidad);
                    busExistente.setEstado(estado);
                    if (idEmpresa != null && !idEmpresa.trim().isEmpty()) {
                        Empresa empresa = this.fachada.obten(new Empresa(idEmpresa));
                        if (empresa == null) {
                            return new ResultadoOperacion(false, "La empresa especificada no existe");
                        }

                        busExistente.setEmpresa(empresa);
                    }

                    this.fachada.actualiza(busExistente);
                    return new ResultadoOperacion(true, "Bus actualizado exitosamente", busExistente);
                }
            }
        } catch (FachadaException e) {
            return new ResultadoOperacion(false, "Error al actualizar bus: " + e.getMessage());
        }
    }

    public ResultadoOperacion eliminarBus(String idBus) {
        try {
            Bus bus = this.fachada.obten(new Bus(idBus));
            if (bus == null) {
                return new ResultadoOperacion(false, "El bus no existe");
            } else {
                for(Viaje viaje : this.fachada.consultaViajes()) {
                    if (viaje.getBus() != null && viaje.getBus().getIdBus().equals(idBus) && !"Completado".equals(viaje.getEstado()) && !"Cancelado".equals(viaje.getEstado())) {
                        return new ResultadoOperacion(false, "No se puede eliminar el bus porque tiene viajes activos");
                    }
                }

                this.fachada.elimina(bus);
                return new ResultadoOperacion(true, "Bus eliminado exitosamente");
            }
        } catch (FachadaException e) {
            return new ResultadoOperacion(false, "Error al eliminar bus: " + e.getMessage());
        }
    }

    public ResultadoOperacion consultarBuses() {
        try {
            List<Bus> buses = this.fachada.consultaBuses();
            return new ResultadoOperacion(true, "Consulta exitosa", buses);
        } catch (FachadaException e) {
            return new ResultadoOperacion(false, "Error al consultar buses: " + e.getMessage());
        }
    }

    public ResultadoOperacion consultarBusesPorEstado(String estado) {
        try {
            List<Bus> buses = this.fachada.consultaBusesPorEstado(estado);
            return new ResultadoOperacion(true, "Consulta exitosa", buses);
        } catch (FachadaException e) {
            return new ResultadoOperacion(false, "Error al consultar buses: " + e.getMessage());
        }
    }

    public ResultadoOperacion agregarRuta(String idRuta, String origen, String destino, double distancia, double precio, String idEmpresa, double tiempoEstimado, String paradas, int tiempoDetencion, String advertencias, String estado) {
        try {
            ResultadoOperacion validacion = this.validarDatosRuta(idRuta, origen, destino, distancia, precio, idEmpresa);
            if (!validacion.isExito()) {
                return validacion;
            } else {
                Ruta rutaExistente = this.fachada.obten(new Ruta(idRuta));
                if (rutaExistente != null) {
                    return new ResultadoOperacion(false, "Ya existe una ruta con ese ID");
                } else {
                    Empresa empresa = this.fachada.obten(new Empresa(idEmpresa));
                    if (empresa == null) {
                        return new ResultadoOperacion(false, "La empresa especificada no existe");
                    } else {
                        Ruta ruta = new Ruta(idRuta, origen, destino, distancia, precio, empresa, tiempoEstimado, paradas, tiempoDetencion, advertencias, estado);
                        this.fachada.agrega(ruta);
                        return new ResultadoOperacion(true, "Ruta agregada exitosamente", ruta);
                    }
                }
            }
        } catch (FachadaException e) {
            return new ResultadoOperacion(false, "Error al agregar ruta: " + e.getMessage());
        }
    }

    public ResultadoOperacion actualizarRuta(String idRuta, String origen, String destino, double distancia, double precio, String idEmpresa, double tiempoEstimado, String paradas, int tiempoDetencion, String advertencias, String estado) {
        try {
            Ruta rutaExistente = this.fachada.obten(new Ruta(idRuta));
            if (rutaExistente == null) {
                return new ResultadoOperacion(false, "La ruta no existe");
            } else {
                ResultadoOperacion validacion = this.validarDatosRuta(idRuta, origen, destino, distancia, precio, idEmpresa);
                if (!validacion.isExito()) {
                    return validacion;
                } else {
                    rutaExistente.setOrigen(origen);
                    rutaExistente.setDestino(destino);
                    rutaExistente.setDistancia(distancia);
                    rutaExistente.setPrecio(precio);
                    rutaExistente.setTiempoEstimado(tiempoEstimado);
                    rutaExistente.setParadas(paradas);
                    rutaExistente.setTiempoDetencion(tiempoDetencion);
                    rutaExistente.setAdvertencias(advertencias);
                    rutaExistente.setEstado(estado);
                    Empresa empresa = this.fachada.obten(new Empresa(idEmpresa));
                    if (empresa == null) {
                        return new ResultadoOperacion(false, "La empresa especificada no existe");
                    } else {
                        rutaExistente.setEmpresa(empresa);
                        this.fachada.actualiza(rutaExistente);
                        return new ResultadoOperacion(true, "Ruta actualizada exitosamente", rutaExistente);
                    }
                }
            }
        } catch (FachadaException e) {
            return new ResultadoOperacion(false, "Error al actualizar ruta: " + e.getMessage());
        }
    }

    public ResultadoOperacion eliminarRuta(String idRuta) {
        try {
            Ruta ruta = this.fachada.obten(new Ruta(idRuta));
            if (ruta == null) {
                return new ResultadoOperacion(false, "La ruta no existe");
            } else {
                for(Viaje viaje : this.fachada.consultaViajesPorRuta(idRuta)) {
                    if (!"Completado".equals(viaje.getEstado()) && !"Cancelado".equals(viaje.getEstado())) {
                        return new ResultadoOperacion(false, "No se puede eliminar la ruta porque tiene viajes activos");
                    }
                }

                this.fachada.elimina(ruta);
                return new ResultadoOperacion(true, "Ruta eliminada exitosamente");
            }
        } catch (FachadaException e) {
            return new ResultadoOperacion(false, "Error al eliminar ruta: " + e.getMessage());
        }
    }

    public ResultadoOperacion consultarRutas() {
        try {
            List<Ruta> rutas = this.fachada.consultaRutas();
            return new ResultadoOperacion(true, "Consulta exitosa", rutas);
        } catch (FachadaException e) {
            return new ResultadoOperacion(false, "Error al consultar rutas: " + e.getMessage());
        }
    }

    private ResultadoOperacion validarDatosBus(String idBus, String placa, String marca, int capacidad, String estado) {
        if (idBus != null && !idBus.trim().isEmpty()) {
            if (placa != null && !placa.trim().isEmpty()) {
                if (marca != null && !marca.trim().isEmpty()) {
                    if (capacidad <= 0) {
                        return new ResultadoOperacion(false, "La capacidad debe ser mayor a 0");
                    } else if (capacidad > 60) {
                        return new ResultadoOperacion(false, "La capacidad no puede ser mayor a 60");
                    } else if (estado != null && !estado.trim().isEmpty()) {
                        return !estado.equalsIgnoreCase("ACTIVO") && !estado.equalsIgnoreCase("INACTIVO") && !estado.equalsIgnoreCase("MANTENIMIENTO") ? new ResultadoOperacion(false, "Estado inválido. Debe ser: ACTIVO, INACTIVO o MANTENIMIENTO") : new ResultadoOperacion(true, "Validación exitosa");
                    } else {
                        return new ResultadoOperacion(false, "El estado es obligatorio");
                    }
                } else {
                    return new ResultadoOperacion(false, "La marca es obligatoria");
                }
            } else {
                return new ResultadoOperacion(false, "La placa es obligatoria");
            }
        } else {
            return new ResultadoOperacion(false, "El ID del bus es obligatorio");
        }
    }

    private ResultadoOperacion validarDatosRuta(String idRuta, String origen, String destino, double distancia, double precio, String idEmpresa) {
        if (idRuta != null && !idRuta.trim().isEmpty()) {
            if (origen != null && !origen.trim().isEmpty()) {
                if (destino != null && !destino.trim().isEmpty()) {
                    if (origen.equalsIgnoreCase(destino)) {
                        return new ResultadoOperacion(false, "El origen y destino no pueden ser iguales");
                    } else if (distancia <= (double)0.0F) {
                        return new ResultadoOperacion(false, "La distancia debe ser mayor a 0");
                    } else if (precio <= (double)0.0F) {
                        return new ResultadoOperacion(false, "El precio debe ser mayor a 0");
                    } else {
                        return idEmpresa != null && !idEmpresa.trim().isEmpty() ? new ResultadoOperacion(true, "Validación exitosa") : new ResultadoOperacion(false, "Debe especificar una empresa");
                    }
                } else {
                    return new ResultadoOperacion(false, "El destino es obligatorio");
                }
            } else {
                return new ResultadoOperacion(false, "El origen es obligatorio");
            }
        } else {
            return new ResultadoOperacion(false, "El ID de la ruta es obligatorio");
        }
    }

    public boolean validarEmail(String email) {
        if (email != null && !email.trim().isEmpty()) {
            String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
            return Pattern.matches(regex, email);
        } else {
            return false;
        }
    }

    public boolean validarTelefono(String telefono) {
        return telefono != null && !telefono.trim().isEmpty() ? telefono.matches("\\d{7,15}") : false;
    }
}
