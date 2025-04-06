package stud.ntnu.no.idatt2105.Findigo.dtos.mappers;

import stud.ntnu.no.idatt2105.Findigo.dtos.sale.SaleResponse;
import stud.ntnu.no.idatt2105.Findigo.entities.Sale;

public class SaleMapper {

    /**
     * Maps a Sale entity to a SaleDTO.
     *
     * @param sale the Sale entity to map
     * @return the mapped SaleDTO
     */
    public static SaleResponse toDto(Sale sale) {
        return new SaleResponse()
                .setId(sale.getId())
                .setListingId(sale.getListing().getId())
                .setSalePrice(sale.getSalePrice())
                .setSaleDate(sale.getSaleDate());
    }

}
