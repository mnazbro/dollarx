package com.github.loyada.jdollarx.singlebrowser;

import com.github.loyada.jdollarx.*;
import com.github.loyada.jdollarx.highlevelapi.CheckBox;
import com.github.loyada.jdollarx.highlevelapi.RadioInput;
import com.github.loyada.jdollarx.singlebrowser.highlevelapi.CheckBoxes;
import com.github.loyada.jdollarx.singlebrowser.highlevelapi.Inputs;
import com.github.loyada.jdollarx.singlebrowser.highlevelapi.RadioInputs;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;

import java.net.URL;
import java.util.concurrent.TimeUnit;

import static com.github.loyada.jdollarx.BasicPath.button;
import static com.github.loyada.jdollarx.BasicPath.div;
import static com.github.loyada.jdollarx.BasicPath.element;
import static com.github.loyada.jdollarx.BasicPath.firstOccurrenceOf;
import static com.github.loyada.jdollarx.BasicPath.input;
import static com.github.loyada.jdollarx.BasicPath.option;
import static com.github.loyada.jdollarx.BasicPath.select;
import static com.github.loyada.jdollarx.ElementProperties.hasId;
import static com.github.loyada.jdollarx.singlebrowser.InBrowserSinglton.clickOn;
import static com.github.loyada.jdollarx.singlebrowser.InBrowserSinglton.driver;
import static com.github.loyada.jdollarx.singlebrowser.InBrowserSinglton.find;
import static com.github.loyada.jdollarx.singlebrowser.custommatchers.CustomMatchers.isPresent;
import static com.github.loyada.jdollarx.utils.PathShortNames.parentOf;
import static java.lang.Boolean.*;
import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.fail;

public class HighLevelPathsIntegration {
    @BeforeClass
    public static void setup() {
        driver = DriverSetup.createStandardChromeDriver();
    }

    private void load_html_file(String path) {
        URL url = HighLevelPathsIntegration.class.getClassLoader().getResource("html/" + path);
        assert url != null;
        driver.get(url.toString());
    }

    @Before
    public void refresh()  {
        driver.navigate().refresh();
    }

    @AfterClass
    public static void teardown(){
        driver.quit();
    }

    @Test
    public void testInputFor(){
        driver.get("https://www.w3schools.com/tags/tryit.asp?filename=tryhtml5_input_type_checkbox");
        driver.switchTo().frame("iframeResult");
        Path myInput = HighLevelPaths.inputFor(" I have a car");
        clickOn(myInput);
    }

    @Test
    public void testCheckbox(){
        driver.get("https://www.w3schools.com/tags/tryit.asp?filename=tryhtml5_input_type_checkbox");
        driver.switchTo().frame("iframeResult");
        CheckBox checkBox =  CheckBoxes.checkBoxWithLabel("I have a car");
        checkBox.check();
        assertTrue(checkBox.isChecked());
        assertThat(checkBox.toString(), equalTo("checkbox for 'I have a car'"));
    }

    @Test
    public void testCheckbox2(){
        driver.get("https://www.w3schools.com/tags/tryit.asp?filename=tryhtml5_input_type_checkbox");
        driver.switchTo().frame("iframeResult");
        CheckBox checkBox =  CheckBoxes.checkboxForInput(Inputs.inputForLabel("I have a car"),
                "have car");
        checkBox.check();
        assertTrue(checkBox.isChecked());
        assertThat(checkBox.toString(), equalTo("checkbox for 'have car'"));
    }

    @Test
    public void testCheckboxUnceck(){
        driver.get("https://www.w3schools.com/tags/tryit.asp?filename=tryhtml5_input_type_checkbox");
        driver.switchTo().frame("iframeResult");
        CheckBox checkBox =  CheckBoxes.checkBoxWithLabel("I have a car");
        checkBox.check();
        checkBox.uncheck();
        assertFalse(checkBox.isChecked());
    }

    @Test
    public void testCheckboxUnceck1(){
        driver.get("https://www.w3schools.com/tags/tryit.asp?filename=tryhtml5_input_type_checkbox");
        driver.switchTo().frame("iframeResult");
        CheckBox checkBox =  CheckBoxes.checkboxForInput(
                Inputs.genericFormInputBeforeField("I have a car"), "have car");
        checkBox.check();
        checkBox.uncheck();
        assertFalse(checkBox.isChecked());
    }

    @Test
    public void testInputFor2(){
        driver.get("https://blueprintjs.com/docs/#core/components/checkbox");
        CheckBox checkbox =  CheckBoxes.checkBoxWithLabel("Checkbox");
        checkbox.check();
        assertTrue(checkbox.isChecked());
    }

    @Test
    public void inputWithProps(){
        driver.get("https://www.w3schools.com/tags/tryit.asp?filename=tryhtml5_input_type_checkbox");
        driver.switchTo().frame("iframeResult");
        CheckBox checkbox =  CheckBoxes.checkBoxWithProperties(ElementProperties.isNthSibling(3));
        checkbox.check();
        assertTrue(checkbox.isChecked());
        assertThat(checkbox.toString(), equalTo("checkbox, that is in place 3 among its siblings"));
    }

