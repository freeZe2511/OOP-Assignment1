import java.io.*;
import java.util.Arrays;

public class Notenauswertung {

    static String author = "Tim Eggers, 5309269";

    // constants used to build the html document (changes can here be made globally)
    static int posx1 = 76;
    static int posy1 = 210;
    static int posx2 = 10;
    static int posy2 = 10;
    static int sizefactor = 15;

    // headline of the csv file to filter out later
    static String csvheader = "MatrNr,Note";

    public static void main(String[] args) {

        // csv-file to read
        String csvFile = "Notenliste.csv";

        // initialising the 2d array for student id ("Matrikelnummer") and grade, and the 1d array for grade evaluation
        // size of 2d array gradelist depends on the amount of usable lines in the csv-file
        int[][] gradelist = new int[countCSVlines(csvFile)][2];
        int[] grades = new int[5];

        // methods to run the evaluation
        readCSV(csvFile, gradelist);
        evaluate(gradelist, grades);
        createHTML(grades, gradelist);
    }

    // method counts the usable lines (with grades) of the csv file, used to initialise the 2d array independently (so file length can vary)
    public static int countCSVlines(String csvFile){

        int lines = 0;
        String line;

        try {
            BufferedReader br = new BufferedReader(new FileReader(csvFile));
            // reads every line
            while((line = br.readLine()) != null) {
                // only lines with the right content will be added, possible empty lines and the header are filtered
                if (line.isEmpty() || line.equals(csvheader)) {
                    continue;
                }
                lines++;
            }
            // filters malformed lines
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    // method reads csv-file and stores correct data into the 2d array
    public static void readCSV (String csvFile, int[][]gradelist){

        String line;
        String csvSplitAt = ",";
        int i = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            // method reads line to store them into 2d array
            while ((line = br.readLine()) != null) {
                // only usable content (id and grade) should be used
                try{
                    // empty lines and header get filtered
                    if (line.isEmpty() || line.equals(csvheader)) {
                        continue;
                    }
                    // usable lines are split with the right delimiter and temporarily stored
                    String[]liste = line.split(csvSplitAt);
                    // id is stored in space 0, grade stored in space 1
                    gradelist[i][0] = Integer.parseInt(liste[0]);
                    gradelist[i][1] = Integer.parseInt(liste[1]);
                    i++;

                    // filters malformed lines (strings)
                }catch(NumberFormatException ex){
                    ex.printStackTrace();
                }
            }
            System.out.println("\nEinlesen erfolgreich!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // method checks every grade, evaluates them according to the conversion table and adds them to the 1d array
    public static void evaluate (int[][]gradelist, int[] grades){

        for (int i=0; i<gradelist.length; i++){
            if(gradelist[i][1] < 101 && gradelist[i][1] > 87){
                grades[0]++;
            }else if(gradelist[i][1] < 88 && gradelist[i][1] > 72){
                grades[1]++;
            }else if(gradelist[i][1] < 73 && gradelist[i][1] > 57){
                grades[2]++;
            }else if(gradelist[i][1] < 58 && gradelist[i][1] > 49){
                grades[3]++;
            }else if(gradelist[i][1] < 50){
                grades[4]++;
            }else{
                System.out.println("Fehler! (Note > 100 vorhanden)");
            }
        }
        // output for console (small evaluation)
        System.out.println("Auswertung: " + Arrays.toString(grades));
        System.out.printf("Anzahl Noten: %02d", (grades[0] + grades[1] + grades[2] + grades[3] + grades[4]));
        System.out.printf("\nBeste Note: %02d", findMax(gradelist));
    }

    //method to find the best grade by comparison
    public static int findMax(int[][]gradelist) {

        int max = gradelist[0][1];
        for (int i = 0; i <gradelist.length; i++) {
            if (gradelist[i][1] > max) {
                max = gradelist[i][1];
            }
        }
        return max;
    }

    // method to find the highest absolute amount of grades 1-5 by comparison, used to dynamically adjust html sizes (if csv-file length would change)
    public static int findMost(int[] grades){

        int most = grades[0];
        for (int i = 0; i<grades.length; i++){
            if(grades[i] > most){
                most = grades[i];
            }
        }
        return most;
    }

    // method to create the html document
    public static void createHTML (int[] grades, int[][]gradelist){

        // strings (changeable, e.g. to english)
        String text = "Auswertung";
        String textauthor = "Autor";
        //  creating title and header
        String title = String.format("\n<!DOCTYPE html>\n<html>\n<head>\n\t<title>%s</title>\n\t<meta charset=\"utf-8\">\n</head>", text);
        String header = String.format("\n<body>\n\t<header>\n\t\t<h1><u>%s</u></h1>\n\t</header>\n\t<main>", text);
        // creating svg-space
        String svg = generateHTMLsvg(grades);
        // creating the diagram for evey grade (colour changeable)
        String grade1 = generateHTMLgrade(grades,1, "287800");
        String grade2 = generateHTMLgrade(grades,2, "479C00");
        String grade3 = generateHTMLgrade(grades,3, "75B900");
        String grade4 = generateHTMLgrade(grades,4, "BBCD0C");
        String grade5 = generateHTMLgrade(grades,5, "E2250B");
        // creating x- and y-axis for the diagram, size of x-axis depends on the highest absolute amount of a grade, arrow on axis possible
        String axisx = generateHTMLaxis(posx1, posy1, findMost(grades)*sizefactor, posy1, "x");
        String axisy = generateHTMLaxis(posx1, posy1, posx1, posy2, "f");
        // creating intervals on x-axis, size of interval changeable
        String intervals = generateHTMLintervals(grades,4);
        // creating output for the best grade
        String best = generateHTMLbest(gradelist);
        // creating footer with author and closing html document
        String end = String.format("\n\t</svg>\n\t</main>\n\t<footer>\n\t\t<p style=\"font-family:Arial; font-size:90%%\">%s: %s </p>\n\t</footer>\n</body>\n</html>", textauthor, author);
        // final String contains every part for the html document
        String htmlAll = title + header + svg + grade1 + grade2 + grade3 + grade4 + grade5 + axisx + axisy + intervals +  best + end;

        // final html string is written into the final html document
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new BufferedWriter(new FileWriter("Auswertung.html")));
            pw.println(htmlAll);
            System.out.println("\nAuswertung erfolgreich!");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (pw != null){
                pw.flush();
                pw.close();
            }
        }
    }

    // method to generate the html code for the svg space, depends on the highest absolute amount of a grade
    public static String generateHTMLsvg(int[]grades){

        return String.format("\n\t<svg id=\"statSvg\" xmlns=\"http://www.w3.org/2000/svg\" width=\"%d\" height=\"%d\">", findMost(grades)*sizefactor+10, posy1+80);
    }

    // method to generate the html code for the individual grades
    public static String generateHTMLgrade(int[] grades, int j, String colour){

        String text = "Note";
        int fontsize = 13;
        // coordinates for text and bar
        int sizey = posy1-posy2;
        int intervaly = sizey/6;
        int rectheight = intervaly/2;
        int recty = intervaly*(j-1)+intervaly/3+3*intervaly/4;
        int texty = recty+rectheight/2+fontsize/2;
        // bar length dependable on amount of grades (times 10 for better visualisation)
        int rectwidth = grades[(j-1)]*10;

        return String.format("\n\t\t<text x=\"%d\" y=\"%d\" font-size=\"%d\" font-family=\"Arial\" fill=\"#404040\">%s %d: %02d </text>\n" +
                "\t\t\t<rect x=\"%d\" y=\"%d\" width=\"%d\" height=\"%d\" rx=\"3\" ry=\"3\" fill=\"#%s\" />",posx2, texty, fontsize, text, j, grades[(j-1)], posx1, recty, rectwidth, rectheight, colour);
    }

    // method to generate the html code for the x- and y-axis (with arrow if preferred)
    public static String generateHTMLaxis(int x1, int y1, int x2, int y2, String arrow){

        // arrow on the end of the axis is possible for both axis if stated
        if (arrow.equals("x")){
            return String.format("\n\t\t<line x1=\"%d\" y1=\"%d\" x2=\"%d\" y2=\"%d\" stroke-width=\"2\" stroke=\"#808080\" />" +
                    "\n\t\t\t<polygon fill=\"#808080\" points=\"%d,%d %d,%d %d,%d\"/>", x1, y1, x2, y2, x2, posy1-5, x2, posy1+5, x2+10, posy1);
        }else if(arrow.equals("y")){
            return String.format("\n\t\t<line x1=\"%d\" y1=\"%d\" x2=\"%d\" y2=\"%d\" stroke-width=\"2\" stroke=\"#808080\" />" +
                    "\n\t\t\t<polygon fill=\"#808080\" points=\"%d,%d %d,%d %d,%d\"/>", x1, y1, x2, y2, posx1-5, posy2, posx1+5, posy2, posx1, posy2-10);
        }else{
            return String.format("\n\t\t<line x1=\"%d\" y1=\"%d\" x2=\"%d\" y2=\"%d\" stroke-width=\"2\" stroke=\"#808080\" />", x1, y1, x2, y2);
        }
    }

    // method to generate the html code for the best grade
    public static String generateHTMLbest(int[][] gradelist){

        String text = "Beste Note";
        int fontsize = 18;
        return String.format("\n\t\t<text x=\"%d\" y=\"%d\" font-size=\"%d\" font-family=\"Arial\" fill=\"#404040\">%s: %02d </text>", posx2, posy1+60, fontsize, text, findMax(gradelist));
    }

    //method to generate the html code for the intervals on the x-axis (interval size changeable)
    public static String generateHTMLintervals(int[]grades, int i){

        // find out how many intervals are needed based on highest absolute amount of a grade, rounded up to get whole number
        double intervalamount = (Math.ceil(findMost(grades))/i);
        // find out the size of the intervals
        double intervalsize = (double)(findMost(grades)*10)/(intervalamount);

        StringBuilder sb = new StringBuilder();
        // generate the intervals (one more than needed for aesthetics)
        for (int j = 0;j<=intervalamount+1; j++){
            int interval = posx1+(int)(intervalsize)*j;
            String intervals = String.format("\n\t\t<text x=\"%d\" y=\"%d\" font-size=\"12\" font-family=\"Arial\" fill=\"#404040\">%02d</text>" +
                    "\n\t\t\t<line x1=\"%d\" y1=\"%d\" x2=\"%d\" y2=\"%d\" stroke-width=\"2\" stroke=\"#808080\" />", interval-6, posy1+15, i*j, interval, posy1-5, interval, posy1+5);
            sb.append(intervals);
        }
        return sb.toString();
    }
}