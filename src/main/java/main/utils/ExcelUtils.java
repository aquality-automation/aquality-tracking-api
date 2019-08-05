package main.utils;

import com.mysql.cj.core.conf.url.ConnectionUrlParser.Pair;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelUtils {
    private XSSFCell firstCell;
    private XSSFCell lastCell;

    public String writeXLSFile(JSONArray objects, List<Pair<String, String>> fields, String fileName, String sheetName) throws IOException, JSONException {

        final String path = System.getProperty("user.dir") + File.separator + "temp" + File.separator + "Exports";
        new File(path).mkdirs();

        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet(sheetName);

        CreateHeadRow(sheet, fields);
        CreateRows(objects, sheet, fields);
        ColumnsAutoHeight(sheet, fields);

        String fPath = path + File.separator + fileName + ".xls";
        FileOutputStream fileOut = new FileOutputStream(fPath);

        wb.write(fileOut);
        fileOut.flush();
        fileOut.close();

        return fPath;
    }

    public String writeXLSXFile(JSONArray objects, List<Pair<String, String>> fields, String fileName, String sheetName) throws IOException, JSONException {
        final String path = System.getProperty("user.dir") + File.separator + "temp" + File.separator + "Exports";
        new File(path).mkdirs();

        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet(sheetName);
        CreateRows(objects, sheet, fields);
        ColumnsAutoHeight(sheet, fields);

        sheet.setAutoFilter(new CellRangeAddress(firstCell.getRowIndex(), lastCell.getRowIndex(), firstCell.getColumnIndex(), lastCell.getColumnIndex()));

        String fPath = path + File.separator + fileName + ".xlsx";
        FileOutputStream fileOut = new FileOutputStream(fPath);

        wb.write(fileOut);
        fileOut.flush();
        fileOut.close();
        return fPath;
    }

    private void CreateHeadRow(HSSFSheet sheet, List<Pair<String, String>> fields){
        HSSFRow headRow = sheet.createRow(0);
        for (int c=0;c < fields.size(); c++ )
        {
            HSSFCell cell = headRow.createCell(c);
            cell.setCellValue(fields.get(c).left);
            CellStyle style = sheet.getWorkbook().createCellStyle();
            Font font = sheet.getWorkbook().createFont();
            font.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
            style.setFont(font);
            style.setFillBackgroundColor(HSSFColor.HSSFColorPredefined.BLUE.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            cell.setCellStyle(style);
        }
    }

    private void CreateRows(JSONArray objects, XSSFSheet sheet, List<Pair<String, String>> fields) throws JSONException {
        for (int i=0;i<objects.length()+1;i++)
        {
            XSSFRow row = sheet.createRow(i);
            JSONObject obj = new JSONObject();
            if (i != 0) {
                obj = (JSONObject) objects.get(i-1);
            }
            for (int j = 0; j < fields.size(); j++)
            {
                XSSFCell localXSSFCell = row.createCell(j);
                if (i == 0) {
                    if(j==0){
                        firstCell = localXSSFCell;
                    }
                    localXSSFCell.setCellValue(fields.get(j).left);
                    CellStyle style = sheet.getWorkbook().createCellStyle();
                    Font font = sheet.getWorkbook().createFont();
                    font.setBold(true);
                    font.setColor(IndexedColors.WHITE.getIndex());
                    style.setFont(font);
                    style.setFillForegroundColor(IndexedColors.BLUE_GREY.getIndex());
                    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

                    localXSSFCell.setCellStyle(style);

                } else {
                    if(i==objects.length() && j==fields.size()-1){
                        lastCell = localXSSFCell;
                    }

                    try{
                        Pair<String, String> field = fields.get(j);
                        localXSSFCell.setCellValue(obj.getString(field.right));
                    }catch (Exception e){
                        localXSSFCell.setCellValue("");
                    }
                }
            }
        }
    }

    private void CreateRows(JSONArray objects, HSSFSheet sheet, List<Pair<String, String>> fields) throws JSONException {
        for (int r=0;r < objects.length(); r++ )
        {
            HSSFRow row = sheet.createRow(r+1);
            JSONObject obj = (JSONObject) objects.get(r);

            for (int c=0;c < fields.size(); c++ )
            {
                HSSFCell cell = row.createCell(c);
                cell.setCellValue(obj.getString(fields.get(c).right));
            }
        }
    }

    private void ColumnsAutoHeight(XSSFSheet sheet, List<Pair<String, String>> fields){
        for (int c=0;c < fields.size(); c++ ){
            sheet.autoSizeColumn(c);
            sheet.setDisplayRowColHeadings(true);
        }
    }

    private void ColumnsAutoHeight(HSSFSheet sheet, List<Pair<String, String>> fields){
        for (int c=0;c < fields.size(); c++ ){
            sheet.autoSizeColumn(c);
        }
    }
}
