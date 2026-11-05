package control;

import excepciones.PersistenciaException;
import excepciones.Validador;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import objetosNegocio.Movimiento;
import objetosNegocio.Pasajero;
import objetosNegocio.Rol;
import objetosNegocio.Usuario;
import objetosServicio.Fecha;
import persistencia.Movimientos;
import persistencia.Pasajeros;

/**
 * Billetera del pasajero. Flujo: el pasajero solicita una recarga (tarjeta o efectivo), el super administrador
 * la aprueba o rechaza y recién entonces el saldo queda disponible para comprar pasajes.
 * De la tarjeta solo se conserva la marca y los últimos 4 dígitos; nunca el número completo ni el CVV.
 */
public class ControladorBilletera {
    public static final double MONTO_MINIMO = 1.00;
    public static final double MONTO_MAXIMO = 500.00;
    public static final int MAX_PENDIENTES = 3;

    private final Movimientos movimientos;
    private final Pasajeros pasajeros;

    public ControladorBilletera() {
        this(new Movimientos("movimientos.dat"), new Pasajeros("pasajeros.dat"));
    }

    public ControladorBilletera(Movimientos movimientos, Pasajeros pasajeros) {
        this.movimientos = movimientos;
        this.pasajeros = pasajeros;
    }

    // ---------------------------------------------------------------- saldo e historial

    public synchronized ResultadoOperacion consultarSaldo(String idPasajero) {
        try {
            return new ResultadoOperacion(true, "OK", centavosASaldo(saldoEnCentavos(idPasajero)));
        } catch (PersistenciaException e) {
            return new ResultadoOperacion(false, e.getMessage());
        }
    }

    public synchronized ResultadoOperacion historial(String idPasajero) {
        try {
            List<Movimiento> lista = movimientos.listaPorPasajero(idPasajero);
            lista.sort(Comparator.comparing(ControladorBilletera::clave).reversed());
            return new ResultadoOperacion(true, "OK", lista);
        } catch (PersistenciaException e) {
            return new ResultadoOperacion(false, e.getMessage());
        }
    }

    public synchronized ResultadoOperacion recargasPendientes() {
        try {
            List<Movimiento> lista = movimientos.lista().stream().filter(Movimiento::esRecargaPendiente)
                    .sorted(Comparator.comparing(ControladorBilletera::clave)).collect(Collectors.toList());
            return new ResultadoOperacion(true, "OK", lista);
        } catch (PersistenciaException e) {
            return new ResultadoOperacion(false, e.getMessage());
        }
    }

    public synchronized ResultadoOperacion todasLasRecargas() {
        try {
            List<Movimiento> lista = movimientos.lista().stream().filter(m -> Movimiento.RECARGA.equals(m.getTipo()))
                    .sorted(Comparator.comparing(ControladorBilletera::clave).reversed()).collect(Collectors.toList());
            return new ResultadoOperacion(true, "OK", lista);
        } catch (PersistenciaException e) {
            return new ResultadoOperacion(false, e.getMessage());
        }
    }

    // ---------------------------------------------------------------- solicitudes de recarga

    public synchronized ResultadoOperacion solicitarRecargaTarjeta(String idPasajero, double monto, String titular, String numero,
                                                                   String vencimiento, String cvv) {
        String problema = validarSolicitud(idPasajero, monto);
        if (problema == null) problema = Validador.errorNombre(titular) == null ? null : "Escribe el nombre del titular como aparece en la tarjeta";
        String digitos = numero == null ? "" : numero.replaceAll("[\\s-]", "");
        if (problema == null && !Validador.esTarjetaValida(digitos)) problema = "El número de tarjeta no es válido";
        if (problema == null) problema = errorVencimiento(vencimiento);
        if (problema == null) {
            boolean amex = digitos.startsWith("34") || digitos.startsWith("37");
            if (cvv == null || !cvv.matches(amex ? "\\d{4}" : "\\d{3}")) {
                problema = amex ? "El CVV de American Express tiene 4 dígitos" : "El CVV debe tener 3 dígitos";
            }
        }
        if (problema != null) {
            return new ResultadoOperacion(false, problema);
        }
        String referencia = marca(digitos) + " ****" + digitos.substring(digitos.length() - 4);
        return registrarSolicitud(idPasajero, monto, Movimiento.METODO_TARJETA, referencia);
    }