    @Test
    public void testRadioUnlabeledText(){
        load_html_file("input-example1.html");
        RadioInput myInput = RadioInputs.withUnlabeledText("Female");
        myInput.select();
        assertTrue(myInput.isSelected());
    }

    @Test
    public void testRadioUnknown(){
        driver.get("https://www.w3schools.com/html/tryit.asp?filename=tryhtml_input_radio");
        driver.switchTo().frame("iframeResult");
        RadioInput myInput = RadioInputs.withTextUnknownDOM("CSS", 5, TimeUnit.SECONDS);
        myInput.select();
        assertTrue(myInput.isSelected());
    }

    @Test
    public void testRadioUnknownLabel(){
        load_html_file("input-example2.html");
        RadioInput myInput = RadioInputs.withTextUnknownDOM("Female", 5, TimeUnit.SECONDS);
        myInput.select();
        assertTrue(myInput.isSelected());
    }

    @Test
    public void testRadioUnknownNoLabel(){
        load_html_file("input-example1.html");
        RadioInput myInput = RadioInputs.withTextUnknownDOM("Female", 5, TimeUnit.SECONDS);
        myInput.select();
        assertTrue(myInput.isSelected());
    }

    @Test
    public void testRadio2(){
        driver.get("https://blueprintjs.com/docs/#core/components/radio");
        RadioInput radio = RadioInputs.withLabeledText("Radio");
        radio.select();
        assertTrue(radio.isSelected());
    }

    @Test
    public void inputForLabelShouldFigureOutTheCorrectWayToFindIt() {
        load_html_file("input-example2.html");
        Path path = Inputs.inputForLabel("Male");
        assertThat(path.toString(), equalTo("input, that has Id \"male\""));
    }

    @Test
    public void genericInputShouldFigureOutTheCorrectWayToFindIt() {
        load_html_file("input-example2.html");
        Path path = Inputs.genericFormInputAfterField("Male");
        assertThat(path.toString(), equalTo("input following field \"Male\""));
    }

    @Test
    public void genericInputFieldShouldWorkForMostCases() throws Operations.OperationFailedException {
        load_html_file("input-example3.html");
        Path myInput = Inputs.genericFormInputAfterField("xxx");
        Inputs.changeInputValue(myInput, "abc");
        String value =  find(myInput).getAttribute("value");
        assertThat(value, equalTo("abc"));
    }

    @Test
    public void genericInputFieldShouldWorkForMostCasesElementIsNotReplaced() throws Operations.OperationFailedException {
        load_html_file("input-example3.html");
        Path myInput = Inputs.genericFormInputAfterField("xxx");
        Inputs.changeInputValueAssumingElementIsNotReplaced(myInput, "abc");
        String value =  find(myInput).getAttribute("value");
        assertThat(value, equalTo("abc"));
    }

    @Test
    public void genericInputFieldShouldWorkForMostCasesApproximateDeletion() throws Operations.OperationFailedException {
        load_html_file("input-example3.html");
        Path myInput = Inputs.genericFormInputAfterField("xxx");
        Inputs.changeInputValueWithApproximateDeletion(myInput, "abc");
        String value =  find(myInput).getAttribute("value");
        assertThat(value, equalTo("abc"));
    }

    @Test
    public void selectOption() {
        InBrowserSinglton.setImplicitTimeout( 5000, MILLISECONDS);
        load_html_file("input-example3.html");
        Path myOption = option.withText("Wyoming");
        Path dropdown = select.parentOf(myOption);
        clickOn(dropdown);
        Inputs.selectDropdownOption(dropdown, myOption);
        assertThat(find(parentOf(myOption)).getAttribute("value"), equalTo("WY"));
    }

    @Test
    public void inputFollowedByUnlabeledTextFindsIt() {
        load_html_file("input-example3.html");
        Path myInput = Inputs.inputFollowedByUnlabeledText("Male");
        clickOn(myInput);
        String inputType = find(myInput).getAttribute("type");
        assertThat(inputType, equalTo("radio"));
    }

    @Test
    public void afterClearingInputItShouldBeEmpty() throws Operations.OperationFailedException {
        load_html_file("input-example3.html");
        Path myInput = Inputs.inputForLabel("xxx");
        Inputs.clearInput(myInput);
        String value =  find(myInput).getAttribute("value");
        assertThat(value, equalTo(""));
    }

    @Test
    public void selectingAnOptionFromASelectElementAndVerifyItIsSelected() {
        load_html_file("input-example3.html");
        Inputs.selectInFieldWithLabel("Choose a car:", "Volvo");
        Path theOption = option.withText("volvo");
        assertTrue(parseBoolean(find(theOption).getAttribute("selected")));
    }

