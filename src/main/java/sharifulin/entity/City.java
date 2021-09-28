package sharifulin.entity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class City implements Comparable<City>{
    private final String name;
    private final String region;
    private final String district;
    private long population;
    private final String foundation;
    private final int hash;

    private static final Pattern pattern = Pattern.compile("([0-9]*);(.*?);(.*?);(.*?);(.*?);(.*?);");

    public void setPopulation(long population) {
        if(population < 0) {
            throw new RuntimeException("New population less than 0");
        }
        this.population = population;
    }

    public String getName() {
        return name;
    }

    public String getRegion() {
        return region;
    }

    public String getDistrict() {
        return district;
    }

    public long getPopulation() {
        return population;
    }

    public String getFoundation() {
        return foundation;
    }

    public int getHash() {
        return hash;
    }

    @Override
    public String toString() {
        return "City{" +
                "name='" + name + '\'' +
                ", region='" + region + '\'' +
                ", district='" + district + '\'' +
                ", population='" + population + '\'' +
                ", foundation='" + foundation + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        City city = (City) o;
        return name.equalsIgnoreCase(city.name) && region.equalsIgnoreCase(city.region);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name.toLowerCase(Locale.ROOT), region.toLowerCase(Locale.ROOT));
    }

    private City(String name, String region, String district, long population, String foundation) {
        this.name = name;
        this.region = region;
        this.district = district;
        this.population = population;
        this.foundation = foundation;
        this.hash = this.hashCode();
    }

    public static City parseString(String str) throws IllegalArgumentException{
        if(str != null) {
            Matcher matcher = pattern.matcher(str);
            if(!matcher.matches()) throw new IllegalArgumentException("argument is null");
            City temp;
            String temp_name, temp_region, temp_district, temp_foundation;
            long temp_population;
            temp_name = matcher.group(2);
            temp_region = matcher.group(3);
            temp_district = matcher.group(4);
            temp_population = Long.parseLong(matcher.group(5));
            temp_foundation = matcher.group(6);
            temp = new City(temp_name, temp_region, temp_district, temp_population, temp_foundation);
            return temp;
        } else throw new IllegalArgumentException("argument is null");
    }

    @Override
    public int compareTo(City o) {
        int result;
        if((result=this.name.compareToIgnoreCase(o.name)) != 0)
            return result;
        else return this.region.compareToIgnoreCase(o.region);
    }

    public static Set<City> transformFromResultSet(ResultSet rs) throws SQLException {
        Set<City> cities = new HashSet<>();
        String temp_name, temp_region, temp_district, temp_foundation;
        long temp_population;
        int hash;
        while(rs.next()){
            hash = rs.getInt(1);
            temp_name = rs.getString(2);
            temp_region = rs.getString(3);
            temp_district = rs.getString(4);
            temp_population = rs.getLong(5);
            temp_foundation = rs.getString(6);
            cities.add(new City(temp_name, temp_region, temp_district, temp_population, temp_foundation));
        }
        return cities;
    }
}
