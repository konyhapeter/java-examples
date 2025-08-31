package app.java8;

import java.time.LocalDate;
import java.time.Month;
import java.time.Period;
import java.util.Arrays;
import java.util.List;

public class Main {

    static void main() {
        List<String> words = Arrays.asList("apple", "banana", "cherry", "date");

        List<String> result = words.stream()
                .filter(w -> w.startsWith("b"))
                .map(String::toUpperCase).toList();

        System.out.println(result); // [BANANA]

        LocalDate today = LocalDate.now();
        LocalDate birthday = LocalDate.of(1993, Month.MARCH, 12);

        Period age = Period.between(birthday, today);

        System.out.println("Today: " + today);
        System.out.println("Age: " + age.getYears() + " years");
        System.out.println("Months: " + age.getMonths());
        System.out.println("Days: " + age.getDays());
        new Car().start();

        List<String> names = Arrays.asList("Anna", "Brian", "Chris");

        // Using lambda
        names.forEach(name -> System.out.println(name));
        System.out.println();
        // Using method reference
        names.forEach(System.out::println);
    }
}

interface Vehicle {
    default void start() {
        System.out.println("Starting vehicle...");
    }
}

class Car implements Vehicle {}
