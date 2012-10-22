package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class used for cleaning selected files from selected strings.
 * 
 * @author Alban
 * 
 */
public class FileCleaner {

  private static final String commonStringFileName = "commonStrings.txt";
  private static List<String> commonStrings;
  private static final String fileListFileName     = "fileList.txt";
  private static List<String> fileList;

  /**
   * Main method. For each file name in the file {@value #fileListFileName}, we
   * clean the file by deleting the lines contained into the file
   * {@value #commonStringFileName}.
   * 
   * @param args
   *          not used.
   */
  public static void main(final String[] args) {
    for (final String fileName : getFileList()) {
      final double percent = ((getFileList().indexOf(fileName) + 1) * 100)
          / getFileList().size();
      System.out.println(percent + "% === Cleaning file : " + fileName);
      cleanFile(fileName);
    }
  }

  /**
   * Clean the file in parameter of all the occurrence of the string in return
   * by the method {@link #getCommonStrings()}.
   * 
   * @param fileName
   */
  private static void cleanFile(final String fileName) {
    final String tempFile = "temp.txt";

    final File input = new File(fileName);
    final File output = new File(tempFile);

    try {
      final BufferedReader reader = new BufferedReader(new FileReader(input));
      final BufferedWriter writer = new BufferedWriter(new FileWriter(output));
      String line = "";
      while ((line = reader.readLine()) != null) {
        if (toKeep(line)) {
          writer.write(line + "\n");
          writer.flush();
        }
      }
      reader.close();
      writer.close();
    } catch (final IOException ioe) {
      System.out.println("Error --" + ioe.toString());
    }

    input.delete();
    output.renameTo(new File(fileName));
  }

  /**
   * Return if the line had to be keeped or not.
   * 
   * @param line
   *          the line to check.
   * @return true if the line had to be keeped.
   */
  private static boolean toKeep(String line) {
    boolean toKeep = true;

    for (final String element : getCommonStrings()) {
      line = line.trim();
      if (line.equals(element)) {
        toKeep = false;
        break;
      }
    }

    return toKeep;
  }

  /**
   * Return the list of files to clean.
   * 
   * @return the list of files from the text file {@link #fileListFileName}.
   */
  private static List<String> getFileList() {
    if (fileList == null) {
      fileList = new ArrayList<String>();
      BufferedReader reader = null;
      try {
        final File input = new File(fileListFileName);
        reader = new BufferedReader(new FileReader(input));
        String line = "";

        while ((line = reader.readLine()) != null) {
          fileList.add(line);
        }

        reader.close();
      } catch (final IOException ioe) {
        System.out.println("Error --" + ioe.toString());
      }
    }
    return fileList;
  }

  /**
   * Return the list of the Strings to delete.
   * 
   * @return the list of the strings to delete from the text file
   *         {@link #commonStringFileName}.
   */
  private static List<String> getCommonStrings() {
    if (commonStrings == null) {
      commonStrings = new ArrayList<String>();
      BufferedReader reader = null;
      try {
        final File input = new File(commonStringFileName);
        reader = new BufferedReader(new FileReader(input));
        String line = "";

        while ((line = reader.readLine()) != null) {
          commonStrings.add(line);
        }

        reader.close();
      } catch (final IOException ioe) {
        System.out.println("Error --" + ioe.toString());
      }
    }
    return commonStrings;
  }
}
