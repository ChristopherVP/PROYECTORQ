package persistencia;

import excepciones.PersistenciaException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import objetosNegocio.Empresa;
import objetosNegocio.Usuario;
import objetosServicio.Seguridad;

/** Convierte el archivo de usuarios del formato anterior (contraseñas en texto plano) al formato v2. */
public final class MigradorDatos {
    /** Tamaño de registro del formato anterior: (10+50+15+20+50+20+20+10+20) caracteres + fecha. */
    static final int TAM_REGISTRO_V1 = (10 + 50 + 15 + 20 + 50 + 20 + 20 + 10 + 20) * 2 + 12;

    private MigradorDatos() {
    }

    public static boolean esFormatoV1(File archivo) {
        return archivo.exists() && archivo.length() > 0 && archivo.length() % TAM_REGISTRO_V1 == 0
                && archivo.length() % Usuarios.TAM_REGISTRO != 0;
    }

    /**
     * Migra el archivo en sitio. Deja una copia del original como "&lt;archivo&gt;.v1.bak".
     * El nombre de usuario (login) pasa a ser el id anterior (USU001...), así que cada persona sigue entrando
     * con el mismo código y su misma contraseña.
     */
    public static int migrarUsuariosV1(String nombreArchivo, BiConsumer<Integer, Integer> avance) throws PersistenciaException {
        File origen = new File(nombreArchivo);
        File respaldo = new File(nombreArchivo + ".v1.bak");
        List<Usuario> antiguos = leerV1(origen);
        try {
            Files.copy(origen.toPath(), respaldo.toPath(), StandardCopyOption.REPLACE_EXISTING);
            File nuevo = new File(nombreArchivo + ".tmp");
            Files.deleteIfExists(nuevo.toPath());
            Usuarios destino = new Usuarios(nuevo.getPath());
            int hechos = 0;
            for (Usuario u : antiguos) {
                u.definirContrasena(u.getContrasena());
                destino.importa(u);
                if (avance != null) {
                    avance.accept(++hechos, antiguos.size());
                }
            }
            Files.move(nuevo.toPath(), origen.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new PersistenciaException("No se pudo migrar el archivo de usuarios", e);
        }
        return antiguos.size();
    }

    private static List<Usuario> leerV1(File origen) throws PersistenciaException {
        List<Usuario> resultado = new ArrayList<>();
        AccesoAleatorio lector = new AccesoAleatorio(origen.getPath(), TAM_REGISTRO_V1);
        try (RandomAccessFile raf = new RandomAccessFile(origen, "r")) {
            lector.archivo = raf;
            long total = raf.length() / TAM_REGISTRO_V1;
            for (long i = 0; i < total; i++) {
                String id = lector.leeString(10);
                String nombre = lector.leeString(50);
                String cedula = lector.leeString(15);
                String clave = lector.leeString(20);
                String email = lector.leeString(50);
                String telefono = lector.leeString(20);
                String tipo = lector.leeString(20);
                objetosServicio.Fecha fecha = lector.leeFecha();
                String idEmpresa = lector.leeString(10);
                String estado = lector.leeString(20);
                if (id.isEmpty()) {
                    continue;
                }
                Usuario u = new Usuario(id);
                u.setLogin(id.toLowerCase());
                u.setNombre(nombre);
                u.setCedula(cedula);
                // la clave en texto plano solo vive en memoria hasta que se protege en migrarUsuariosV1
                u.setContrasena(clave.isEmpty() ? java.util.UUID.randomUUID().toString() : clave);
                u.setEmail(email);
                u.setTelefono(telefono);
                u.setTipo(tipo);
                u.setFechaRegistro(fecha);
                u.setEmpresa(idEmpresa.isEmpty() ? null : new Empresa(idEmpresa));
                u.setEstado(estado.isEmpty() ? Usuario.ESTADO_ACTIVO : estado);
                u.setPreguntaSeguridad("");
                u.setRespuestaSeguridad("");
                u.setIdPerfil("");
                resultado.add(u);
            }
        } catch (IOException e) {
            throw new PersistenciaException("No se pudo leer el archivo de usuarios anterior", e);
        }
        return resultado;
    }
}
