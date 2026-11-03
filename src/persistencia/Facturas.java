//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package persistencia;

import excepciones.PersistenciaException;
import excepciones.Validador;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import objetosNegocio.Empresa;
import objetosNegocio.Factura;
import objetosNegocio.Pasajero;
import objetosNegocio.Reserva;

public class Facturas extends AccesoAleatorio {
    public Facturas(String nomArchivo) {
        super(nomArchivo, 630);
    }

    private Factura leeFactura() throws IOException {
        Factura factura = new Factura();
        factura.setIdFactura(this.leeString(10));
        factura.setPasajero(new Pasajero(this.leeString(10)));
        factura.setReserva(new Reserva(this.leeString(10)));
        factura.setTotal(this.archivo.readDouble());
        factura.setFechaEmision(this.leeFecha());
        factura.setEstado(this.leeString(20));
        factura.setEmpresa(new Empresa(this.leeString(10)));
        factura.setNombreEmpresaSnapshot(this.leeString(50));
        factura.setRucEmpresaSnapshot(this.leeString(15));
        factura.setDireccionEmpresaSnapshot(this.leeString(80));
        factura.setNombreVendedor(this.leeString(50));
        factura.setDatosPasajeroSnapshot(this.leeString(50));
        return factura;
    }

    private void escribeFactura(Factura factura) throws IOException {
        this.escribeString(factura.getIdFactura(), 10);
        String idPasajero = factura.getPasajero() != null ? factura.getPasajero().getIdPasajero() : "";
        String idReserva = factura.getReserva() != null ? factura.getReserva().getIdReserva() : "";
        this.escribeString(idPasajero, 10);
        this.escribeString(idReserva, 10);
        this.archivo.writeDouble(factura.getTotal());
        this.escribeFecha(factura.getFechaEmision());
        this.escribeString(factura.getEstado(), 20);
        String idEmpresa = factura.getEmpresa() != null ? factura.getEmpresa().getIdEmpresa() : "";
        this.escribeString(idEmpresa, 10);
        this.escribeString(factura.getNombreEmpresaSnapshot(), 50);
        this.escribeString(factura.getRucEmpresaSnapshot(), 15);
        this.escribeString(factura.getDireccionEmpresaSnapshot(), 80);
        this.escribeString(factura.getNombreVendedor(), 50);
        this.escribeString(factura.getDatosPasajeroSnapshot(), 50);
    }

    private boolean esFacturaValida(Factura factura) {
        return factura != null && factura.getIdFactura() != null && !factura.getIdFactura().trim().isEmpty();
    }

    private String generarSiguienteCodigoFactura() throws PersistenciaException {
        try {
            this.archivo = new RandomAccessFile(this.nomArchivo, "r");
            int maxNumero = 0;

            try {
                while(true) {
                    Factura factura = this.leeFactura();
                    if (this.esFacturaValida(factura)) {
                        String codigo = factura.getIdFactura();
                        if (codigo.startsWith("FAC") && codigo.length() > 3) {
                            try {
                                int numero = Integer.parseInt(codigo.substring(3));
                                if (numero > maxNumero) {
                                    maxNumero = numero;
                                }
                            } catch (NumberFormatException var5) {
                            }
                        }
                    }
                }
            } catch (EOFException var6) {
                this.archivo.close();
                int siguienteNumero = maxNumero + 1;
                return String.format("FAC%03d", siguienteNumero);
            }
        } catch (FileNotFoundException var7) {
            return "FAC001";
        } catch (IOException e) {
            throw new PersistenciaException("Error al generar código de factura", e);
        }
    }

    private void validarFactura(Factura factura) throws PersistenciaException {
        if (!Validador.validarNumeroPositivo(factura.getTotal())) {
            throw new PersistenciaException(Validador.getMensajeErrorNumeroPositivo("El total"));
        } else if (!Validador.validarNoVacio(factura.getEstado())) {
            throw new PersistenciaException("El estado no puede estar vacío");
        } else if (factura.getPasajero() != null && Validador.validarNoVacio(factura.getPasajero().getIdPasajero())) {
            if (factura.getReserva() != null && Validador.validarNoVacio(factura.getReserva().getIdReserva())) {
                if (factura.getEmpresa() == null || !Validador.validarNoVacio(factura.getEmpresa().getIdEmpresa())) {
                    throw new PersistenciaException("Debe asignar una empresa a la factura");
                }
            } else {
                throw new PersistenciaException("Debe asignar una reserva a la factura");
            }
        } else {
            throw new PersistenciaException("Debe asignar un pasajero a la factura");
        }
    }

