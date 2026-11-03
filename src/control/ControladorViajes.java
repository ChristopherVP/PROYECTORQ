//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package control;

import excepciones.FachadaException;
import fachadas.FachadaArchivos;
import interfaces.IFachada;
import java.util.ArrayList;
import java.util.List;
import objetosNegocio.Asiento;
import objetosNegocio.Bus;
import objetosNegocio.Conductor;
import objetosNegocio.Empresa;
import objetosNegocio.Reserva;
import objetosNegocio.Ruta;
import objetosNegocio.Viaje;
import objetosServicio.Fecha;
import objetosServicio.Periodo;

public class ControladorViajes {
    private IFachada fachada = FachadaArchivos.getInstance();

    public ResultadoOperacion programarViaje(String idViaje, String idBus, String idRuta, Fecha fechaSalida, Fecha fechaLlegada, String idEmpresa, String estado, boolean generarAsientos, String horaSalida, String horaLlegada, double precio, String idConductor1, String idConductor2) {
        try {
            ResultadoOperacion validacion = this.validarDatosViaje(idBus, idRuta, fechaSalida, fechaLlegada, estado, idEmpresa);
            if (!validacion.isExito()) {
                return validacion;
            } else {
                if (idViaje != null && !idViaje.trim().isEmpty()) {
                    Viaje viajeExistente = this.fachada.obten(new Viaje(idViaje));
                    if (viajeExistente != null) {
                        return new ResultadoOperacion(false, "Ya existe un viaje con ese ID");
                    }
                }

                Bus bus = this.fachada.obten(new Bus(idBus));
                if (bus == null) {
                    return new ResultadoOperacion(false, "El bus especificado no existe");
                } else if (!"Mantenimiento".equalsIgnoreCase(bus.getEstado()) && !"INACTIVO".equalsIgnoreCase(bus.getEstado())) {
                    Ruta ruta = this.fachada.obten(new Ruta(idRuta));
                    if (ruta == null) {
                        return new ResultadoOperacion(false, "La ruta especificada no existe");
                    } else if (precio <= (double)0.0F && ruta.getPrecio() <= (double)0.0F) {
                        return new ResultadoOperacion(false, "Error de Precios: Debe definir un precio para el viaje o la ruta debe tener un precio base mayor a 0.");
                    } else {
                        Empresa empresa = this.fachada.obten(new Empresa(idEmpresa));
                        if (empresa == null) {
                            return new ResultadoOperacion(false, "La empresa especificada no existe");
                        } else if (bus.getEmpresa() != null && ruta.getEmpresa() != null && !bus.getEmpresa().getIdEmpresa().equals(ruta.getEmpresa().getIdEmpresa())) {
                            return new ResultadoOperacion(false, "El bus y la ruta deben pertenecer a la misma empresa.");
                        } else if (idConductor1 != null && !idConductor1.trim().isEmpty()) {
                            ResultadoOperacion disponibilidad = this.verificarDisponibilidadBus(idBus, fechaSalida, fechaLlegada);
                            if (!disponibilidad.isExito()) {
                                return disponibilidad;
                            } else {
                                Viaje viaje = new Viaje(idViaje, bus, ruta, fechaSalida, fechaLlegada, bus.getCapacidad(), estado, empresa, horaSalida, horaLlegada, precio);
                                viaje.setConductor1(new Conductor(idConductor1));
                                if (idConductor2 != null && !idConductor2.isEmpty()) {
                                    viaje.setConductor2(new Conductor(idConductor2));
                                }

                                this.fachada.agrega(viaje);
                                if (generarAsientos) {
                                    try {
                                        ((FachadaArchivos)this.fachada).generarAsientosParaViaje(idViaje, bus.getCapacidad());
                                    } catch (Exception var22) {
                                        System.err.println("Advertencia: No se pudieron generar los asientos automáticamente");
                                    }
                                }

                                return new ResultadoOperacion(true, "Viaje programado exitosamente", viaje);
                            }
                        } else {
                            return new ResultadoOperacion(false, "Debe asignar un conductor principal.");
                        }
                    }
                } else {
                    return new ResultadoOperacion(false, "El bus está " + bus.getEstado() + " y no puede ser asignado.");
                }
            }
        } catch (FachadaException e) {
            return new ResultadoOperacion(false, "Error al programar viaje: " + e.getMessage());
        }
    }

