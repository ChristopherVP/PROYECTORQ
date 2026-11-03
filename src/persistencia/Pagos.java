//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package persistencia;

import excepciones.PersistenciaException;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import objetosNegocio.Factura;
import objetosNegocio.Pago;
import objetosNegocio.Usuario;

public class Pagos extends AccesoAleatorio {
    public Pagos(String nomArchivo) {
        super(nomArchivo, 200);
    }

    private Pago leePago() throws IOException {
        Pago pago = new Pago();
        pago.setIdPago(this.leeString(10));
        pago.setUsuario(new Usuario(this.leeString(10)));
        pago.setFactura(new Factura(this.leeString(10)));
        pago.setMonto(this.archivo.readDouble());
        pago.setFechaPago(this.leeFecha());
        pago.setMetodo(this.leeString(20));
        return pago;
    }

    private void escribePago(Pago pago) throws IOException {
        this.escribeString(pago.getIdPago(), 10);
        String idUsuario = pago.getUsuario() != null ? pago.getUsuario().getIdUsuario() : "";
        String idFactura = pago.getFactura() != null ? pago.getFactura().getIdFactura() : "";
        this.escribeString(idUsuario, 10);
        this.escribeString(idFactura, 10);
        this.archivo.writeDouble(pago.getMonto());
        this.escribeFecha(pago.getFechaPago());
        this.escribeString(pago.getMetodo(), 20);
    }

    public Pago obten(Pago pago) throws PersistenciaException {
        Pago var3;
        try {
            this.archivo = new RandomAccessFile(this.nomArchivo, "r");

            Pago pagoLeido;
            do {
                pagoLeido = this.leePago();
            } while(!pago.equals(pagoLeido));

            var3 = pagoLeido;
            return var3;
        } catch (FileNotFoundException var15) {
            throw new PersistenciaException("Archivo inexistente");
        } catch (EOFException var16) {
            var3 = null;
        } catch (IOException e) {
            throw new PersistenciaException("Error al acceder al archivo", e);
        } finally {
            try {
                if (this.archivo != null) {
                    this.archivo.close();
                }
            } catch (IOException e) {
                throw new PersistenciaException("Error al cerrar el archivo", e);
            }

        }

        return var3;
    }

    public void agrega(Pago pago) throws PersistenciaException {
        try {
            this.archivo = new RandomAccessFile(this.nomArchivo, "rw");
            this.archivo.seek(this.archivo.length());
            this.escribePago(pago);
        } catch (FileNotFoundException var10) {
            throw new PersistenciaException("Archivo inexistente");
        } catch (IOException e) {
            throw new PersistenciaException("Error al acceder al archivo", e);
        } finally {
            try {
                if (this.archivo != null) {
                    this.archivo.close();
                }
            } catch (IOException e) {
                throw new PersistenciaException("Error al cerrar el archivo", e);
            }

        }

    }

    public void actualiza(Pago pago) throws PersistenciaException {
        try {
            this.archivo = new RandomAccessFile(this.nomArchivo, "rw");

            Pago pagoLeido;
            do {
                pagoLeido = this.leePago();
            } while(!pago.equals(pagoLeido));

            this.archivo.seek(this.archivo.getFilePointer() - (long)this.tamRegistro);
            this.escribePago(pago);
        } catch (FileNotFoundException var12) {
            throw new PersistenciaException("Archivo inexistente");
        } catch (EOFException var13) {
            throw new PersistenciaException("El pago no existe");
        } catch (IOException e) {
            throw new PersistenciaException("Error al acceder al archivo", e);
        } finally {
            try {
                if (this.archivo != null) {
                    this.archivo.close();
                }
            } catch (IOException e) {
                throw new PersistenciaException("Error al cerrar el archivo", e);
            }

        }

    }

    public void elimina(Pago pago) throws PersistenciaException {
        try {
            this.archivo = new RandomAccessFile(this.nomArchivo, "rw");

            Pago pagoLeido;
            do {
                pagoLeido = this.leePago();
            } while(!pago.equals(pagoLeido));

            this.archivo.seek(this.archivo.getFilePointer() - (long)this.tamRegistro);
            this.borraRegistro();
            this.empaca();
        } catch (FileNotFoundException var12) {
            throw new PersistenciaException("Archivo inexistente");
        } catch (EOFException var13) {
            throw new PersistenciaException("El pago no existe");
        } catch (IOException e) {
            throw new PersistenciaException("Error al acceder al archivo", e);
        } finally {
            try {
                if (this.archivo != null) {
                    this.archivo.close();
                }
            } catch (IOException e) {
                throw new PersistenciaException("Error al cerrar el archivo", e);
            }

        }

    }

    public List<Pago> lista() throws PersistenciaException {
        List<Pago> lista = new ArrayList();

        Object var3;
        try {
            this.archivo = new RandomAccessFile(this.nomArchivo, "r");

            while(true) {
                lista.add(this.leePago());
            }
        } catch (FileNotFoundException var14) {
            throw new PersistenciaException("Archivo inexistente");
        } catch (EOFException var15) {
            var3 = lista;
        } catch (IOException e) {
            throw new PersistenciaException("Error al acceder al archivo", e);
        } finally {
            try {
                if (this.archivo != null) {
                    this.archivo.close();
                }
            } catch (IOException e) {
                throw new PersistenciaException("Error al cerrar el archivo", e);
            }

        }

        return (List<Pago>)var3;
    }

    private static boolean esPagoDeUsuario(Pago pago, String idUsuario) {
        return pago.getUsuario() != null && idUsuario.equals(pago.getUsuario().getIdUsuario());
    }

    private static boolean esPagoConMetodo(Pago pago, String metodo) {
        return metodo.equals(pago.getMetodo());
    }

    public List<Pago> listaPorUsuario(String idUsuario) throws PersistenciaException {
        return (List)this.lista().stream().filter((pago) -> esPagoDeUsuario(pago, idUsuario)).collect(Collectors.toList());
    }

    public List<Pago> listaPorMetodo(String metodo) throws PersistenciaException {
        return (List)this.lista().stream().filter((pago) -> esPagoConMetodo(pago, metodo)).collect(Collectors.toList());
    }
}
