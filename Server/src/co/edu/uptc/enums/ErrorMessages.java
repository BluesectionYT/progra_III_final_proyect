package co.edu.uptc.enums;

public enum ErrorMessages {
    AMOUNT_GREATER_THAN_ZERO("El monto debe ser mayor que cero"),
    CATEGORY_WITHOUT_BUDGET("La categoría no tiene presupuesto suficiente"),
    USER_NULL("El usuario no puede ser nulo"),
    SELF_IN_CIRCLE("No puedes agregarte a tu propio círculo"),
    PROJECT_NULL("El proyecto no puede ser nulo"),
    PROJECT_IS_INACTIVE("El proyecto está inactivo"),
    NO_PENDING_INVITATION("No existe una invitación pendiente para este proyecto"),
    USER_WITHOUT_PROJECT("El usuario no pertenece a ningún proyecto actualmente"),
    ADMIN_CANNOT_LEAVE("El administrador debe transferir el cargo o cerrar el proyecto"),
    REASON_REQUIRED("El motivo es obligatorio"),
    ONLY_APPROVE_PENDING("solo se pueden aprobar solicitudes en estado PENDING. Estado actual: %s"),
    ONLY_REJECT_PENDING("solo se pueden rechazar solicitudes en estado PENDING. Estado actual: %s"),
    USER_ALREADY_REGISTERED("Usuario ya existe"),
    PROJECT_NAME_TAKEN("Ya existe un proyecto con ese nombre"),
    PROJECT_ALREADY_CLOSED("El proyecto ya está cerrado"),
    PROJECT_DATA_REQUIRED("Project name and total budget and categories are required"),
    PROJECT_INACTIVE("Proyecto inactivo"),
    USER_ALREADY_IN_PROJECT("Usuario ya existente en este proyecto"),
    USER_IN_OTHER_PROJECT("Usuario ya existente en otro proyecto"),
    NO_INVITATION_FOR_USER("El usuario no tiene invitación pendiente"),
    USER_NOT_MEMBER("El usuario no es miembro de este proyecto"),
    USER_NOT_IN_PROJECT("Usuario no existente en este proyecto"),
    ADMIN_ALREADY("El nuevo administrador no puede ser ya administrador"),
    INVITEE_IN_OTHER_PROJECT("Usuario existente en otro proyecto"),
    INVITEE_IN_THIS_PROJECT("Usuario existente en este proyecto"),
    INVITEE_NOT_IN_CIRCLE("Solo puede invitar a personas en su circulo de trabajo"),
    REQUEST_NULL("La solicitud no puede ser nula"),
    REQUESTER_NOT_MEMBER("Solo los integrantes pueden enviar solicitudes"),
    CATEGORY_NOT_IN_THIS_PROJECT("La categoría no pertenece a este proyecto"),
    REQUEST_NOT_SUBMITTABLE("La solicitud no es válida para enviarse"),
    REASON_NULL("La razon no puede ser nula"),
    REQUEST_ALREADY_RESOLVED("La solicitud ya fue resuelta: %s"),
    OLDEST_REQUEST_FIRST("Debe resolver primero la solicitud más antigua"),
    INSUFFICIENT_BALANCE("Saldo disponible insuficiente"),
    INVALID_DATE_RANGE("La fecha inicial no puede ser posterior a la final"),
    SORT_ARGUMENTS_NULL("Las solicitudes y el criterio no pueden ser nulos"),
    PASSWORD_EMPTY("La contraseña no puede estar vacía"),
    HASH_FAILED("No se pudo calcular el hash"),
    FIELD_RESTORE_FAILED("No se pudo restaurar el campo %s"),
    MANAGER_NOT_EMPTY("El manager debe estar vacío para cargar el estado"),
    INVITATION_TO_MISSING_PROJECT("Invitación a un proyecto inexistente: %s"),
    ALREADY_IN_OTHER_PROJECT("%s ya pertenece a otro proyecto"),
    REQUEST_IDS_WITH_GAPS("Los id de solicitudes de %s deben ser 0, 1, 2... sin huecos"),
    REQUEST_WITHOUT_STATE("Solicitud %d sin estado o sin fecha de creación"),
    REQUEST_INVALID_AMOUNT("Solicitud %d con monto inválido"),
    RESOLVED_WITHOUT_DATA("Solicitud %d resuelta sin motivo o sin fecha de resolución"),
    APPROVED_EXCEEDS_BUDGET("Lo aprobado supera el presupuesto total de %s"),
    UNKNOWN_USER("Usuario inexistente: %s"),
    UNKNOWN_CATEGORY("Categoría inexistente en %s: %s"),
    INVALID_REQUEST("Solicitud inválida"),
    LOGIN_REQUIRED("Debes iniciar sesión"),
    NO_PROJECT("No perteneces a ningún proyecto"),
    ADMIN_ONLY("Solo el administrador puede realizar esta acción"),
    USER_NOT_FOUND("El usuario no existe"),
    PROJECT_NOT_FOUND("El proyecto ya no existe"),
    CREDENTIALS_REQUIRED("Usuario y contraseña son obligatorios"),
    INVALID_CREDENTIALS("Usuario o contraseña incorrectos"),
    SESSION_ALREADY_STARTED("Ya hay una sesión iniciada"),
    SESSION_ACTIVE_ELSEWHERE("Este usuario ya tiene una sesión activa"),
    WRONG_PASSWORD("Contraseña incorrecta"),
    PROJECT_NAME_REQUIRED("El nombre del proyecto es obligatorio"),
    BUDGET_GREATER_THAN_ZERO("El presupuesto total debe ser mayor que cero"),
    ALREADY_HAS_PROJECT("Ya perteneces a un proyecto"),
    CATEGORY_NOT_IN_PROJECT("La categoría no existe en este proyecto"),
    CONCEPT_REQUIRED("El concepto es obligatorio"),
    SELECT_CATEGORY("Selecciona una categoría"),
    REQUEST_NOT_FOUND("La solicitud no existe"),
    INVALID_STATUS("Estado inválido"),
    INVALID_ORDER("Criterio de orden inválido"),
    INVALID_DATE("Fecha inválida: usa el formato AAAA-MM-DD"),
    CATEGORIES_REQUIRED("Ingresa al menos una categoría"),
    INVALID_CATEGORY("Categoría inválida"),
    CATEGORY_DATA_REQUIRED("Cada categoría necesita un nombre y un monto mayor que cero"),
    CATEGORY_REPEATED("La categoría «%s» está repetida"),
    CATEGORIES_EXCEED_BUDGET("La suma de las categorías supera el presupuesto total");

    private final String text;

    ErrorMessages(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public String format(Object... arguments) {
        return String.format(text, arguments);
    }
}
