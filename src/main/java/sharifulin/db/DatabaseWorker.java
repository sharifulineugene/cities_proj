package sharifulin.db;

import sharifulin.entity.City;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

public class DatabaseWorker{
    private static final String jdbc = "jdbc:h2:/Users/u19571283/IdeaProjects/sber_project/src/main/resources/database";


    static {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
	    System.exit(1);	
        }
    }



    public void updateValues(City c){
        try(Connection conn = DriverManager.getConnection(jdbc,"sa","");
            PreparedStatement updateStatement = conn.prepareStatement("UPDATE city SET population = ? where id = ?"))
        {
            updateStatement.setLong(1,c.getPopulation());
            updateStatement.setInt(2,c.getHash());
            updateStatement.executeUpdate();
        }catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertValues(City c) throws SQLException{
        Connection conn = DriverManager.getConnection(jdbc,"sa","");
        PreparedStatement insertStatement = conn.prepareStatement(
                "INSERT INTO city(id,name,region,district,population,foundation)" +
                        " values(?,?,?,?,?,?)");

            insertStatement.setInt(1, c.getHash());
            insertStatement.setString(2, c.getName());
            insertStatement.setString(3, c.getRegion());
            insertStatement.setString(4, c.getDistrict());
            insertStatement.setLong(5, c.getPopulation());
            insertStatement.setString(6, c.getFoundation());
            insertStatement.executeUpdate();
        if(insertStatement != null) insertStatement.close();
        if(conn != null) conn.close();
    }

    public Set<City> getValues() {
        Set<City> cities= null;
        try(Connection conn = DriverManager.getConnection(jdbc,"sa","");
        PreparedStatement simpleSelectStatement = conn.prepareStatement("SELECT * FROM city"))
        {
            ResultSet results = simpleSelectStatement.executeQuery();
            cities = City.transformFromResultSet(results);
        }catch(SQLException e) {
            e.printStackTrace();
        }
        return cities;
    }

    public void printValues(){
        System.out.println(getValues());
    }

    public void getSortedValues(boolean ascending) {
        TreeSet<City> cities = new TreeSet<City>(getValues());
        if(!ascending) {
            cities = (TreeSet)cities.descendingSet();
        }
        System.out.println(cities);
    }
    public void getGroupByPopulation(){
        try(Connection conn = DriverManager.getConnection(jdbc,"sa","");
        PreparedStatement groupByPopulation = conn.prepareStatement("select * from city where POPULATION=(select max(population) from city)")) {
            Set<City> cities = City.transformFromResultSet(groupByPopulation.executeQuery());
            System.out.println(cities);
        }catch(SQLException e) {
            e.printStackTrace();
        }

    }

    public void sortedByDistrictAndName() {
        try(Connection conn = DriverManager.getConnection(jdbc,"sa","");
        PreparedStatement sortedByDistrictAndNameStatement = conn.prepareStatement("SELECT * FROM city ORDER BY district,name")) {
            Set<City> cities = City.transformFromResultSet(sortedByDistrictAndNameStatement.executeQuery());
            System.out.println(cities);
        }catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void getGroupByPopulation2() {
        try(Connection conn = DriverManager.getConnection(jdbc,"sa","");
        PreparedStatement groupByPopulation = conn.prepareStatement("select * from city where POPULATION=(select max(population) from city)")) {
            Set<City> cities = City.transformFromResultSet(groupByPopulation.executeQuery());
            City[] ar_city = new City[cities.size()];
            int i = 0;
            long max = 0;
            int max_i = 0;
            for (City c : cities) {
                ar_city[i] = c;
                if (max < c.getPopulation()) {
                    max = c.getPopulation();
                    max_i = i;
                }
                ++i;
            }
            System.out.println("[" + max_i + "]=" + max);
        }catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void countOfCities() {
        try(PreparedStatement stmnt = DriverManager.getConnection(jdbc,"sa","")
                .prepareStatement("select upper(region), count(name) from city group by upper(region)")) {
            ResultSet rs = stmnt.executeQuery();
            while (rs.next()) {
                StringBuilder strbld = new StringBuilder();
                strbld.append(rs.getString(1));
                strbld.append(" - ");
                strbld.append(rs.getInt(2));
                System.out.println(strbld.toString());
            }
        }catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void scanningFile(String arg) {
        Scanner sc = null;

        try {
            if(arg != null) {
                sc = new Scanner(new File(arg));
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
            }
        }
        sc.close();
    }

}
