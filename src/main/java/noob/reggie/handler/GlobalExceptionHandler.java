package noob.reggie.handler;

import lombok.extern.slf4j.Slf4j;
import noob.reggie.domain.pojo.R;
import noob.reggie.exception.CustomException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

@Slf4j
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
public class GlobalExceptionHandler {

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> handler(SQLIntegrityConstraintViolationException ex) {
        log.error(ex.getMessage());
        if (ex.getMessage().contains("Duplicate entry")) {
            final String[] split = ex.getMessage().split(" ");
            return R.error(split[2] + " 已存在");
        }

        return R.error("未知错误");
    }

    @ExceptionHandler(CustomException.class)
    public R<String> handler(CustomException ex) {
        return R.error(ex.getMessage());
    }
}