    public synchronized ResultadoOperacion solicitarRecargaEfectivo(String idPasajero, double monto, String comprobante) {
        String problema = validarSolicitud(idPasajero, monto);
        String limpio = comprobante == null ? "" : comprobante.trim();
        if (problema == null && !limpio.matches("\\d{6,12}")) {
            problema = "El número de comprobante de pago en efectivo debe tener entre 6 y 12 dígitos";
        }
        try {
            if (problema == null) {
                String referencia = "COMP " + limpio;
                boolean repetido = movimientos.lista().stream().anyMatch(m -> referencia.equals(m.getReferencia())
                        && !Movimiento.RECHAZADA.equals(m.getEstado()));
                if (repetido) {
                    problema = "Ese comprobante ya fue registrado";
                }
            }
        } catch (PersistenciaException e) {
            return new ResultadoOperacion(false, e.getMessage());
        }
        if (problema != null) {
            return new ResultadoOperacion(false, problema);
        }
        return registrarSolicitud(idPasajero, monto, Movimiento.METODO_EFECTIVO, "COMP " + limpio);
    }

    private String validarSolicitud(String idPasajero, double monto) {
        try {
            if (idPasajero == null || pasajeros.obten(new Pasajero(idPasajero)) == null) {
                return "Pasajero no encontrado";
            }
            if (Double.isNaN(monto) || monto < MONTO_MINIMO || monto > MONTO_MAXIMO) {
                return String.format("El monto debe estar entre $%.2f y $%.2f", MONTO_MINIMO, MONTO_MAXIMO);
            }
            if (Math.abs(monto * 100 - Math.round(monto * 100)) > 1e-6) {
                return "El monto no puede tener más de 2 decimales";
            }
            long pendientes = movimientos.listaPorPasajero(idPasajero).stream().filter(Movimiento::esRecargaPendiente).count();
            if (pendientes >= MAX_PENDIENTES) {
                return "Ya tienes " + MAX_PENDIENTES + " recargas esperando aprobación. Espera a que el administrador las revise.";
            }
            return null;
        } catch (PersistenciaException e) {
            return e.getMessage();
        }
    }

    private ResultadoOperacion registrarSolicitud(String idPasajero, double monto, String metodo, String referencia) {
        try {
            Movimiento m = nuevoMovimiento(idPasajero, Movimiento.RECARGA, monto, metodo, Movimiento.PENDIENTE, referencia);
            movimientos.agrega(m);
            return new ResultadoOperacion(true, "Solicitud de recarga enviada. Quedará disponible cuando el administrador la apruebe.", m);
        } catch (PersistenciaException e) {
            return new ResultadoOperacion(false, e.getMessage());
        }
    }

    // ---------------------------------------------------------------- revisión del administrador

    public synchronized ResultadoOperacion aprobarRecarga(Usuario actor, String idMovimiento) {
        return revisar(actor, idMovimiento, true, null);
    }

    public synchronized ResultadoOperacion rechazarRecarga(Usuario actor, String idMovimiento, String motivo) {
        if (motivo == null || motivo.trim().length() < 5) {
            return new ResultadoOperacion(false, "Escribe el motivo del rechazo (mínimo 5 caracteres)");
        }
        return revisar(actor, idMovimiento, false, motivo.trim());
    }

    private ResultadoOperacion revisar(Usuario actor, String idMovimiento, boolean aprobar, String motivo) {
        if (actor == null || actor.getRol() != Rol.ADMINISTRADOR || !actor.esSuperAdministrador() || !actor.estaActivo()) {
            return new ResultadoOperacion(false, "Solo el administrador general puede revisar recargas");
        }
        try {
            Movimiento m = movimientos.obten(idMovimiento);
            if (m == null || !Movimiento.RECARGA.equals(m.getTipo())) {
                return new ResultadoOperacion(false, "Recarga no encontrada");
            }
            if (!Movimiento.PENDIENTE.equals(m.getEstado())) {
                return new ResultadoOperacion(false, "Esta recarga ya fue revisada (" + m.getEstado() + ")");
            }
            m.setEstado(aprobar ? Movimiento.APROBADA : Movimiento.RECHAZADA);
            m.setIdRevisor(actor.getIdUsuario());
            m.setMotivo(aprobar ? "Aprobada" : motivo);
            movimientos.actualiza(m);
            return new ResultadoOperacion(true, aprobar ? String.format("Recarga aprobada: +$%.2f", m.getMonto()) : "Recarga rechazada", m);
        } catch (PersistenciaException e) {
            return new ResultadoOperacion(false, e.getMessage());
        }
    }

    // ---------------------------------------------------------------- uso del saldo

    public synchronized ResultadoOperacion debitarCompra(String idPasajero, double monto, String idReserva) {
        try {
            long cobro = Math.round(monto * 100);
            if (cobro <= 0) {
                return new ResultadoOperacion(false, "Monto inválido");
            }
            if (saldoEnCentavos(idPasajero) < cobro) {
                return new ResultadoOperacion(false, "Saldo insuficiente en la billetera");
            }
            Movimiento m = nuevoMovimiento(idPasajero, Movimiento.COMPRA, monto, Movimiento.METODO_BILLETERA, Movimiento.APROBADA, idReserva);
            movimientos.agrega(m);
            return new ResultadoOperacion(true, "Pago realizado con la billetera", m);
        } catch (PersistenciaException e) {
            return new ResultadoOperacion(false, e.getMessage());
        }
    }

