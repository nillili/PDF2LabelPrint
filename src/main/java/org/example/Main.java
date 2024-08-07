package org.example;

import com.google.zxing.oned.Code128Writer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Main
{
    public static void main(String[] args) throws IOException
    {

        // 폰트 파일 경로를 지정합니다.
        String fontPath = "C:\\works\\lib\\maruburi\\TTF\\MaruBuri-Regular.ttf";  // ttf


        // PDF 문서 크기를 설정합니다. 40mm x 12mm를 포인트로 변환 (1인치는 72포인트)
        float width = 40 / 25.4f * 72;
        float height = 12 / 25.4f * 72;
        PDRectangle customPageSize = new PDRectangle(width, height);

        // 새로운 PDF 문서를 생성합니다.
        PDDocument document = new PDDocument();

        // 새로운 페이지를 생성합니다.
        PDPage page = new PDPage(customPageSize);
        document.addPage(page);


        // 한글 폰트를 로드합니다.
        PDType0Font font = PDType0Font.load(document, new File(fontPath));

        // 페이지에 내용을 작성할 ContentStream을 엽니다.
        try(PDPageContentStream contentStream = new PDPageContentStream(document, page))
        {
            // 폰트를 설정합니다.
            contentStream.setFont(font, 12);

            // 텍스트를 작성할 위치를 설정합니다.
            contentStream.beginText();
//            contentStream.newLineAtOffset(100, 700);
            contentStream.newLineAtOffset(5, height - 10);
            contentStream.showText("우리는 국가와 국민에");
            contentStream.endText();

            // 바코드 생성
            String barcodeText = "1234567890";
            BufferedImage barcodeImage = generateBarcodeImage(barcodeText);

            // 바코드 이미지를 PDF에 추가
            PDImageXObject pdImage = PDImageXObject.createFromByteArray(document, convertBufferedImageToByteArray(barcodeImage), "barcode");
//            contentStream.drawImage(pdImage, 100, 600, pdImage.getWidth() / 2, pdImage.getHeight() / 2); // 크기 조절
            contentStream.drawImage(pdImage, 5, 2, width - 10, (height / 2) - 2);
        } catch(IOException | WriterException e)
        {
            e.printStackTrace();
        }

        // PDF 문서를 저장하고 닫습니다.
        try
        {
            document.save("example_with_barcode.pdf");
            document.close();
        } catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    // 바코드 이미지를 생성하는 메서드
    private static BufferedImage generateBarcodeImage(String barcodeText) throws WriterException
    {
//        QRCodeWriter barcodeWriter = new QRCodeWriter();
        Code128Writer barcodeWriter = new Code128Writer();
        BitMatrix bitMatrix = barcodeWriter.encode(barcodeText, BarcodeFormat.CODE_128, 200, 50);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }

    // BufferedImage를 byte 배열로 변환하는 메서드
    private static byte[] convertBufferedImageToByteArray(BufferedImage image) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", baos);
        return baos.toByteArray();
    }
}