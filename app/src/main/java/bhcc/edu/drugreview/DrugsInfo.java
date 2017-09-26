package bhcc.edu.drugreview;

/**
 * Created by svasco on 9/25/2017.
 */

public class DrugsInfo {

    //private int mTextResID; //text resource ID will hold ID for strings.  Medicine's String ID
    private String genericName;
    private String brandName;
    private String purpose;
    private String mDEAsch;
    private String specialInstruc; //special instructions corresponding to a few of the drugs
    private String category;
    private String studyTopic;

    public DrugsInfo(String genericName, String brandName, String purpose, String DEAsch,
                     String specialInstruc, String category, String studyTopic) {
        this.genericName = genericName;
        this.brandName = brandName;
        this.purpose = purpose;
        mDEAsch = DEAsch;
        this.specialInstruc = specialInstruc;
        this.category = category;
        this.studyTopic = studyTopic;
    }


    //Getters and Setters in case needed
    public String getGenericName() {
        return genericName;
    }

    public void setGenericName(String genericName) {
        this.genericName = genericName;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getDEAsch() {
        return mDEAsch;
    }

    public void setDEAsch(String DEAsch) {
        mDEAsch = DEAsch;
    }

    public String getSpecialInstruc() {
        return specialInstruc;
    }

    public void setSpecialInstruc(String specialInstruc) {
        this.specialInstruc = specialInstruc;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStudyTopic() {
        return studyTopic;
    }

    public void setStudyTopic(String studyTopic) {
        this.studyTopic = studyTopic;
    }

    /**
     * For part of the app where user can just view the Drugs in flashcard form, a toString mehtod
     * might be useful to display Drug's information
     */
    public String toString() {
        String toString = getGenericName() + "\n" + getBrandName() + "\n" + getPurpose() + "\n" + getDEAsch() + "\n" + getSpecialInstruc()
                + "\n" + getCategory() + "\n" + getStudyTopic();

        return toString;

    }
}
