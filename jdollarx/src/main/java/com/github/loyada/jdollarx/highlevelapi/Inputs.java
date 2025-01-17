package com.github.loyada.jdollarx.highlevelapi;

import com.github.loyada.jdollarx.InBrowser;
import com.github.loyada.jdollarx.Operations.OperationFailedException;
import com.github.loyada.jdollarx.Path;
import com.google.common.base.Strings;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.github.loyada.jdollarx.BasicPath.*;
import static com.github.loyada.jdollarx.ElementProperties.contains;
import static com.github.loyada.jdollarx.ElementProperties.hasAggregatedTextEqualTo;
import static com.github.loyada.jdollarx.ElementProperties.hasId;
import static com.github.loyada.jdollarx.ElementProperties.not;
import static com.github.loyada.jdollarx.HighLevelPaths.hasType;
import static com.github.loyada.jdollarx.NPath.exactly;
import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * High-level API to define and interact with various input elements.
 * High level API's are not optimized. A definition of an element may interact with the browser
 * to understand the structure of the DOM.
 */
public final class Inputs {
    static final int MAX_NUM_OF_TRIES_TO_CLEAR_INPUT = 20;

    private Inputs() {}

    /**
     * A lazy way to find an input based on the label. Mote that unlike
     * It looks for a label element that has an ID. If it finds one, it returns
     * a Path to an input with that ID. Otherwise it returns a Path to an  input
     * inside the label element.
     * @param browser the browser
     * @param labelText the label to look for
     * @return a Path to the input, on a best effort basis
     */
    public static Path inputForLabel(InBrowser browser, String labelText) {
        Path myLabel = label.that(hasAggregatedTextEqualTo(labelText.trim()));
        String theId = browser.find(myLabel).getAttribute("for");
        if (isNullOrEmpty(theId)) {
            return input.inside(myLabel);
        }
        return input.that(hasId(theId));
    }

    /**
     * A generic, reasonable guess of an input field in a form.
     * @param fieldName - the field before the input
     * @return a Path for the input field
     */
    public static Path genericFormInputAfterField(String fieldName) {
        Path fieldNameEl = element.that(hasAggregatedTextEqualTo(fieldName)).and(not(contains(input)));

        // note: we ensure the ancestor is not too high up in the DOM hierarchy
        Path ancestor = element.afterSibling(fieldNameEl).that(
                contains(exactly(1).occurrencesOf(input)));
        return (input.inside(ancestor)).or(
                input.afterSibling(fieldNameEl))
                .describedBy(String.format("input following field \"%s\"", fieldName));
    }

    /**
     * A generic, reasonable guess of an input field in a form.
     * @param fieldName - the field before the input
     * @return a Path for the input field
     */
    public static Path genericFormInputBeforeField(String fieldName) {
        Path fieldNameEl = element.that(hasAggregatedTextEqualTo(fieldName));

        // note: we ensure the ancestor is not too high up in the DOM hierarchy
        Path ancestor = element.immediatelyBeforeSibling(fieldNameEl).that(
                contains(exactly(1).occurrencesOf(input)));
        return lastOccurrenceOf((input.inside(ancestor)).or(
                input.immediatelyBeforeSibling(fieldNameEl)))
                .describedBy(String.format("input before field \"%s\"", fieldName));
    }

    /**
     * Input followed by text that does not have its on label element.
     * @param text the text following the input
     * @return a Path to the input element
     */
    public static Path inputFollowedByUnlabeledText(String text) {
        return input.immediatelyBeforeSibling(textNode(text));
    }

    /**
     * Takes an input element and returns such an input of type checkbox.
     * @param inp the input element
     * @return a Path to the input
     */
    public static Path checkboxType(Path inp) {
        return inp.that(hasType("checkbox"));
    }

    /**
     * Takes an input element and returns such an input of type radio.
     * @param inp the input element
     * @return a Path to the input
     */
    public static Path radioType(Path inp) {
        return inp.that(hasType("radio"));
    }

    /**
     * Clear operation on an input element. This enforces successful clear. If it fails, it throws
     * and exception.
     * @param browser the browser
     * @param field the input element
     */
    public static void clearInput(InBrowser browser, Path field) throws OperationFailedException {
        Inputs.clearInputInternal(browser, field, true);
    }

    /**
     * Clear operation on an input element. This enforces successful clear. If it fails, it throws
     * and exception.
     * @param browser the browser
     * @param field the input element
     */
    public static void clearInput(InBrowser browser, WebElement field) throws OperationFailedException {
        Inputs.clearInputInternal(browser, field);
    }

    /**
     * Quickly try to clear input, assuming it is not too long. This is not guaranteed to work.
     * @param browser the browser
     * @param field the input element
     */
    public static void quickTryClearInput(InBrowser browser, Path field) throws OperationFailedException {
        int MAX_LENGTH = 100;
        sendDeletionKeys(browser, MAX_LENGTH, field);
    }

