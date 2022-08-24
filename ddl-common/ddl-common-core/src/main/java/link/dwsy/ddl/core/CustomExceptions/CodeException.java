package link.dwsy.ddl.core.CustomExceptions;

/**
 * @Author Dwsy
 * @Date 2022/8/24
 */
public class CodeException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    private int code;

    public CodeException(int code, String name){
        super(name);
        this.code = code;
    }

    public CodeException(String name){
        super(name);
        code = 500;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