    /** Devuelve a la billetera lo cobrado por una reserva. Es idempotente: no reembolsa dos veces. */
    public synchronized ResultadoOperacion reembolsarReserva(String idReserva) {
        try {
            List<Movimiento> todos = movimientos.lista();
            Movimiento compra = todos.stream().filter(m -> Movimiento.COMPRA.equals(m.getTipo()) && idReserva.equals(m.getReferencia())).findFirst().orElse(null);
            if (compra == null) {
                return new ResultadoOperacion(false, "La reserva no fue pagada con la billetera");
            }
            boolean yaReembolsada = todos.stream().anyMatch(m -> Movimiento.REEMBOLSO.equals(m.getTipo()) && idReserva.equals(m.getReferencia()));
            if (yaReembolsada) {
                return new ResultadoOperacion(true, "La reserva ya había sido reembolsada");
            }
            Movimiento r = nuevoMovimiento(compra.getIdPasajero(), Movimiento.REEMBOLSO, compra.getMonto(), Movimiento.METODO_BILLETERA, Movimiento.APROBADA, idReserva);
            movimientos.agrega(r);
            return new ResultadoOperacion(true, String.format("Se reembolsaron $%.2f a la billetera.", compra.getMonto()), r);
        } catch (PersistenciaException e) {
            return new ResultadoOperacion(false, e.getMessage());
        }
    }

    // ---------------------------------------------------------------- utilidades

    private long saldoEnCentavos(String idPasajero) throws PersistenciaException {
        long saldo = 0;
        for (Movimiento m : movimientos.listaPorPasajero(idPasajero)) {
            long centavos = Math.round(m.getMonto() * 100);
            if (Movimiento.RECARGA.equals(m.getTipo()) && Movimiento.APROBADA.equals(m.getEstado())) {
                saldo += centavos;
            } else if (Movimiento.REEMBOLSO.equals(m.getTipo()) && Movimiento.APROBADA.equals(m.getEstado())) {
                saldo += centavos;
            } else if (Movimiento.COMPRA.equals(m.getTipo()) && Movimiento.APROBADA.equals(m.getEstado())) {
                saldo -= centavos;
            }
        }
        return saldo;
    }

    private static double centavosASaldo(long centavos) {
        return centavos / 100.0;
    }

    private Movimiento nuevoMovimiento(String idPasajero, String tipo, double monto, String metodo, String estado, String referencia) {
        LocalDateTime ahora = LocalDateTime.now();
        Movimiento m = new Movimiento();
        m.setIdPasajero(idPasajero);
        m.setTipo(tipo);
        m.setMonto(Math.round(monto * 100) / 100.0);
        m.setMetodo(metodo);
        m.setEstado(estado);
        m.setReferencia(referencia);
        m.setFecha(new Fecha(ahora.getDayOfMonth(), ahora.getMonthValue(), ahora.getYear()));
        m.setHora(ahora.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        m.setIdRevisor("");
        m.setMotivo("");
        return m;
    }

    private static String clave(Movimiento m) {
        Fecha f = m.getFecha();
        String fecha = f == null ? "00000000" : String.format("%04d%02d%02d", f.getAnio(), f.getMes(), f.getDia());
        return fecha + (m.getHora() == null ? "" : m.getHora()) + m.getIdMovimiento();
    }

    private static String errorVencimiento(String vencimiento) {
        if (vencimiento == null || !vencimiento.trim().matches("(0[1-9]|1[0-2])/\\d{2}")) {
            return "El vencimiento debe tener el formato MM/AA";
        }
        String[] p = vencimiento.trim().split("/");
        YearMonth vence = YearMonth.of(2000 + Integer.parseInt(p[1]), Integer.parseInt(p[0]));
        if (vence.isBefore(YearMonth.from(LocalDate.now()))) {
            return "La tarjeta está vencida";
        }
        if (vence.isAfter(YearMonth.from(LocalDate.now()).plusYears(12))) {
            return "La fecha de vencimiento no es válida";
        }
        return null;
    }

    private static String marca(String digitos) {
        if (digitos.startsWith("4")) return "VISA";
        if (digitos.matches("^(5[1-5]|2[2-7]).*")) return "MASTERCARD";
        if (digitos.startsWith("34") || digitos.startsWith("37")) return "AMEX";
        return "TARJETA";
    }

    public List<Movimiento> comoLista(ResultadoOperacion r) {
        @SuppressWarnings("unchecked")
        List<Movimiento> lista = r.isExito() && r.getDatos() instanceof List ? (List<Movimiento>) r.getDatos() : new ArrayList<>();
        return lista;
    }
}
