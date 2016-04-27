package com.github.xsocket.job.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class PdfUtils {
  public static String parsePdf2Text(InputStream input) throws Exception {
    PDDocument doc = PDDocument.load(input);
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    OutputStreamWriter writer = new OutputStreamWriter(output);
    try {
      PDFTextStripper stripper = new PDFTextStripper();
      stripper.writeText(doc, writer);
    } finally {
      doc.close();
      input.close();
      output.close();
      writer.close();
    }
    return new String(output.toByteArray());
  }
}
