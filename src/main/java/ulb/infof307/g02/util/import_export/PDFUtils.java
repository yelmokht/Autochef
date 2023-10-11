package ulb.infof307.g02.util.import_export;

import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.layout.Document;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.borders.*;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import javafx.stage.FileChooser;
import ulb.infof307.g02.gui.controller.main.Autochef;
import ulb.infof307.g02.model.sql_model.ingredient_model.Ingredient;
import ulb.infof307.g02.model.sql_model.shoppinglist_model.IngredientShoppingList;
import ulb.infof307.g02.model.sql_model.shoppinglist_model.ShoppingList;
import ulb.infof307.g02.util.AlertUtils;
import ulb.infof307.g02.util.attributes.Department;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Random;

public class PDFUtils {

    private static final DeviceRgb[] colorList = {  new DeviceRgb(52, 152, 219),
                                                    new DeviceRgb(26, 188, 156),
                                                    new DeviceRgb(243, 156, 18),
                                                    new DeviceRgb(192, 57, 43),
                                                    new DeviceRgb(46, 204, 113),
                                                    new DeviceRgb(39, 174, 96),
                                                    new DeviceRgb(41, 128, 185),
                                                    new DeviceRgb(230, 126, 34),
                                                    new DeviceRgb(231, 76, 60),
                                                    new DeviceRgb(211, 84, 0)};

    private PDFUtils() {}

    /**
     * Used to create a PDF File
     * @param shoppingList data object used to create PDF
     * @return The final PDF
     * */

    public static File createPDF(ShoppingList shoppingList) throws IOException, SQLException {
        File tempFile = File.createTempFile("shoppingList",".pdf");
        return writePDF(shoppingList, tempFile);
    }


    /**
     * used to add content in the PDF
     * @param shoppingList data object used to create PDF
     * @param PDF The File PDF without the content
     * @return The final PDF
     * */
    public static File writePDF(ShoppingList shoppingList, File PDF) throws IOException, SQLException {
        try (PdfWriter writer = new PdfWriter(PDF.getAbsolutePath());
             PdfDocument pdfDoc = new PdfDocument(writer);
             Document document = new Document(pdfDoc)) {
            formatDocument(document, shoppingList);
        }

        return PDF;
    }


    /**
     * Setup the window used to save the PDF
     * @param shoppingListName classique name for the PDF
     * @return window FileSystem
     * */
    public static FileChooser getFileChooser(String shoppingListName) {
        //setup fileChooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisissez o\u00f9 sauvegarder la liste :");
        File listDir = new File("./listes de courses/");
        // setup save Directory
        if(!listDir.isDirectory()){
            listDir.mkdir();
        }
        fileChooser.setInitialDirectory(listDir);
        // setup extension
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));
        fileChooser.setInitialFileName(shoppingListName + ".pdf");

        return fileChooser;
    }


    /**
     * basic function calling subfunction to create a PDF
     * @param shoppingList data object
     * */
    public static void saveShoppingListAsPDF(ShoppingList shoppingList) throws SQLException {
        FileChooser fileChooser = getFileChooser(shoppingList.getName());
        File pdf = fileChooser.showSaveDialog(Autochef.getStage());
        // Write to PDF
        try{
            if(pdf!=null && !pdf.exists()){
                writePDF(shoppingList, pdf);
            }
        } catch (IOException ex){
            Autochef.getLogger().error("Error while creating pdf file", ex);
            AlertUtils.alertWarningMessage("Le fichier n'a pas pu \u00eatre cr\u00e9\u00e9");
            pdf.delete();
        }
    }


    /**
     * add content in the pdf
     * @param document used like a paper to write data
     * @param shoppingList data object
     * */
    private static void formatDocument(Document document, ShoppingList shoppingList) throws SQLException {
        // Create Title
        Text titleText = new Text(shoppingList.getName()).setUnderline()
                .setBold()
                .setFontSize(22)
                .setTextAlignment(TextAlignment.CENTER);
        Paragraph titleParagraph = new Paragraph(titleText);
        titleParagraph.setTextAlignment(TextAlignment.CENTER);
        document.add(titleParagraph);
        // Create list Table
        Table table = new Table(2);
        table.setWidth(UnitValue.createPercentValue(100));
        table.setFixedLayout();
        fillTable(table, shoppingList);
        document.add(table);
    }


    /**
     * Split ingredient by type and put them in cells
     * @param table table containing cells
     * @param shoppingList data object
     * */
    private static void fillTable(Table table, ShoppingList shoppingList) throws SQLException {
        // Create a new list per store Department
        for (Department department : Department.values()) {
            List list = new List();
            list.setListSymbol("- ");
            // add the ingredients relating to that department to the list
            for (IngredientShoppingList ingredientShoppingList : shoppingList.getIngredients()) {
                Ingredient searchIngredient = Ingredient.newBuilder()
                        .setId(ingredientShoppingList.getIngredientID())
                        .setType(department)
                        .build();
                try {
                    searchIngredient.getModel()
                            .ifPresent(ingredient
                                    -> list.add(ingredient.getName() + " : " + ingredientShoppingList.getQuantity() + " " + ingredientShoppingList.getUnit().getName())
                            );
                } catch (SQLException e) {
                    Autochef.getLogger().error("", e);
                    // TODO
                }

            }
            // if any ingredient matches with the Department, add a cell to the table corresponding to that department
            if(!list.isEmpty()) {
                Cell departmentCell = makeCellOfDepartment(list, department);
                table.addCell(departmentCell);
            }
        }
    }


    /**
     * create a cell for 1 type(departement)
     * @param list list of ingredient
     * @param department type of ingredient
     * @return box
     * */
    public static Cell makeCellOfDepartment(List list, Department department) {
        Cell cell = new Cell();
        Paragraph paragraph = new Paragraph();
        int i = new Random().nextInt(colorList.length);
        cell.setBackgroundColor(colorList[i]);
        cell.setBorder(new SolidBorder(new DeviceRgb(255,255,255), 10));
        paragraph.add(new Text(department.getDepartment()).setUnderline());
        cell.add(paragraph);
        cell.add(list);

        return cell;
    }
}
