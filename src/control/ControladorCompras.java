package control;

import excepciones.FachadaException;
import fachadas.FachadaArchivos;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import objetosNegocio.Empresa;
import objetosNegocio.Pasajero;
import objetosNegocio.Reserva;
import objetosNegocio.Ruta;
import objetosNegocio.Viaje;
import objetosServicio.Fecha;

/**
 * Compra de pasajes por parte del pasajero: cotiza, verifica el saldo de la billetera, crea la reserva,
 * debita y confirma. Si algún paso falla se deshace lo anterior para no dejar cobros ni asientos a medias.
 */
public class ControladorCompras {
    public static final int MAX_ASIENTOS = 5;
    private static final int MINUTOS_ANTES_DE_SALIDA = 30;

    /** Viaje disponible para comprar, con los datos que necesita la pantalla. */
    public static class Oferta {
        private final Viaje viaje;
        private final LocalDateTime salida;
        private final double precioUnitario;

        Oferta(Viaje viaje, LocalDateTime salida, double precioUnitario) {
            this.viaje = viaje;
            this.salida = salida;
            this.precioUnitario = precioUnitario;
        }

        public Viaje getViaje() { return viaje; }
        public LocalDateTime getSalida() { return salida; }
        public double getPrecioUnitario() { return precioUnitario; }
        public String getOrigen() { return viaje.getRuta() != null ? viaje.getRuta().getOrigen() : "?"; }
        public String getDestino() { return viaje.getRuta() != null ? viaje.getRuta().getDestino() : "?"; }
        public String getEmpresa() {
            Empresa e = viaje.getEmpresa();
            return e == null ? "" : (e.getNombre() != null ? e.getNombre() : e.getIdEmpresa());
        }
    }

    private final FachadaArchivos fachada = FachadaArchivos.getInstance();
    private final ControladorReservas reservas = new ControladorReservas();
    private final ControladorBilletera billetera = new ControladorBilletera();

    public static LocalDateTime salidaDe(Viaje v) {
        Fecha f = v.getFechaSalida();
        if (f == null) {
            return null;
        }
        try {
            LocalTime hora = LocalTime.MIDNIGHT;
            if (v.getHoraSalida() != null && v.getHoraSalida().matches("\\d{1,2}:\\d{2}")) {
                hora = LocalTime.parse(v.getHoraSalida().length() == 4 ? "0" + v.getHoraSalida() : v.getHoraSalida());
            }
            return LocalDateTime.of(LocalDate.of(f.getAnio(), f.getMes(), f.getDia()), hora);
        } catch (DateTimeException e) {
            return null;
        }
    }

    public static boolean esComprable(Viaje v) {
        LocalDateTime salida = salidaDe(v);
        return v != null && "Programado".equalsIgnoreCase(v.getEstado() == null ? "" : v.getEstado().trim())
                && v.getAsientosDisponibles() > 0 && salida != null
                && salida.isAfter(LocalDateTime.now().plusMinutes(MINUTOS_ANTES_DE_SALIDA));
    }

    private static double precioBase(Viaje v, Ruta r) {
        if (v.getPrecio() > 0) {
            return v.getPrecio();
        }
        return r != null ? r.getPrecio() : 0;
    }

    public ResultadoOperacion listarOfertas() {
        try {
            List<Oferta> ofertas = new ArrayList<>();
            for (Viaje v : fachada.consultaViajes()) {
                if (esComprable(v)) {
                    double precio = precioBase(v, v.getRuta());
                    if (precio > 0) {
                        ofertas.add(new Oferta(v, salidaDe(v), precio));
                    }
                }
            }
            ofertas.sort(Comparator.comparing(Oferta::getSalida));
            return new ResultadoOperacion(true, "OK", ofertas);
        } catch (FachadaException e) {
            return new ResultadoOperacion(false, "No se pudieron consultar los viajes: " + e.getMessage());
        }
    }

    /** Devuelve en datos el total (Double) que costaría comprar esa cantidad de asientos. */
    public ResultadoOperacion cotizar(String idPasajero, String idViaje, int cantidad) {
        try {
            if (cantidad < 1 || cantidad > MAX_ASIENTOS) {
                return new ResultadoOperacion(false, "Puedes comprar entre 1 y " + MAX_ASIENTOS + " asientos por compra");
            }
            Pasajero pasajero = fachada.obten(new Pasajero(idPasajero));
            Viaje viaje = fachada.obten(new Viaje(idViaje));
            if (pasajero == null || viaje == null) {
                return new ResultadoOperacion(false, "Pasajero o viaje no encontrado");
            }
            double base = precioBase(viaje, fachada.obten(viaje.getRuta()));
            if (base <= 0) {
                return new ResultadoOperacion(false, "El viaje no tiene precio configurado");
            }
            return new ResultadoOperacion(true, "OK", Math.round(reservas.calcularPrecio(base, pasajero, cantidad) * 100) / 100.0);
        } catch (FachadaException e) {
            return new ResultadoOperacion(false, e.getMessage());
        }
    }

