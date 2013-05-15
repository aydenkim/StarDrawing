package model.agent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import jxl.Cell;
import jxl.CellType;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class ReadExcel {

	private String inputFile;
	private static ExcelContents contents;
	private int rowCount = 0;

	public void setInputFile(String inputFile) {
		this.inputFile = inputFile;
	}

	public void read() throws IOException {
		File inputWorkbook = new File(inputFile);
		Workbook w;
		try {
			w = Workbook.getWorkbook(inputWorkbook);

			contents = new ExcelContents();
			// Get the first sheet
			Sheet sheet = w.getSheet(0);
			// Loop over first 10 column and lines

			for (int j = 0; j < sheet.getColumns(); j++) {
				for (int i = 0; i < sheet.getRows(); i++) {
					Cell cell = sheet.getCell(j, i);
					CellType type = cell.getType();

					if (j == 0) {
						contents.setCell1(cell.getContents());
					} else if (j == 1) {
						contents.setCell2(cell.getContents());
					} else if (j == 2) {
						contents.setCell3(cell.getContents());
					} else if (j == 3) {
						contents.setCell4(cell.getContents());
					} else if (j == 4) {
						contents.setCell5(cell.getContents());
					}
				}
				rowCount++;
			}
		} catch (BiffException e) {
			e.printStackTrace();
		}
	}

	public static ExcelContents getContents() {
		return contents;
	}

	public class ExcelContents {

		public List<String> cell1 = new ArrayList<String>();
		public List<String> cell2 = new ArrayList<String>();
		public List<String> cell3 = new ArrayList<String>();
		public List<String> cell4 = new ArrayList<String>();
		public List<String> cell5 = new ArrayList<String>();

		ExcelContents() {
		};

		public void setCell1(String message){
			cell1.add(message);
		}

		public void setCell2(String message) {
			cell2.add(message);
		}

		public void setCell3(String message) {
			cell3.add(message);
		}

		public void setCell4(String message) {
			cell4.add(message);
		}

		public void setCell5(String message) {
			cell5.add(message);
		}

		public List<String> getCell1(){
			return cell1;
		}

		public List<String> getCell2(){
			return cell2;
		}

		public List<String> getCell3(){
			return cell3;
		}

		public List<String> getCell4(){
			return cell4;
		}

		public List<String> getCell5(){
			return cell5;
		}

		public int getRowCount() {
			return rowCount;
		}

	}
	
}
