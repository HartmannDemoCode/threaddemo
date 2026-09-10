package dk.ek;

//import java.util.concurrent.

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) throws InterruptedException {
        HttpFetcher fetcher = new HttpFetcher();
        String[] urls = new String[]{
                "https://icanhazdadjoke.com/"
                , "https://api.chucknorris.io/jokes/random"
                , "https://api.kanye.rest"
                , "https://api.whatdoestrumpthink.com/api/v1/quotes/random"
//                ,"https://v2.jokeapi.dev/joke/Coding?type=single"
//                ,"https://geek-jokes.sameerkumar.website/api?format=json"
//                ,"https://official-joke-api.appspot.com/random_joke"
        };

        ExecutorService executorService = Executors.newFixedThreadPool(4);
        try {

            List<Callable<String>> callableList = new ArrayList<>();
            List<Future<String>> futureList = new ArrayList<>();
            List<String> jokes = new ArrayList<>();

            // 1. Create the Callables
            for (String url : urls) {
                Callable<String> callable = () -> {
                    return fetcher.fetch(url).content();
                };
                callableList.add(callable);
            }

            // 2. Create the Futures
            for (Callable<String> callable : callableList) {
                Future<String> future = executorService.submit(callable);
                futureList.add(future);
            }

            // 3. Create the Jokes as json strings (containing the full json with meta data etc.)
            for (Future<String> future : futureList) {
                try {
                    String joke = future.get();
                    jokes.add(joke);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }

            // Extract the actual joke and set it in the JokeDTO according to index in jokes list
            try {
                JokesDTO jokesDTO = new JokesDTO(
                        extractJoke(jokes.get(0)),
                        extractJoke(jokes.get(1)),
                        extractJoke(jokes.get(2)),
                        extractJoke(jokes.get(3))
                );
                System.out.println(jokesDTO);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } finally {
            executorService.shutdown();
        }
    }

    // Use the ObjectMapper to extract the actual joke from the json string and return it as a String
    private static String extractJoke(String json) throws JsonProcessingException {
        return objectMapper.readValue(json, JokeRecord.class).joke();
    }

    public void test() throws InterruptedException {

        Person person = new Person("Holger", 33);
        System.out.println("Hi from " + Thread.currentThread().getName());
        Thread thread1 = new Thread(() -> System.out.println("Hi from " + Thread.currentThread().getName()));
        Runnable runnable = new Runnable() {

            @Override
            public void run() {

                System.out.println("Hi from " + Thread.currentThread().getName());
            }
        };
        thread1.start();
        Thread thread2 = new Thread(new MyRunnable(person));
        thread2.start();
        System.out.println(person);
        thread1.join();
        thread2.join();
        System.out.println("After join: " + person);
        System.out.println("Finish program");
    }

    private static class MyRunnable implements Runnable {
        Person person;

        public MyRunnable(Person person) {
            this.person = person;
        }

        @Override
        public void run() {
            person.setName("Helga");
        }
    }

    private static class Person {
        private String name;
        private final int age;

        private Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        private void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "Person{name='" + name + "', age=" + age + "}";
        }
    }

    private record JokesDTO(
            String dadJoke,
            String chuckJoke,
            String kanyeJoke,
            String trumpJoke
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record JokeRecord(
            @JsonAlias({"message", "value", "joke", "setup", "quote"})
            String joke
    ) {
    }
}
