import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Class with {@link #matches(String, String)},
 * that works without throwing exceptions and only limited time - {@link #MAX_TIMEOUT_SECONDS_TIME}
 */
public class MatchesProblem {
    /**
     * Time in seconds after the expiry of which {@link #matches(String, String)} return {@code false}
     */
    private final static long MAX_TIMEOUT_SECONDS_TIME = 3;

    /**
     * @param text  text to be matched
     * @param regex the expression to be compiled and matched
     * @return If timeout reached or exception thrown, return {@code false}.
     * Return result of {@link Matcher#matches()} otherwise.
     */
    public static boolean matches(final String text, final String regex) {
        // Compile pattern
        final Pattern pattern;
        try {
            pattern = Pattern.compile(regex);
        } catch (final IllegalArgumentException e) {
            return false;
        }
        final Matcher matcher = pattern.matcher(text);
        // Create single thread with matches via executorService
        final ExecutorService executorService = Executors.newSingleThreadExecutor();
        final Future<Boolean> futureResult = executorService.submit(matcher::matches);
        /* Run matches with timer
         * If any exceptions thrown result will be false
         */
        boolean matchesAnswer = false;
        try {
            matchesAnswer = futureResult.get(MAX_TIMEOUT_SECONDS_TIME, TimeUnit.SECONDS);
        } catch (final TimeoutException e) {
            futureResult.cancel(true);
        } catch (final ExecutionException | InterruptedException e) {
            // Ignored
        } finally {
            executorService.shutdown();
        }
        return matchesAnswer;
    }
}
