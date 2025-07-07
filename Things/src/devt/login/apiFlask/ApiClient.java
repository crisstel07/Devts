
package devt.login.apiFlask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import com.google.gson.Gson; // Asegúrate de que Gson esté importado correctamente
import com.google.gson.JsonObject; // Necesario para construir JSON
 
public class ApiClient {

    private static final String API_BASE_URL = "http://localhost:5000/api/";
    private static final Gson gson = new Gson();

    // Clase interna para representar la respuesta JSON de la API
    public static class ApiResponse {
        public boolean success;
        public String message;
        public Integer user_id; // Campo para el ID de usuario en registro y otros
        public JsonObject user; // Campo para el objeto 'user' en la respuesta de login
    }

    /**
     * Registra un nuevo usuario en la API Flask.
     * @param nombreUsuario Nombre de usuario.
     * @param correo Correo electrónico del usuario.
     * @param password Contraseña del usuario.
     * @return Objeto ApiResponse con el resultado.
     */
    public static ApiResponse registerUser(String nombreUsuario, String correo, String password) {
        try {
            URL url = new URL(API_BASE_URL + "register");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true); // Indica que vamos a escribir en el cuerpo de la petición

            // Construir el cuerpo JSON de la petición
            JsonObject jsonInput = new JsonObject();
            jsonInput.addProperty("nombre_usuario", nombreUsuario);
            jsonInput.addProperty("correo", correo);
            jsonInput.addProperty("password", password);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInput.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            StringBuilder response = new StringBuilder();
            
            // Si la respuesta es un error (4xx o 5xx), leemos el InputStream de error
            // De lo contrario, leemos el InputStream normal
            InputStreamReader isr;
            if (responseCode >= 200 && responseCode <= 299) {
                isr = new InputStreamReader(conn.getInputStream(), "utf-8");
            } else {
                isr = new InputStreamReader(conn.getErrorStream(), "utf-8");
            }

            try (BufferedReader br = new BufferedReader(isr)) {
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            }

            System.out.println("Respuesta del servidor /register (" + responseCode + "): " + response.toString());

            // Parsear la respuesta JSON
            ApiResponse apiResponse = gson.fromJson(response.toString(), ApiResponse.class);
            return apiResponse;

        } catch (Exception e) {
            System.err.println("Error al registrar usuario: " + e.getMessage());
            // Manejo de errores: Retornar un ApiResponse con éxito=false y un mensaje de error
            ApiResponse errorResponse = new ApiResponse();
            errorResponse.success = false;
            errorResponse.message = "Error de conexión o de red: " + e.getMessage();
            return errorResponse;
        }
    }

    /**
     * Verifica la cuenta de un usuario con un código.
     * @param userId ID del usuario.
     * @param code Código de verificación recibido por correo.
     * @return Objeto ApiResponse con el resultado.
     */
    public static ApiResponse verifyUser(int userId, String code) {
        try {
            URL url = new URL(API_BASE_URL + "verify");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            // Construir el cuerpo JSON de la petición
            JsonObject jsonInput = new JsonObject();
            jsonInput.addProperty("user_id", userId);
            jsonInput.addProperty("code", code);

            // Linea de depuracion 
            System.out.println("ApiClient: JSON enviado a /verify: " + jsonInput.toString());
            
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInput.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            StringBuilder response = new StringBuilder();
            
            InputStreamReader isr;
            if (responseCode >= 200 && responseCode <= 299) {
                isr = new InputStreamReader(conn.getInputStream(), "utf-8");
            } else {
                isr = new InputStreamReader(conn.getErrorStream(), "utf-8");
            }

            try (BufferedReader br = new BufferedReader(isr)) {
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            }

            System.out.println("Respuesta del servidor /verify (" + responseCode + "): " + response.toString());
            ApiResponse apiResponse = gson.fromJson(response.toString(), ApiResponse.class);
            return apiResponse;

        } catch (Exception e) {
            System.err.println("Error al verificar usuario: " + e.getMessage());
            ApiResponse errorResponse = new ApiResponse();
            errorResponse.success = false;
            errorResponse.message = "Error de conexión o de red durante la verificación: " + e.getMessage();
            return errorResponse;
        }
    }

    /**
     * Inicia sesión para un usuario.
     * @param correo Correo electrónico del usuario.
     * @param password Contraseña del usuario.
     * @return Objeto ApiResponse con el resultado.
     */
    public static ApiResponse loginUser(String correo, String password) {
        try {
            URL url = new URL(API_BASE_URL + "login");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            // Construir el cuerpo JSON de la petición
            JsonObject jsonInput = new JsonObject();
            jsonInput.addProperty("correo", correo);
            jsonInput.addProperty("password", password);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInput.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            StringBuilder response = new StringBuilder();
            
            InputStreamReader isr;
            if (responseCode >= 200 && responseCode <= 299) {
                isr = new InputStreamReader(conn.getInputStream(), "utf-8");
            } else {
                isr = new InputStreamReader(conn.getErrorStream(), "utf-8");
            }

            try (BufferedReader br = new BufferedReader(isr)) {
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            }

            System.out.println("Respuesta del servidor /login (" + responseCode + "): " + response.toString());
            ApiResponse apiResponse = gson.fromJson(response.toString(), ApiResponse.class);
            return apiResponse;

        } catch (Exception e) {
            System.err.println("Error al iniciar sesión: " + e.getMessage());
            ApiResponse errorResponse = new ApiResponse();
            errorResponse.success = false;
            errorResponse.message = "Error de conexión o de red durante el inicio de sesión: " + e.getMessage();
            return errorResponse;
        }
    }

    public static void main(String[] args) {
        // --- EJEMPLO DE USO ---
        // Asegúrate de que tu API Flask esté corriendo en http://localhost:5000/

        // Paso 1: Intentar registrar un nuevo usuario
        System.out.println("--- Intentando registrar un nuevo usuario ---");
        // ¡CAMBIA ESTOS DATOS por unos NUEVOS y ÚNICOS para cada prueba completa!
        // Usa un correo de mailinator.com para ver fácilmente los códigos.
        String testUsername = "Rodrigo";
        String testEmail = "javatestcristel123@mailinator.com";
        String testPassword = "UnaContraseña";

        ApiResponse registerResult = registerUser(testUsername, testEmail, testPassword);

        if (registerResult.success) {
            System.out.println("Registro exitoso: " + registerResult.message);
            System.out.println("User ID: " + registerResult.user_id);

            // Paso 2: Si el registro es exitoso, procede a la verificación
            // ¡IMPORTANTE! Deberás obtener el código de verificación del correo REAL que se envió.
            // Para esta prueba, reemplaza "CODIGO_REAL" con el código que recibiste.
            if (registerResult.user_id != null) {
                String verificationCode = "CODIGO_REAL"; // <-- ¡REEMPLAZA ESTO!
                System.out.println("\n--- Intentando verificar el usuario ---");
                ApiResponse verifyResult = verifyUser(registerResult.user_id, verificationCode);

                if (verifyResult.success) {
                    System.out.println("Verificación exitosa: " + verifyResult.message);

                    // Paso 3: Si la verificación es exitosa, procede al login
                    System.out.println("\n--- Intentando iniciar sesión ---");
                    ApiResponse loginResult = loginUser(testEmail, testPassword); // Usa el correo y password del registro
                    if (loginResult.success) {
                        System.out.println("Login exitoso: " + loginResult.message);
                        System.out.println("Datos del usuario: " + loginResult.user.toString());
                    } else {
                        System.err.println("Login fallido: " + loginResult.message);
                    }
                } else {
                    System.err.println("Verificación fallida: " + verifyResult.message);
                }
            } else {
                System.err.println("No se pudo obtener User ID para la verificación.");
            }
        } else {
            System.err.println("Registro fallido: " + registerResult.message);
        }
    }
}
