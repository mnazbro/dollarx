package com.github.loyada.jdollarx.visual;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.stream.IntStream;

import static com.github.loyada.jdollarx.visual.ImageComparator.getErrorImage;
import static com.github.loyada.jdollarx.visual.ImageComparator.verifyImagesAreEqual;
import static com.github.loyada.jdollarx.visual.ImageComparator.verifyImagesAreEqualWithShift;
import static com.github.loyada.jdollarx.visual.ImageComparator.verifyImagesAreSimilar;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.fail;

public class ImagesComparatorTest {
    private static BufferedImage image1;
    private BufferedImage image2;

    @Rule
    public final ExpectedException exception = ExpectedException.none();


    @BeforeClass
    public static void setupAll() throws IOException {
        ClassLoader classLoader = ImagesComparatorTest.class.getClassLoader();
        File file = new File(classLoader.getResource("sample1.png").getFile());
        image1 =  ImageIO.read(file);
    }

    @Before
    public void setup() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("sample1.png").getFile());
        image2 =  ImageIO.read(file);
    }

    @Test
    public void identityIsEqual() {
        verifyImagesAreEqual(image1, image1);
    }

    @Test
    public void identityIsSimilar() {
        verifyImagesAreSimilar(image1, image1, 100);
    }

    @Test
    public void identityIsShifted() {
        verifyImagesAreEqualWithShift(image1, image1, 10);
    }

    @Test
    public void equalityWrongPixel() {
        image2.setRGB(1, 5, 100);
        try {
            verifyImagesAreEqual(image1, image2);
            fail("should throw");
        } catch (AssertionError e) {
            assertThat(e.getMessage(), containsString("found a different pixel at 1, 5"));
        }
    }

    @Test(expected=AssertionError.class)
    public void shiftedWrongPixel() {
        image2.setRGB(100, 50, 100);
        verifyImagesAreEqualWithShift(image1, image2, 5);
    }

    @Test
    public void equalityWrongWidth() {
        try {
            verifyImagesAreEqual(image1,
                    image1.getSubimage(0, 0, image1.getWidth() - 1, image1.getHeight()));
            fail("should throw");
        } catch (AssertionError e) {
            assertThat(e.getMessage(), startsWith("width"));
        }
    }

    @Test
    public void equalityWrongHeight() {
        try {
            verifyImagesAreEqual(image1,
                    image1.getSubimage(0, 0, image1.getWidth(), image1.getHeight()-5));
            fail("should throw");
        } catch (AssertionError e) {
            assertThat(e.getMessage(), startsWith("height"));
        }
    }

    @Test
    public void SimilarWrongWidth() {
        try {
            verifyImagesAreSimilar(image1,
                    image1.getSubimage(0, 0, image1.getWidth() - 20, image1.getHeight()), 10);
            fail("should throw");
        } catch (AssertionError e) {
            assertThat(e.getMessage(), startsWith("width"));
        }
    }

    @Test
    public void SimilarWrongHeight() {
        try {
            verifyImagesAreSimilar(image1,
                    image1.getSubimage(0, 0, image1.getWidth(), image1.getHeight()-5), 10);
            fail("should throw");
        } catch (AssertionError e) {
            assertThat(e.getMessage(), startsWith("height"));
        }
    }

    @Test
    public void similarMinorDifferencesInColor() {
        //The center of the image is pretty much white
        IntStream.range(270, 320).forEach(y -> {
            IntStream.range(480, 520).forEach(x ->  {
                int rgb = image2.getRGB(x,y);
                Color c1 = new Color(rgb, false);
                Color c2 = new Color((int)(c1.getRed()*0.95), (int)(c1.getGreen()*0.95), (int)(c1.getBlue()*0.95));
                image2.setRGB(x, y, c2.getRGB());
            });
        });
        verifyImagesAreSimilar(image1, image2, 100000);
    }

    @Test
    public void similarMinorDifferencesInColorRealImages() throws IOException {
        ClassLoader classLoader = ImagesComparatorTest.class.getClassLoader();
        File file1 = new File(classLoader.getResource("Hoh.png").getFile());
        BufferedImage image =  ImageIO.read(file1);
        File file2 = new File(classLoader.getResource("Hoh-variation.png").getFile());
        BufferedImage similarImage =  ImageIO.read(file2);

        verifyImagesAreSimilar(image, similarImage, 10000);
    }

    @Test
    public void similarMajorDifferencesInColorRealImages() throws IOException {
        ClassLoader classLoader = ImagesComparatorTest.class.getClassLoader();
        File file1 = new File(classLoader.getResource("Hoh.png").getFile());
        BufferedImage image =  ImageIO.read(file1);
        File file2 = new File(classLoader.getResource("Hoh-variation2.png").getFile());
        BufferedImage similarImage =  ImageIO.read(file2);
        exception.expect(AssertionError.class);
        verifyImagesAreSimilar(image, similarImage, 10);
    }

    @Test
    public void similarMajorDifferencesInColorRealImages2() throws IOException {
        ClassLoader classLoader = ImagesComparatorTest.class.getClassLoader();
        File file1 = new File(classLoader.getResource("Hoh.png").getFile());
        BufferedImage image =  ImageIO.read(file1);
        File file2 = new File(classLoader.getResource("Hoh-variation2.png").getFile());
        BufferedImage similarImage =  ImageIO.read(file2);

        verifyImagesAreSimilar(image, similarImage, 2);
    }


    @Test(expected = AssertionError.class)
    public void similarMajorDifferencesInColor() {
        IntStream.range(0, 50).forEach(y -> {
            IntStream.range(0, 50).forEach(x ->  {
                int rgb = image2.getRGB(x,y);
                Color c1 = new Color(rgb, false);
                Color c2 = new Color((int)(c1.getRed()*2), (int)(c1.getGreen()*2), (int)(c1.getBlue()*2));
                image2.setRGB(x, y, c2.getRGB());
            });
        });
        verifyImagesAreSimilar(image1, image2, 1000);
    }

    @Test
    public void similarSmallNumberOfBadPixels() {
        IntStream.range(0, 20).forEach(x ->  image2.setRGB(x, 5, 10));
            verifyImagesAreSimilar(image1, image2, 10);
    }

    @Test(expected = AssertionError.class)
    public void similarLargeNumberOfBadPixels() {
        IntStream.range(0, 200).forEach(x ->  image2.setRGB(x, 5, 10));
        verifyImagesAreSimilar(image1, image2, 10000);
    }

    @Test
    public void croppedPictures1() {
        BufferedImage shiftedImage = image2.getSubimage(0,0,
                image2.getWidth()-30, image2.getHeight() -50);
        verifyImagesAreEqualWithShift(shiftedImage, image1, 50);
    }

    @Test
    public void croppedPictures2() {
        BufferedImage shiftedImage = image2.getSubimage(20,0,
                image2.getWidth()-20, image2.getHeight());
        verifyImagesAreEqualWithShift(image1, shiftedImage, 50);
    }

    @Test
    public void shiftedCroppedPictures1() {
        BufferedImage shiftedImage = image2.getSubimage(20,30,
                image2.getWidth()-30, image2.getHeight() -50);
        verifyImagesAreEqualWithShift(shiftedImage, image1, 50);
    }

    @Test
    public void shiftedCroppedPictures2() {
        BufferedImage shiftedImage = image2.getSubimage(20,30,
                image2.getWidth()-30, image2.getHeight() -50);
        verifyImagesAreEqualWithShift(image1, shiftedImage, 50);
    }

    @Test(expected = AssertionError.class)
    public void shiftedCroppedPicturesTooMany() {
        BufferedImage shiftedImage = image2.getSubimage(20,30,
                image2.getWidth()-30, image2.getHeight() -50);
        verifyImagesAreEqualWithShift(image1, shiftedImage, 10);
    }

    @Test
    public void errorImageVerify() throws IOException {
        ClassLoader classLoader = ImagesComparatorTest.class.getClassLoader();
        File file = new File(classLoader.getResource("sample1-a.png").getFile());
        BufferedImage imageVariation =  ImageIO.read(file);
        BufferedImage errImage = getErrorImage(imageVariation, image1).get();

        BufferedImage expectedErrImage =  ImageIO.read(
                new File(classLoader.getResource("err_sample.png").getFile()));
        verifyImagesAreEqual(errImage, expectedErrImage);
    }

    @Test
    public void errorImageNoDiff() throws IOException {
       getErrorImage(image1, image1).ifPresent(e -> fail());
    }

    @Test(expected = AssertionError.class)
    public void errorImageDifferentDimensions() throws IOException {
        BufferedImage otherImage = new BufferedImage(image1.getWidth()+1,
                image1.getHeight(),BufferedImage.TYPE_INT_RGB);

        getErrorImage(otherImage, image1);
    }

    @Test
    public void verifyShiftedImageComparison() throws IOException, URISyntaxException{
        URL url = ImagesComparatorTest.class.getClassLoader().getResource("sample1.png");
        BufferedImage expectedImage =  ImageIO.read( new File(url.toURI()));

        BufferedImage shiftedImage = expectedImage.getSubimage(20,30,
                expectedImage.getWidth()-30, expectedImage.getHeight() -50);

       verifyImagesAreEqualWithShift(shiftedImage, expectedImage, 50);
    }


}
