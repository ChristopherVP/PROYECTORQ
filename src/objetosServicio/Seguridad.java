package objetosServicio;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.text.Normalizer;
import java.util.Base64;
import java.util.Locale;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Hash de contraseñas y respuestas de seguridad con PBKDF2-HMAC-SHA256 (solo JDK, sin librerías).
 * Formato almacenado: iteraciones$salBase64$hashBase64 (76 caracteres con los valores actuales).
 */
public final class Seguridad {
    private static final int ITERACIONES = 100_000;
    private static final int BYTES_SAL = 16;
    private static final int BITS_HASH = 256;
    private static final SecureRandom ALEATORIO = new SecureRandom();

    private Seguridad() {
    }

    public static String hash(String texto) {
        if (texto == null) {
            throw new IllegalArgumentException("El texto a proteger no puede ser nulo");
        }
        byte[] sal = new byte[BYTES_SAL];
        ALEATORIO.nextBytes(sal);
        byte[] derivado = derivar(texto, sal, ITERACIONES);
        return ITERACIONES + "$" + Base64.getEncoder().encodeToString(sal) + "$" + Base64.getEncoder().encodeToString(derivado);
    }

    public static boolean verificar(String texto, String almacenado) {
        if (texto == null || !esHash(almacenado)) {
            return false;
        }
        try {
            String[] partes = almacenado.split("\\$");
            int iteraciones = Integer.parseInt(partes[0]);
            if (iteraciones < 1_000 || iteraciones > 5_000_000) {
                return false;
            }
            byte[] sal = Base64.getDecoder().decode(partes[1]);
            byte[] esperado = Base64.getDecoder().decode(partes[2]);
            return MessageDigest.isEqual(esperado, derivar(texto, sal, iteraciones));
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static boolean esHash(String valor) {
        return valor != null && valor.matches("\\d{4,7}\\$[A-Za-z0-9+/=]+\\$[A-Za-z0-9+/=]+");
    }

    /** Normaliza la respuesta de seguridad: sin tildes, minúsculas y espacios simples. */
    public static String normalizarRespuesta(String respuesta) {
        if (respuesta == null) {
            return "";
        }
        String sinTildes = Normalizer.normalize(respuesta, Normalizer.Form.NFD).replaceAll("\\p{M}+", "");
        return sinTildes.toLowerCase(Locale.ROOT).trim().replaceAll("\\s+", " ");
    }

    private static byte[] derivar(String texto, byte[] sal, int iteraciones) {
        PBEKeySpec spec = new PBEKeySpec(texto.toCharArray(), sal, iteraciones, BITS_HASH);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("PBKDF2 no disponible en esta JVM", e);
        } finally {
            spec.clearPassword();
        }
    }

    static byte[] bytes(String s) {
        return s.getBytes(StandardCharsets.UTF_8);
    }
}
