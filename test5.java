import java.io.*;
import java.util.Arrays;

public class test5 {

    static String author = "Tim Eggers, 5309269";
    static int const1 = 76;
    static int const2 = 200;
    static int const3 = 10;

    public static void main(String[] args) {

        String csvFile = "C:\\Users\\Tim\\Desktop\\THM\\Sem1\\OOP\\HausübungSS2020_A1\\Notenliste.csv";

        int[][] gradelist = new int[countCSVlines(csvFile)][2];
        int[] grades = new int[5];

        readCSV(csvFile, gradelist);
        evaluate(gradelist, grades);
        createHTML(grades, gradelist, const1, const2, const3, author);

    }

    public static int countCSVlines(String csvFile){

        int lines = 0;
        String line = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(csvFile));        //doppelt br?
            while((line = br.readLine()) != null) {
                if (line.isEmpty() || line.equals("MatrNr,Note")) {
                    continue;
                }
                lines++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    public static void readCSV (String csvFile, int[][]gradelist){

        String line = "";
        String csvSplitAt = ",";
        int i = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            while ((line = br.readLine()) != null) {

                String[]liste = line.split(csvSplitAt);

                try{
                    gradelist[i][0] = Integer.parseInt(liste[0]);
                    gradelist[i][1] = Integer.parseInt(liste[1]);
                    i++;

                }catch(NumberFormatException ex){
                    ex.getMessage(); //?
                }
            }
            System.out.println("\nEinlesen erfolgreich!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
                System.out.println("Fehler!");
            }
        }
        System.out.println("Auswertung: \n" + Arrays.toString(grades));
        System.out.printf("Anzahl Noten: %02d", (grades[0] + grades[1] + grades[2] + grades[3] + grades[4]));
        System.out.printf("\nBestnote: %02d", findMax(gradelist));
    }

    public static int findMax(int[][]gradelist) {

        int max = gradelist[0][1];
        for (int i = 0; i <gradelist.length; i++) {
            if (gradelist[i][1] > max) {
                max = gradelist[i][1];
            }
        }
        return max;
    }

    public static int findMost(int[] grades){

        int most = grades[0];
        for (int i = 0; i<grades.length; i++){
            if(grades[i] > most){
                most = grades[i];
            }
        }
        return most;
    }

    public static void createHTML (int[] grades, int[][]gradelist, int const1, int const2, int const3, String author){

        String title = "<!DOCTYPE html> \n <html> \n <head> \n \t <title>Auswertung</title> \n \t <meta charset=\"utf-8\"> \n </head>";
        String header = "<body> \n \t <header> \n \t \t <h1><u>Auswertung</u></h1> \n \t </header> \n\t <main>";
        String svg = generateHTMLsvg(grades, 15, const2+80);      //x Faktor für most
        String grade1 = generateHTMLgrade(grades, 0, "287800");
        String grade2 = generateHTMLgrade(grades, 1, "479C00");
        String grade3 = generateHTMLgrade(grades, 2, "75B900");
        String grade4 = generateHTMLgrade(grades, 3, "BBCD0C");
        String grade5 = generateHTMLgrade(grades, 4, "E2250B");
        String axisx = generateHTMLaxis(const1, const2, findMost(grades)*10+126, const2, "x");
        String axisy = generateHTMLaxis(const1, const3, const1, const2, "n");
        String intervalAll = generateHTMLintervals(grades,5, const1);
        String best = generateHTMLbest(gradelist, const3, const2+60);
        String end = String.format("\t </svg> \n\t </main> \n\t <footer> \n\t\t <p style=\"font-family:Arial; font-size:90%%\">Autor: %s </p> \n\t </footer>\n </body> \n </html>", author);

        PrintWriter pWriter = null;
        try {
            pWriter = new PrintWriter(new BufferedWriter(new FileWriter("Auswertung1.html")));
            pWriter.println(title + header + svg + grade1 + grade2 + grade3 + grade4 + grade5 + axisx + axisy + intervalAll +  best + end);
            System.out.println("\nAuswertung erfolgreich!");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (pWriter != null){
                pWriter.flush();
                pWriter.close();
            }
        }
    }

    public static String generateHTMLgrade(int[] grades, int j, String colour){

        int texty = 50+30*j;
        int recty = 35+30*j;
        int rectwidth = grades[j]*10;

        return String.format("\t \t <text x=\"10\" y=\"%d\" font-size=\"13\" font-family=\"Arial\" fill=\"#404040\">Note %d: %02d </text>\n" +
                "\t\t\t<rect x=\"75\" y=\"%d\" width=\"%d\" height=\"20\" rx=\"3\" ry=\"3\" fill=\"#%s\" />",texty, j+1, grades[j], recty, rectwidth, colour);

    }

    public static String generateHTMLaxis(int x1, int y1, int x2, int y2, String arrow){

        if (arrow.equals("x")){
            return String.format("\t\t <line x1=\"%d\" y1=\"%d\" x2=\"%d\" y2=\"%d\" stroke-width=\"2\" stroke=\"#808080\" />" +
                    "<polygon fill=\"#808080\" points=\"%d,195 %d,205 %d,200\"/>", x1, y1, x2, y2, x2, x2, x2+10);
        }else if(arrow.equals("y")){
            return String.format("\t\t <line x1=\"%d\" y1=\"%d\" x2=\"%d\" y2=\"%d\" stroke-width=\"2\" stroke=\"#808080\" />" +
                    "<polygon fill=\"#808080\" points=\"71,10 81,10 76,0\"/>", x1, y1, x2, y2);
        }else{
            return String.format("\t\t <line x1=\"%d\" y1=\"%d\" x2=\"%d\" y2=\"%d\" stroke-width=\"2\" stroke=\"#808080\" />", x1, y1, x2, y2);
        }
    }

    public static String generateHTMLbest(int[][] gradelist, int x, int y){

        return String.format("\t\t <text x=\"%d\" y=\"%d\" font-size=\"18\" font-family=\"Arial\" fill=\"#404040\">Beste Note: %02d </text>", x, y, findMax(gradelist));

    }

    public static String generateHTMLsvg(int[]grades, int x, int y){

        return String.format("\t <svg id=\"statSvg\" xmlns=\"http://www.w3.org/2000/svg\" width=\"%d\" height=\"%d\">", findMost(grades)*x, y);

    }

    public static String generateHTMLintervals(int[]grades, int i, int const1){

        double interval2 = (Math.ceil(findMost(grades))/i);
        double interval1 = (double)(findMost(grades)*10)/(interval2);

        StringBuilder sb = new StringBuilder();
        String intervals;

        for (int j = 0;j<=interval2+1; j++){
            int interval3 = const1+(int)(interval1)*j;
            String interval = String.format("\n\t \t <text x=\"%d\" y=\"215\" font-size=\"12\" font-family=\"Arial\" fill=\"#404040\">%02d</text>\n" +
                    "\t\t\t <line x1=\"%d\" y1=\"195\" x2=\"%d\" y2=\"205\" stroke-width=\"2\" stroke=\"#808080\" />", interval3-6, i*j, interval3, interval3);
            sb.append(interval);
        }
        return intervals = sb.toString();
    }
}