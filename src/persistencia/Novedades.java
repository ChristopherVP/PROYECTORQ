package persistencia;

import excepciones.PersistenciaException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import objetosNegocio.Novedad;

public class Novedades extends AccesoAleatorio {
    public static final int TAM_REGISTRO = (10 + 10 + 10 + 20 + 60 + 300 + 8 + 12 + 200 + 10) * 2 + 12;

    public Novedades(String nomArchivo) {
        super(nomArchivo, TAM_REGISTRO);
    }

    private Novedad lee() throws IOException {
        Novedad n = new Novedad();
        n.setIdNovedad(leeString(10));
        n.setIdConductor(leeString(10));
        n.setIdViaje(leeString(10));
        n.setTipo(leeString(20));
        n.setAsunto(leeString(60));
        n.setMensaje(leeString(300));
        n.setFecha(leeFecha());
        n.setHora(leeString(8));
        n.setEstado(leeString(12));
        n.setRespuesta(leeString(200));
        n.setIdRevisor(leeString(10));
        return n;
    }

    private void escribe(Novedad n) throws IOException {
        escribeString(n.getIdNovedad(), 10);
        escribeString(n.getIdConductor(), 10);
        escribeString(n.getIdViaje(), 10);
        escribeString(n.getTipo(), 20);
        escribeString(n.getAsunto(), 60);
        escribeString(n.getMensaje(), 300);
        escribeFecha(n.getFecha());
        escribeString(n.getHora(), 8);
        escribeString(n.getEstado(), 12);
        escribeString(n.getRespuesta(), 200);
        escribeString(n.getIdRevisor(), 10);
    }

    public synchronized void agrega(Novedad n) throws PersistenciaException {
        int max = 0;
        for (Novedad x : lista()) {
            if (x.getIdNovedad().startsWith("NOV")) {
                try {
                    max = Math.max(max, Integer.parseInt(x.getIdNovedad().substring(3)));
                } catch (NumberFormatException ignorado) {
                    // otro formato de id
                }
            }
        }
        n.setIdNovedad(String.format("NOV%05d", max + 1));
        try (RandomAccessFile raf = new RandomAccessFile(nomArchivo, "rw")) {
            archivo = raf;
            raf.seek(raf.length());
            escribe(n);
        } catch (IOException e) {
            throw new PersistenciaException("Error al guardar la novedad", e);
        } finally {
            archivo = null;
        }
    }

    public synchronized void actualiza(Novedad n) throws PersistenciaException {
        try (RandomAccessFile raf = new RandomAccessFile(nomArchivo, "rw")) {
            archivo = raf;
            long total = raf.length() / TAM_REGISTRO;
            for (long i = 0; i < total; i++) {
                long pos = i * TAM_REGISTRO;
                raf.seek(pos);
                Novedad x = lee();
                if (x.getIdNovedad().equals(n.getIdNovedad())) {
                    raf.seek(pos);
                    escribe(n);
                    return;
                }
            }
            throw new PersistenciaException("Novedad no encontrada: " + n.getIdNovedad());
        } catch (IOException e) {
            throw new PersistenciaException("Error al actualizar la novedad", e);
        } finally {
            archivo = null;
        }
    }

    public synchronized List<Novedad> lista() throws PersistenciaException {
        List<Novedad> resultado = new ArrayList<>();
        try (RandomAccessFile raf = new RandomAccessFile(nomArchivo, "r")) {
            archivo = raf;
            long total = raf.length() / TAM_REGISTRO;
            for (long i = 0; i < total; i++) {
                Novedad n = lee();
                if (n.getIdNovedad() != null && !n.getIdNovedad().isEmpty()) {
                    resultado.add(n);
                }
            }
        } catch (IOException e) {
            throw new PersistenciaException("Error al leer las novedades", e);
        } finally {
            archivo = null;
        }
        return resultado;
    }

    public Novedad obten(String id) throws PersistenciaException {
        for (Novedad n : lista()) {
            if (n.getIdNovedad().equals(id)) {
                return n;
            }
        }
        return null;
    }
}
