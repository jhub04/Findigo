package stud.ntnu.no.idatt2105.Findigo.dtos.sale;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class SaleResponse {
    private Long id;
    private Long listingId;
    private Double salePrice;
    private Date saleDate;

}