    public ResultadoOperacion actualizarViaje(String idViaje, String idBus, String idRuta, Fecha fechaSalida, Fecha fechaLlegada, String idEmpresa, int asientosDisponibles, String estado, double precio, String horaSalida, String horaLlegada, String idConductor1, String idConductor2) {
        try {
            if (idViaje != null && !idViaje.trim().isEmpty()) {
                Viaje viajeExistente = this.fachada.obten(new Viaje(idViaje));
                if (viajeExistente == null) {
                    return new ResultadoOperacion(false, "El viaje no existe");
                } else {
                    ResultadoOperacion validacion = this.validarDatosViaje(idBus, idRuta, fechaSalida, fechaLlegada, estado, idEmpresa);
                    if (!validacion.isExito()) {
                        return validacion;
                    } else {
                        Bus bus = this.fachada.obten(new Bus(idBus));
                        Ruta ruta = this.fachada.obten(new Ruta(idRuta));
                        Empresa empresa = this.fachada.obten(new Empresa(idEmpresa));
                        if (bus != null && ruta != null && empresa != null) {
                            if (precio <= (double)0.0F && ruta.getPrecio() <= (double)0.0F) {
                                return new ResultadoOperacion(false, "Error de Precios: Debe definir un precio para el viaje o la ruta debe tener un precio base mayor a 0.");
                            } else if (!"Mantenimiento".equalsIgnoreCase(bus.getEstado()) && !"INACTIVO".equalsIgnoreCase(bus.getEstado())) {
                                if (bus.getEmpresa() != null && ruta.getEmpresa() != null && !bus.getEmpresa().getIdEmpresa().equals(ruta.getEmpresa().getIdEmpresa())) {
                                    return new ResultadoOperacion(false, "El bus y la ruta deben pertenecer a la misma empresa.");
                                } else {
                                    viajeExistente.setBus(bus);
                                    viajeExistente.setRuta(ruta);
                                    viajeExistente.setEmpresa(empresa);
                                    viajeExistente.setFechaSalida(fechaSalida);
                                    viajeExistente.setFechaLlegada(fechaLlegada);
                                    viajeExistente.setAsientosDisponibles(asientosDisponibles);
                                    viajeExistente.setEstado(estado);
                                    viajeExistente.setPrecio(precio);
                                    viajeExistente.setHoraSalida(horaSalida);
                                    viajeExistente.setHoraLlegada(horaLlegada);
                                    viajeExistente.setConductor1(new Conductor(idConductor1));
                                    viajeExistente.setConductor2(idConductor2 != null && !idConductor2.isEmpty() ? new Conductor(idConductor2) : null);
                                    this.fachada.actualiza(viajeExistente);
                                    return new ResultadoOperacion(true, "Viaje actualizado exitosamente", viajeExistente);
                                }
                            } else {
                                return new ResultadoOperacion(false, "El bus está " + bus.getEstado() + " y no puede ser asignado.");
                            }
                        } else {
                            return new ResultadoOperacion(false, "Bus, ruta o empresa no válidos");
                        }
                    }
                }
            } else {
                return new ResultadoOperacion(false, "El ID del viaje es obligatorio");
            }
        } catch (FachadaException e) {
            return new ResultadoOperacion(false, "Error al actualizar viaje: " + e.getMessage());
        }
    }

