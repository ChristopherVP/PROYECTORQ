package persistencia;

import excepciones.PersistenciaException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import objetosNegocio.Cargo;
import objetosNegocio.Empresa;
import objetosNegocio.Rol;
import objetosNegocio.Usuario;
import objetosServicio.Seguridad;

/**
 * Persistencia de usuarios en archivo de acceso aleatorio (registro fijo).
 * Formato v2: id, login, nombre, cédula, hash de clave, email, teléfono, tipo, fecha, empresa, estado,
 * pregunta, hash de respuesta, idPerfil, intentos de recuperación y bloqueo.
 */
public class Usuarios extends AccesoAleatorio {
    private static final int CARACTERES = 10 + 20 + 50 + 15 + 80 + 50 + 20 + 20 + 10 + 20 + 80 + 80 + 10 + Cargo.LONGITUD_MAXIMA;
    /** Fecha (12) + intentos (4) + bloqueo (8) + clave pendiente (4). */
    public static final int TAM_REGISTRO = CARACTERES * 2 + 12 + 4 + 8 + 4;

    public Usuarios(String nomArchivo) {
        super(nomArchivo, TAM_REGISTRO);
    }

    private Usuario leeUsuario() throws IOException {
        Usuario u = new Usuario();
        u.setIdUsuario(leeString(10));
        u.setLogin(leeString(20));
        u.setNombre(leeString(50));
        u.setCedula(leeString(15));
        u.setContrasena(leeString(80));
        u.setEmail(leeString(50));
        u.setTelefono(leeString(20));
        u.setTipo(leeString(20));
        u.setFechaRegistro(leeFecha());
        String idEmpresa = leeString(10);
        u.setEmpresa(idEmpresa.isEmpty() ? null : new Empresa(idEmpresa));
        u.setEstado(leeString(20));
        u.setPreguntaSeguridad(leeString(80));
        u.setRespuestaSeguridad(leeString(80));
        u.setIdPerfil(leeString(10));
        u.setCargo(leeString(Cargo.LONGITUD_MAXIMA));
        u.setIntentosRecuperacion(archivo.readInt());
        u.setBloqueoHasta(archivo.readLong());
        u.setClavePendiente(archivo.readInt() == 1);
        return u;
    }

    private void escribeUsuario(Usuario u) throws IOException {
        escribeString(u.getIdUsuario(), 10);
        escribeString(u.getLogin(), 20);
        escribeString(u.getNombre(), 50);
        escribeString(u.getCedula(), 15);
        escribeString(u.getContrasena(), 80);
        escribeString(u.getEmail(), 50);
        escribeString(u.getTelefono(), 20);
        escribeString(u.getTipo(), 20);
        escribeFecha(u.getFechaRegistro());
        escribeString(u.tieneEmpresa() ? u.getEmpresa().getIdEmpresa() : "", 10);
        escribeString(u.getEstado(), 20);
        escribeString(u.getPreguntaSeguridad(), 80);
        escribeString(u.getRespuestaSeguridad(), 80);
        escribeString(u.getIdPerfil(), 10);
        escribeString(u.getCargo(), Cargo.LONGITUD_MAXIMA);
        archivo.writeInt(u.getIntentosRecuperacion());
        archivo.writeLong(u.getBloqueoHasta());
        archivo.writeInt(u.isClavePendiente() ? 1 : 0);
    }

    private static boolean esValido(Usuario u) {
        return u != null && u.getIdUsuario() != null && !u.getIdUsuario().trim().isEmpty();
    }

    /** Validación estructural del registro (las reglas de negocio finas están en ControladorUsuarios). */
    private void validar(Usuario u) throws PersistenciaException {
        if (u.getLogin() == null || u.getLogin().trim().isEmpty()) {
            throw new PersistenciaException("El nombre de usuario es obligatorio");
        }
        if (u.getNombre() == null || u.getNombre().trim().isEmpty()) {
            throw new PersistenciaException("El nombre no puede estar vacío");
        }
        if (u.getCedula() == null || !u.getCedula().trim().matches("\\d{10}")) {
            throw new PersistenciaException("La cédula debe tener 10 dígitos");
        }
        if (!Seguridad.esHash(u.getContrasena())) {
            throw new PersistenciaException("La contraseña debe almacenarse protegida (hash)");
        }
        Rol rol = u.getRol();
        if (rol == null) {
            throw new PersistenciaException("Rol de usuario inválido: " + u.getTipo());
        }
        if (u.getCargo() != null && u.getCargo().length() > Cargo.LONGITUD_MAXIMA) {
            throw new PersistenciaException("El cargo no puede superar " + Cargo.LONGITUD_MAXIMA + " caracteres");
        }
        if (rol == Rol.OPERADOR && !u.tieneEmpresa()) {
            throw new PersistenciaException("Los operadores deben pertenecer a una empresa");
        }
        if ((rol == Rol.CONDUCTOR || rol == Rol.PASAJERO) && (u.getIdPerfil() == null || u.getIdPerfil().trim().isEmpty())) {
            throw new PersistenciaException("Los conductores y pasajeros deben tener una ficha asociada");
        }
        if (u.getEstado() == null || u.getEstado().trim().isEmpty()) {
            throw new PersistenciaException("El estado del usuario es obligatorio");
        }
    }

