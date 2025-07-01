
package devt.login.model;


public class ModelUser {
    
    private int id;
    private String nombre_usuario;
    private String correo;
    private String password;
    
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


    public ModelUser(int id, String nombre_usuario, String correo, String password) {
        this.id = id;
        this.nombre_usuario = nombre_usuario;
        this.correo = correo;
        this.password = password;
    }

}
