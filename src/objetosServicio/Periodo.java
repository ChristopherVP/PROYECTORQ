//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package objetosServicio;

public class Periodo {
    private objetosServicio.Fecha fechaInicio;
    private objetosServicio.Fecha fechaFin;

    public Periodo(objetosServicio.Fecha fechaInicio, objetosServicio.Fecha fechaFin) {
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    public boolean contiene(objetosServicio.Fecha fecha) {
        if (fecha != null && this.fechaInicio != null && this.fechaFin != null) {
            if (fecha.getAnio() >= this.fechaInicio.getAnio() && fecha.getAnio() <= this.fechaFin.getAnio()) {
                if (fecha.getAnio() == this.fechaInicio.getAnio()) {
                    if (fecha.getMes() < this.fechaInicio.getMes()) {
                        return false;
                    }

                    if (fecha.getMes() == this.fechaInicio.getMes() && fecha.getDia() < this.fechaInicio.getDia()) {
                        return false;
                    }
                }

                if (fecha.getAnio() == this.fechaFin.getAnio()) {
                    if (fecha.getMes() > this.fechaFin.getMes()) {
                        return false;
                    }

                    if (fecha.getMes() == this.fechaFin.getMes() && fecha.getDia() > this.fechaFin.getDia()) {
                        return false;
                    }
                }

                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public objetosServicio.Fecha getFechaInicio() {
        return this.fechaInicio;
    }

    public void setFechaInicio(objetosServicio.Fecha fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public objetosServicio.Fecha getFechaFin() {
        return this.fechaFin;
    }

    public void setFechaFin(objetosServicio.Fecha fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String toString() {
        String var10000 = String.valueOf(this.fechaInicio);
        return var10000 + " - " + String.valueOf(this.fechaFin);
    }
}
