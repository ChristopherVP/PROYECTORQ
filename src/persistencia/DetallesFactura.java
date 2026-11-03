//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package persistencia;

import excepciones.PersistenciaException;
import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import objetosNegocio.DetalleFactura;

public class DetallesFactura extends AccesoAleatorio {
    public DetallesFactura(String nomArchivo) {
        super(nomArchivo, 220);
    }

    private DetalleFactura leeDetalle() throws IOException {
        DetalleFactura d = new DetalleFactura();
        d.setIdDetalle(this.leeString(10));
        d.setIdFactura(this.leeString(10));
        d.setDescripcion(this.leeString(80));
        d.setCantidad(this.archivo.readInt());
        d.setPrecioUnitario(this.archivo.readDouble());
        d.setSubtotal(this.archivo.readDouble());
        return d;
    }

    private void escribeDetalle(DetalleFactura d) throws IOException {
        this.escribeString(d.getIdDetalle(), 10);
        this.escribeString(d.getIdFactura(), 10);
        this.escribeString(d.getDescripcion(), 80);
        this.archivo.writeInt(d.getCantidad());
        this.archivo.writeDouble(d.getPrecioUnitario());
        this.archivo.writeDouble(d.getSubtotal());
    }

    private String generarSiguienteCodigo() throws IOException {
        long posOriginal = this.archivo.getFilePointer();
        this.archivo.seek(0L);
        int max = 0;

        try {
            while(true) {
                DetalleFactura d = this.leeDetalle();
                if (d.getIdDetalle().startsWith("DET")) {
                    try {
                        int num = Integer.parseInt(d.getIdDetalle().substring(3));
                        if (num > max) {
                            max = num;
                        }
                    } catch (NumberFormatException var6) {
                    }
                }
            }
        } catch (EOFException var7) {
            this.archivo.seek(posOriginal);
            return String.format("DET%05d", max + 1);
        }
    }

    public void agrega(DetalleFactura detalle) throws PersistenciaException {
        try {
            this.archivo = new RandomAccessFile(this.nomArchivo, "rw");
            if (detalle.getIdDetalle() == null || detalle.getIdDetalle().isEmpty()) {
                detalle.setIdDetalle(this.generarSiguienteCodigo());
            }

            this.archivo.seek(this.archivo.length());
            this.escribeDetalle(detalle);
        } catch (IOException e) {
            throw new PersistenciaException("Error al agregar detalle", e);
        } finally {
            try {
                if (this.archivo != null) {
                    this.archivo.close();
                }
            } catch (IOException var9) {
            }

        }

    }

    public List<DetalleFactura> listaPorFactura(String idFactura) throws PersistenciaException {
        List<DetalleFactura> lista = new ArrayList();

        Object var4;
        try {
            this.archivo = new RandomAccessFile(this.nomArchivo, "r");

            while(true) {
                DetalleFactura d = this.leeDetalle();
                if (d.getIdFactura().trim().equals(idFactura)) {
                    lista.add(d);
                }
            }
        } catch (EOFException var14) {
            var4 = lista;
        } catch (IOException e) {
            throw new PersistenciaException("Error al leer detalles", e);
        } finally {
            try {
                if (this.archivo != null) {
                    this.archivo.close();
                }
            } catch (IOException var13) {
            }

        }

        return (List<DetalleFactura>)var4;
    }

    public void eliminarPorFactura(String idFactura) throws PersistenciaException {
        try {
            this.archivo = new RandomAccessFile(this.nomArchivo, "rw");
            boolean huboEliminacion = false;

            try {
                while(true) {
                    long pos = this.archivo.getFilePointer();
                    DetalleFactura d = this.leeDetalle();
                    if (d.getIdFactura().trim().equals(idFactura)) {
                        this.archivo.seek(pos);
                        this.borraRegistro();
                        huboEliminacion = true;
                    }
                }
            } catch (EOFException var14) {
                if (huboEliminacion) {
                    this.empaca();
                }
            }
        } catch (IOException e) {
            throw new PersistenciaException("Error al eliminar detalles de la factura", e);
        } finally {
            try {
                if (this.archivo != null) {
                    this.archivo.close();
                }
            } catch (IOException var13) {
            }

        }

    }
}
