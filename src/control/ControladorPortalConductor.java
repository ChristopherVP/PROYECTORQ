package control;

import excepciones.FachadaException;
import fachadas.FachadaArchivos;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import objetosNegocio.Rol;
import objetosNegocio.Usuario;
import objetosNegocio.Viaje;

/** Consultas del conductor: solo ve los viajes que el administrador le asignó. */
public class ControladorPortalConductor {
    private final FachadaArchivos fachada = FachadaArchivos.getInstance();

    public ResultadoOperacion viajesAsignados(Usuario conductor) {
        if (conductor == null || conductor.getRol() != Rol.CONDUCTOR || conductor.getIdPerfil() == null || conductor.getIdPerfil().isEmpty()) {
            return new ResultadoOperacion(false, "Acción no permitida");
        }
        try {
            List<Viaje> lista = fachada.consultaViajes().stream()
                    .filter(v -> ControladorNovedades.esConductorDelViaje(v, conductor.getIdPerfil()))
                    .sorted(Comparator.comparing((Viaje v) -> {
                        LocalDateTime s = ControladorCompras.salidaDe(v);
                        return s == null ? LocalDateTime.MIN : s;
                    }))
                    .collect(Collectors.toList());
            return new ResultadoOperacion(true, "OK", lista);
        } catch (FachadaException e) {
            return new ResultadoOperacion(false, e.getMessage());
        }
    }
}
