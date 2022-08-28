package run.oasis.xaorm;

import run.oasis.xaorm.session.Session;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Engine {

    static String DRIVER;
    static String DB_URL;
    static String USER;
    static String PASSWD;

    private Logger log;
    private Connection conn;

    static {
        Properties params = new Properties();
        String config = "database.properties";
        InputStream is = Engine.class.getClassLoader().getResourceAsStream(config);
        try {
            params.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        DRIVER = params.getProperty("driver");
        DB_URL = params.getProperty("url");
        USER = params.getProperty("user");
        PASSWD = params.getProperty("password");
    }

    public Engine() {
        log = Logger.getLogger("xaorm");
        try {
            Class.forName(DRIVER);
            log.log(Level.INFO, "connecting to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASSWD);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Logger getLog() {
        return log;
    }

    public Connection getConn() {
        return conn;
    }

    public Session createSession() {
        return new Session(this);
    }

    public void sync(Class<?>... clazz) throws SQLException {
        var session = createSession();
        for (var m : clazz) {
            session.schema(m).createTable();
            var table = session.getSchema();
            var rawRows = session.raw(String.format("SELECT * FROM %s LIMIT 1", table.getName())).query();
            var meta = rawRows.getMetaData();

            var columns = new ArrayList<String>();
            var columnTypes = new ArrayList<String>();
            for (int i = 1; i <= meta.getColumnCount(); i++) {
                columns.add(meta.getColumnName(i));
                columnTypes.add(meta.getColumnTypeName(i));
            }
            var addColumns = minus(List.of(table.getFieldNames()), columns);
            var delColumns = minus(columns, List.of(table.getFieldNames()));
            var modColumns = intersect(columns, List.of(table.getFieldNames())).stream()
                    .filter(c -> !table.getField(c).getType().equals(columnTypes.get(columns.indexOf(c))))
                    .collect(ArrayList<String>::new, ArrayList::add, ArrayList::addAll);

            for (var c : addColumns) {
                session.raw(String.format("ALTER TABLE %s ADD COLUMN %s %s", table.getName(), c, table.getField(c).getType())).exec();
            }
            for (var c : delColumns) {
                session.raw(String.format("ALTER TABLE %s DROP COLUMN %s", table.getName(), c)).exec();
            }
            for (var c : modColumns) {
                session.raw(String.format("ALTER TABLE %s MODIFY COLUMN %s %s", table.getName(), c, table.getField(c).getType())).exec();
            }
        }
    }

    private List<String> minus(List<String> a, List<String> b) {
        return a.stream().filter(x -> !b.contains(x)).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    private List<String> intersect(List<String> a, List<String> b) {
        return a.stream().filter(x -> b.contains(x)).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
}
