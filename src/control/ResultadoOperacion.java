//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package control;

public class ResultadoOperacion {
    private boolean exito;
    private String mensaje;
    private Object datos;

    public ResultadoOperacion(boolean exito, String mensaje) {
        this.exito = exito;
        this.mensaje = mensaje;
        this.datos = null;
    }

    public ResultadoOperacion(boolean exito, String mensaje, Object datos) {
        this.exito = exito;
        this.mensaje = mensaje;
        this.datos = datos;
    }

    public boolean isExito() {
        return this.exito;
    }

    public String getMensaje() {
        return this.mensaje;
    }

    public Object getDatos() {
        return this.datos;
    }

    public <T> T getDatosComoTipo(Class<T> tipo) {
        return (T)(this.datos != null && tipo.isInstance(this.datos) ? this.datos : null);
    }

    public boolean tieneExitoConDatos() {
        return this.exito && this.datos != null;
    }

    public String toString() {
        return String.format("ResultadoOperacion{exito=%s, mensaje='%s', datos=%s}", this.exito, this.mensaje, this.datos != null ? "presente" : "null");
    }
}
