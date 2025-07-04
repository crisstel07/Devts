
package devt.login.service;

import devt.login.connection.DBConnection;
import devt.login.model.ModelUser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Random;

public class ServiceUser {

    private final Connection con;

    public ServiceUser() {
        con = DBConnection.getInstance().getConnection();
    }

    public void insertUser(ModelUser user) throws SQLException {
          // Genera el código de verificación aquí, asegurando que sea único
        String generatedCode = generateVerifyCode(); // Llama a tu método privado que verifica duplicados

        PreparedStatement p = con.prepareStatement(
                "INSERT INTO `Usuarios` (nombre_usuario, correo, password, VerifyCode, Status) VALUES (?,?,?,?,?)",
                PreparedStatement.RETURN_GENERATED_KEYS
        );
        p.setString(1,user.getnombre_usuario());
        p.setString(2, user.getcorreo());
        p.setString(3, user.getPassword());
        p.setString(4, generatedCode); // Vacío, porque ya se verificó
        p.setString(5, "Pending");

        p.execute();

        ResultSet r = p.getGeneratedKeys();
        if (r.next()) {
             user.setid(r.getInt(1));
        }
        user.setVerifyCode(generatedCode);

        r.close();
        p.close();
    }

    private String generateVerifyCode() throws SQLException {
        DecimalFormat df = new DecimalFormat("000000");
        Random ran = new Random();
        String code = df.format(ran.nextInt(1000000));
         do {
            code = df.format(ran.nextInt(1000000));
        } while (checkDuplicateCode(code)); // Asegura que el código no exista ya en la DB
        return code;
    }

    private boolean checkDuplicateCode(String code) throws SQLException {
        boolean duplicate = false;
        // Usa try-with-resources para cerrar PreparedStatement y ResultSet automáticamente
        try (PreparedStatement p = con.prepareStatement("SELECT id FROM `Usuarios` WHERE VerifyCode=? LIMIT 1")) {
            p.setString(1, code);
            try (ResultSet r = p.executeQuery()) {
                if (r.next()) {
                    duplicate = true;
                }
            }
        } // p y r se cierran automáticamente aquí
        return duplicate;
    }

    public boolean checkDuplicateUser(String userName) throws SQLException {
        boolean duplicate = false;
        try (PreparedStatement p = con.prepareStatement("SELECT id FROM `Usuarios` WHERE nombre_usuario=? AND `Status`='Verified' LIMIT 1")) {
            p.setString(1, userName);
            try (ResultSet r = p.executeQuery()) {
                if (r.next()) {
                    duplicate = true;
                }
            }
        }
        return duplicate;
    }

   public boolean checkDuplicateEmail(String email) throws SQLException { // Cambiado 'correo' a 'email' para consistencia con ModelUser
        boolean duplicate = false;
        try (PreparedStatement p = con.prepareStatement("SELECT id FROM `Usuarios` WHERE correo=? AND `Status`='Verified' LIMIT 1")) {
            p.setString(1, email);
            try (ResultSet r = p.executeQuery()) {
                if (r.next()) {
                    duplicate = true;
                }
            }
        }
        return duplicate;
    }

    public void doneVerify(int id) throws SQLException {
        try (PreparedStatement p = con.prepareStatement("UPDATE `Usuarios` SET VerifyCode='', `Status`='Verified' WHERE id=? LIMIT 1")) {
            p.setInt(1, id);
            p.execute();
        }
    }

     public boolean verifyCodeWithUser(int id, String code) throws SQLException {
        boolean verify = false;
        PreparedStatement p = con.prepareStatement("SELECT id FROM `Usuarios` WHERE id=? AND VerifyCode=? LIMIT 1");
        p.setInt(1, id);
        p.setString(2, code);
        ResultSet r = p.executeQuery();
        if (r.next()) {
            verify = true;
        }
        r.close();
        p.close();
        return verify;
    }
     
    
}

