package com.github.loyada.jdollarx.singlebrowser.custommatchers;

import static com.github.loyada.jdollarx.RelationOperator.*;

import com.github.loyada.jdollarx.InBrowser;
import com.github.loyada.jdollarx.Path;
import com.github.loyada.jdollarx.custommatchers.CustomMatchersUtil.NTimesMatcher;
import com.github.loyada.jdollarx.singlebrowser.InBrowserSinglton;
import org.hamcrest.Matcher;

/**
 * Internal implementation - not to be instantiated directly.
 * This matcher is optimized for the success use-case. In that case it match for a single element
 * with exact number of elements wanted.
 * In case of failure, it will make another call to get the actual number of elements on
 * the page, in order to provide a detailed error message.
 * So the trade off is: In case of success it's faster, In case of failure it's slower. It makes sense since most
 * of the time we expect success.
 */
public class IsPresentNTimes {
    private final int nTimes;

    public IsPresentNTimes(int nTimes) {
        if (nTimes <= 0) {
            throw new IllegalArgumentException("use isAbsent() for asserting an element is not present");
        }
        this.nTimes = nTimes;
    }

    public Matcher<Path> times() {
        return new NTimesMatcher(nTimes, exactly, new InBrowser(InBrowserSinglton.driver));
    }

    public Matcher<Path> timesOrMore() {
        return new NTimesMatcher(nTimes, orMore, new InBrowser(InBrowserSinglton.driver));
    }

    public Matcher<Path> timesOrLess() {
        return new NTimesMatcher(nTimes, orLess, new InBrowser(InBrowserSinglton.driver));
    }
}
