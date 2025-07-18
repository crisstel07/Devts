package devt.login.apiFlask;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonElement;
import com.google.gson.JsonArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ApiClient {

    private static final String API_BASE_URL = "http://localhost:5000/api/";
    private static final Gson gson = new Gson();

    /**
     * Clase interna para representar la respuesta genérica de la API.
     * Ahora incluye el código de error HTTP.
     */
    public static class ApiResponse {
        private boolean success;
        private String message;
        private JsonElement data; // Un solo campo para todos los datos variables (user, character, inventory, etc.)
        private int errorCode; // Nuevo: Para almacenar el código de respuesta HTTP

        // Constructor para uso manual (ej. para errores de conexión)
        public ApiResponse(boolean success, String message, JsonElement data, int errorCode) {
            this.success = success;
            this.message = message;
            this.data = data;
            this.errorCode = errorCode;
        }

        // Getters para acceder a los campos
        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public JsonElement getData() {
            return data;
        }

        /**
         * Intenta obtener los datos como un JsonObject.
         * @return JsonObject si 'data' es un objeto JSON, de lo contrario null.
         */
        public JsonObject getDataAsJsonObject() {
            if (data != null && data.isJsonObject()) {
                return data.getAsJsonObject();
            }
            return null;
        }

        /**
         * Intenta obtener los datos como un JsonArray.
         * @return JsonArray si 'data' es un array JSON, de lo contrario null.
         */
        public JsonArray getDataAsJsonArray() {
            if (data != null && data.isJsonArray()) {
                return data.getAsJsonArray();
            }
            return null;
        }

        public int getErrorCode() {
            return errorCode;
        }
    }

    /**
     * Método auxiliar para realizar solicitudes HTTP genéricas (POST, GET, PUT).
     * Centraliza la lógica de conexión y manejo de respuesta.
     * @param endpoint El endpoint de la API (ej. "register", "profile/123").
     * @param method El método HTTP (ej. "POST", "GET", "PUT").
     * @param jsonInput El JsonObject a enviar en el cuerpo de la solicitud (puede ser null para GET).
     * @return ApiResponse con el resultado de la operación.
     */
    // ¡MODIFICADO! Se quitó 'static'
    private ApiResponse sendRequest(String endpoint, String method, JsonObject jsonInput) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(API_BASE_URL + endpoint);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            
            // Deshabilitar doOutput para GET requests
            if (method.equals("GET")) {
                conn.setDoOutput(false);
            } else {
                conn.setDoOutput(true);
            }

            if (jsonInput != null && conn.getDoOutput()) {
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonInput.toString().getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }
            }

            int responseCode = conn.getResponseCode();
            StringBuilder responseBuilder = new StringBuilder();
            
            InputStreamReader isr;
            // Leer del InputStream para respuestas exitosas (2xx) y ErrorStream para errores (4xx, 5xx)
            if (responseCode >= 200 && responseCode < 300) {
                isr = new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8);
            } else {
                isr = new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8);
            }

            try (BufferedReader br = new BufferedReader(isr)) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    responseBuilder.append(responseLine.trim());
                }
            }

            String rawResponse = responseBuilder.toString();
            System.out.println("Respuesta del servidor /" + endpoint + " (" + responseCode + "): " + rawResponse);
            
            // Parsear la respuesta JSON
            JsonObject jsonResponse;
            try {
                jsonResponse = JsonParser.parseString(rawResponse).getAsJsonObject();
            } catch (Exception e) {
                // Si la respuesta no es un JSON válido, o está vacía
                return new ApiResponse(false, "Respuesta inválida del servidor: " + rawResponse, null, responseCode);
            }

            boolean success = jsonResponse.has("success") ? jsonResponse.get("success").getAsBoolean() : false;
            String message = jsonResponse.has("message") ? jsonResponse.get("message").getAsString() : "No message";
            JsonElement dataElement = jsonResponse.has("data") ? jsonResponse.get("data") : null;

            // Devolver la ApiResponse con todos los datos
            return new ApiResponse(success, message, dataElement, responseCode);

        } catch (java.net.ConnectException e) {
            System.err.println("Error de conexión: " + e.getMessage());
            return new ApiResponse(false, "No se pudo conectar al servidor Flask. Asegúrate de que esté corriendo.", null, 0);
        } catch (java.net.SocketTimeoutException e) {
            System.err.println("Tiempo de espera agotado: " + e.getMessage());
            return new ApiResponse(false, "Tiempo de espera agotado al conectar o leer del servidor.", null, 0);
        } catch (Exception e) {
            System.err.println("Error inesperado en sendRequest para /" + endpoint + ": " + e.getMessage());
            e.printStackTrace();
            return new ApiResponse(false, "Error inesperado: " + e.getMessage(), null, 0);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    // --- Métodos específicos de la API ---

    // ¡MODIFICADO! Se quitó 'static'
    public ApiResponse registerUser(String nombreUsuario, String correo, String password) {
        JsonObject jsonInput = new JsonObject();
        jsonInput.addProperty("nombre_usuario", nombreUsuario);
        jsonInput.addProperty("correo", correo);
        jsonInput.addProperty("password", password);
        return sendRequest("register", "POST", jsonInput);
    }

    // ¡MODIFICADO! Se quitó 'static'
    public ApiResponse verifyUser(String email, String code) {
        JsonObject jsonInput = new JsonObject();
        jsonInput.addProperty("correo", email);
        jsonInput.addProperty("code", code);
        return sendRequest("verify", "POST", jsonInput);
    }

    // ¡MODIFICADO! Se quitó 'static'
    public ApiResponse loginUser(String correo, String password) {
        JsonObject jsonInput = new JsonObject();
        jsonInput.addProperty("correo", correo);
        jsonInput.addProperty("password", password);
        return sendRequest("login", "POST", jsonInput);
    }

    // ¡MODIFICADO! Se quitó 'static'
    public ApiResponse resendVerificationCode(String email) {
        JsonObject jsonInput = new JsonObject();
        jsonInput.addProperty("correo", email);
        return sendRequest("resend_code", "POST", jsonInput);
    }

    // ¡MODIFICADO! Se quitó 'static'
    public ApiResponse getOrCreateCharacterProfile(int userId) {
        return sendRequest("profile/" + userId, "GET", null);
    }

    // ¡MODIFICADO! Se quitó 'static'
    public ApiResponse updateCharacterProfile(int characterId, JsonObject updateData) {
        return sendRequest("profile/" + characterId, "PUT", updateData);
    }

    // ¡MODIFICADO! Se quitó 'static'
    public ApiResponse updateUserProfilePicture(int userId, String photoUrl) {
        JsonObject payload = new JsonObject();
        payload.addProperty("foto_perfil_url", photoUrl);
        return sendRequest("users/" + userId + "/profile_picture", "PUT", payload);
    }

    // ¡MODIFICADO! Se quitó 'static'
    public ApiResponse getCharacterInventory(int characterId) {
        return sendRequest("inventory/" + characterId, "GET", null);
    }

    // ¡MODIFICADO! Se quitó 'static'
    public ApiResponse addItemToInventory(int characterId, int itemId, int quantity) {
        JsonObject jsonInput = new JsonObject();
        jsonInput.addProperty("item_id", itemId);
        jsonInput.addProperty("cantidad", quantity);
        return sendRequest("inventory/" + characterId + "/add", "POST", jsonInput);
    }
    
    // ¡MODIFICADO! Se quitó 'static'
    public ApiResponse getEnemiesDefeated(int characterId) {
        return sendRequest("enemies_defeated/" + characterId, "GET", null);
    }
    
    // --- ¡MÉTODOS PARA RESTABLECIMIENTO DE CONTRASEÑA! ---
    // ¡MODIFICADO! Se quitó 'static'
    public ApiResponse requestPasswordResetCode(String email) {
        JsonObject jsonInput = new JsonObject();
        jsonInput.addProperty("email", email);
        return sendRequest("request_password_reset_code", "POST", jsonInput);
    }

    // ¡MODIFICADO! Se quitó 'static'
    public ApiResponse resetPasswordWithCode(String email, String code, String newPassword) {
        JsonObject jsonInput = new JsonObject();
        jsonInput.addProperty("email", email);
        jsonInput.addProperty("code", code);
        jsonInput.addProperty("new_password", newPassword);
        return sendRequest("reset_password_with_code", "POST", jsonInput);
    }
}