    public ResultadoOperacion cancelarViaje(String idViaje, String motivo) {
        try {
            Viaje viaje = this.fachada.obten(new Viaje(idViaje));
            if (viaje == null) {
                return new ResultadoOperacion(false, "El viaje no existe");
            } else if ("Completado".equals(viaje.getEstado())) {
                return new ResultadoOperacion(false, "No se puede cancelar un viaje completado");
            } else if ("Cancelado".equals(viaje.getEstado())) {
                return new ResultadoOperacion(false, "El viaje ya está cancelado");
            } else {
                List<Reserva> reservas = this.fachada.consultaReservasPorViaje(idViaje);
                int reservasCanceladas = 0;

                for(Reserva reserva : reservas) {
                    if ("Confirmada".equals(reserva.getEstado())) {
                        reserva.setEstado("Cancelada");
                        this.fachada.actualiza(reserva);
                        ++reservasCanceladas;
                    }
                }

                viaje.setEstado("Cancelado");
                this.fachada.actualiza(viaje);
                String mensaje = String.format("Viaje cancelado. Se cancelaron %d reserva(s)", reservasCanceladas);
                return new ResultadoOperacion(true, mensaje, viaje);
            }
        } catch (FachadaException e) {
            return new ResultadoOperacion(false, "Error al cancelar viaje: " + e.getMessage());
        }
    }

    public ResultadoOperacion eliminarViaje(String idViaje) {
        try {
            Viaje viaje = this.fachada.obten(new Viaje(idViaje));
            if (viaje == null) {
                return new ResultadoOperacion(false, "El viaje no existe");
            } else {
                for(Reserva reserva : this.fachada.consultaReservasPorViaje(idViaje)) {
                    if ("Confirmada".equals(reserva.getEstado())) {
                        return new ResultadoOperacion(false, "No se puede eliminar el viaje porque tiene reservas activas");
                    }
                }

                this.fachada.elimina(viaje);
                return new ResultadoOperacion(true, "Viaje eliminado exitosamente");
            }
        } catch (FachadaException e) {
            return new ResultadoOperacion(false, "Error al eliminar viaje: " + e.getMessage());
        }
    }

    public ResultadoOperacion liberarAsientosEnRuta(String idViaje, String parada, List<Integer> numerosAsientos) {
        try {
            Viaje viaje = this.fachada.obten(new Viaje(idViaje));
            if (viaje == null) {
                return new ResultadoOperacion(false, "Viaje no encontrado");
            } else if (!"En Curso".equalsIgnoreCase(viaje.getEstado())) {
                return new ResultadoOperacion(false, "Solo se pueden liberar asientos en ruta para viajes 'En Curso'.");
            } else {
                List<Asiento> todosAsientos = this.fachada.consultaAsientosPorViaje(idViaje);
                int liberadosCount = 0;

                for(Integer num : numerosAsientos) {
                    for(Asiento asiento : todosAsientos) {
                        if (asiento.getNumeroAsiento() == num) {
                            if ("Ocupado".equals(asiento.getEstado()) || "Reservado".equals(asiento.getEstado())) {
                                asiento.setEstado("Disponible");
                                asiento.setIdReserva((String)null);
                                asiento.setLiberadoEn(parada);
                                this.fachada.actualiza(asiento);
                                ++liberadosCount;
                            }
                            break;
                        }
                    }
                }

                if (liberadosCount > 0) {
                    viaje.setAsientosDisponibles(viaje.getAsientosDisponibles() + liberadosCount);
                    this.fachada.actualiza(viaje);
                    return new ResultadoOperacion(true, "Se liberaron " + liberadosCount + " asientos en " + parada + ".");
                } else {
                    return new ResultadoOperacion(false, "No se pudieron liberar los asientos seleccionados (verifique que estén ocupados).");
                }
            }
        } catch (FachadaException e) {
            return new ResultadoOperacion(false, "Error al liberar asientos: " + e.getMessage());
        }
    }

