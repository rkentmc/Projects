/**
 * FolioCharge class
 * 
 * This data type is a convenience to encapsulate the information
 * contained in a single charge. Charges are typically posted to
 * guest folios, and contain billable charges to the room.
 * 
 */
package hms.model;

/**
 * 
 * @author Team SLAM
 */
public class FolioCharge {
    
    private String code;
    private String description;
    private String date;
    private double amount;

    public FolioCharge(String code, String description, String date, double amount) {
        this.code = code;
        this.description = description;
        this.date = date;
        this.amount = amount;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

}