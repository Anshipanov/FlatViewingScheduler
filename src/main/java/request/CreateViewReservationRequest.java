package request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateViewReservationRequest
{
    private int flatId;
    private int tenantId;
    private String startTime;
}
