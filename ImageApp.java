import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeListener;

public class ImageApp
{
  public static void main(String[] args)
  {
    // use any file from the lib folder
    String pictureFile = "lib/beach.jpg";

    // Get an image, get 2d array of pixels, show a color of a pixel, and display the image
    Picture origImg = new Picture(pictureFile);
    Pixel[][] origPixels = origImg.getPixels2D();
    System.out.println("Top-left pixel color: " + origPixels[0][0].getColor());
    origImg.explore();

    // Image #1: Recolor an image by changing the RGB color of each Pixel

    Picture recoloredImg = new Picture(pictureFile);
    Pixel[][] recoloredPixels = recoloredImg.getPixels2D();
    for (int r = 0; r < recoloredPixels.length; r++)
    {
      for (int c = 0; c < recoloredPixels[0].length; c++)
      {
        Pixel p = recoloredPixels[r][c];
        // Example: Swap Red and Blue channels, boost Green slightly
        int newRed = p.getBlue();
        int newGreen = Math.min(255, (int) (p.getGreen() * 1.1));
        int newBlue = p.getRed();
        p.setColor(new Color(newRed, newGreen, newBlue));
      }
    }
    recoloredImg.explore();

    // Image #2: Create a photographic negative of the image

    Picture negImg = new Picture(pictureFile);
    Pixel[][] negPixels = negImg.getPixels2D();
    for (int r = 0; r < negPixels.length; r++)
    {
      for (int c = 0; c < negPixels[0].length; c++)
      {
        Pixel p = negPixels[r][c];
        p.setColor(new Color(255 - p.getRed(), 255 - p.getGreen(), 255 - p.getBlue()));
      }
    }
    negImg.explore();

    // Image #3: Create a grayscale version of the image

    Picture grayscaleImg = new Picture(pictureFile);
    Pixel[][] grayscalePixels = grayscaleImg.getPixels2D();
    for (int r = 0; r < grayscalePixels.length; r++)
    {
      for (int c = 0; c < grayscalePixels[0].length; c++)
      {
        Pixel p = grayscalePixels[r][c];
        int avg = (p.getRed() + p.getGreen() + p.getBlue()) / 3;
        p.setColor(new Color(avg, avg, avg));
      }
    }
    grayscaleImg.explore();

    // Image #4: Rotate it 180 degrees

    Picture upsidedownImage = new Picture(pictureFile);
    Pixel[][] upsideDownPixels = upsidedownImage.getPixels2D();
    int rows = origPixels.length;
    int cols = origPixels[0].length;
    for (int r = 0; r < rows; r++)
    {
      for (int c = 0; c < cols; c++)
      {
        upsideDownPixels[rows - 1 - r][cols - 1 - c].setColor(origPixels[r][c].getColor());
      }
    }
    upsidedownImage.explore();

    // Image #5: Rotate image 90 degrees clockwise
    // Dimensions swap (height becomes width, width becomes height)

    Picture rotateImg = new Picture(cols, rows);
    Pixel[][] rotatePixels = rotateImg.getPixels2D();
    for (int r = 0; r < rows; r++)
    {
      for (int c = 0; c < cols; c++)
      {
        rotatePixels[c][rows - 1 - r].setColor(origPixels[r][c].getColor());
      }
    }
    rotateImg.explore();

    // Image #6: Rotate image -90 degrees (counter-clockwise)

    Picture rotateImg2 = new Picture(cols, rows);
    Pixel[][] rotatePixels2 = rotateImg2.getPixels2D();
    for (int r = 0; r < rows; r++)
    {
      for (int c = 0; c < cols; c++)
      {
        rotatePixels2[cols - 1 - c][r].setColor(origPixels[r][c].getColor());
      }
    }
    rotateImg2.explore();

    // Final Image: Add a small image to a larger one (Picture-in-Picture / Stamp)

    Picture compositeImg = new Picture(pictureFile);
    Pixel[][] largePixels = compositeImg.getPixels2D();
    // Using a scaled-down version of the original image as the "small image"
    // (You can replace this with: new Picture("lib/smallIcon.jpg"))
    Picture smallImg = scalePicture(origImg, 0.25);
    Pixel[][] smallPixels = smallImg.getPixels2D();

    int startRow = 20; // vertical offset
    int startCol = 20; // horizontal offset
    for (int r = 0; r < smallPixels.length && (r + startRow) < largePixels.length; r++)
    {
      for (int c = 0; c < smallPixels[0].length && (c + startCol) < largePixels[0].length; c++)
      {
        Color smallColor = smallPixels[r][c].getColor();
        // Optional green-screen/white-background transparency filter:
        // if (smallColor.getRed() < 240 || smallColor.getGreen() < 240 || smallColor.getBlue() < 240)
        largePixels[r + startRow][c + startCol].setColor(smallColor);
      }
    }
    compositeImg.explore();

    // For testing 2D algorithms

    int[][] test1 = {
      { 1, 2, 3, 4 },
      { 5, 6, 7, 8 },
      { 9, 10, 11, 12 },
      { 13, 14, 15, 16 }
    };
    int[][] test2 = new int[4][4];
    // Example: Rotate test1 90 degrees clockwise into test2
    for (int r = 0; r < test1.length; r++)
    {
      for (int c = 0; c < test1[0].length; c++)
      {
        test2[c][test1.length - 1 - r] = test1[r][c];
      }
    }

    // Launch Interactive Slider Studio GUI

    SwingUtilities.invokeLater(() -> launchInteractiveEditor(origImg));
  }

