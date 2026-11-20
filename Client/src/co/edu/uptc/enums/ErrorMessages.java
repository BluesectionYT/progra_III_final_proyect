package co.edu.uptc.enums;

public enum ErrorMessages {
    CONNECTION_FAILED("No se pudo conectar con el servidor (%s:%d).\nVerifica que esté en ejecución."),
    CONNECTION_LOST("Se perdió la conexión con el servidor"),
    CONNECTION_LOST_LISTENER("conexion perdida"),
    ACCOUNT_CREATED("Cuenta creada. Ya puedes ingresar."),
    UNKNOWN_ERROR("Error desconocido"),
    CREDENTIALS_REQUIRED("Ingresa usuario y contraseña"),
    INVALID_CREDENTIALS("Usuario o contraseña incorrectos"),
    PASSWORDS_DIFFER("Las contraseñas no coinciden"),
    PASSWORD_REQUIRED("La contraseña es obligatoria."),
    PROJECT_NAME_REQUIRED("El nombre del proyecto es obligatorio."),
    BUDGET_INVALID("El presupuesto total debe ser un número mayor que cero."),
    CATEGORIES_REQUIRED("Ingresa al menos una categoría con el formato Nombre:monto."),
    CATEGORY_INVALID("Categoría inválida: «%s». Usa el formato Nombre:monto."),
    CATEGORY_REPEATED("La categoría «%s» está repetida."),
    CATEGORIES_EXCEED_BUDGET("La suma de las categorías supera el presupuesto total."),
    CONFIRM_CREATE_PROJECT("Confirma tu contraseña para crear el proyecto"),
    CONCEPT_REQUIRED("El concepto es obligatorio."),
    AMOUNT_INVALID("El monto debe ser un número mayor que cero."),
    SELECT_CATEGORY("Selecciona una categoría."),
    SELECT_REQUEST("Selecciona una solicitud de la lista."),
    REASON_REQUIRED("●  El motivo es obligatorio"),
    INVALID_DATES("Las fechas deben tener el formato AAAA-MM-DD."),
    DATE_ORDER("La fecha «Desde» no puede ser posterior a «Hasta»."),
    SELECT_INVITEE("Selecciona a quién invitar."),
    SELECT_MEMBER("Selecciona un integrante de la lista."),
    CONFIRM_REMOVE("Confirma tu contraseña para eliminar a %s"),
    CONFIRM_TRANSFER("Confirma tu contraseña para transferir la administración a %s"),
    CONFIRM_CLOSE("Confirma tu contraseña para cerrar el proyecto"),
    CIRCLE_USER_REQUIRED("Escribe el usuario que quieres agregar a tu círculo."),
    SELECT_INVITATION("Selecciona una invitación de la lista."),
    CONFIRM_LEAVE("¿Seguro que quieres salir de «%s»?");

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