    public ResultadoOperacion buscarViajesDisponibles(String origen, String destino, Fecha fecha) {
        try {
            List<Viaje> todosViajes = this.fachada.consultaViajes();
            List<Viaje> viajesDisponibles = new ArrayList();

            for(Viaje viaje : todosViajes) {
                if ("Programado".equals(viaje.getEstado()) && viaje.getAsientosDisponibles() > 0) {
                    if (viaje.getRuta() != null) {
                        boolean origenCoincide = origen == null || origen.isEmpty() || viaje.getRuta().getOrigen().equalsIgnoreCase(origen);
                        boolean destinoCoincide = destino == null || destino.isEmpty() || viaje.getRuta().getDestino().equalsIgnoreCase(destino);
                        if (!origenCoincide || !destinoCoincide) {
                            continue;
                        }
                    }

                    if (fecha == null || viaje.getFechaSalida() == null || this.compararFechas(viaje.getFechaSalida(), fecha)) {
                        viajesDisponibles.add(viaje);
                    }
                }
            }

            String mensaje = viajesDisponibles.isEmpty() ? "No se encontraron viajes disponibles con los criterios especificados" : String.format("Se encontraron %d viaje(s) disponible(s)", viajesDisponibles.size());
            return new ResultadoOperacion(true, mensaje, viajesDisponibles);
        } catch (FachadaException e) {
            return new ResultadoOperacion(false, "Error al buscar viajes: " + e.getMessage());
        }
    }

    public ResultadoOperacion consultarViajes() {
        try {
            List<Viaje> viajes = this.fachada.consultaViajes();
            return new ResultadoOperacion(true, "Consulta exitosa", viajes);
        } catch (FachadaException e) {
            return new ResultadoOperacion(false, "Error al consultar viajes: " + e.getMessage());
        }
    }

    public ResultadoOperacion consultarViajesPorRuta(String idRuta) {
        try {
            List<Viaje> viajes = this.fachada.consultaViajesPorRuta(idRuta);
            return new ResultadoOperacion(true, "Consulta exitosa", viajes);
        } catch (FachadaException e) {
            return new ResultadoOperacion(false, "Error al consultar viajes: " + e.getMessage());
        }
    }

    public ResultadoOperacion consultarViajesPorPeriodo(Fecha fechaInicio, Fecha fechaFin) {
        try {
            Periodo periodo = new Periodo(fechaInicio, fechaFin);
            List<Viaje> viajes = this.fachada.consultaViajesPorPeriodo(periodo);
            return new ResultadoOperacion(true, "Consulta exitosa", viajes);
        } catch (FachadaException e) {
            return new ResultadoOperacion(false, "Error al consultar viajes: " + e.getMessage());
        }
    }

    public ResultadoOperacion obtenerDetallesViaje(String idViaje) {
        try {
            Viaje viaje = this.fachada.obten(new Viaje(idViaje));
            if (viaje == null) {
                return new ResultadoOperacion(false, "Viaje no encontrado");
            } else {
                List<Reserva> reservas = this.fachada.consultaReservasPorViaje(idViaje);
                List<Asiento> asientos = this.fachada.consultaAsientosPorViaje(idViaje);
                DetalleViaje detalle = new DetalleViaje(viaje, reservas, asientos);
                return new ResultadoOperacion(true, "Detalles obtenidos exitosamente", detalle);
            }
        } catch (FachadaException e) {
            return new ResultadoOperacion(false, "Error al obtener detalles: " + e.getMessage());
        }
    }

    private ResultadoOperacion validarDatosViaje(String idBus, String idRuta, Fecha fechaSalida, Fecha fechaLlegada, String estado, String idEmpresa) {
        if (idBus != null && !idBus.trim().isEmpty()) {
            if (idRuta != null && !idRuta.trim().isEmpty()) {
                if (idEmpresa != null && !idEmpresa.trim().isEmpty()) {
                    if (fechaSalida == null) {
                        return new ResultadoOperacion(false, "La fecha de salida es obligatoria");
                    } else if (fechaLlegada == null) {
                        return new ResultadoOperacion(false, "La fecha de llegada es obligatoria");
                    } else if (!this.validarOrdenFechas(fechaSalida, fechaLlegada)) {
                        return new ResultadoOperacion(false, "La fecha de llegada debe ser posterior o igual a la fecha de salida");
                    } else if (estado != null && !estado.trim().isEmpty()) {
                        return !estado.equalsIgnoreCase("Programado") && !estado.equalsIgnoreCase("En Curso") && !estado.equalsIgnoreCase("Finalizado") && !estado.equalsIgnoreCase("Cancelado") && !estado.equalsIgnoreCase("Completado") ? new ResultadoOperacion(false, "Estado inválido. Debe ser: Programado, En Curso, Finalizado o Cancelado") : new ResultadoOperacion(true, "Validación exitosa");
                    } else {
                        return new ResultadoOperacion(false, "El estado es obligatorio");
                    }
                } else {
                    return new ResultadoOperacion(false, "Debe seleccionar una empresa");
                }
            } else {
                return new ResultadoOperacion(false, "Debe seleccionar una ruta");
            }
        } else {
            return new ResultadoOperacion(false, "Debe seleccionar un bus");
        }
    }