  /**
   * Launches a Swing window with real-time sliders for:
   * - Red, Green, Blue color shifts
   * - Grayscale intensity blend (0% to 100%)
   * - Color Inversion / Negative blend (0% to 100%)
   * - Arbitrary Degree Rotation (0 to 360 degrees)
   * - Resize Scale (25% to 200%)
   */
  public static void launchInteractiveEditor(Picture basePicture)
  {
    JFrame frame = new JFrame("ImageApp Interactive Modifier Studio");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLayout(new BorderLayout(10, 10));

    // Image display area inside a scroll pane (handles large resized images cleanly)
    JLabel imageLabel = new JLabel(new ImageIcon(basePicture.getBufferedImage()));
    imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
    JScrollPane scrollPane = new JScrollPane(imageLabel);
    scrollPane.setPreferredSize(new Dimension(750, 550));
    frame.add(scrollPane, BorderLayout.CENTER);

    // Control Panel on the right
    JPanel controlPanel = new JPanel();
    controlPanel.setLayout(new GridLayout(0, 1, 5, 5));
    controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 15));
    controlPanel.setPreferredSize(new Dimension(300, 550));

    // Sliders
    JSlider redSlider = createSlider(-255, 255, 0, 100);
    JSlider greenSlider = createSlider(-255, 255, 0, 100);
    JSlider blueSlider = createSlider(-255, 255, 0, 100);
    JSlider graySlider = createSlider(0, 100, 0, 25);
    JSlider invertSlider = createSlider(0, 100, 0, 25);
    JSlider rotateSlider = createSlider(0, 360, 0, 90);
    JSlider resizeSlider = createSlider(25, 200, 100, 25);
    JCheckBox stampCheckBox = new JCheckBox("Overlay Corner Thumbnail");

    // Labels that update dynamically with values
    JLabel redLabel = new JLabel("Red Shift: 0");
    JLabel greenLabel = new JLabel("Green Shift: 0");
    JLabel blueLabel = new JLabel("Blue Shift: 0");
    JLabel grayLabel = new JLabel("Grayscale Blend: 0%");
    JLabel invertLabel = new JLabel("Invert Colors: 0%");
    JLabel rotateLabel = new JLabel("Rotate Angle: 0°");
    JLabel resizeLabel = new JLabel("Resize Scale: 100%");

    // Reset button
    JButton resetButton = new JButton("Reset All Modifiers");
    resetButton.addActionListener(e -> {
      redSlider.setValue(0);
      greenSlider.setValue(0);
      blueSlider.setValue(0);
      graySlider.setValue(0);
      invertSlider.setValue(0);
      rotateSlider.setValue(0);
      resizeSlider.setValue(100);
      stampCheckBox.setSelected(false);
    });

    // Update pipeline triggered whenever any slider changes
    ChangeListener updatePipeline = e -> {
      int rShift = redSlider.getValue();
      int gShift = greenSlider.getValue();
      int bShift = blueSlider.getValue();
      double grayPct = graySlider.getValue() / 100.0;
      double invertPct = invertSlider.getValue() / 100.0;
      int angle = rotateSlider.getValue();
      double scale = resizeSlider.getValue() / 100.0;

      redLabel.setText("Red Shift: " + rShift);
      greenLabel.setText("Green Shift: " + gShift);
      blueLabel.setText("Blue Shift: " + bShift);
      grayLabel.setText("Grayscale Blend: " + graySlider.getValue() + "%");
      invertLabel.setText("Invert Colors: " + invertSlider.getValue() + "%");
      rotateLabel.setText("Rotate Angle: " + angle + "°");
      resizeLabel.setText("Resize Scale: " + resizeSlider.getValue() + "%");

      Picture processed = applyAllModifiers(
          basePicture, rShift, gShift, bShift, grayPct, invertPct, angle, scale, stampCheckBox.isSelected()
      );
      imageLabel.setIcon(new ImageIcon(processed.getBufferedImage()));
    };

    redSlider.addChangeListener(updatePipeline);
    greenSlider.addChangeListener(updatePipeline);
    blueSlider.addChangeListener(updatePipeline);
    graySlider.addChangeListener(updatePipeline);
    invertSlider.addChangeListener(updatePipeline);
    rotateSlider.addChangeListener(updatePipeline);
    resizeSlider.addChangeListener(updatePipeline);
    stampCheckBox.addActionListener(e -> updatePipeline.stateChanged(null));

    // Add components to control panel
    controlPanel.add(redLabel);
    controlPanel.add(redSlider);
    controlPanel.add(greenLabel);
    controlPanel.add(greenSlider);
    controlPanel.add(blueLabel);
    controlPanel.add(blueSlider);
    controlPanel.add(grayLabel);
    controlPanel.add(graySlider);
    controlPanel.add(invertLabel);
    controlPanel.add(invertSlider);
    controlPanel.add(rotateLabel);
    controlPanel.add(rotateSlider);
    controlPanel.add(resizeLabel);
    controlPanel.add(resizeSlider);
    controlPanel.add(stampCheckBox);
    controlPanel.add(resetButton);

    frame.add(controlPanel, BorderLayout.EAST);
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }

  private static JSlider createSlider(int min, int max, int initial, int majorTick)
  {
    JSlider slider = new JSlider(min, max, initial);
    slider.setMajorTickSpacing(majorTick);
    slider.setPaintTicks(true);
    return slider;
  }

  /**
   * Applies resize, color adjustments, grayscale, inversion, and arbitrary-angle rotation
   * directly on 2D Pixel arrays.
   */
  public static Picture applyAllModifiers(
      Picture src,
      int rShift, int gShift, int bShift,
      double grayPct, double invertPct,
      int angleDegrees, double scale,
      boolean addThumbnail)
  {
    // 1. Resize first (keeps rotation and pixel loops fast when scaled down)
    Picture scaled = scalePicture(src, scale);
    Pixel[][] pixels = scaled.getPixels2D();
    int h = pixels.length;
    int w = pixels[0].length;

    // 2. Apply RGB Recolor, Grayscale Blend, and Inversion Blend per pixel
    for (int r = 0; r < h; r++)
    {
      for (int c = 0; c < w; c++)
      {
        Pixel p = pixels[r][c];
        int red = clamp(p.getRed() + rShift);
        int green = clamp(p.getGreen() + gShift);
        int blue = clamp(p.getBlue() + bShift);

        // Grayscale interpolation
        if (grayPct > 0)
        {
          int avg = (red + green + blue) / 3;
          red = (int) (red * (1 - grayPct) + avg * grayPct);
          green = (int) (green * (1 - grayPct) + avg * grayPct);
          blue = (int) (blue * (1 - grayPct) + avg * grayPct);
        }

        // Color inversion interpolation
        if (invertPct > 0)
        {
          red = (int) (red * (1 - invertPct) + (255 - red) * invertPct);
          green = (int) (green * (1 - invertPct) + (255 - green) * invertPct);
          blue = (int) (blue * (1 - invertPct) + (255 - blue) * invertPct);
        }

        p.setColor(new Color(clamp(red), clamp(green), clamp(blue)));
      }
    }

    // 3. Optional small image overlay
    if (addThumbnail)
    {
      Picture thumb = scalePicture(src, scale * 0.25);
      Pixel[][] thumbPixels = thumb.getPixels2D();
      for (int r = 0; r < thumbPixels.length && r + 10 < h; r++)
      {
        for (int c = 0; c < thumbPixels[0].length && c + 10 < w; c++)
        {
          pixels[r + 10][c + 10].setColor(thumbPixels[r][c].getColor());
        }
      }
    }

    // 4. Arbitrary-degree rotation using inverse 2D rotation matrix (prevents holes/aliasing)
    if (angleDegrees % 360 != 0)
    {
      return rotateByDegrees(scaled, angleDegrees);
    }

    return scaled;
  }

  /**
   * Nearest-neighbor scaling using 2D Pixel arrays.
   */
  public static Picture scalePicture(Picture src, double scale)
  {
    Pixel[][] srcPixels = src.getPixels2D();
    int srcH = srcPixels.length;
    int srcW = srcPixels[0].length;

    int newH = Math.max(1, (int) Math.round(srcH * scale));
    int newW = Math.max(1, (int) Math.round(srcW * scale));

    Picture dest = new Picture(newW, newH);
    Pixel[][] destPixels = dest.getPixels2D();

    for (int r = 0; r < newH; r++)
    {
      int srcR = Math.min(srcH - 1, (int) (r / scale));
      for (int c = 0; c < newW; c++)
      {
        int srcC = Math.min(srcW - 1, (int) (c / scale));
        destPixels[r][c].setColor(srcPixels[srcR][srcC].getColor());
      }
    }
    return dest;
  }

  /**
   * Rotates a Picture by any angle (in degrees) using reverse mapping so no pixels are missed.
   */
  public static Picture rotateByDegrees(Picture src, double degrees)
  {
    Pixel[][] srcPixels = src.getPixels2D();
    int srcH = srcPixels.length;
    int srcW = srcPixels[0].length;

    double rad = Math.toRadians(degrees);
    double sin = Math.abs(Math.sin(rad));
    double cos = Math.abs(Math.cos(rad));

    // Calculate bounding box of rotated image
    int newW = (int) Math.floor(srcW * cos + srcH * sin);
    int newH = (int) Math.floor(srcH * cos + srcW * sin);

    Picture dest = new Picture(newW, newH);
    Pixel[][] destPixels = dest.getPixels2D();

    double srcCenterX = srcW / 2.0;
    double srcCenterY = srcH / 2.0;
    double destCenterX = newW / 2.0;
    double destCenterY = newH / 2.0;

    double cosNeg = Math.cos(-rad);
    double sinNeg = Math.sin(-rad);

    for (int r = 0; r < newH; r++)
    {
      for (int c = 0; c < newW; c++)
      {
        // Translate destination pixel back to origin, rotate backwards, and translate to source center
        double dx = c - destCenterX;
        double dy = r - destCenterY;

        int srcC = (int) Math.round(dx * cosNeg - dy * sinNeg + srcCenterX);
        int srcR = (int) Math.round(dx * sinNeg + dy * cosNeg + srcCenterY);

        if (srcR >= 0 && srcR < srcH && srcC >= 0 && srcC < srcW)
        {
          destPixels[r][c].setColor(srcPixels[srcR][srcC].getColor());
        }
        else
        {
          destPixels[r][c].setColor(Color.BLACK); // Background fill outside rotated corners
        }
      }
    }
    return dest;
  }

  private static int clamp(int val)
  {
    return Math.max(0, Math.min(255, val));
  }
}