package br.com.razzie.utils;

import br.com.razzie.dtos.WinInfoResponseDTO;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class IntervalUtil {

    public static final String MIN = "min";
    public static final String MAX = "max";

    public static List<WinInfoResponseDTO> calculateMinMaxInterval(List<Integer> yearsSorted, String producerName) {
        log.debug("IntervalUtil.calculateMinMaxInterval - start - Calculating min and max intervals for producer: {}", producerName);

        int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
        int minPrev = 0, minNext = 0, maxPrev = 0, maxNext = 0;

        for (int i = 1; i < yearsSorted.size(); i++) {
            int prev = yearsSorted.get(i - 1);
            int curr = yearsSorted.get(i);
            int diff = curr - prev;

            log.debug("IntervalUtil.calculateMinMaxInterval - Comparing years: {} and {} - Difference: {}", prev, curr, diff);

            if (diff < min) {
                log.debug("IntervalUtil.calculateMinMaxInterval - Found new min interval: {} ({} - {})", diff, curr, prev);
                min = diff;
                minPrev = prev;
                minNext = curr;
            }
            if (diff > max) {
                log.debug("IntervalUtil.calculateMinMaxInterval - Found new max interval: {} ({} - {})", diff, curr, prev);
                max = diff;
                maxPrev = prev;
                maxNext = curr;
            }
        }

        if (min == max) {
            log.debug("IntervalUtil.calculateMinMaxInterval - Min and Max intervals, Interval: {}, Previous: {}, Next: {}", min, minPrev, minNext);
            return List.of(new WinInfoResponseDTO(producerName, min, minPrev, minNext));
        }

        log.debug("IntervalUtil.calculateMinMaxInterval - Min interval: {}, Previous: {}, Next: {}", min, minPrev, minNext);
        log.debug("IntervalUtil.calculateMinMaxInterval - Max interval: {}, Previous: {}, Next: {}", max, maxPrev, maxNext);
        return List.of(
                new WinInfoResponseDTO(producerName, min, minPrev, minNext),
                new WinInfoResponseDTO(producerName, max, maxPrev, maxNext)
        );
    }
}
