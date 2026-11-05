package persistencia;

import excepciones.PersistenciaException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import objetosNegocio.Movimiento;

/** Libro de movimientos de la billetera. El saldo siempre se calcula a partir de este libro. */
public class Movimientos extends AccesoAleatorio {
    public static final int TAM_REGISTRO = (10 + 10 + 12 + 12 + 12 + 40 + 8 + 10 + 60) * 2 + 8 + 12;

    public Movimientos(String nomArchivo) {
        super(nomArchivo, TAM_REGISTRO);
    }

    private Movimiento lee() throws IOException {
        Movimiento m = new Movimiento();
        m.setIdMovimiento(leeString(10));
        m.setIdPasajero(leeString(10));
        m.setTipo(leeString(12));
        m.setMonto(archivo.readDouble());
        m.setMetodo(leeString(12));
        m.setEstado(leeString(12));
        m.setReferencia(leeString(40));
        m.setFecha(leeFecha());
        m.setHora(leeString(8));
        m.setIdRevisor(leeString(10));
        m.setMotivo(leeString(60));
        return m;
    }

    private void escribe(Movimiento m) throws IOException {
        escribeString(m.getIdMovimiento(), 10);
        escribeString(m.getIdPasajero(), 10);
        escribeString(m.getTipo(), 12);
        archivo.writeDouble(m.getMonto());
        escribeString(m.getMetodo(), 12);
        escribeString(m.getEstado(), 12);
        escribeString(m.getReferencia(), 40);
        escribeFecha(m.getFecha());
        escribeString(m.getHora(), 8);
        escribeString(m.getIdRevisor(), 10);
        escribeString(m.getMotivo(), 60);
    }

    public synchronized void agrega(Movimiento m) throws PersistenciaException {
        int max = 0;
        for (Movimiento x : lista()) {
            if (x.getIdMovimiento().startsWith("MOV")) {
                try {
                    max = Math.max(max, Integer.parseInt(x.getIdMovimiento().substring(3)));
                } catch (NumberFormatException ignorado) {
                    // otro formato de id: no participa del correlativo
                }
            }
        }
        m.setIdMovimiento(String.format("MOV%05d", max + 1));
        try (RandomAccessFile raf = new RandomAccessFile(nomArchivo, "rw")) {
            archivo = raf;
            raf.seek(raf.length());
            escribe(m);
        } catch (IOException e) {
            throw new PersistenciaException("Error al guardar el movimiento", e);
        } finally {
            archivo = null;
        }
    }

    public synchronized void actualiza(Movimiento m) throws PersistenciaException {
        try (RandomAccessFile raf = new RandomAccessFile(nomArchivo, "rw")) {
            archivo = raf;
            long total = raf.length() / TAM_REGISTRO;
            for (long i = 0; i < total; i++) {
                long pos = i * TAM_REGISTRO;
                raf.seek(pos);
                Movimiento x = lee();
                if (x.getIdMovimiento().equals(m.getIdMovimiento())) {
                    raf.seek(pos);
                    escribe(m);
                    return;
                }
            }
            throw new PersistenciaException("Movimiento no encontrado: " + m.getIdMovimiento());
        } catch (IOException e) {
            throw new PersistenciaException("Error al actualizar el movimiento", e);
        } finally {
            archivo = null;
        }
    }

    public synchronized List<Movimiento> lista() throws PersistenciaException {
        List<Movimiento> resultado = new ArrayList<>();
        try (RandomAccessFile raf = new RandomAccessFile(nomArchivo, "r")) {
            archivo = raf;
            long total = raf.length() / TAM_REGISTRO;
            for (long i = 0; i < total; i++) {
                Movimiento m = lee();
                if (m.getIdMovimiento() != null && !m.getIdMovimiento().isEmpty()) {
                    resultado.add(m);
                }
            }
        } catch (IOException e) {
            throw new PersistenciaException("Error al leer los movimientos", e);
        } finally {
            archivo = null;
        }
        return resultado;
    }

    public Movimiento obten(String idMovimiento) throws PersistenciaException {
        for (Movimiento m : lista()) {
            if (m.getIdMovimiento().equals(idMovimiento)) {
                return m;
            }
        }
        return null;
    }

    public List<Movimiento> listaPorPasajero(String idPasajero) throws PersistenciaException {
        return lista().stream().filter(m -> idPasajero.equals(m.getIdPasajero())).collect(Collectors.toList());
    }
}
