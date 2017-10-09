package com.sj.printlandscape;

import android.os.AsyncTask;
import android.os.Environment;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPage;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by SiddharthJain on 7/24/2017.
 */

public class PrintSummaryTask extends AsyncTask<Void,Void,String> {

    private final int MAX_ITEMS = 20;
    private RotateEvent event;

    public PrintSummaryTask() {

    }

    @Override
    protected String doInBackground(Void... params) {
        final String PARENT_PATH = Environment.getExternalStorageDirectory().getPath() + "/sample";
        Document document = null;
        try {
            File file = new File(PARENT_PATH);
            if (!file.exists()) {
                file.mkdirs();
            }
            File pdfFile = new File(file, "temp.pdf");

//            int margin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PT, 20, )
            int margin = 20;
//            document = new Document(PageSize.A4.rotate(), margin, margin, margin, margin);
            document = new Document();
            document.setPageSize(PageSize.A4.rotate());
//            document = new Document();
            event = new RotateEvent();

//            document.setPageSize();
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
//            writer.setPageSize(PageSize.A4.rotate());
            writer.setPageEvent(event);

            document.open();
            event.setOrientation(PdfPage.LANDSCAPE);

            PdfContentByte cb = writer.getDirectContent();
            printPage(document, writer, cb);

            if (document != null && document.isOpen()) {
                document.close();
            }
            return pdfFile.getPath();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (document != null && document.isOpen()) {
                document.close();
            }
        }
        return null;
    }

    private int getNoOfPages() {
        int noOfItems = 30;
        int moud = noOfItems%MAX_ITEMS;
        int dividend = noOfItems/MAX_ITEMS;
        int noOfPage =  moud == 0 ? dividend : dividend+1;
        return noOfPage;
    }

