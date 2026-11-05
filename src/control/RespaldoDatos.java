package control;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;

/** Copia de seguridad de los archivos .dat al iniciar (se conservan las últimas 5). */
public final class RespaldoDatos {
    private static final int CONSERVAR = 5;

    private RespaldoDatos() {
    }

    public static File respaldar(File carpetaDatos) throws IOException {
        File[] datos = carpetaDatos.listFiles((d, n) -> n.endsWith(".dat"));
        if (datos == null || datos.length == 0) {
            return null;
        }
        File raiz = new File(carpetaDatos, "respaldos");
        File destino = new File(raiz, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")));
        if (!destino.mkdirs()) {
            throw new IOException("No se pudo crear la carpeta de respaldo " + destino);
        }
        for (File f : datos) {
            Files.copy(f.toPath(), new File(destino, f.getName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        File[] anteriores = raiz.listFiles(File::isDirectory);
        if (anteriores != null && anteriores.length > CONSERVAR) {
            Arrays.sort(anteriores, Comparator.comparing(File::getName));
            for (int i = 0; i < anteriores.length - CONSERVAR; i++) {
                borrar(anteriores[i]);
            }
        }
        return destino;
    }

    private static void borrar(File carpeta) {
        File[] hijos = carpeta.listFiles();
        if (hijos != null) {
            for (File h : hijos) {
                if (!h.delete()) {
                    h.deleteOnExit();
                }
            }
        }
        if (!carpeta.delete()) {
            carpeta.deleteOnExit();
        }
    }
}
