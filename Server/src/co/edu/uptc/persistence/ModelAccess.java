package co.edu.uptc.persistence;

import co.edu.uptc.model.ExpenseRequest;
import co.edu.uptc.model.Project;
import co.edu.uptc.model.RequestStatus;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

public class ModelAccess {
    public static void restoreRequest(ExpenseRequest request, int id, RequestStatus status, String reason,
                               LocalDateTime createdAt, LocalDateTime resolvedAt) {
        set(request, "id", id);
        set(request, "status", status);
        set(request, "reason", reason);
        set(request, "createdAt", createdAt);
        set(request, "resolvedAt", resolvedAt);
    }

    public static void restoreBalance(Project project, double availableBalance) {
        set(project, "availableBalance", availableBalance);
    }

    private static void set(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("No se pudo restaurar el campo " + fieldName, e);
        }
    }
}
