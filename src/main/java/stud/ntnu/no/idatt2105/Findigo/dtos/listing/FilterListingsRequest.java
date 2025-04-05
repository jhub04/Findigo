package stud.ntnu.no.idatt2105.Findigo.dtos.listing;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterListingsRequest {
  private Long categoryId;
  private String query;
  private int fromPrice;
  private int toPrice;
  private Date fromDate;
}
