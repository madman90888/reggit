package noob.reggie.domain.dto;

import lombok.Data;
import noob.reggie.domain.entity.Dish;
import noob.reggie.domain.entity.DishFlavor;

import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
