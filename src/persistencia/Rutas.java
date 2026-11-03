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
import objetosNegocio.Ruta;

public class Rutas extends AccesoAleatorio {
    public Rutas(String nomArchivo) {
        super(nomArchivo, 708);
    }

    private Ruta leeRuta() throws IOException {
        Ruta ruta = new Ruta();
        ruta.setIdRuta(this.leeString(10));
        ruta.setOrigen(this.leeString(50));
        ruta.setDestino(this.leeString(50));
        ruta.setDistancia(this.archivo.readDouble());
        ruta.setPrecio(this.archivo.readDouble());
        ruta.setEmpresa(new Empresa(this.leeString(10)));
        ruta.setTiempoEstimado(this.archivo.readDouble());
        ruta.setParadas(this.leeString(100));
        ruta.setTiempoDetencion(this.archivo.readInt());
        ruta.setAdvertencias(this.leeString(100));
        ruta.setEstado(this.leeString(20));
        return ruta;
    }

    private void escribeRuta(Ruta ruta) throws IOException {
        this.escribeString(ruta.getIdRuta(), 10);
        this.escribeString(ruta.getOrigen(), 50);
        this.escribeString(ruta.getDestino(), 50);
        this.archivo.writeDouble(ruta.getDistancia());
        this.archivo.writeDouble(ruta.getPrecio());
        String idEmpresa = ruta.getEmpresa() != null ? ruta.getEmpresa().getIdEmpresa() : "";
        this.escribeString(idEmpresa, 10);
        this.archivo.writeDouble(ruta.getTiempoEstimado());
        this.escribeString(ruta.getParadas() != null ? ruta.getParadas() : "", 100);
        this.archivo.writeInt(ruta.getTiempoDetencion());
        this.escribeString(ruta.getAdvertencias() != null ? ruta.getAdvertencias() : "", 100);
        this.escribeString(ruta.getEstado() != null ? ruta.getEstado() : "ACTIVA", 20);
    }

    private boolean esRutaValida(Ruta ruta) {
        return ruta != null && ruta.getIdRuta() != null && !ruta.getIdRuta().trim().isEmpty();
    }