    /**
     * Clear operation on an input element, but does not enforces a complete clear.
     * In other words, it will try to clear as much as it can, and not fail if it can clear it completely.
     * @param browser the browser
     * @param field the input element
     */
    public static void clearInputNonStrict(InBrowser browser, Path field) throws OperationFailedException {
        Inputs.clearInputInternal(browser, field, false);
    }


    private static void sendDeletionKeys(InBrowser browser, int length, Path field) throws OperationFailedException {
        String keysBack = IntStream.range(0, length)
                .mapToObj(i-> Keys.BACK_SPACE)
                .collect(Collectors.joining());
        String keysDel = IntStream.range(0, length)
            .mapToObj(i-> Keys.DELETE)
                .collect(Collectors.joining());
        browser.sendKeys(keysBack+keysDel).to(field);
    }

    private static void sendDeletionKeys(InBrowser browser, int length, WebElement field) throws OperationFailedException {
        String keysBack = IntStream.range(0, length)
                .mapToObj(i-> Keys.BACK_SPACE)
                .collect(Collectors.joining());
        String keysDel = IntStream.range(0, length)
                .mapToObj(i-> Keys.DELETE)
                .collect(Collectors.joining());
        browser.sendKeys(keysBack+keysDel).to(field);
    }

    private static void clearInputInternal(InBrowser browser, Path field, boolean enforce)
            throws OperationFailedException {
        // sometimes clear() works. Try that first.
        String value = browser.find(field).getAttribute("value");
        if (Strings.isNullOrEmpty(value)) {
            return;
        }
        browser.find(field).clear();

        value = browser.find(field).getAttribute("value");
        if (!Strings.isNullOrEmpty(value)) {
            sendDeletionKeys(browser, value.length(), field);
        }

        value = browser.find(field).getAttribute("value");
        if (Strings.isNullOrEmpty(value)) {
            return;
        }
        // previous clears don't work in all cases. If they didn't, use the expensive
        // method of sending one backspace at a time, until we guarantee it is empty
        boolean anythingLeft = true;
        int num_of_tries_left = enforce ? MAX_NUM_OF_TRIES_TO_CLEAR_INPUT : 3;
        int lastLength = Integer.MAX_VALUE;
        while(anythingLeft && num_of_tries_left>0) {
            num_of_tries_left-=1;
            String currentValue = browser.find(field).getAttribute("value");
            if (enforce && (currentValue.length()>lastLength || num_of_tries_left<1))
                throw new OperationFailedException("clearing input does not work. Is this an" +
                        " autocomplete or another custom input?");
            lastLength = currentValue.length();

            if (!Strings.isNullOrEmpty(currentValue)) {
                for (int i = 0; i < currentValue.length(); i++) {
                    browser.sendKeys(Keys.BACK_SPACE).to(field);
                }
                for (int i = 0; i < currentValue.length(); i++) {
                    browser.sendKeys(Keys.DELETE).to(field);
                }
            }
            anythingLeft = currentValue.length() > 0;
        }
    }

    private static void clearInputInternal(InBrowser browser, WebElement field)
            throws OperationFailedException {
        // sometimes clear() works. Try that first.
        String value = field.getAttribute("value");
        if (Strings.isNullOrEmpty(value)) {
            return;
        }
        field.clear();

        value = field.getAttribute("value");
        if (!Strings.isNullOrEmpty(value)) {
            sendDeletionKeys(browser, value.length(), field);
        }

        value = field.getAttribute("value");
        if (Strings.isNullOrEmpty(value)) {
            return;
        }
        // previous clears don't work in all cases. If they didn't, use the expensive
        // method of sending one backspace at a time, until we guarantee it is empty
        boolean anythingLeft = true;
        int num_of_tries_left = MAX_NUM_OF_TRIES_TO_CLEAR_INPUT;
        int lastLength = Integer.MAX_VALUE;
        while(anythingLeft && num_of_tries_left>0) {
            num_of_tries_left-=1;
            String currentValue = field.getAttribute("value");
            if (currentValue.length()>lastLength || num_of_tries_left<1)
                throw new OperationFailedException("clearing input does not work. Is this an" +
                        " autocomplete or another custom input?");
            lastLength = currentValue.length();

            if (!Strings.isNullOrEmpty(currentValue)) {
                for (int i = 0; i < currentValue.length(); i++) {
                    browser.sendKeys(Keys.BACK_SPACE).to(field);
                }
                for (int i = 0; i < currentValue.length(); i++) {
                    browser.sendKeys(Keys.DELETE).to(field);
                }
            }
            anythingLeft = currentValue.length() > 0;
        }
    }



    /**
     * Perform a selection of an option in a select element.
     * It expects to find the label element with the given text before the select element
     * @param browser the browser
     * @param labelText The text of the select label
     * @param option The option text
     * @return the Path of the select element
     */
    public static Path selectInFieldWithLabel(InBrowser browser, String labelText, String option) {
        Path selector = select.after(label.withText(labelText));
        browser.clickOn(selector);
        browser.getSelect(selector).selectByVisibleText(option);
        return selector;
    }

