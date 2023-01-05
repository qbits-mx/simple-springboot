package mx.qbits.publisher.access.api.mapper;

import java.sql.*;
import lombok.extern.slf4j.Slf4j;
import mx.qbits.publisher.access.api.model.domain.Par;
import mx.qbits.publisher.access.api.model.domain.Registro;
import mx.qbits.publisher.access.api.utils.Generator;

@Slf4j
public class StoreDB {
    private Connection c = null;
    
    public StoreDB(String driver, String url, String user, String pass) throws SQLException {
        c = getConnection(driver, url, user, pass);
        this.createIfNotExist();
    }
    
    private Connection getConnection(String driver, String url, String user, String pass) throws SQLException {
        log.info("Getting connection to DB...");
        try {
            Class.forName(driver);
            return DriverManager.getConnection(url, user, pass);
        } catch (ClassNotFoundException e) {
            System.out.println("Algo falló: " + e.getMessage());
            return null;
        }
    }
    
    public void done() throws SQLException {
        c.close();
    }

    /**
     * Actualiza el campo 'password' de la tabla 'mdl_user' para cierta llave id.
     * 
     * @param hashed Valor de actualización
     * @param id llave de actualización
     * 
     * @throws SQLException
     */
    public void actualiza(String hashed, int id) throws SQLException {
        long now = System.currentTimeMillis()/1000;
        String query = "UPDATE mdl_user SET" +
        "  firstaccess=" + now +
        ", lastaccess=" + now +
        ", lastlogin=" + now +
        ", currentlogin=" + now +
        ", password='" + hashed +
        "' WHERE id=" + id;
        log.info(query);
        Statement stmt = c.createStatement();
        stmt.executeUpdate(query);
        stmt.close();
    }
    
    /**
     * Inserta el correo y el id en la custom table 'sync'.
     * 
     * @param correo
     * @param id
     * @throws SQLException
     */
    public void sicroniza(Registro r, int idUser) throws SQLException {
        String query = "INSERT INTO sync(correo, telefono, curp, nombreCompleto, linkedin, iduser) values('"
        + r.getCorreo()         + "', '" 
        + r.getTelefono()       + "', '" 
        + r.getCurp()           + "', '" 
        + r.getNombreCompleto() + "', '" 
        + r.getLinkedin()       + "', " 
        + idUser                + ")";
        log.info(query);
        Statement stmt = c.createStatement();
        stmt.execute(query);
        stmt.close();
    }
    
    /**
     * Indica si existe en la tabla 'sync' un correo dado.
     * 
     * @param correo
     * @return
     * @throws SQLException
     */
    public boolean registrado(String correo) throws SQLException {
        String query = "SELECT correo from sync where correo='"+correo+"'";
        log.info(query);
        Statement stmt = c.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        boolean r = rs.next();
        rs.close();
        stmt.close();
        return r;
    }
    
    /**
     * Regresa el primer ID de la tabla 'mdl_user' cuyo campo 
     * lastaccess sea mas viejo que un valor dado.
     * 
     * @param olderThan
     * @return
     * @throws SQLException
     */
    public Par obtenId(String olderThan) throws SQLException {
        String query = "SELECT id, username from mdl_user WHERE lastaccess<"+olderThan+" AND username like 'student%';";
        log.info(query);
        Statement stmt = c.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        Par par = new Par();
        if(rs.next()) {
            par.setId(rs.getInt(1));
            par.setUsername(rs.getString(2));
            rs.close();
        }
        stmt.close();
        return par;
    }
    
    private void createIfNotExist() {
        try {
            String query = "SELECT * from sync";
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            if(rs.next()) {
                rs.close();
            }
            stmt.close();
            return;
        } catch(SQLException e) {
            try {
                String query = getQuery();
                Statement stmt = c.createStatement();
                stmt.execute(query);
                stmt.close();
            } catch(SQLException ee) {
                ee.printStackTrace();
            }
        }
    }
    
    private String getQuery() {
        String q = "";
        q = q + "create table sync(";
        q = q + "  id int(11) not null auto_increment,";
        q = q + "  correo varchar(70) not null,"; // gustavo.arellanos@ultrasist.com.mx <-- 35 chars
        q = q + "  telefono varchar(32) not null,"; // +52 55 1691 3070 <-- 16 chars
        q = q + "  curp varchar(25) not null,"; // AESG 671022 HAS RNS12 <-- 21 chars
        q = q + "  nombreCompleto varchar(60) not null,"; // María Verónica Martinez de la Vega y Mansilla <-- 45 chars
        q = q + "  linkedin varchar(100) not null,"; // https://www.linkedin.com/in/gustavo-adolfo-arellano-sandoval-085021b0/ <-- 70 chars 
        q = q + "  idUser int(11) not null,";
        q = q + "  primary key(id),";
        q = q + "  UNIQUE KEY idx_sync_correo (correo)";
        q = q + ");";
        return q;
    }
    
    public void reset() throws SQLException {
        String hashed = Generator.genHashed("UrbiEtOrbi1");
        String query="update mdl_user set firstaccess=0, lastaccess=0, lastlogin=0, currentlogin=0, password='" + hashed + "' where username like 'student%';";
        log.info(query);
        Statement stmt = c.createStatement();
        stmt.execute(query);
        // now, lets delete sync
        query = "delete from sync;";
        stmt.execute(query);
        stmt.close();
    }

}