    @Test
    public void changeInputValueAndVerifyItWasChanged() throws Operations.OperationFailedException {
        load_html_file("input-example3.html");
        Path myInput = Inputs.inputForLabel("xxx");
        Inputs.changeInputValue(myInput, "abc");
        String value =  find(myInput).getAttribute("value");
        assertThat(value, equalTo("abc"));
    }

    @Test
    public void changeInputValueNonStrictAndVerifyItWasChanged() throws Operations.OperationFailedException {
        load_html_file("input-example3.html");
        Path myInput = Inputs.inputForLabel("xxx");
        Inputs.changeInputValueNonStrictClearing(myInput, "abc");
        String value =  find(myInput).getAttribute("value");
        assertThat(value, equalTo("abc"));
    }

    @Test
    public void changeInputWithEnterAndVerifyItWasChanged() throws Operations.OperationFailedException {
        load_html_file("input-example3.html");
        Path myInput = Inputs.inputForLabel("xxx");
        Inputs.changeInputValueWithEnter(myInput, "abc");
        String value =  find(myInput).getAttribute("value");
        assertThat(value, equalTo("abc"));
        assertThat(BasicPath.form.withClass("submitted"), isPresent());
    }

    @Test
    public void changeInputWithEnterApproximateDeletionAndVerifyItWasChanged() throws Operations.OperationFailedException {
        load_html_file("input-example3.html");
        Path myInput = Inputs.inputForLabel("xxx");
        Inputs.changeInputValueWithEnterApproximateDeletion(myInput, "abc");
        String value =  find(myInput).getAttribute("value");
        assertThat(value, equalTo("abc"));
        assertThat(BasicPath.form.withClass("submitted"), isPresent());
    }

    @Test
    public void waiterWorks() {
        load_html_file("input-example3.html");
        Path disabledButton = BasicPath.lastOccurrenceOf(button);
        InBrowserSinglton.setImplicitTimeout(3, TimeUnit.SECONDS);
        long start = System.currentTimeMillis();
        try {
            clickOn(disabledButton);
            fail();
        } catch (TimeoutException e) {
            long timeElapsed = System.currentTimeMillis() - start;
            assertThat((int)timeElapsed, greaterThan(3000));
            assertThat((int)timeElapsed, lessThan(4000));
        }
    }

    @Test
    public void waitUntilStableWaits() {
        load_html_file("input-example3.html");
        long start = System.currentTimeMillis();
        int numOfIterations1 = InBrowserSinglton.waitUntilStable(element, 10);
        long timeElapsed1 = System.currentTimeMillis() - start;
        assertThat((int)timeElapsed1, lessThan(200));
        InBrowserSinglton.driver.get("https://www.ag-grid.com/example.php");
        start = System.currentTimeMillis();
        // Now change the browser screen size repeatedly to change rows
        int numOfIterations2 = InBrowserSinglton.waitUntilStable(AgGrid.rowOfGrid(div.that(hasId("myGrid"))), 5000);
        long timeElapsed2 = System.currentTimeMillis() - start;
        System.out.printf("numOfIterations2: %d, timeElapsed2: %d%n", numOfIterations2, timeElapsed2);
    }

    @Test
    public void countingElements() {
        load_html_file("input-example3.html");
        int count = InBrowserSinglton.countAll(input);
        assertThat(count, equalTo(InBrowserSinglton.findAll(input).size()));
    }

    @Test
    public void isCoveredWorks() {
        load_html_file("cover.html");
        assertTrue(InBrowserSinglton.isCovered(button));
        assertTrue(InBrowserSinglton.isCovered(div.withClass("container")));
        assertFalse(InBrowserSinglton.isCovered(firstOccurrenceOf(div.withClass("box"))));
        assertFalse(InBrowserSinglton.isCovered(firstOccurrenceOf(div.withClass("box"))));

    }
    @Test
    public void timeoutOverrideWorks() {
        load_html_file("input-example3.html");
        InBrowserSinglton.setImplicitTimeout(3, TimeUnit.SECONDS);

        long start = System.currentTimeMillis();
        try(TemporaryChangedTimeout timeout = new TemporaryChangedTimeout(100, TimeUnit.MILLISECONDS)) {
            Inputs.inputForLabel("qweqweqweqwe");
            fail();
        } catch (NoSuchElementException ex){
            long timeElapsed = System.currentTimeMillis() - start;
            assertThat((int)timeElapsed, lessThan(200));
        } catch (Exception ex) {
            fail();
        }

        // verify we are back to normal timeout
        try {
            Inputs.inputForLabel("qweqweqweqwe");
            fail();
        } catch (NoSuchElementException ex){
            long timeElapsed = System.currentTimeMillis() - start;
            assertThat((int)timeElapsed, lessThan(3500));
            assertThat((int)timeElapsed, greaterThan(2999));
        } catch (Exception ex) {
            fail();
        }
    }

}
