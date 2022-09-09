package link.dwsy.ddl.core.CustomExceptions;

import link.dwsy.ddl.core.constant.CustomerErrorCode;

/**
 * @Author Dwsy
 * @Date 2022/8/24
 */
public class CodeException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private int code;

    public CodeException(int code, String message){
        super(message);
        this.code = code;
    }

    public CodeException(CustomerErrorCode customerErrorCode) {
        super(customerErrorCode.getMessage());
        this.code = customerErrorCode.getCode();
    }

    public CodeException(CustomerErrorCode customerErrorCode,String message) {
        super(message);
        this.code = customerErrorCode.getCode();
    }

    public CodeException(String message){
        super(message);
        code = 1;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

}
