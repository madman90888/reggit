package noob.reggie.domain.dto;

import lombok.Data;
import noob.reggie.domain.entity.Setmeal;
import noob.reggie.domain.entity.SetmealDish;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
