package InterfacesUsuario;

import control.ControladorUsuarios;
import java.awt.Window;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import objetosNegocio.Rol;
import objetosNegocio.Usuario;

/** Decide qué ventana mostrar según el estado del sistema y el rol de quien inicia sesión. */
public final class Navegador {
    private Navegador() {
    }

    /** Primer arranque: si no existe ningún administrador general se pide crearlo; si no, inicio de sesión. */
    public static void iniciar() {
        if (!new ControladorUsuarios().existeSuperAdministrador()) {
            new PantallaConfiguracionInicial().setVisible(true);
        } else {
            new PantallaInicioSesion().setVisible(true);
        }
    }

    public static void abrirSesion(Usuario usuario, Window origen) {
        if (origen != null) {
            origen.dispose();
        }
        Rol rol = usuario.getRol();
        JFrame ventana;
        if (rol == Rol.PASAJERO) {
            ventana = new PantallaPortalPasajero(usuario);
        } else if (rol == Rol.CONDUCTOR) {
            ventana = new PantallaPortalConductor(usuario);
        } else {
            ventana = new PantallaPrincipal(usuario);
        }
        ventana.setIconImage(IconoRQ.crear(64));
        ventana.setVisible(true);
        if (!usuario.tienePreguntaSeguridad()) {
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(ventana,
                    "Tu cuenta aún no tiene pregunta de seguridad.\nConfigúrala en Cuenta ▸ Mi perfil para poder recuperar tu contraseña si la olvidas.",
                    "Recomendación de seguridad", JOptionPane.INFORMATION_MESSAGE));
        }
    }

    public static void cerrarSesion(Window actual) {
        if (actual != null) {
            actual.dispose();
        }
        new PantallaInicioSesion().setVisible(true);
    }

    /** Copia en "destino" los datos actualizados de la cuenta para que la ventana abierta los refleje. */
    public static void refrescar(Usuario destino, Usuario fuente) {
        destino.setNombre(fuente.getNombre());
        destino.setEmail(fuente.getEmail());
        destino.setTelefono(fuente.getTelefono());
        destino.setContrasena(fuente.getContrasena());
        destino.setPreguntaSeguridad(fuente.getPreguntaSeguridad());
        destino.setRespuestaSeguridad(fuente.getRespuestaSeguridad());
        destino.setEstado(fuente.getEstado());
    }
}
