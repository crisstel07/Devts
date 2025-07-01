
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
    
     public void insertUser(ModelUser VEILWALKER) throws SQLException {
        PreparedStatement p = con.prepareStatement("insert into `VEILWALKER` (nombre_usuario, correo, `contraseña`) values (?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
        p.setString(1, VEILWALKER.getnombre_usuario());
        p.setString(2, VEILWALKER.getcorreo());
        p.setString(3, VEILWALKER.getPassword());
        p.execute();
        ResultSet r = p.getGeneratedKeys();
        r.first();
        int id = r.getInt(1);
        r.close();
        p.close();
        VEILWALKER.setid(id);
     }
     
     private String generateVerifyCode() throws SQLException {
        DecimalFormat df = new DecimalFormat("000000");
        Random ran = new Random();
        String code = df.format(ran.nextInt(1000000));  //  Random from 0 to 999999
        while (checkDuplicateCode(code)) {
            code = df.format(ran.nextInt(1000000));
        }
        return code;
    }
     
     private boolean checkDuplicateCode(String code) throws SQLException {
        boolean duplicate = false;
        PreparedStatement p = con.prepareStatement("select UserID from `VEILWALKER` where VerifyCode=? limit 1");
        p.setString(1, code);
        ResultSet r = p.executeQuery();
        if (r.first()) {
            duplicate = true;
        }
        r.close();
        p.close();
        return duplicate;
    }
     
      public boolean checkDuplicateUser(String user) throws SQLException {
        boolean duplicate = false;
        PreparedStatement p = con.prepareStatement("select id from `VEILWALKER` where UserName=? and `Status`='Verified' limit 1");
        p.setString(1, user);
        ResultSet r = p.executeQuery();
        if (r.first()) {
            duplicate = true;
        }
        r.close();
        p.close();
        return duplicate;
    }
      
       public boolean checkDuplicateEmail(String user) throws SQLException {
        boolean duplicate = false;
        PreparedStatement p = con.prepareStatement("select UserID from `VEILWALKER` where correo=? and `Status`='Verified' limit 1");
        p.setString(1, user);
        ResultSet r = p.executeQuery();
        if (r.first()) {
            duplicate = true;
        }
        r.close();
        p.close();
        return duplicate;
    }

    public void doneVerify(int id) throws SQLException {
        PreparedStatement p = con.prepareStatement("update `VEILWALKER` set VerifyCode='', `Status`='Verified' where UserID=? limit 1");
        p.setInt(1, id);
        p.execute();
        p.close();
    }

    public boolean verifyCodeWithUser(int id, String code) throws SQLException {
        boolean verify = false;
        PreparedStatement p = con.prepareStatement("select id from `VEILWALKER` where id=? and VerifyCode=? limit 1");
        p.setInt(1, id);
        p.setString(2, code);
        ResultSet r = p.executeQuery();
        if (r.first()) {
            verify = true;
        }
        r.close();
        p.close();
        return verify;
    }
}
