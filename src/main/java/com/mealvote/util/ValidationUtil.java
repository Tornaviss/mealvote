package com.mealvote.util;

import com.mealvote.HasId;
import com.mealvote.util.exception.IllegalRequestDataException;
import com.mealvote.util.exception.NotFoundException;
import org.slf4j.Logger;

import javax.servlet.http.HttpServletRequest;

public class ValidationUtil {

    private ValidationUtil() {
    }

    public static <T> T checkNotFoundWithId(T object, int id, String entityType) {
        return checkNotFound(object, "id = " + id, entityType);
    }

    public static void checkNotFoundWithId(boolean found, int id, String entityType) {
        checkNotFound(found, "id = " + id, entityType);
    }

    public static <T> T checkNotFound(T object, String msg, String entityType) {
        checkNotFound(object != null, msg, entityType);
        return object;
    }

    public static void checkNotFound(boolean found, String arg, String entityType) {
        if (!found) {
            throw new NotFoundException(entityType + " with " + arg);
        }
    }

    public static void checkNew(HasId bean) {
        if (!bean.isNew()) {
            throw new IllegalRequestDataException(bean + " must be new (id=null)");
        }
    }

    public static void assureIdConsistent(HasId bean, int id) {
//      conservative when you reply, but accept liberally (http://stackoverflow.com/a/32728226/548473)
        if (bean.isNew()) {
            bean.setId(id);
        } else if (bean.getId() != id) {
            throw new IllegalRequestDataException(bean + " must be with id=" + id);
        }
    }

    //  http://stackoverflow.com/a/28565320/548473
    public static Throwable getRootCause(Throwable t) {
        Throwable result = t;
        Throwable cause;

        while (null != (cause = result.getCause()) && (result != cause)) {
            result = cause;
        }
        return result;
    }

    public static String getMessage(Throwable e) {
        return e.getLocalizedMessage() != null ? e.getLocalizedMessage() : e.getClass().getName();
    }

    public static Throwable logAndGetRootCause(Logger log, HttpServletRequest req, Exception e, boolean logException) {
        Throwable rootCause = ValidationUtil.getRootCause(e);
        if (logException) {
            log.error(e.getClass().getSimpleName() + " at request " + req.getRequestURL(), rootCause);
        } else {
            log.warn("{} at request  {}: {}", e.getClass().getSimpleName(), req.getRequestURL(), rootCause.toString());
        }
        return rootCause;
    }
}