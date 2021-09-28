package sharifulin.db;

import sharifulin.entity.City;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.Collections;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

public class DatabaseWorker {
    private static final String jdbc = "jdbc:h2:/Users/u19571283/IdeaProjects/sber_project/src/main/resources/database";
    private static Connection conn = null;
    private static PreparedStatement simpleSelectStatement = null;
    private static PreparedStatement insertStatement = null;
    private static PreparedStatement updateStatement = null;
    private static PreparedStatement sortedByDistrictAndNameStatement = null;
    private static PreparedStatement groupByPopulation = null;

    public static void init() {
        try {
            Class.forName("org.h2.Driver");
            conn = DriverManager.getConnection(jdbc,"sa","");
            simpleSelectStatement = conn.prepareStatement("SELECT * FROM city");
            insertStatement = conn.prepareStatement(
                    "INSERT INTO city(id,name,region,district,population,foundation)" +
                            " values(?,?,?,?,?,?)");
            updateStatement = conn.prepareStatement("UPDATE city SET population = ? where id = ?");
            sortedByDistrictAndNameStatement = conn.prepareStatement("SELECT * FROM city ORDER BY district,name");
            groupByPopulation = conn.prepareStatement("select * from city where POPULATION=(select max(population) from city)");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public static void destroy() {
        try {
            if(simpleSelectStatement != null)
                simpleSelectStatement.close();
            if(insertStatement != null)
                insertStatement.close();
            if(updateStatement != null)
                updateStatement.close();
            if(sortedByDistrictAndNameStatement != null)
                sortedByDistrictAndNameStatement.close();
            if(groupByPopulation != null)
                groupByPopulation.close();
            if(conn != null)
                conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateValues(City c) throws SQLException{
        updateStatement.setLong(1,c.getPopulation());
        updateStatement.setInt(2,c.getHash());
        updateStatement.executeUpdate();
    }

    public static void insertValues(City c) throws SQLException {
        insertStatement.setInt(1,c.getHash());
        insertStatement.setString(2,c.getName());
        insertStatement.setString(3,c.getRegion());
        insertStatement.setString(4,c.getDistrict());
        insertStatement.setLong(5,c.getPopulation());
        insertStatement.setString(6,c.getFoundation());
        insertStatement.executeUpdate();
    }

    public static Set<City> getValues() throws SQLException {
        ResultSet results = simpleSelectStatement.executeQuery();
        return City.transformFromResultSet(results);
    }

    public static void printValues() throws SQLException {
        System.out.println(getValues());
    }

    public static void getSortedValues(boolean ascending) throws SQLException{
        TreeSet<City> cities = new TreeSet<City>(getValues());
        if(!ascending) {
            cities = (TreeSet)cities.descendingSet();
        }
        System.out.println(cities);
    }
    public static void getGroupByPopulation() throws SQLException {
        Set<City> cities = City.transformFromResultSet(groupByPopulation.executeQuery());
        System.out.println(cities);
    }

    public static void sortedByDistrictAndName() throws SQLException {
        Set<City> cities = City.transformFromResultSet(sortedByDistrictAndNameStatement.executeQuery());
        System.out.println(cities);
    }

    public static void getGroupByPopulation2() throws SQLException {
        Set<City> cities = City.transformFromResultSet(groupByPopulation.executeQuery());
        City[] ar_city = new City[cities.size()];
        int i = 0;
        long max = 0;
        int max_i = 0;
        for(City c : cities) {
            ar_city[i] = c;
            if(max < c.getPopulation())
            {
                max = c.getPopulation();
                max_i = i;
            }
            ++i;
        }
        System.out.println("["+max_i+"]="+max);
    }

    public static void countOfCities() throws SQLException{
        PreparedStatement stmnt = conn.prepareStatement("select upper(region), count(name) from city group by upper(region)");
        ResultSet rs = stmnt.executeQuery();
        while(rs.next()) {
            StringBuilder strbld = new StringBuilder();
            strbld.append(rs.getString(1));
            strbld.append(" - ");
            strbld.append(rs.getInt(2));
            System.out.println(strbld.toString());
        }
        stmnt.close();
    }

    public static void scanningFile(String[] args) {
        Scanner sc = null;

        try {
            if(args.length != 0) {
                sc = new Scanner(new File(args[0]));
            }
            if(sc==null) {
                System.err.println("Set textfile in arguments");
                System.exit(1);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.err.println("\n\ntextfile is not found");
            System.exit(1);
        }

        int count = 0;
        while(sc.hasNext()) {
            ++count;
            try{
                City c = City.parseString(sc.nextLine());
                try {
                    insertValues(c);
                } catch(SQLException ex) {
                    updateValues(c);

                }
            } catch(IllegalArgumentException ex) {
                System.err.println(count+" row is incorrect");
            } catch(SQLException ex){
                ex.printStackTrace();
            }
        }
        sc.close();
    }
}
