package org.wisdom.core.validate;

public class Result {
    public static final Result SUCCESS = new Result(true, null);

    public static Result Error(String msg){
        return new Result(false, msg);
    }
    private boolean success;
    private String message;

    private Result(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess(){
        return success;
    }

    public String getMessage(){
        return message;
    }
}