    private String siguienteId(List<Usuario> existentes) {
        int max = 0;
        for (Usuario u : existentes) {
            String id = u.getIdUsuario();
            if (id.startsWith("USU") && id.length() > 3) {
                try {
                    max = Math.max(max, Integer.parseInt(id.substring(3)));
                } catch (NumberFormatException ignorado) {
                    // ids con otro formato no participan del correlativo
                }
            }
        }
        return String.format("USU%03d", max + 1);
    }

    public synchronized void agrega(Usuario usuario) throws PersistenciaException {
        validar(usuario);
        usuario.setIdUsuario(siguienteId(lista()));
        escribeAlFinal(usuario);
    }

    /** Agrega conservando el id recibido (migraciones). */
    public synchronized void importa(Usuario usuario) throws PersistenciaException {
        validar(usuario);
        if (!esValido(usuario)) {
            throw new PersistenciaException("El usuario importado necesita un id");
        }
        escribeAlFinal(usuario);
    }

    private void escribeAlFinal(Usuario usuario) throws PersistenciaException {
        try (RandomAccessFile raf = new RandomAccessFile(nomArchivo, "rw")) {
            archivo = raf;
            raf.seek(raf.length());
            escribeUsuario(usuario);
        } catch (IOException e) {
            throw new PersistenciaException("Error al guardar el usuario", e);
        } finally {
            archivo = null;
        }
    }

    private long posicionDe(RandomAccessFile raf, String idUsuario) throws IOException {
        long total = raf.length() / TAM_REGISTRO;
        for (long i = 0; i < total; i++) {
            long pos = i * TAM_REGISTRO;
            raf.seek(pos);
            Usuario u = leeUsuario();
            if (esValido(u) && u.getIdUsuario().equals(idUsuario)) {
                return pos;
            }
        }
        return -1;
    }

    public synchronized Usuario obten(Usuario usuario) throws PersistenciaException {
        if (usuario == null || usuario.getIdUsuario() == null) {
            return null;
        }
        return obtenPorIdUsuario(usuario.getIdUsuario());
    }

    public synchronized Usuario obtenPorIdUsuario(String idUsuario) throws PersistenciaException {
        for (Usuario u : lista()) {
            if (u.getIdUsuario().equals(idUsuario)) {
                return u;
            }
        }
        return null;
    }

    public synchronized Usuario buscaPorLogin(String login) throws PersistenciaException {
        if (login == null) {
            return null;
        }
        for (Usuario u : lista()) {
            if (u.getLogin() != null && u.getLogin().equalsIgnoreCase(login.trim())) {
                return u;
            }
        }
        return null;
    }

    public synchronized Usuario buscaPorCedula(String cedula) throws PersistenciaException {
        if (cedula == null) {
            return null;
        }
        for (Usuario u : lista()) {
            if (cedula.trim().equals(u.getCedula())) {
                return u;
            }
        }
        return null;
    }

    public synchronized void actualiza(Usuario usuario) throws PersistenciaException {
        validar(usuario);
        try (RandomAccessFile raf = new RandomAccessFile(nomArchivo, "rw")) {
            archivo = raf;
            long pos = posicionDe(raf, usuario.getIdUsuario());
            if (pos < 0) {
                throw new PersistenciaException("Usuario no encontrado: " + usuario.getIdUsuario());
            }
            raf.seek(pos);
            escribeUsuario(usuario);
        } catch (IOException e) {
            throw new PersistenciaException("Error al actualizar el usuario", e);
        } finally {
            archivo = null;
        }
    }

    public synchronized void elimina(Usuario usuario) throws PersistenciaException {
        try (RandomAccessFile raf = new RandomAccessFile(nomArchivo, "rw")) {
            archivo = raf;
            long pos = posicionDe(raf, usuario.getIdUsuario());
            if (pos < 0) {
                throw new PersistenciaException("Usuario no encontrado: " + usuario.getIdUsuario());
            }
            raf.seek(pos);
            borraRegistro();
        } catch (IOException e) {
            throw new PersistenciaException("Error al eliminar el usuario", e);
        } finally {
            archivo = null;
        }
    }

    public synchronized List<Usuario> lista() throws PersistenciaException {
        List<Usuario> resultado = new ArrayList<>();
        try (RandomAccessFile raf = new RandomAccessFile(nomArchivo, "r")) {
            archivo = raf;
            long total = raf.length() / TAM_REGISTRO;
            for (long i = 0; i < total; i++) {
                Usuario u = leeUsuario();
                if (esValido(u)) {
                    resultado.add(u);
                }
            }
        } catch (IOException e) {
            throw new PersistenciaException("Error al leer los usuarios", e);
        } finally {
            archivo = null;
        }
        return resultado;
    }

    public List<Usuario> listaPorTipo(String tipo) throws PersistenciaException {
        return lista().stream().filter(u -> u.getTipo() != null && u.getTipo().equalsIgnoreCase(tipo)).collect(Collectors.toList());
    }
}