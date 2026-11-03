//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package control;

import excepciones.FachadaException;
import fachadas.FachadaArchivos;
import interfaces.IFachada;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import objetosNegocio.Asiento;
import objetosNegocio.Bus;
import objetosNegocio.Empresa;
import objetosNegocio.Pasajero;
import objetosNegocio.Reserva;
import objetosNegocio.Ruta;
import objetosNegocio.Viaje;
import objetosServicio.Fecha;
import persistencia.Asientos;

public class ControladorReservas {
    private IFachada fachada = FachadaArchivos.getInstance();

    public ResultadoOperacion crearReserva(String idReserva, String idPasajero, String idViaje, int cantidadAsientos, List<Integer> numerosAsientos, String idEmpresa, Fecha fechaReserva, String metodoPago, int ninosBrazos) {
        try {
            ResultadoOperacion validacion = this.validarDatosReserva(idReserva, idPasajero, idViaje, cantidadAsientos, idEmpresa);
            if (!validacion.isExito()) {
                return validacion;
            } else {
                Reserva reservaExistente = this.fachada.obten(new Reserva(idReserva));
                if (reservaExistente != null) {
                    return new ResultadoOperacion(false, "Ya existe una reserva con ese ID");
                } else {
                    Pasajero pasajero = this.fachada.obten(new Pasajero(idPasajero));
                    if (pasajero == null) {
                        return new ResultadoOperacion(false, "Pasajero no encontrado");
                    } else {
                        Viaje viaje = this.fachada.obten(new Viaje(idViaje));
                        if (viaje == null) {
                            return new ResultadoOperacion(false, "Viaje no encontrado");
                        } else {
                            String estadoViaje = viaje.getEstado() != null ? viaje.getEstado().trim() : "";
                            if (!"Programado".equalsIgnoreCase(estadoViaje) && !"En Curso".equalsIgnoreCase(estadoViaje)) {
                                return new ResultadoOperacion(false, "El viaje no está disponible para reservas (Estado: " + estadoViaje + ")");
                            } else {
                                Empresa empresa = this.fachada.obten(new Empresa(idEmpresa));
                                if (empresa == null) {
                                    return new ResultadoOperacion(false, "La empresa especificada no existe");
                                } else {
                                    for(Reserva r : this.fachada.consultaReservasPorPasajero(idPasajero)) {
                                        if (r.getViaje() != null && r.getViaje().getIdViaje().equals(idViaje) && ("Confirmada".equalsIgnoreCase(r.getEstado()) || "Pendiente".equalsIgnoreCase(r.getEstado()))) {
                                            return new ResultadoOperacion(false, "El pasajero ya tiene una reserva activa para este viaje (" + r.getIdReserva() + ").");
                                        }
                                    }

                                    if (viaje.getAsientosDisponibles() < cantidadAsientos) {
                                        return new ResultadoOperacion(false, String.format("Solo hay %d asiento(s) disponible(s)", viaje.getAsientosDisponibles()));
                                    } else {
                                        Bus bus = this.fachada.obten(viaje.getBus());
                                        Ruta ruta = this.fachada.obten(viaje.getRuta());
                                        double precioBase = (double)0.0F;
                                        if (viaje.getPrecio() > (double)0.0F) {
                                            precioBase = viaje.getPrecio();
                                        } else if (ruta != null) {
                                            precioBase = ruta.getPrecio();
                                        }

                                        if (precioBase <= (double)0.0F) {
                                            return new ResultadoOperacion(false, "Configuración inválida: El viaje no tiene precio asignado (ni específico ni por ruta).");
                                        } else {
                                            double precioTotal = this.calcularPrecio(precioBase, pasajero, cantidadAsientos);
                                            String estadoInicial = "PENDIENTE_PAGO";
                                            if (metodoPago != null && "EFECTIVO".equalsIgnoreCase(metodoPago)) {
                                                estadoInicial = "CONFIRMADA";
                                            }

                                            Reserva reserva = new Reserva(idReserva, pasajero, viaje, cantidadAsientos, fechaReserva, estadoInicial, empresa, precioTotal, metodoPago, ninosBrazos);
                                            this.fachada.agrega(reserva);
                                            if (numerosAsientos != null && !numerosAsientos.isEmpty()) {
                                                ResultadoOperacion resultadoAsientos = this.reservarAsientosEspecificos(idViaje, reserva.getIdReserva(), numerosAsientos);
                                                if (!resultadoAsientos.isExito()) {
                                                    this.fachada.elimina(reserva);
                                                    return resultadoAsientos;
                                                }

                                                for(Integer num : numerosAsientos) {
                                                    reserva.agregarAsiento(String.valueOf(num));
                                                }
                                            }

                                            viaje.setAsientosDisponibles(viaje.getAsientosDisponibles() - cantidadAsientos);
                                            this.fachada.actualiza(viaje);
                                            return new ResultadoOperacion(true, "Reserva creada. Total: $" + String.format("%.2f", precioTotal), reserva);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (FachadaException e) {
            return new ResultadoOperacion(false, "Error al crear reserva: " + e.getMessage());
        }
    }

    private double calcularPrecio(double precioBase, Pasajero pasajero, int cantidad) {
        double factorPasajero = (double)1.0F;
        String tipo = pasajero.getTipoPasajero();
        if ("NIÑO".equals(tipo) || "ADOLESCENTE".equals(tipo) || "TERCERA_EDAD".equals(tipo) || "DISCAPACITADO".equals(tipo)) {
            factorPasajero = (double)0.5F;
        }

        return precioBase * factorPasajero * (double)cantidad;
    }

    public ResultadoOperacion actualizarReserva(Reserva reservaOriginal, String idPasajero, String idViaje, List<Integer> nuevosAsientos, String idEmpresa, Fecha fechaReserva) {
        try {
            List<Integer> asientosAnteriores = new ArrayList();
            if (reservaOriginal.getViaje() != null) {
                List<Asiento> listaAsientos = this.fachada.consultaAsientosPorViaje(reservaOriginal.getViaje().getIdViaje());
                String idResOriginal = reservaOriginal.getIdReserva() != null ? reservaOriginal.getIdReserva().trim() : "";

                for(Asiento a : listaAsientos) {
                    String idResAsiento = a.getIdReserva() != null ? a.getIdReserva().trim() : "";
                    if (!idResOriginal.isEmpty() && idResOriginal.equalsIgnoreCase(idResAsiento)) {
                        asientosAnteriores.add(a.getNumeroAsiento());
                    }
                }
            }

            reservaOriginal.getNumerosAsientos().clear();

            for(Integer i : asientosAnteriores) {
                reservaOriginal.agregarAsiento(String.valueOf(i));
            }

            boolean cambioViaje = !reservaOriginal.getViaje().getIdViaje().equals(idViaje);
            String idViajeAnterior = reservaOriginal.getViaje().getIdViaje();
            Asientos asientosDB = new Asientos("asientos.dat");
            if (cambioViaje) {
                for(Integer num : asientosAnteriores) {
                    this.liberarAsientoDirecto(asientosDB, idViajeAnterior, num);
                }

                reservaOriginal.getNumerosAsientos().clear();
                Viaje viajeAnt = this.fachada.obten(reservaOriginal.getViaje());
                viajeAnt.setAsientosDisponibles(viajeAnt.getAsientosDisponibles() + asientosAnteriores.size());
                this.fachada.actualiza(viajeAnt);
            } else {
                for(Integer num : asientosAnteriores) {
                    if (!nuevosAsientos.contains(num)) {
                        this.liberarAsientoDirecto(asientosDB, idViajeAnterior, num);
                    }
                }
            }

            for(Integer num : nuevosAsientos) {
                if (cambioViaje || !asientosAnteriores.contains(num)) {
                    boolean ok = ((FachadaArchivos)this.fachada).reservarAsiento(idViaje, num, reservaOriginal.getIdReserva());
                    if (!ok) {
                        return new ResultadoOperacion(false, "El asiento " + num + " ya no está disponible.");
                    }
                }
            }

            Pasajero p = this.fachada.obten(new Pasajero(idPasajero));
            Viaje v = this.fachada.obten(new Viaje(idViaje));
            reservaOriginal.setPasajero(p);
            reservaOriginal.setViaje(v);
            reservaOriginal.setEmpresa(this.fachada.obten(new Empresa(idEmpresa)));
            reservaOriginal.setFechaReserva(fechaReserva);
            reservaOriginal.setCantidadAsientos(nuevosAsientos.size());
            Ruta r = this.fachada.obten(v.getRuta());
            double precioBase = v.getPrecio() > (double)0.0F ? v.getPrecio() : (r != null ? r.getPrecio() : (double)0.0F);
            if (precioBase <= (double)0.0F) {
                return new ResultadoOperacion(false, "Configuración inválida: El viaje no tiene precio asignado.");
            } else {
                double nuevoTotal = this.calcularPrecio(precioBase, p, nuevosAsientos.size());
                reservaOriginal.setPrecioTotal(nuevoTotal);
                reservaOriginal.getNumerosAsientos().clear();

                for(Integer i : nuevosAsientos) {
                    reservaOriginal.agregarAsiento(String.valueOf(i));
                }

                this.fachada.actualiza(reservaOriginal);
                Viaje viajeActual = this.fachada.obten(new Viaje(idViaje));
                int delta = nuevosAsientos.size() - (cambioViaje ? 0 : asientosAnteriores.size());
                viajeActual.setAsientosDisponibles(viajeActual.getAsientosDisponibles() - delta);
                this.fachada.actualiza(viajeActual);
                return new ResultadoOperacion(true, "Reserva actualizada exitosamente");
            }
        } catch (Exception e) {
            return new ResultadoOperacion(false, "Error al actualizar: " + e.getMessage());
        }
    }

    private void liberarAsientoDirecto(Asientos asientosDB, String idViaje, int numero) {
        try {
            Asiento a = asientosDB.obtenPorViajeYNumero(idViaje, numero);
            if (a != null) {
                a.setEstado("Disponible");
                a.setIdReserva("");
                asientosDB.actualiza(a);
                System.out.println("Asiento " + numero + " liberado forzosamente.");
            }
        } catch (Exception e) {
            System.err.println("Error liberando asiento directo: " + e.getMessage());
        }

    }

    public ResultadoOperacion modificarReserva(String idReserva, int nuevaCantidadAsientos) {
        try {
            Reserva reserva = this.fachada.obten(new Reserva(idReserva));
            if (reserva == null) {
                return new ResultadoOperacion(false, "Reserva no encontrada");
            } else if (!"Confirmada".equals(reserva.getEstado())) {
                return new ResultadoOperacion(false, "Solo se pueden modificar reservas confirmadas");
            } else {
                Viaje viaje = this.fachada.obten(reserva.getViaje());
                if (viaje == null) {
                    return new ResultadoOperacion(false, "Viaje no encontrado");
                } else {
                    int diferencia = nuevaCantidadAsientos - reserva.getCantidadAsientos();
                    if (diferencia > 0) {
                        if (viaje.getAsientosDisponibles() < diferencia) {
                            return new ResultadoOperacion(false, String.format("No hay suficientes asientos disponibles. Disponibles: %d", viaje.getAsientosDisponibles()));
                        }

                        viaje.setAsientosDisponibles(viaje.getAsientosDisponibles() - diferencia);
                    } else if (diferencia < 0) {
                        viaje.setAsientosDisponibles(viaje.getAsientosDisponibles() + Math.abs(diferencia));
                    }

                    reserva.setCantidadAsientos(nuevaCantidadAsientos);
                    this.fachada.actualiza(reserva);
                    this.fachada.actualiza(viaje);
                    return new ResultadoOperacion(true, "Reserva modificada exitosamente", reserva);
                }
            }
        } catch (FachadaException e) {
            return new ResultadoOperacion(false, "Error al modificar reserva: " + e.getMessage());
        }
    }

    public ResultadoOperacion cancelarReserva(String idReserva, String motivo) {
        try {
            Reserva reserva = this.fachada.obten(new Reserva(idReserva));
            if (reserva == null) {
                return new ResultadoOperacion(false, "Reserva no encontrada");
            } else if ("CANCELADA".equalsIgnoreCase(reserva.getEstado())) {
                return new ResultadoOperacion(false, "La reserva ya está cancelada");
            } else if ("ABORDADO".equalsIgnoreCase(reserva.getEstado())) {
                return new ResultadoOperacion(false, "No se puede cancelar: El pasajero ya abordó el bus");
            } else if ("COMPLETADA".equalsIgnoreCase(reserva.getEstado())) {
                return new ResultadoOperacion(false, "No se puede cancelar: El viaje ya finalizó");
            } else {
                if (reserva.getViaje() != null) {
                    Viaje v = this.fachada.obten(reserva.getViaje());
                    if (v != null && v.getFechaSalida() != null) {
                        Calendar calSalida = Calendar.getInstance();
                        Fecha f = v.getFechaSalida();
                        calSalida.set(f.getAnio(), f.getMes() - 1, f.getDia());

                        try {
                            String[] horaParts = v.getHoraSalida().split(":");
                            calSalida.set(11, Integer.parseInt(horaParts[0]));
                            calSalida.set(12, Integer.parseInt(horaParts[1]));
                        } catch (Exception var9) {
                        }

                        long diff = calSalida.getTimeInMillis() - System.currentTimeMillis();
                        if (diff < 86400000L) {
                            return new ResultadoOperacion(false, "Solo se puede cancelar con al menos 24 horas de anticipación.");
                        }
                    }
                }

                Viaje viaje = this.fachada.obten(reserva.getViaje());
                if (viaje != null) {
                    List<Integer> asientosALiberar = new ArrayList();

                    for(Asiento a : this.fachada.consultaAsientosPorViaje(viaje.getIdViaje())) {
                        if (a.getIdReserva() != null && idReserva.trim().equalsIgnoreCase(a.getIdReserva().trim())) {
                            asientosALiberar.add(a.getNumeroAsiento());
                        }
                    }

                    for(Integer numero : asientosALiberar) {
                        ((FachadaArchivos)this.fachada).liberarAsiento(viaje.getIdViaje(), numero);
                    }

                    viaje.setAsientosDisponibles(viaje.getAsientosDisponibles() + asientosALiberar.size());
                    this.fachada.actualiza(viaje);
                }

                reserva.setEstado("CANCELADA");
                this.fachada.actualiza(reserva);
                return new ResultadoOperacion(true, "Reserva cancelada exitosamente", reserva);
            }
        } catch (FachadaException e) {
            return new ResultadoOperacion(false, "Error al cancelar reserva: " + e.getMessage());
        }
    }

    public ResultadoOperacion eliminarReserva(String idReserva) {
        try {
            Reserva reserva = this.fachada.obten(new Reserva(idReserva));
            if (reserva == null) {
                return new ResultadoOperacion(false, "Reserva no encontrada");
            } else {
                if (reserva.getViaje() != null) {
                    String idViaje = reserva.getViaje().getIdViaje();
                    List<Asiento> todosAsientos = this.fachada.consultaAsientosPorViaje(idViaje);
                    List<Integer> asientosALiberar = new ArrayList();

                    for(Asiento a : todosAsientos) {
                        if (a.getIdReserva() != null && idReserva.trim().equalsIgnoreCase(a.getIdReserva().trim())) {
                            asientosALiberar.add(a.getNumeroAsiento());
                        }
                    }

                    for(Integer num : asientosALiberar) {
                        try {
                            ((FachadaArchivos)this.fachada).liberarAsiento(idViaje, num);
                        } catch (Exception var9) {
                            System.err.println("Error liberando asiento " + num);
                        }
                    }

                    if (!asientosALiberar.isEmpty()) {
                        Viaje viaje = this.fachada.obten(reserva.getViaje());
                        if (viaje != null) {
                            viaje.setAsientosDisponibles(viaje.getAsientosDisponibles() + asientosALiberar.size());
                            this.fachada.actualiza(viaje);
                        }
                    }
                }

                this.fachada.elimina(reserva);
                return new ResultadoOperacion(true, "Reserva eliminada y asientos liberados correctamente");
            }
        } catch (Exception e) {
            return new ResultadoOperacion(false, "Error al eliminar reserva: " + e.getMessage());
        }
    }

    public ResultadoOperacion abordarReserva(String idReserva) {
        try {
            Reserva reserva = this.fachada.obten(new Reserva(idReserva));
            if (reserva == null) {
                return new ResultadoOperacion(false, "Reserva no encontrada");
            } else if (!"CONFIRMADA".equalsIgnoreCase(reserva.getEstado())) {
                return new ResultadoOperacion(false, "Solo se puede abordar con una reserva CONFIRMADA. Estado actual: " + reserva.getEstado());
            } else {
                reserva.setEstado("ABORDADO");
                this.fachada.actualiza(reserva);
                if (reserva.getViaje() != null && !reserva.getNumerosAsientos().isEmpty()) {
                    for(String numAsiento : reserva.getNumerosAsientos()) {
                        try {
                            int numero = Integer.parseInt(numAsiento);

                            for(Asiento asiento : this.fachada.consultaAsientosPorViaje(reserva.getViaje().getIdViaje())) {
                                if (asiento.getNumeroAsiento() == numero && "Reservado".equals(asiento.getEstado())) {
                                    asiento.marcarOcupado();
                                    this.fachada.actualiza(asiento);
                                    break;
                                }
                            }
                        } catch (Exception var9) {
                            System.err.println("Error al marcar asiento como ocupado: " + numAsiento);
                        }
                    }
                }

                return new ResultadoOperacion(true, "Pasajero abordó correctamente. Estado: ABORDADO", reserva);
            }
        } catch (FachadaException e) {
            return new ResultadoOperacion(false, "Error al confirmar uso: " + e.getMessage());
        }
    }

    public ResultadoOperacion pagarReserva(String idReserva) {
        try {
            Reserva reserva = this.fachada.obten(new Reserva(idReserva));
            if (reserva == null) {
                return new ResultadoOperacion(false, "Reserva no encontrada");
            } else if (!"PENDIENTE_PAGO".equalsIgnoreCase(reserva.getEstado())) {
                return new ResultadoOperacion(false, "La reserva no está pendiente de pago.");
            } else {
                reserva.setEstado("CONFIRMADA");
                this.fachada.actualiza(reserva);
                return new ResultadoOperacion(true, "Pago registrado. Reserva CONFIRMADA.");
            }
        } catch (FachadaException e) {
            return new ResultadoOperacion(false, "Error al pagar: " + e.getMessage());
        }
    }

    public ResultadoOperacion completarReserva(String idReserva) {
        try {
            Reserva reserva = this.fachada.obten(new Reserva(idReserva));
            if (reserva == null) {
                return new ResultadoOperacion(false, "Reserva no encontrada");
            } else if (!"ABORDADO".equalsIgnoreCase(reserva.getEstado())) {
                return new ResultadoOperacion(false, "Solo se pueden completar reservas que han sido ABORDADAS.");
            } else {
                reserva.setEstado("COMPLETADA");
                this.fachada.actualiza(reserva);
                return new ResultadoOperacion(true, "Viaje finalizado. Reserva COMPLETADA.");
            }
        } catch (FachadaException e) {
            return new ResultadoOperacion(false, "Error al completar: " + e.getMessage());
        }
    }

    public ResultadoOperacion actualizarEstadoReserva(String idReserva, String nuevoEstado) {
        try {
            Reserva reserva = this.fachada.obten(new Reserva(idReserva));
            if (reserva == null) {
                return new ResultadoOperacion(false, "Reserva no encontrada");
            } else {
                reserva.setEstado(nuevoEstado);
                this.fachada.actualiza(reserva);
                return new ResultadoOperacion(true, "Estado de reserva actualizado a: " + nuevoEstado);
            }
        } catch (FachadaException e) {
            return new ResultadoOperacion(false, "Error al actualizar estado: " + e.getMessage());
        }
    }

    public ResultadoOperacion consultarReservasPorPasajero(String idPasajero) {
        try {
            List<Reserva> reservas = this.fachada.consultaReservasPorPasajero(idPasajero);
            return new ResultadoOperacion(true, "Consulta exitosa", reservas);
        } catch (FachadaException e) {
            return new ResultadoOperacion(false, "Error al consultar reservas: " + e.getMessage());
        }
    }

    public ResultadoOperacion consultarReservasPorViaje(String idViaje) {
        try {
            List<Reserva> reservas = this.fachada.consultaReservasPorViaje(idViaje);
            return new ResultadoOperacion(true, "Consulta exitosa", reservas);
        } catch (FachadaException e) {
            return new ResultadoOperacion(false, "Error al consultar reservas: " + e.getMessage());
        }
    }

    public ResultadoOperacion consultarReservas() {
        try {
            List<Reserva> reservas = this.fachada.consultaReservas();
            return new ResultadoOperacion(true, "Consulta exitosa", reservas);
        } catch (FachadaException e) {
            return new ResultadoOperacion(false, "Error al consultar reservas: " + e.getMessage());
        }
    }

    public ResultadoOperacion obtenerHistorialPasajero(String idPasajero) {
        try {
            List<Reserva> todasReservas = this.fachada.consultaReservasPorPasajero(idPasajero);
            List<Reserva> activas = new ArrayList();
            List<Reserva> completadas = new ArrayList();
            List<Reserva> canceladas = new ArrayList();

            for(Reserva r : todasReservas) {
                switch (r.getEstado()) {
                    case "Confirmada":
                        activas.add(r);
                        break;
                    case "Usada":
                        completadas.add(r);
                        break;
                    case "Cancelada":
                        canceladas.add(r);
                }
            }

            HistorialReservas historial = new HistorialReservas(activas, completadas, canceladas);
            return new ResultadoOperacion(true, "Historial obtenido exitosamente", historial);
        } catch (FachadaException e) {
            return new ResultadoOperacion(false, "Error al obtener historial: " + e.getMessage());
        }
    }

    public ResultadoOperacion obtenerAsientosDisponibles(String idViaje) {
        try {
            List<Asiento> asientos = this.fachada.consultaAsientosDisponiblesPorViaje(idViaje);
            return asientos.isEmpty() ? new ResultadoOperacion(false, "No hay asientos disponibles para este viaje") : new ResultadoOperacion(true, "Asientos disponibles obtenidos", asientos);
        } catch (FachadaException e) {
            return new ResultadoOperacion(false, "Error al obtener asientos: " + e.getMessage());
        }
    }

    private ResultadoOperacion reservarAsientosEspecificos(String idViaje, String idReserva, List<Integer> numerosAsientos) {
        for(Integer numAsiento : numerosAsientos) {
            try {
                boolean reservado = ((FachadaArchivos)this.fachada).reservarAsiento(idViaje, numAsiento, idReserva);
                if (!reservado) {
                    return new ResultadoOperacion(false, "No se pudo reservar el asiento #" + numAsiento + ". Puede estar ocupado o no existir");
                }
            } catch (Exception e) {
                e.printStackTrace();
                return new ResultadoOperacion(false, "Error técnico al reservar asiento #" + numAsiento + ": " + e.getMessage());
            }
        }

        return new ResultadoOperacion(true, "Asientos reservados exitosamente");
    }

    public ResultadoOperacion obtenerMapaAsientos(String idViaje) {
        try {
            List<Asiento> asientos = this.fachada.consultaAsientosPorViaje(idViaje);
            if (asientos.isEmpty()) {
                Viaje viaje = this.fachada.obten(new Viaje(idViaje));
                if (viaje != null && viaje.getBus() != null) {
                    int capacidad = viaje.getBus().getCapacidad();
                    if (capacidad > 0) {
                        try {
                            ((FachadaArchivos)this.fachada).generarAsientosParaViaje(idViaje, capacidad);
                            asientos = this.fachada.consultaAsientosPorViaje(idViaje);
                        } catch (Exception e) {
                            System.err.println("Error auto-generando asientos: " + e.getMessage());
                        }
                    }
                }
            }

            if (asientos.isEmpty()) {
                return new ResultadoOperacion(false, "No se pudieron cargar los asientos para este viaje.");
            } else {
                List<Asiento> asientosUnicos = new ArrayList();
                Set<Integer> procesados = new HashSet();

                for(Asiento a : asientos) {
                    if (!procesados.contains(a.getNumeroAsiento())) {
                        procesados.add(a.getNumeroAsiento());
                        asientosUnicos.add(a);
                    }
                }

                int disponibles = 0;
                int reservados = 0;
                int ocupados = 0;

                for(Asiento a : asientosUnicos) {
                    String estado = a.getEstado() != null ? a.getEstado().trim() : "";
                    if ("Disponible".equalsIgnoreCase(estado)) {
                        ++disponibles;
                    } else if (!"Reservado".equalsIgnoreCase(estado) && !"Confirmada".equalsIgnoreCase(estado)) {
                        if ("Ocupado".equalsIgnoreCase(estado) || "Usada".equalsIgnoreCase(estado)) {
                            ++ocupados;
                        }
                    } else {
                        ++reservados;
                    }
                }

                MapaAsientos mapa = new MapaAsientos(asientosUnicos, disponibles, reservados, ocupados);
                return new ResultadoOperacion(true, "Mapa de asientos obtenido", mapa);
            }
        } catch (FachadaException e) {
            return new ResultadoOperacion(false, "Error al obtener mapa: " + e.getMessage());
        }
    }

    private ResultadoOperacion validarDatosReserva(String idReserva, String idPasajero, String idViaje, int cantidadAsientos, String idEmpresa) {
        if (idReserva != null && !idReserva.trim().isEmpty()) {
            if (idPasajero != null && !idPasajero.trim().isEmpty()) {
                if (idViaje != null && !idViaje.trim().isEmpty()) {
                    if (idEmpresa != null && !idEmpresa.trim().isEmpty()) {
                        if (cantidadAsientos <= 0) {
                            return new ResultadoOperacion(false, "La cantidad de asientos debe ser mayor a 0");
                        } else {
                            return cantidadAsientos > 10 ? new ResultadoOperacion(false, "No se pueden reservar más de 10 asientos a la vez") : new ResultadoOperacion(true, "Validación exitosa");
                        }
                    } else {
                        return new ResultadoOperacion(false, "Debe especificar una empresa");
                    }
                } else {
                    return new ResultadoOperacion(false, "Debe especificar un viaje");
                }
            } else {
                return new ResultadoOperacion(false, "Debe especificar un pasajero");
            }
        } else {
            return new ResultadoOperacion(false, "El ID de la reserva es obligatorio");
        }
    }

    public static class HistorialReservas {
        private List<Reserva> activas;
        private List<Reserva> completadas;
        private List<Reserva> canceladas;

        public HistorialReservas(List<Reserva> activas, List<Reserva> completadas, List<Reserva> canceladas) {
            this.activas = activas;
            this.completadas = completadas;
            this.canceladas = canceladas;
        }

        public List<Reserva> getActivas() {
            return this.activas;
        }

        public List<Reserva> getCompletadas() {
            return this.completadas;
        }

        public List<Reserva> getCanceladas() {
            return this.canceladas;
        }

        public int getTotalReservas() {
            return this.activas.size() + this.completadas.size() + this.canceladas.size();
        }
    }

    public static class MapaAsientos {
        private List<Asiento> asientos;
        private int disponibles;
        private int reservados;
        private int ocupados;

        public MapaAsientos(List<Asiento> asientos, int disponibles, int reservados, int ocupados) {
            this.asientos = asientos;
            this.disponibles = disponibles;
            this.reservados = reservados;
            this.ocupados = ocupados;
        }

        public List<Asiento> getAsientos() {
            return this.asientos;
        }

        public int getDisponibles() {
            return this.disponibles;
        }

        public int getReservados() {
            return this.reservados;
        }

        public int getOcupados() {
            return this.ocupados;
        }

        public int getTotal() {
            return this.asientos.size();
        }

        public double getPorcentajeOcupacion() {
            return this.asientos.isEmpty() ? (double)0.0F : (double)(this.reservados + this.ocupados) * (double)100.0F / (double)this.asientos.size();
        }
    }
}
