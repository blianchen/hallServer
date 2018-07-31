package top.yxgu.hall.model.config;

import java.util.List;

public class ConfGun extends ConfData {
	public int type;
	public int bullet_num;
	public List<Integer> upgrade_or_forge_cost;
	public List<Integer> upgrade_or_forge_award;
	public List<Integer> forge_success_also_cost;
	public int ability_type1;
	public int gun_ability1;
	public int chance;
	public List<Integer> fail_award;
}