    private ResultadoOperacion verificarDisponibilidadBus(String idBus, Fecha fechaSalida, Fecha fechaLlegada) {
        try {
            for(Viaje viaje : this.fachada.consultaViajes()) {
                if (viaje.getBus() != null && viaje.getBus().getIdBus().equals(idBus) && !"Cancelado".equals(viaje.getEstado()) && !"Completado".equals(viaje.getEstado()) && this.hayConflictoFechas(fechaSalida, fechaLlegada, viaje.getFechaSalida(), viaje.getFechaLlegada())) {
                    return new ResultadoOperacion(false, "El bus no está disponible en ese período. Conflicto con viaje: " + viaje.getIdViaje());
                }
            }

            return new ResultadoOperacion(true, "Bus disponible");
        } catch (FachadaException e) {
            return new ResultadoOperacion(false, "Error al verificar disponibilidad: " + e.getMessage());
        }
    }

    private boolean hayConflictoFechas(Fecha inicio1, Fecha fin1, Fecha inicio2, Fecha fin2) {
        if (inicio1 != null && fin1 != null && inicio2 != null && fin2 != null) {
            long i1 = this.fechaAValor(inicio1);
            long f1 = this.fechaAValor(fin1);
            long i2 = this.fechaAValor(inicio2);
            long f2 = this.fechaAValor(fin2);
            return f1 >= i2 && f2 >= i1;
        } else {
            return false;
        }
    }

    private boolean validarOrdenFechas(Fecha fecha1, Fecha fecha2) {
        long v1 = this.fechaAValor(fecha1);
        long v2 = this.fechaAValor(fecha2);
        return v2 >= v1;
    }

    private boolean compararFechas(Fecha fecha1, Fecha fecha2) {
        return fecha1.getDia() == fecha2.getDia() && fecha1.getMes() == fecha2.getMes() && fecha1.getAnio() == fecha2.getAnio();
    }

    private long fechaAValor(Fecha fecha) {
        return (long)fecha.getAnio() * 10000L + (long)fecha.getMes() * 100L + (long)fecha.getDia();
    }

    public static class DetalleViaje {
        private Viaje viaje;
        private List<Reserva> reservas;
        private List<Asiento> asientos;

        public DetalleViaje(Viaje viaje, List<Reserva> reservas, List<Asiento> asientos) {
            this.viaje = viaje;
            this.reservas = reservas;
            this.asientos = asientos;
        }

        public Viaje getViaje() {
            return this.viaje;
        }

        public List<Reserva> getReservas() {
            return this.reservas;
        }

        public List<Asiento> getAsientos() {
            return this.asientos;
        }

        public int getTotalReservas() {
            return this.reservas != null ? this.reservas.size() : 0;
        }

        public int getAsientosDisponibles() {
            if (this.asientos == null) {
                return 0;
            } else {
                int disponibles = 0;

                for(Asiento a : this.asientos) {
                    if ("Disponible".equals(a.getEstado())) {
                        ++disponibles;
                    }
                }

                return disponibles;
            }
        }

        public int getAsientosReservados() {
            if (this.asientos == null) {
                return 0;
            } else {
                int reservados = 0;

                for(Asiento a : this.asientos) {
                    if ("Reservado".equals(a.getEstado()) || "Ocupado".equals(a.getEstado())) {
                        ++reservados;
                    }
                }

                return reservados;
            }
        }
    }
}
