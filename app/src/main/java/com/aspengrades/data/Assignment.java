package com.aspengrades.data;

import androidx.annotation.NonNull;

import org.jsoup.nodes.Element;

/**
 * A class that represents a single assignment in Aspen
 */
public class Assignment {

    /**
     * The name of the assignment in Aspen
     */
    private String name;

    /**
     * The category that the assignment is in
     */
    private String category;

    /**
     * An array of two score strings, the first to be displayed for "fraction" mode and the second to be displayed for "percent" mode
     */
    private String[] scores;

    /**
     * Creates a new Assignment object using the given row from the assignments table
     * @param row The JSoup element containing the row containing the assignment
     * @param nameIndex The index of the cell that holds the assignment name
     * @param categoryIndex The index of the cell that holds the category name
     * @param scoreIndex The index of the cell that holds the score information
     */
    public Assignment(Element row, int nameIndex, int categoryIndex, int scoreIndex){
        name = row.child(nameIndex).text();
        category = row.child(categoryIndex).text();
        scores = getScoreTextSmart(row.child(scoreIndex));
    }

    /**
     * Determines the text to show for the assignment's score.  If the format of the html in the score cell is not recognized, this method falls back
     * on the simple method for determining score.  Otherwise, the method distinguishes pieces of information based on what cells they appear in within
     * the score cell.  Assignments out of 0 points (extra credit) are returned in the format +points.
     * @param scoreCell The cell in the assignment's row containing score information
     * @return The scores array
     */
    private String[] getScoreTextSmart(Element scoreCell){
        Element tr = trFromScoreCell(scoreCell);
        if(tr == null) return getScoreTextSimple(scoreCell);
        Element percentBar = scoreCell.selectFirst("div[class=percentFieldContainer]");
        FractionParser fraction = fractionParserFromTr(tr);

        if(percentBar != null){
            String percentText = percentBar.text();
            if(fraction != null) return new String[] {fraction.text, percentBar.text()};
            else return new String[] {percentText, percentText};
        }
        else if(fraction != null){
            if(fraction.denominator == 0) return new String[] {fraction.text, "+" + fraction.numerator};
            return new String[] {fraction.text, fraction.eval() + "%"};
        }
        else if(tr.children().size() == 1) {
            String text = tr.child(0).text();
            return new String[] {text, text};
        }
        else if(tr.children().size() == 2){
            String text = tr.child(1).text();
            return new String[] {text, text};
        }
        return getScoreTextSimple(scoreCell);
    }

    /**
     * Uses a simple method to determine score text based only on the contents of the score cell written as a string.  The text is split into a
     * list of "words" around spaces.  If a slash is found in the text, the words around the slash are used for the fractional representation and the first
     * word is used for the percent representation.  If no slash is found, the first word is used for both representations.
     * @param scoreCell The cell the assignment's row containing score information
     * @return The scores array
     */
    private String[] getScoreTextSimple(Element scoreCell){
        String[] split = scoreCell.text().split(" ");
        int slashIndex = -1;
        for (int x = 0; x < split.length; x++) {
            if (split[x].equals("/")) {
                slashIndex = x;
                break;
            }
        }
        if (slashIndex < 1 || slashIndex > split.length - 2) return new String[] {split[0], split[0]};
        else {
            String fracText = split[slashIndex - 1] + " " + split[slashIndex] + " " + split[slashIndex + 1];
            return new String[]{fracText, split[0]};
        }
    }

    /**
     * Gets the <tr> element nested 3 layers within each score cell. This <tr> contains cells that hold each piece of score
     * related information.
     * @param scoreCell The score cell
     * @return The <tr> element, or null if an exception was thrown while finding it
     */
    private Element trFromScoreCell(Element scoreCell){
        try{
            return scoreCell.child(0).child(0).child(0);
        } catch(NullPointerException | IndexOutOfBoundsException e){
            return null;
        }
    }

    /**
     * Searches the <tr> element from trFromScoreCell for a fractional representation of the score.
     * @param tr The <tr> from trFromScoreCell
     * @return A FractionParser containing the fraction, or null if no fraction is found
     */
    private FractionParser fractionParserFromTr(Element tr){
        for(int i = 0; i < tr.children().size(); i++) {
            FractionParser fp = new FractionParser(tr.child(i).text());
            if(fp.isFraction) return fp;
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getFractionScore(){
        return scores[0];
    }

    public String getPercentScore(){
        return scores[1];
    }

    @Override
    @NonNull
    public String toString(){
        return name + ", " + category + ", " + scores[0];
    }

    /**
     * A class that checks a string to see if it represents a fraction and holds the resulting data
     */
    private static class FractionParser {
        public boolean isFraction = false;
        public float numerator;
        public float denominator;
        public String text;

        public FractionParser(String str){
            text = str;
            if(str.contains("/")){
                String[] split = str.split("/");
                if(split.length != 2) return;
                try{
                    numerator = Float.parseFloat(split[0]);
                    denominator = Float.parseFloat(split[1]);
                } catch (NumberFormatException e) {
                    return;
                }
                isFraction = true;
            }
        }

        /**
         * If the FractionParser represents a fraction and has a nonzero denominator, this method returns an integer
         * approximation of the fraction.  Otherwise, the method returns 0.
         * @return An integer approximation of the fraction if possible, 0 otherwise
         */
        private int eval(){
            if(denominator == 0 || !isFraction) return 0;
            return (int) (numerator / denominator);
        }
    }
}
