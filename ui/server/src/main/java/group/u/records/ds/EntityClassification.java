package group.u.records.ds;

public class EntityClassification {

    private int start;
    private int end;
    private String text;

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public String getText() {
        return text;
    }

    public String getType() {
        return type;
    }

    private String type;

    public Classification getClassification(){
        switch(type){
            case "ORG":
                return Classification.COMPANY;
            case "GPE":
                return Classification.COUNTRY;
        }
        return Classification.PERSON;
    }
}
