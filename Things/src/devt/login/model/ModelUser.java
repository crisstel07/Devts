
package devt.login.model;

public class ModelUser {

    private int id;
    private String nombre_usuario;
    private String correo;
    private String password;
    private String verifyCode;

    // Constructor completo
    public ModelUser(int id, String nombre_usuario, String correo, String password, String verifyCode) {
        this.id = id;
        this.nombre_usuario = nombre_usuario;
        this.correo = correo;
        this.password = password;
        this.verifyCode = verifyCode;
    }

    // Constructor sin VerifyCode
    public ModelUser(int id, String nombre_usuario, String correo, String password) {
        this(id, nombre_usuario, correo, password, null);
    }

    // Constructor vacío (muy útil si se usará con setters)
    public ModelUser() {}

    // Getters y Setters
    public int getid() {
        return id;
    }

    public void setid(int id) {
        this.id = id;
    }

    public String getnombre_usuario() {
        return nombre_usuario;
    }

    public void setnombre_usuario(String nombre_usuario) {
        this.nombre_usuario = nombre_usuario;
    }

    public String getcorreo() {
        return correo;
    }

    public void setcorreo(String correo) {
        this.correo = correo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }
}