    public ResultadoOperacion comprar(String idPasajero, String idViaje, List<Integer> asientos) {
        try {
            if (asientos == null || asientos.isEmpty()) {
                return new ResultadoOperacion(false, "Elige al menos un asiento");
            }
            if (new HashSet<>(asientos).size() != asientos.size()) {
                return new ResultadoOperacion(false, "Hay asientos repetidos en la selección");
            }
            ResultadoOperacion cotizacion = cotizar(idPasajero, idViaje, asientos.size());
            if (!cotizacion.isExito()) {
                return cotizacion;
            }
            double total = (Double) cotizacion.getDatos();
            Viaje viaje = fachada.obten(new Viaje(idViaje));
            if (!esComprable(viaje)) {
                return new ResultadoOperacion(false, "Este viaje ya no está disponible para la compra");
            }
            ResultadoOperacion saldo = billetera.consultarSaldo(idPasajero);
            if (!saldo.isExito()) {
                return saldo;
            }
            double disponible = (Double) saldo.getDatos();
            if (disponible + 1e-9 < total) {
                return new ResultadoOperacion(false, String.format("Saldo insuficiente: el pasaje cuesta $%.2f y tu saldo es $%.2f. Solicita una recarga.", total, disponible));
            }
            Fecha hoy = new Fecha(LocalDate.now().getDayOfMonth(), LocalDate.now().getMonthValue(), LocalDate.now().getYear());
            ResultadoOperacion creada = reservas.crearReserva("TEMP", idPasajero, idViaje, asientos.size(), asientos,
                    viaje.getEmpresa().getIdEmpresa(), hoy, "BILLETERA", 0);
            if (!creada.isExito()) {
                return creada;
            }
            Reserva reserva = creada.getDatosComoTipo(Reserva.class);
            ResultadoOperacion cobro = billetera.debitarCompra(idPasajero, reserva.getPrecioTotal(), reserva.getIdReserva());
            if (!cobro.isExito()) {
                reservas.eliminarReserva(reserva.getIdReserva());
                return new ResultadoOperacion(false, "No se pudo cobrar: " + cobro.getMensaje());
            }
            ResultadoOperacion confirmada = reservas.pagarReserva(reserva.getIdReserva());
            if (!confirmada.isExito()) {
                billetera.reembolsarReserva(reserva.getIdReserva());
                reservas.eliminarReserva(reserva.getIdReserva());
                return new ResultadoOperacion(false, "No se pudo confirmar la reserva: " + confirmada.getMensaje());
            }
            Reserva final_ = fachada.obten(new Reserva(reserva.getIdReserva()));
            return new ResultadoOperacion(true, String.format("¡Compra exitosa! Reserva %s por $%.2f", reserva.getIdReserva(), reserva.getPrecioTotal()),
                    final_ != null ? final_ : reserva);
        } catch (FachadaException e) {
            return new ResultadoOperacion(false, "Error al comprar: " + e.getMessage());
        }
    }

    /** "Origen → Destino" de un viaje, resolviendo la ruta si solo se conoce su id. */
    public String describirRuta(Viaje viaje) {
        try {
            Ruta ruta = viaje == null ? null : fachada.obten(viaje.getRuta());
            return ruta == null ? "(ruta no disponible)" : ruta.getOrigen() + " → " + ruta.getDestino();
        } catch (FachadaException e) {
            return "(ruta no disponible)";
        }
    }

    public ResultadoOperacion misPasajes(String idPasajero) {
        try {
            List<Reserva> lista = fachada.consultaReservasPorPasajero(idPasajero).stream()
                    .sorted(Comparator.comparing(Reserva::getIdReserva).reversed()).collect(Collectors.toList());
            return new ResultadoOperacion(true, "OK", lista);
        } catch (FachadaException e) {
            return new ResultadoOperacion(false, e.getMessage());
        }
    }

    /** Cancela una reserva propia (regla de 24 h del sistema) y reembolsa a la billetera. */
    public ResultadoOperacion cancelar(String idPasajero, String idReserva) {
        try {
            Reserva reserva = fachada.obten(new Reserva(idReserva));
            if (reserva == null || reserva.getPasajero() == null || !idPasajero.equals(reserva.getPasajero().getIdPasajero())) {
                return new ResultadoOperacion(false, "Esa reserva no te pertenece");
            }
            return reservas.cancelarReserva(idReserva, "Cancelada por el pasajero");
        } catch (FachadaException e) {
            return new ResultadoOperacion(false, e.getMessage());
        }
    }
}
