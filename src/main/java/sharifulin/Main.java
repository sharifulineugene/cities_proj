package sharifulin;

import sharifulin.db.DatabaseWorker;
import sharifulin.entity.City;

import java.sql.SQLException;
import java.util.Scanner;
import java.util.Set;

public class Main {
    public static void main(String[] args){
        String file = null;
        DatabaseWorker db = new DatabaseWorker();
        if(args.length !=0)
        for(int i=0;i<args.length;++i) {
           switch(args[i]) {
               case("-f"): {
                   file = args[i+1];
                   break;
               }

               case("--help"): {}
               case("-h"): {
                   System.out.println("Для запуска приложения укажите входной файл через ключ -f:" +
                           "\tПример: java Main -f /etc/fstab");
                   try {
                       Thread.sleep(5000);
                   } catch (InterruptedException e) {
                       e.printStackTrace();
                   }
                   System.exit(1);
                   break;
               } default:
                   System.out.println("\n\nВыбран файл по умолчанию");
                   break;
           }
        }
        if(file == null)
            file = "src/main/resources/sources.txt";
        Scanner scanner = new Scanner(System.in);
        System.out.println("Выберите действие:" +
                "\n\t1)Список всех городов." +
                "\n\t2)Отсортированный список городов по названию в прямом порядке" +
                "\n\t3)Отсортированный список в обратном порядке" +
                "\n\t4)Сортировка списка городов по федеральному округу и наименованию города внутри ФО в алфавитном порядке" +
                "\n\t5)Поиск города с наибольшим количеством жителей(через запроссы SQL)" +
                "\n\t6)Поиск города с наибольшим количеством жителей(через перебор массива)" +
                "\n\t7)Поиск количества городов в разрезе регионов");
        int number = scanner.nextInt();
        scanner.close();
        db.scanningFile(file);
        switch(number) {
            case(1): {
                db.printValues();
                break;
            }
            case(2): {
                db.getSortedValues(true);
                break;
            }
            case(3): {
                db.getSortedValues(false);
                break;
            }
            case(4): {
                db.sortedByDistrictAndName();
                break;
            }
            case(5): {
                db.getGroupByPopulation();
                break;
            }
            case(6): {
                db.getGroupByPopulation2();
                break;
            }
            case(7): {
                db.countOfCities();
                break;
            }
            default: {
                System.err.println("Incorrect input number ...\nSwitched number to 1");
            }
        }

    }
}