    private String generarSiguienteCodigoRuta() throws PersistenciaException {
        try {
            this.archivo = new RandomAccessFile(this.nomArchivo, "r");
            int maxNumero = 0;

            try {
                while(true) {
                    Ruta ruta = this.leeRuta();
                    if (this.esRutaValida(ruta)) {
                        String codigo = ruta.getIdRuta();
                        if (codigo.startsWith("RUTA") && codigo.length() > 4) {
                            try {
                                int numero = Integer.parseInt(codigo.substring(4));
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
                return String.format("RUTA%03d", siguienteNumero);
            }
        } catch (FileNotFoundException var7) {
            return "RUTA001";
        } catch (IOException e) {
            throw new PersistenciaException("Error al generar código de ruta", e);
        }
    }

    private void validarRuta(Ruta ruta) throws PersistenciaException {
        if (!Validador.validarNoVacio(ruta.getOrigen())) {
            throw new PersistenciaException("El origen no puede estar vacío");
        } else if (!Validador.validarNoVacio(ruta.getDestino())) {
            throw new PersistenciaException("El destino no puede estar vacío");
        } else if (ruta.getOrigen().trim().equalsIgnoreCase(ruta.getDestino().trim())) {
            throw new PersistenciaException("El origen y destino deben ser diferentes");
        } else if (!Validador.validarNumeroPositivo(ruta.getDistancia())) {
            throw new PersistenciaException(Validador.getMensajeErrorNumeroPositivo("La distancia"));
        } else if (!Validador.validarNumeroPositivo(ruta.getPrecio())) {
            throw new PersistenciaException(Validador.getMensajeErrorNumeroPositivo("El precio"));
        } else if (ruta.getEmpresa() == null || !Validador.validarNoVacio(ruta.getEmpresa().getIdEmpresa())) {
            throw new PersistenciaException("Debe asignar una empresa a la ruta");
        }
    }

    public Ruta obten(Ruta ruta) throws PersistenciaException {
        Ruta var3;
        try {
            this.archivo = new RandomAccessFile(this.nomArchivo, "r");

            Ruta rutaLeida;
            do {
                rutaLeida = this.leeRuta();
            } while(!this.esRutaValida(rutaLeida) || !rutaLeida.getIdRuta().equals(ruta.getIdRuta()));

            var3 = rutaLeida;
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

    public void agrega(Ruta ruta) throws PersistenciaException {
        String codigoGenerado = this.generarSiguienteCodigoRuta();
        ruta.setIdRuta(codigoGenerado);
        this.validarRuta(ruta);

        try {
            this.archivo = new RandomAccessFile(this.nomArchivo, "rw");
            this.archivo.seek(this.archivo.length());
            this.escribeRuta(ruta);
            System.out.println("Ruta agregrada: " + ruta.getIdRuta());
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

    public void actualiza(Ruta ruta) throws PersistenciaException {
        this.validarRuta(ruta);
        boolean encontrada = false;

        try {
            this.archivo = new RandomAccessFile(this.nomArchivo, "rw");

            long posicionAntes;
            Ruta rutaLeida;
            do {
                posicionAntes = this.archivo.getFilePointer();
                rutaLeida = this.leeRuta();
            } while(!this.esRutaValida(rutaLeida) || !rutaLeida.getIdRuta().equals(ruta.getIdRuta()));

            this.archivo.seek(posicionAntes);
            this.escribeRuta(ruta);
            System.out.println("✓ Ruta actualizada: " + ruta.getIdRuta());
            encontrada = true;
        } catch (FileNotFoundException var16) {
            throw new PersistenciaException("Archivo inexistente");
        } catch (EOFException var17) {
            if (!encontrada) {
                throw new PersistenciaException("La ruta no existe");
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
            throw new PersistenciaException("La ruta no existe");
        }
    }

    public void elimina(Ruta ruta) throws PersistenciaException {
        File archivoOriginal = new File(this.nomArchivo);
        File archivoTemporal = new File(this.nomArchivo + ".tmp");

        try {
            System.out.println("\n=== ELIMINANDO RUTA: " + ruta.getIdRuta() + " ===");
            this.archivo = new RandomAccessFile(archivoOriginal, "r");
            List<Ruta> todasLasRutas = new ArrayList();
            boolean encontrada = false;

            try {
                while(true) {
                    Ruta r = this.leeRuta();
                    if (this.esRutaValida(r)) {
                        if (!r.getIdRuta().equals(ruta.getIdRuta())) {
                            todasLasRutas.add(r);
                            System.out.println("  → Mantener: " + r.getIdRuta());
                        } else {
                            encontrada = true;
                            System.out.println("  ✗ Eliminar: " + r.getIdRuta());
                        }
                    }
                }
            } catch (EOFException var17) {
                this.archivo.close();
                this.archivo = null;
                if (!encontrada) {
                    throw new PersistenciaException("La ruta no existe");
                }
            }

            System.out.println("Total a mantener: " + todasLasRutas.size());
            this.archivo = new RandomAccessFile(archivoTemporal, "rw");

            for(Ruta r : todasLasRutas) {
                this.escribeRuta(r);
            }

            this.archivo.close();
            this.archivo = null;
            if (!archivoOriginal.delete()) {
                throw new PersistenciaException("No se pudo eliminar el archivo original");
            }

            if (!archivoTemporal.renameTo(archivoOriginal)) {
                throw new PersistenciaException("No se pudo renombrar el archivo temporal");
            }

            System.out.println("✓ Ruta eliminada exitosamente");
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

    public List<Ruta> lista() throws PersistenciaException {
        List<Ruta> lista = new ArrayList();

        Object var3;
        try {
            this.archivo = new RandomAccessFile(this.nomArchivo, "r");

            while(true) {
                Ruta rutaLeida = this.leeRuta();
                if (this.esRutaValida(rutaLeida)) {
                    lista.add(rutaLeida);
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

        return (List<Ruta>)var3;
    }

    private static boolean esRutaConOrigen(Ruta ruta, String origen) {
        return origen.equals(ruta.getOrigen());
    }

    private static boolean esRutaConDestino(Ruta ruta, String destino) {
        return destino.equals(ruta.getDestino());
    }

    public List<Ruta> listaPorOrigen(String origen) throws PersistenciaException {
        return (List)this.lista().stream().filter((ruta) -> esRutaConOrigen(ruta, origen)).collect(Collectors.toList());
    }

    public List<Ruta> listaPorDestino(String destino) throws PersistenciaException {
        return (List)this.lista().stream().filter((ruta) -> esRutaConDestino(ruta, destino)).collect(Collectors.toList());
    }
}
