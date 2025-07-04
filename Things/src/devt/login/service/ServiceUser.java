
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
        String code = generateVerifyCode();

        PreparedStatement p = con.prepareStatement(
            "INSERT INTO `Usuarios` (nombre_usuario, correo, password, VerifyCode, Status) VALUES (?,?,?,?,?)",
            PreparedStatement.RETURN_GENERATED_KEYS
        );
         p.setString(1, user.getnombre_usuario());
    p.setString(2, user.getcorreo());
    p.setString(3, user.getPassword());
    p.setString(4, ""); // Vacío, porque ya se verificó
    p.setString(5, "Verified");
    
        p.execute();

        ResultSet r = p.getGeneratedKeys();
        if (r.next()) {
             user.setid(r.getInt(1));
        }
        user.setVerifyCode(code);

        r.close();
        p.close();
    }

    private String generateVerifyCode() throws SQLException {
        DecimalFormat df = new DecimalFormat("000000");
        Random ran = new Random();
        String code = df.format(ran.nextInt(1000000));
        while (checkDuplicateCode(code)) {
            code = df.format(ran.nextInt(1000000));
        }
        return code;
    }

    private boolean checkDuplicateCode(String code) throws SQLException {
        boolean duplicate = false;
        PreparedStatement p = con.prepareStatement("SELECT id FROM `Usuarios` WHERE VerifyCode=? LIMIT 1");
        p.setString(1, code);
        ResultSet r = p.executeQuery();
        if (r.next()) {
            duplicate = true;
        }
        r.close();
        p.close();
        return duplicate;
    }

    public boolean checkDuplicateUser(String user) throws SQLException {
        boolean duplicate = false;
        PreparedStatement p = con.prepareStatement("SELECT id FROM `Usuarios` WHERE nombre_usuario=? AND `Status`='Verified' LIMIT 1");
        p.setString(1, user);
        ResultSet r = p.executeQuery();
        if (r.next()) {
            duplicate = true;
        }
        r.close();
        p.close();
        return duplicate;
    }

    public boolean checkDuplicateEmail(String email) throws SQLException {
        boolean duplicate = false;
        PreparedStatement p = con.prepareStatement("SELECT id FROM `Usuarios` WHERE correo=? AND `Status`='Verified' LIMIT 1");
        p.setString(1, email);
        ResultSet r = p.executeQuery();
        if (r.next()) {
            duplicate = true;
        }
        r.close();
        p.close();
        return duplicate;
    }

    public void doneVerify(int id) throws SQLException {
        PreparedStatement p = con.prepareStatement("UPDATE `Usuarios` SET VerifyCode='', `Status`='Verified' WHERE id=? LIMIT 1");
        p.setInt(1, id);
        p.execute();
        p.close();
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
    
    public String generateCode() {
    // Genera un número aleatorio entre 100000 y 999999
    return String.valueOf((int)(Math.random() * 900000) + 100000);
}
}

