
package devt.login.apiFlask;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class ApiClient {

    private static final String API_BASE_URL = "http://localhost:5000/api/";
    private static final Gson gson = new Gson();

    public static class ApiResponse {
        public boolean success;
        public String message;
        public Integer user_id;
        public JsonObject user; // Campo para los datos del usuario
        public JsonObject character; // Campo para los datos del personaje
        public JsonObject inventory; // Campo para los datos del inventario
        public Integer slot; // Campo para el slot del inventario (usado en add_item)
    }

    /**
     * Registra un nuevo usuario en la API Flask.
     * @param nombreUsuario Nombre de usuario.
     * @param correo Correo electrónico.
     * @param password Contraseña.
     * @return ApiResponse con el resultado del registro.
     */
    public static ApiResponse registerUser(String nombreUsuario, String correo, String password) {
        try {
            URL url = new URL(API_BASE_URL + "register");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

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
            
            InputStreamReader isr;
            if (responseCode >= 200 && responseCode < 300) { 
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
            ApiResponse apiResponse = gson.fromJson(response.toString(), ApiResponse.class);
            return apiResponse;

        } catch (Exception e) {
            System.err.println("Error al registrar usuario: " + e.getMessage());
            ApiResponse errorResponse = new ApiResponse();
            errorResponse.success = false;
            errorResponse.message = "Error de conexión o de red: " + e.getMessage();
            return errorResponse;
        }
    }

    /**
     * Verifica el código de un usuario registrado.
     * @param userId ID del usuario.
     * @param code Código de verificación.
     * @return ApiResponse con el resultado de la verificación.
     */
    public static ApiResponse verifyUser(int userId, String code) {
        try {
            URL url = new URL(API_BASE_URL + "verify");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            JsonObject jsonInput = new JsonObject();
            jsonInput.addProperty("user_id", userId);
            jsonInput.addProperty("code", code);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInput.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            StringBuilder response = new StringBuilder();
            
            InputStreamReader isr;
            if (responseCode >= 200 && responseCode < 300) {
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
     * Inicia sesión de un usuario.
     * @param correo Correo electrónico del usuario.
     * @param password Contraseña del usuario.
     * @return ApiResponse con los datos del usuario si el inicio de sesión es exitoso.
     */
    public static ApiResponse loginUser(String correo, String password) {
        try {
            URL url = new URL(API_BASE_URL + "login");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

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
            if (responseCode >= 200 && responseCode < 300) {
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

    /**
     * Obtiene el perfil completo de un usuario por su ID.
     * Esto es útil para recargar los datos del usuario (no del personaje) después de ciertas operaciones.
     * @param userId El ID del usuario.
     * @return Un objeto ApiResponse que contiene los datos del usuario en el campo 'user'.
     */
    public static ApiResponse getUserProfile(int userId) { // <-- ¡ESTE ES EL NUEVO MÉTODO QUE DEBES TENER!
        try {
            URL url = new URL(API_BASE_URL + "user/" + userId); // Asume que tienes un endpoint /api/user/<id> en Flask
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            int responseCode = conn.getResponseCode();
            StringBuilder response = new StringBuilder();
            
            InputStreamReader isr;
            if (responseCode >= 200 && responseCode < 300) {
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

            System.out.println("Respuesta del servidor /user/" + userId + " (" + responseCode + "): " + response.toString());
            ApiResponse apiResponse = gson.fromJson(response.toString(), ApiResponse.class);
            return apiResponse;

        } catch (Exception e) {
            System.err.println("Error al obtener perfil de usuario: " + e.getMessage());
            ApiResponse errorResponse = new ApiResponse();
            errorResponse.success = false;
            errorResponse.message = "Error de conexión o de red al cargar perfil de usuario: " + e.getMessage();
            return errorResponse;
        }
    }

    /**
     * Obtiene el perfil de un personaje o lo crea si no existe para el user_id dado.
     * @param userId ID del usuario al que pertenece el personaje.
     * @return ApiResponse con los datos del personaje.
     */
    public static ApiResponse getOrCreateCharacterProfile(int userId) {
        try {
            URL url = new URL(API_BASE_URL + "profile/" + userId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            int responseCode = conn.getResponseCode();
            StringBuilder response = new StringBuilder();
            
            InputStreamReader isr;
            if (responseCode >= 200 && responseCode < 300) {
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

            System.out.println("Respuesta del servidor /profile (" + responseCode + "): " + response.toString());
            ApiResponse apiResponse = gson.fromJson(response.toString(), ApiResponse.class);
            return apiResponse;

        } catch (Exception e) {
            System.err.println("Error al obtener/crear perfil de personaje: " + e.getMessage());
            ApiResponse errorResponse = new ApiResponse();
            errorResponse.success = false;
            errorResponse.message = "Error de conexión o de red al cargar perfil: " + e.getMessage();
            return errorResponse;
        }
    }

    /**
     * Actualiza el perfil de un personaje existente.
     * @param characterId ID del personaje a actualizar.
     * @param updateData JsonObject con los campos a actualizar (ej. nombre_personaje, foto_perfil_url).
     * @return ApiResponse con el resultado de la actualización.
     */
    public static ApiResponse updateCharacterProfile(int characterId, JsonObject updateData) {
        try {
            URL url = new URL(API_BASE_URL + "profile/" + characterId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = updateData.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            StringBuilder response = new StringBuilder();
            
            InputStreamReader isr;
            if (responseCode >= 200 && responseCode < 300) {
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

            System.out.println("Respuesta del servidor PUT /profile (" + responseCode + "): " + response.toString());
            ApiResponse apiResponse = gson.fromJson(response.toString(), ApiResponse.class);
            return apiResponse;

        } catch (Exception e) {
            System.err.println("Error al actualizar perfil de personaje: " + e.getMessage());
            ApiResponse errorResponse = new ApiResponse();
            errorResponse.success = false;
            errorResponse.message = "Error de conexión o de red al actualizar perfil: " + e.getMessage();
            return errorResponse;
        }
    }

    /**
     * Obtiene el inventario de un personaje.
     * @param characterId ID del personaje.
     * @return ApiResponse con la lista de ítems en el inventario.
     */
    public static ApiResponse getCharacterInventory(int characterId) {
        try {
            URL url = new URL(API_BASE_URL + "inventory/" + characterId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            int responseCode = conn.getResponseCode();
            StringBuilder response = new StringBuilder();
            
            InputStreamReader isr;
            if (responseCode >= 200 && responseCode < 300) {
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

            System.out.println("Respuesta del servidor /inventory (" + responseCode + "): " + response.toString());
            ApiResponse apiResponse = gson.fromJson(response.toString(), ApiResponse.class);
            return apiResponse;

        } catch (Exception e) {
            System.err.println("Error al obtener inventario de personaje: " + e.getMessage());
            ApiResponse errorResponse = new ApiResponse();
            errorResponse.success = false;
            errorResponse.message = "Error de conexión o de red al cargar inventario: " + e.getMessage();
            return errorResponse;
        }
    }

    /**
     * Añade o actualiza un ítem en el inventario de un personaje.
     * @param characterId ID del personaje.
     * @param itemId ID del ítem a añadir.
     * @param quantity Cantidad a añadir.
     * @return ApiResponse con el resultado de la operación.
     */
    public static ApiResponse addItemToInventory(int characterId, int itemId, int quantity) {
        try {
            URL url = new URL(API_BASE_URL + "inventory/" + characterId + "/add");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            JsonObject jsonInput = new JsonObject();
            jsonInput.addProperty("item_id", itemId);
            jsonInput.addProperty("cantidad", quantity);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInput.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            StringBuilder response = new StringBuilder();
            
            InputStreamReader isr;
            if (responseCode >= 200 && responseCode < 300) {
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

            System.out.println("Respuesta del servidor /inventory/add (" + responseCode + "): " + response.toString());
            ApiResponse apiResponse = gson.fromJson(response.toString(), ApiResponse.class);
            return apiResponse;

        } catch (Exception e) {
            System.err.println("Error al añadir ítem al inventario: " + e.getMessage());
            ApiResponse errorResponse = new ApiResponse();
            errorResponse.success = false;
            errorResponse.message = "Error de conexión o de red al añadir ítem: " + e.getMessage();
            return errorResponse;
        }
    }
}
