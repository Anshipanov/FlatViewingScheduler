package response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateViewReservationResponse
{
    private Integer id;
    private ViewReservationValidationStatus status;

    public static CreateViewReservationResponse ok(int id)
    {
        return new CreateViewReservationResponse(id, ViewReservationValidationStatus.OK);
    }

    public static CreateViewReservationResponse fail(ViewReservationValidationStatus status)
    {
        return new CreateViewReservationResponse(null, status);
    }
}
