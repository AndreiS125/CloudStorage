package Server;



import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class repository {
    public Connection c;
    private String url;
    public repository() throws Exception {

        url = "jdbc:sqlite:users.db";
        Class.forName("org.sqlite.JDBC").newInstance();

        c = getConnection();


        Statement s = c.createStatement();

        s.executeUpdate("CREATE TABLE IF NOT EXISTS users ('name' TEXT, 'password' TEXT)");
        s.executeUpdate("CREATE TABLE IF NOT EXISTS files ('owner' TEXT,'name' TEXT)");



    }

    public Connection getConnection() throws Exception {
        return DriverManager.getConnection(url);
    }


    public void addFile(String owner,  File file) {
        try {

            Statement s = c.createStatement();
            s.executeUpdate(String.format("INSERT INTO files VALUES ('%s','%s')", owner,file.getName()));
            s.close();

        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void add(String name, String password) {
        try {

            Statement s = c.createStatement();
            s.executeUpdate(String.format("INSERT INTO users VALUES ('%s','%s')", name,password));
            s.close();

        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public String get(String name) {
        try {

            Statement s = c.createStatement();

            ResultSet resultSet = s.executeQuery("Select * from users WHERE name = '"+name+"'");

            String answ=resultSet.getString("password");
            s.close();

            return answ;
        }
        catch(Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    public void remove(String name) {
        try {

            Statement s = c.createStatement();
            s.executeUpdate("DELETE FROM users WHERE name = '" + name + "'");
            s.close();

        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void update(String name, String password) {
        try {

            Statement s = c.createStatement();
            String st="";

            st=String.format("UPDATE users SET password = '%s' WHERE name = '%s'",password, name);

            s.executeUpdate(st);
            s.close();

        }
        catch(Exception e) {

        }
    }
    public boolean auth(String name, String password) {
        try {

            Statement s = c.createStatement();

            ResultSet resultSet = s.executeQuery("Select * from users WHERE name = '"+name+"'");

            boolean boo=password.equals(resultSet.getString("password"));
            s.close();
            return boo;



        }
        catch(Exception e) {

        }
        return false;
    }
    public ArrayList<String> getallmyfiles(String name) {
        //if the answer = true, then u cant break blocks

        try {

            Statement s = c.createStatement();
            ResultSet resultSet = s.executeQuery("Select * from files");

            ArrayList<String> exit=new ArrayList<>();


            while (resultSet.next()) {
                if (resultSet.getString("owner").equals(name)){
                    exit.add(resultSet.getString("name"));
                }


            }
            s.close();
            return exit;

        }
        catch(Exception e) {
            e.printStackTrace();
            return null;
        }



    }

}
