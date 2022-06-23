package cn.guo.onetext.ui.reflected;


import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class ReflectedException extends RuntimeException {

    public ReflectedException() {}

    public ReflectedException(@Nullable String message) {
        super(message);
    }

    public ReflectedException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    public ReflectedException(@Nullable Throwable cause) {
        super(cause);
    }

    @RequiresApi(/* Build.VERSION_CODES.N */ 24)
    public ReflectedException(@Nullable String message, @Nullable Throwable cause,
                              boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
