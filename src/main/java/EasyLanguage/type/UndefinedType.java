package EasyLanguage.type;

public class UndefinedType {
    private static final UndefinedType instance=new UndefinedType();
    public  static UndefinedType getInstance(){
        return instance;
    }
    public UndefinedType(){

    }

    public String toString(){
        return "undefined";
    }
}
