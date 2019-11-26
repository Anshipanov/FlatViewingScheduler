package entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Flat
{
    private Integer id;
    private String address;
    private Integer currentTenantId;

    public Flat(String address, Integer currentTenantId)
    {
        this.address = address;
        this.currentTenantId = currentTenantId;
    }
}
