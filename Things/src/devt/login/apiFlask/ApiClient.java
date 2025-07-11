package devt.login.apiFlask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement; // Importar JsonElement para mayor flexibilidad
import com.google.gson.JsonParser; // Importar JsonParser para parsear la respuesta
import com.google.gson.JsonSyntaxException;

public class ApiClient {

    private static final String API_BASE_URL = "http://localhost:5000/api/";
    private static final Gson gson = new Gson();

    // Clase ApiResponse interna actualizada para ser más flexible
    public static class ApiResponse {
        private boolean success;
        private String message;
        private JsonElement data; // Ahora es JsonElement para manejar objetos, arrays, etc.

        // Constructor vacío para Gson
        public ApiResponse() {}

        // Constructor completo (útil para crear respuestas de error manualmente)
        public ApiResponse(boolean success, String message, JsonElement data) {
            this.success = success;
            this.message = message;
            this.data = data;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public JsonElement getData() { // Retorna JsonElement
            return data;
        }

        // Métodos de ayuda para acceder a los datos si sabes su tipo
        public JsonObject getDataAsJsonObject() {
            if (data != null && data.isJsonObject()) {
                return data.getAsJsonObject();
            }
            return null;
        }

        public com.google.gson.JsonArray getDataAsJsonArray() {
            if (data != null && data.isJsonArray()) {
                return data.getAsJsonArray();
            }
            return null;
        }
    }

    // --- Métodos de ayuda para realizar peticiones HTTP ---
    // (Refactorizados para ser reutilizables y devolver ApiResponse)

    private static ApiResponse executeRequest(String urlString, String method, JsonObject jsonInput) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setConnectTimeout(5000); // 5 segundos de timeout para conexión
            conn.setReadTimeout(5000); // 5 segundos de timeout para lectura

            if (jsonInput != null) {
                conn.setDoOutput(true);
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonInput.toString().getBytes("utf-8");
                    os.write(input, 0, input.length);
                }
            }

            int responseCode = conn.getResponseCode();
            StringBuilder responseBuilder = new StringBuilder();
            
            InputStreamReader isr;
            if (responseCode >= 200 && responseCode < 300) {
                isr = new InputStreamReader(conn.getInputStream(), "utf-8");
            } else {
                isr = new InputStreamReader(conn.getErrorStream(), "utf-8");
            }

            try (BufferedReader br = new BufferedReader(isr)) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    responseBuilder.append(responseLine.trim());
                }
            }

            String rawResponse = responseBuilder.toString();
            System.out.println("Respuesta del servidor (" + method + " " + urlString + ") [" + responseCode + "]: " + rawResponse);
            
            // Parsear la respuesta en el nuevo formato ApiResponse
            try {
                JsonObject jsonResponse = JsonParser.parseString(rawResponse).getAsJsonObject();
                boolean success = jsonResponse.has("success") && jsonResponse.get("success").getAsBoolean();
                String message = jsonResponse.has("message") && !jsonResponse.get("message").isJsonNull() ? jsonResponse.get("message").getAsString() : null;
                JsonElement data = jsonResponse.has("data") && !jsonResponse.get("data").isJsonNull() ? jsonResponse.get("data") : null;
                return new ApiResponse(success, message, data);
            } catch (JsonSyntaxException e) {
                return new ApiResponse(false, "Error de sintaxis JSON en la respuesta del servidor: " + e.getMessage() + ". Raw: " + rawResponse, null);
            }

        } catch (Exception e) {
            System.err.println("Error en la petición HTTP a " + urlString + ": " + e.getMessage());
            return new ApiResponse(false, "Error de conexión o de red: " + e.getMessage(), null);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    // --- Métodos específicos de la API (usando el nuevo executeRequest) ---

    public static ApiResponse registerUser(String nombreUsuario, String correo, String password) {
        JsonObject jsonInput = new JsonObject();
        jsonInput.addProperty("nombre_usuario", nombreUsuario);
        jsonInput.addProperty("correo", correo);
        jsonInput.addProperty("password", password);
        return executeRequest(API_BASE_URL + "register", "POST", jsonInput);
    }

    public static ApiResponse verifyUser(int userId, String code) {
        JsonObject jsonInput = new JsonObject();
        jsonInput.addProperty("user_id", userId);
        jsonInput.addProperty("code", code);
        return executeRequest(API_BASE_URL + "verify", "POST", jsonInput);
    }

    public static ApiResponse loginUser(String correo, String password) {
        JsonObject jsonInput = new JsonObject();
        jsonInput.addProperty("correo", correo);
        jsonInput.addProperty("password", password);
        return executeRequest(API_BASE_URL + "login", "POST", jsonInput);
    }

    public static ApiResponse getOrCreateCharacterProfile(int userId) {
        // Asegúrate de que tu API Flask tenga un endpoint GET /api/profile/<user_id>
        return executeRequest(API_BASE_URL + "profile/" + userId, "GET", null);
    }

    // Método para obtener un personaje por su ID (nuevo, usado por PanelProfileAndInventory)
    public static ApiResponse getCharacterById(int characterId) {
        // Asegúrate de que tu API Flask tenga un endpoint GET /api/characters/<character_id>
        return executeRequest(API_BASE_URL + "characters/" + characterId, "GET", null);
    }

    // Método para actualizar el perfil de un personaje (existente, adaptado)
    // Ahora acepta un JsonObject completo para la actualización
    public static ApiResponse updateCharacterProfile(int characterId, JsonObject updateData) {
        // Asegúrate de que tu API Flask tenga un endpoint PUT /api/profile/<character_id>
        // o /api/characters/<character_id> si usas el que te di antes.
        // Usaré /profile/ por consistencia con tu código original.
        return executeRequest(API_BASE_URL + "profile/" + characterId, "PUT", updateData);
    }
    
    // Método para obtener el inventario de un personaje (existente, adaptado)
    public static ApiResponse getCharacterInventory(int characterId) {
        // Asegúrate de que tu API Flask tenga un endpoint GET /api/inventory/<character_id>
        return executeRequest(API_BASE_URL + "inventory/" + characterId, "GET", null);
    }

    // Método para añadir un ítem al inventario (existente, adaptado)
    public static ApiResponse addItemToInventory(int characterId, int itemId, int quantity) {
        JsonObject jsonInput = new JsonObject();
        jsonInput.addProperty("item_id", itemId);
        jsonInput.addProperty("cantidad", quantity);
        // Asegúrate de que tu API Flask tenga un endpoint POST /api/inventory/<character_id>/add
        return executeRequest(API_BASE_URL + "inventory/" + characterId + "/add", "POST", jsonInput);
    }

    // Nuevo método para obtener enemigos derrotados (usado por PanelProfileAndInventory)
    public static ApiResponse getEnemiesDefeated(int characterId) {
        // Asegúrate de que tu API Flask tenga un endpoint GET /api/enemies_defeated/<character_id>
        return executeRequest(API_BASE_URL + "enemies_defeated/" + characterId, "GET", null);
    }
}