    private void printPage(Document document, PdfWriter pdfWriter, PdfContentByte pdfContentByte) throws Exception{


        int noOfPages = getNoOfPages();
        BaseFont latoLight = BaseFont.createFont("assets/Lato-Light.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font light = new Font(latoLight, 8);
        for (int i=1;i<=noOfPages;i++) {
            if (i != 1) {


                document.newPage();
                document.setPageSize(PageSize.A4.rotate());
                event.setOrientation(PdfPage.LANDSCAPE);
            }

            addTopPart(document);

            addMiddleTable(document, i);

            if (noOfPages>1) {
                Paragraph paragraph = new Paragraph(new Phrase("Page " + i + "/" + noOfPages, light));
                paragraph.setAlignment(Element.ALIGN_CENTER);
                paragraph.setSpacingBefore(8f);
                ColumnText.showTextAligned(pdfContentByte, Element.ALIGN_CENTER,
                        paragraph,
                        (document.right() - document.left()) / 2 + document.leftMargin(),
                        document.bottom() - 10, 0);
            }

        }
    }


    private void addTopPart(Document document) throws Exception{

        BaseFont latoRegular = BaseFont.createFont("assets/Lato-Regular.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        BaseFont openSansSemiBold = BaseFont.createFont("assets/OpenSans-Semibold.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        BaseFont openSansRegular = BaseFont.createFont("assets/OpenSans-Regular.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

        Font companyNameFont = new Font(latoRegular, 11);
        Font rangeFont1 = new Font(openSansRegular, 9);
        Font rangeFont = new Font(openSansRegular, 8);

        Paragraph companyName = new Paragraph("ABCD", companyNameFont);
        companyName.setAlignment(Element.ALIGN_CENTER);
        document.add(companyName);

        PdfPTable totalTranTable = new PdfPTable(2);
        float width1 = (document.right() - document.left()) /2;
        float width2 = (document.right() - document.left()) - width1;

        totalTranTable.setTotalWidth(new float[]{width1, width2});
        totalTranTable.setLockedWidth(true);

        Paragraph title = new Paragraph("Summary", rangeFont1);
        title.setAlignment(Element.ALIGN_RIGHT);
        PdfPCell summaryCell = new PdfPCell(title);
        summaryCell.setBorder(Rectangle.NO_BORDER);
        summaryCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        Paragraph totalText = new Paragraph("TOTAL TRANSACTION 30     ", rangeFont1);
        totalText.setAlignment(Element.ALIGN_RIGHT);
        PdfPCell totalTranCell = new PdfPCell(totalText);
        totalTranCell.setBorder(Rectangle.NO_BORDER);
        totalTranCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        totalTranTable.addCell(summaryCell);
        totalTranTable.addCell(totalTranCell);

        document.add(totalTranTable);
        Paragraph rangeElement = new Paragraph("", rangeFont);
        rangeElement.setAlignment(Element.ALIGN_CENTER);
        document.add(rangeElement);


    }

    private void addMiddleTable(Document document, int pageNo) throws Exception{

        int noOFCoulmn = 10;
        int taxCellCount = (noOFCoulmn - 2)/2;
        PdfPTable pdfPTable = new PdfPTable(noOFCoulmn);
        pdfPTable.setWidthPercentage(100);
        pdfPTable.setSpacingBefore(8);

        float columnWidth = 45.83f;
        BaseFont openSansSemiBold = BaseFont.createFont("assets/OpenSans-Semibold.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font cellFont = new Font(openSansSemiBold, 8.64f);
        cellFont.setColor(153,153,153);

        BaseFont openSansRegular = BaseFont.createFont("assets/OpenSans-Regular.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font fontNormal = new Font(openSansRegular, 7.5f);
        Font fontBold = new Font(openSansRegular, 7.5f, Font.BOLD);



//        pdfPTable.completeRow();

        /********/

        /*Second Row Headers*/
        pdfPTable.addCell(getHeaderCell("Column1", cellFont));
        pdfPTable.addCell(getHeaderCell("Column2", cellFont));

        for (int i=0;i<4;i++) {
            String cgstText = "A"+i;
            String sgstText = "B"+i;
//            float fontSize = 8.64f;
//            float width = openSansSemiBold.getWidthPoint(cgstText, fontSize);
//            while (width>columnWidth) {
//                fontSize = fontSize -0.5f;
//                width = openSansSemiBold.getWidthPoint(cgstText, fontSize);
//            }

//            cellFont.setSize(6);

            pdfPTable.addCell(getValueBreakdownCell(cgstText, cellFont));
            pdfPTable.addCell(getValueBreakdownCell(sgstText, cellFont));
        }

        int itemCount = 30;

//        for (int i=0;i<noOFCoulmn;i++) {
//            PdfPCell cell = getCellWithNoBorder();
//            cell.setFixedHeight(2);
//            pdfPTable.addCell(cell);
//        }

        int noOfItems = Math.min(itemCount, (pageNo*MAX_ITEMS));

        for (int i=((pageNo-1)*MAX_ITEMS);i<noOfItems;i++) {

            addItemInTable(pdfPTable, (i+1), fontNormal);

        }

        int itemsLeftToDraw = (pageNo*MAX_ITEMS) - noOfItems;
        if (itemsLeftToDraw>2) {
            itemsLeftToDraw=2;
        }

        for (int i=0;i<(itemsLeftToDraw*noOFCoulmn);i++) {
            pdfPTable.addCell(getCellWithNoBorder());
        }

        for (int i=0;i<noOFCoulmn;i++) {
            pdfPTable.addCell(getCellWithLeftRightBottomBorder());
        }

        Paragraph paragraph = new Paragraph();
        paragraph.add(new Phrase(" ", fontNormal));
        PdfPCell serialNo = getCellWithNoBottomBorder(paragraph);
        pdfPTable.addCell(serialNo);
        addTotalRowTable(pdfPTable, fontBold);

        document.add(pdfPTable);
    }

    private void addItemInTable(PdfPTable pTable, int no, Font font) {

        PdfPCell cell1 = getCellWithBottomBorder(new Phrase("10000", font));
        PdfPCell cell2 = getCellWithBottomBorder(new Phrase("1000", font));
        pTable.addCell(cell1);
        pTable.addCell(cell2);

        for (int i=0;i<4;i++) {
            double amount = 200;
            boolean hasZeroslab = false;

            double half = amount/2;
            String halfTax =  String.format("%.2f", half);

            PdfPCell cellA = getCellWithBottomBorder(new Phrase(halfTax, font));
            PdfPCell cellB = getCellWithBottomBorder(new Phrase(halfTax, font));

            pTable.addCell(cellA);
            pTable.addCell(cellB);
        }

    }

    private void addTotalRowTable(PdfPTable pTable, Font font) {



        PdfPCell hsnCode = getCellWithNoBottomBorder(new Phrase("10000", font));
        pTable.addCell(hsnCode);

        for (int i=0;i<4;i++) {
            double amount = 500;
            double half = amount/2;
            pTable.addCell(getCellWithNoBottomBorder(new Phrase(String.format("%.2f", half), font)));
            pTable.addCell(getCellWithNoBottomBorder(new Phrase(String.format("%.2f", half), font)));
        }
//        pTable.completeRow();

    }

    public PdfPCell getHeaderCell(String text, Font font) {
        return getCell(text, font, 1, 17f, Element.ALIGN_CENTER);
    }



    public PdfPCell getHeadingCell(String text, Font font) {
        return getCell(text, font, 1, 14.6f, Element.ALIGN_LEFT);
    }

    public PdfPCell getGSTValueCell(String text, Font font) {
        return getCell(text, font, 2, 14.6f, Element.ALIGN_CENTER);
    }

    public PdfPCell getValueBreakdownCell(String text, Font font) {
        return getCell(text, font, 1, 14.6f, Element.ALIGN_CENTER);
    }

    public PdfPCell getCell(String text, Font font, int columnSpan, float columnHeight, int horizontalAlignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setFixedHeight(columnHeight);
        cell.setColspan(columnSpan);
        cell.setIndent(3f);
        cell.setHorizontalAlignment(horizontalAlignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorder(Rectangle.BOTTOM);
        return cell;
    }

    public PdfPCell getCellWithBottomBorder() {
        PdfPCell cell = new PdfPCell();
        cell.setPadding(0);
        cell.setIndent(3f);
        cell.setFixedHeight(12f);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorder(PdfPCell.BOTTOM);
        return cell;
    }

    public PdfPCell getCellWithBottomBorder(Phrase phrase) {
        PdfPCell cell = new PdfPCell(phrase);
        cell.setPadding(3f);
//        cell.setIndent(3f);
//        cell.setFixedHeight(12.5f);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorder(PdfPCell.BOTTOM);
        return cell;
    }

    public PdfPCell getCellWithNoBottomBorder(Phrase phrase) {
        PdfPCell cell = new PdfPCell(phrase);
        cell.setPadding(3f);
//        cell.setFixedHeight(12f);
//        cell.setIndent(3f);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }

    public PdfPCell getCellWithNoBorder() {
        PdfPCell cell = new PdfPCell();
        cell.setPadding(0);
        cell.setFixedHeight(12f);
        cell.setIndent(3f);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }

    public PdfPCell getCellWithLeftRightBottomBorder() {
        PdfPCell cell = new PdfPCell();
        cell.setPadding(0);
        cell.setIndent(3f);
        cell.setFixedHeight(12f);
        cell.setBorder(PdfPCell.BOTTOM);
        return cell;
    }
}