    /**
     * Change input value: clear it and then enter another text in it
     * @param browser the browser
     * @param field Path to the input field
     * @param text the text to enter in the input field
     * @throws OperationFailedException failed to perform the operation
     */
    public static void changeInputValue(InBrowser browser, Path field, String text)
            throws OperationFailedException {
        browser.clickOn(field);
        clearInput(browser, field);
        browser.sendKeys(text).to(field);
    }

    /**
     * Change input value: clear it and then enter another text in it. This assumes the input element is not
     * replaced by another element, as with that assumption, it is supposed to be slightly faster.
     * @param browser the browser
     * @param field Path to the input field
     * @param text the text to enter in the input field
     * @throws OperationFailedException failed to perform the operation
     */
    public static void changeInputValueAssumingElementIsNotReplaced(InBrowser browser, Path field, String text)
            throws OperationFailedException {
        WebElement el = browser.clickOn(field);
        clearInput(browser, el);
        browser.sendKeys(text).to(el);
    }

    /**
     * Change input value: Try to clear it first  and then enter another text in it.
     * Clearing the input focuses on speed and is not guaranteed.
     * @param browser the browser
     * @param field Path to the input field
     * @param text the text to enter in the input field
     * @throws OperationFailedException failed to perform the operation
     */
    public static void changeInputValueWithQuickApproximateDeletion(InBrowser browser, Path field, String text)
            throws OperationFailedException {
        browser.clickOn(field);
        quickTryClearInput(browser, field);
        browser.sendKeys(text).to(field);
    }

    /**
     * Change input value: clear it and then enter another text in it. This variation does not ensure cleaning
     * up the input, but tries to clean as much as it can.
     * @param browser the browser
     * @param field Path to the input field
     * @param text the text to enter in the input field
     * @throws OperationFailedException failed to perform the operation
     */
    public static void changeInputValueNonStrictClearing(InBrowser browser, Path field, String text)
            throws OperationFailedException {
        browser.clickOn(field);
        clearInputNonStrict(browser, field);
        browser.sendKeys(text).to(field);
    }

    /**
     * Similar to changeInputValue, but adds an ENTER after setting the value of the input
     * @param browser the browser
     * @param field Path to the input field
     * @param text the text to enter in the input field
     * @throws OperationFailedException failed to perform the operation
     */
    public static void changeInputValueWithEnter(InBrowser browser, Path field, String text)
            throws OperationFailedException {
       changeInputValue(browser, field, text);
        browser.sendKeys(Keys.ENTER).to(field);
    }

    /**
     * Similar to changeInputValue, but adds an ENTER after setting the value of the input
     * @param browser the browser
     * @param field Path to the input field
     * @param text the text to enter in the input field
     * @throws OperationFailedException failed to perform the operation
     */
    public static void changeInputValueWithApproximateDeletionWithEnter(InBrowser browser, Path field, String text)
            throws OperationFailedException {
        changeInputValueWithQuickApproximateDeletion(browser, field, text);
        browser.sendKeys(Keys.ENTER).to(field);
    }

    /**
     * Similar to changeInputValueNonStrictClearing, but adds an ENTER after setting the value of the input
     * @param browser the browser
     * @param field Path to the input field
     * @param text the text to enter in the input field
     * @throws OperationFailedException failed to perform the operation
     */
    public static void changeInputValueWithEnterNonStrictClearing(InBrowser browser, Path field, String text)
            throws OperationFailedException {
        changeInputValueNonStrictClearing(browser, field, text);
        browser.sendKeys(Keys.ENTER).to(field);
    }

    /**
     *
     */
    public static void selectDropdownOption(InBrowser browser, Path dropdownContent, Path myOption) {
        Path dropdown = element.parentOf(dropdownContent);
        Predicate<WebElement> isVisible = el -> {
            WebElement visibleContent = browser.find(dropdown);
            int bottomOfList = visibleContent.getSize().height + visibleContent.getLocation().getY();
            return el.isDisplayed() && (el.getLocation().y  + el.getSize().height <= bottomOfList);
        };

        browser.scrollElement(dropdown).toTopCorner();

        long timeoutInMillisec = browser.getImplicitTimeoutInMillisec();
        browser.setImplicitTimeout(10, MILLISECONDS);
        try {
            browser.scrollElementWithStepOverride(dropdown, 50).downUntilPredicate(myOption, isVisible);
        } finally {
            browser.setImplicitTimeout((int) timeoutInMillisec, MILLISECONDS);
        }

        // Unfortunately we have to give time for the rendering to stabilize
        // otherwise, because selenium click is non-atomic, the content can switch between
        // finding the element and executing a click.
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        browser.clickOn(myOption);
    }
}
