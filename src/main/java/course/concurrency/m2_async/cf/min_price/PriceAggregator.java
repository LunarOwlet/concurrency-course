package course.concurrency.m2_async.cf.min_price;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class PriceAggregator {

    private PriceRetriever priceRetriever = new PriceRetriever();

    public void setPriceRetriever(PriceRetriever priceRetriever) {
        this.priceRetriever = priceRetriever;
    }

    private Collection<Long> shopIds = Set.of(10l, 45l, 66l, 345l, 234l, 333l, 67l, 123l, 768l);

    public void setShops(Collection<Long> shopIds) {
        this.shopIds = shopIds;
    }

    public double getMinPrice(long itemId) {
        List<CompletableFuture<Double>> futures = new ArrayList<>();
        shopIds.forEach(shopId -> futures.add(getFuturePrice(itemId, shopId)));

        return futures.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder())
                .orElse(Double.NaN);
    }

    private CompletableFuture<Double> getFuturePrice(long itemId, long shopId) {
        return CompletableFuture.supplyAsync(() -> priceRetriever.getPrice(itemId, shopId))
                .completeOnTimeout(null, 2900, TimeUnit.MILLISECONDS)
                .handle((res, ex) -> {
                    if (ex != null) {
                        System.out.println("Getting price for #" + itemId + " item from shop #" + shopId + " failed with exception: " + ex);
                        return null;
                    }
                    return res;
                });
    }
}
