package response;

public enum ViewReservationValidationStatus
{
    OK,
    FLAT_NOT_EXISTS,
    TENANT_NOT_EXISTS,
    NOT_VALID_START_TIME,
    TIME_ALREADY_RESERVED,
    ;
}
