package persistencia;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import objetosServicio.Fecha;

public class AccesoAleatorio {
    protected RandomAccessFile archivo;
    protected String nomArchivo;
    protected int tamRegistro;
    protected byte[] blancos;

    public AccesoAleatorio(String nomArchivo, int tamRegistro) {
        this.nomArchivo = nomArchivo;
        this.tamRegistro = tamRegistro;
        this.blancos = new byte[tamRegistro];

        for(int i = 0; i < tamRegistro; ++i) {
            this.blancos[i] = 0;
        }

        this.inicializaArchivo();
    }

    protected void inicializaArchivo() {
        File file = new File(this.nomArchivo);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException var3) {
                System.err.println("No se puede crear el archivo: " + this.nomArchivo);
            }
        }

    }

    public String leeString(int tam) throws IOException {
        char[] cadena = new char[tam];

        for(int i = 0; i < tam; ++i) {
            cadena[i] = this.archivo.readChar();
        }

        String sCadena = new String(cadena);
        sCadena = sCadena.replace('\u0000', ' ');
        sCadena = sCadena.trim();
        return sCadena;
    }

    public Fecha leeFecha() throws IOException {
        int dia = this.archivo.readInt();
        int mes = this.archivo.readInt();
        int anho = this.archivo.readInt();
        return new Fecha(dia, mes, anho);
    }

    public void escribeString(String sCadena, int tam) throws IOException {
        StringBuffer cadena;
        if (sCadena != null) {
            cadena = new StringBuffer(sCadena);
        } else {
            cadena = new StringBuffer(tam);
        }

        cadena.setLength(tam);
        this.archivo.writeChars(cadena.toString());
    }

    public void escribeFecha(Fecha fecha) throws IOException {
        if (fecha != null) {
            this.archivo.writeInt(fecha.getDia());
            this.archivo.writeInt(fecha.getMes());
            this.archivo.writeInt(fecha.getAnio());
        } else {
            this.archivo.writeInt(0);
            this.archivo.writeInt(0);
            this.archivo.writeInt(0);
        }

    }

    public void borraRegistro() throws IOException {
        this.archivo.write(this.blancos);
    }

    public boolean estaRegistroBorrado(byte[] registro) {
        for(int i = 0; i < this.tamRegistro; ++i) {
            if (registro[i] != 0) {
                return false;
            }
        }

        return true;
    }

    public void empaca() throws IOException {
        byte[] registro = new byte[this.tamRegistro];
        int registrosBorrados = 0;
        int numRegistros = (int)this.archivo.length() / this.tamRegistro;

        for(int i = 0; i < numRegistros; ++i) {
            this.archivo.seek((long)(i * this.tamRegistro));
            this.archivo.read(registro);
            if (this.estaRegistroBorrado(registro)) {
                for(int j = i; j < numRegistros - 1; ++j) {
                    this.archivo.seek((long)((j + 1) * this.tamRegistro));
                    this.archivo.read(registro);
                    this.archivo.seek((long)(j * this.tamRegistro));
                    this.archivo.write(registro);
                }

                --numRegistros;
                ++registrosBorrados;
                --i;
            }
        }

        this.archivo.setLength(this.archivo.length() - (long)(registrosBorrados * this.tamRegistro));
    }
}