    public Factura obten(Factura factura) throws PersistenciaException {
        Factura var3;
        try {
            this.archivo = new RandomAccessFile(this.nomArchivo, "r");

            Factura facturaLeida;
            do {
                facturaLeida = this.leeFactura();
            } while(!this.esFacturaValida(facturaLeida) || !facturaLeida.getIdFactura().equals(factura.getIdFactura()));

            var3 = facturaLeida;
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

    public void agrega(Factura factura) throws PersistenciaException {
        String codigoGenerado = this.generarSiguienteCodigoFactura();
        factura.setIdFactura(codigoGenerado);
        this.validarFactura(factura);

        try {
            this.archivo = new RandomAccessFile(this.nomArchivo, "rw");
            this.archivo.seek(this.archivo.length());
            this.escribeFactura(factura);
            System.out.println("Factura agregada: " + factura.getIdFactura());
        } catch (FileNotFoundException var11) {
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

    public void actualiza(Factura factura) throws PersistenciaException {
        this.validarFactura(factura);
        boolean encontrada = false;

        try {
            this.archivo = new RandomAccessFile(this.nomArchivo, "rw");

            long posicionAntes;
            Factura facturaLeida;
            do {
                posicionAntes = this.archivo.getFilePointer();
                facturaLeida = this.leeFactura();
            } while(!this.esFacturaValida(facturaLeida) || !facturaLeida.getIdFactura().equals(factura.getIdFactura()));

            this.archivo.seek(posicionAntes);
            this.escribeFactura(factura);
            System.out.println("✓ Factura actualizada: " + factura.getIdFactura());
            encontrada = true;
        } catch (FileNotFoundException var16) {
            throw new PersistenciaException("Archivo inexistente");
        } catch (EOFException var17) {
            if (!encontrada) {
                throw new PersistenciaException("La factura no existe");
            }
        } catch (IOException e) {
            throw new PersistenciaException("Error al acceder al archivo", e);
        } finally {
            try {
                if (this.archivo != null) {
                    this.archivo.close();
                }
            } catch (IOException e) {
                throw new PersistenciaException("Error al cerrar", e);
            }

        }

        if (!encontrada) {
            throw new PersistenciaException("La factura no existe");
        }
    }

    public void elimina(Factura factura) throws PersistenciaException {
        File archivoOriginal = new File(this.nomArchivo);
        File archivoTemporal = new File(this.nomArchivo + ".tmp");

        try {
            System.out.println("\n=== ELIMINANDO FACTURA: " + factura.getIdFactura() + " ===");
            this.archivo = new RandomAccessFile(archivoOriginal, "r");
            List<Factura> todasLasFacturas = new ArrayList();
            boolean encontrada = false;

            try {
                while(true) {
                    Factura f = this.leeFactura();
                    if (this.esFacturaValida(f)) {
                        if (!f.getIdFactura().equals(factura.getIdFactura())) {
                            todasLasFacturas.add(f);
                            System.out.println("  → Mantener: " + f.getIdFactura());
                        } else {
                            encontrada = true;
                            System.out.println("  ✗ Eliminar: " + f.getIdFactura());
                        }
                    }
                }
            } catch (EOFException var17) {
                this.archivo.close();
                this.archivo = null;
                if (!encontrada) {
                    throw new PersistenciaException("La factura no existe");
                }
            }

            System.out.println("Total a mantener: " + todasLasFacturas.size());
            this.archivo = new RandomAccessFile(archivoTemporal, "rw");

            for(Factura f : todasLasFacturas) {
                this.escribeFactura(f);
            }

            this.archivo.close();
            this.archivo = null;
            if (!archivoOriginal.delete()) {
                throw new PersistenciaException("No se pudo eliminar el archivo original");
            }

            if (!archivoTemporal.renameTo(archivoOriginal)) {
                throw new PersistenciaException("No se pudo renombrar el archivo temporal");
            }

            System.out.println("✓ Factura eliminada exitosamente");
            System.out.println("===================================\n");
        } catch (FileNotFoundException var18) {
            throw new PersistenciaException("Archivo inexistente");
        } catch (IOException e) {
            throw new PersistenciaException("Error: " + e.getMessage(), e);
        } finally {
            try {
                if (this.archivo != null) {
                    this.archivo.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (archivoTemporal.exists()) {
                archivoTemporal.delete();
            }

        }

    }

    public List<Factura> lista() throws PersistenciaException {
        List<Factura> lista = new ArrayList();

        Object var3;
        try {
            this.archivo = new RandomAccessFile(this.nomArchivo, "r");

            while(true) {
                Factura facturaLeida = this.leeFactura();
                if (this.esFacturaValida(facturaLeida)) {
                    lista.add(facturaLeida);
                }
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
                throw new PersistenciaException("Error al cerrar", e);
            }

        }

        return (List<Factura>)var3;
    }

    private static boolean esFacturaDePasajero(Factura factura, String idPasajero) {
        return factura.getPasajero() != null && idPasajero.equals(factura.getPasajero().getIdPasajero());
    }

    private static boolean esFacturaConEstado(Factura factura, String estado) {
        return estado.equals(factura.getEstado());
    }

    public List<Factura> listaPorPasajero(String idPasajero) throws PersistenciaException {
        return (List)this.lista().stream().filter((factura) -> esFacturaDePasajero(factura, idPasajero)).collect(Collectors.toList());
    }

    public List<Factura> listaPorEstado(String estado) throws PersistenciaException {
        return (List)this.lista().stream().filter((factura) -> esFacturaConEstado(factura, estado)).collect(Collectors.toList());
    }
}
