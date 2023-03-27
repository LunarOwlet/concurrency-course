package course.concurrency.m3_shared.collections;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class RestaurantService {

    private Map<String, Restaurant> restaurantMap = new ConcurrentHashMap<>() {{
        put("A", new Restaurant("A"));
        put("B", new Restaurant("B"));
        put("C", new Restaurant("C"));
    }};

    private Map<String, Integer> stat = new ConcurrentHashMap<>() {{
        put("A", 0);
        put("B", 0);
        put("C", 0);
    }};

    public Restaurant getByName(String restaurantName) {
        addToStat(restaurantName);
        return restaurantMap.get(restaurantName);
    }

    public void addToStat(String restaurantName) {
        stat.computeIfPresent(restaurantName, (k, v) -> v + 1);
    }

    public Set<String> printStat() {
        return stat.entrySet()
                .stream()
                .map(entry -> entry.getKey() + " - " + entry.getValue())
                .collect(Collectors.toSet());
    }
}
