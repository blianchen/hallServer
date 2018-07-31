package top.yxgu.hall.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import redis.clients.jedis.Jedis;
import top.yxgu.hall.model.config.ConfCv1;
import top.yxgu.hall.model.config.ConfCv2;
import top.yxgu.hall.model.config.ConfData;
import top.yxgu.hall.model.config.ConfFish;
import top.yxgu.hall.model.config.ConfFishBoom;
import top.yxgu.hall.model.config.ConfFishContain;
import top.yxgu.hall.model.config.ConfFishGroup;
import top.yxgu.hall.model.config.ConfFishPos;
import top.yxgu.hall.model.config.ConfFishStandard;
import top.yxgu.hall.model.config.ConfGun;
import top.yxgu.hall.model.config.ConfGunSkin;
import top.yxgu.hall.model.config.ConfItem;
import top.yxgu.hall.model.config.ConfLanguage;
import top.yxgu.hall.model.config.ConfLottery;
import top.yxgu.hall.model.config.ConfPath;
import top.yxgu.hall.model.config.ConfRoomType;
import top.yxgu.hall.model.config.ConfSv;
import top.yxgu.hall.model.config.ConfWorldDrop;
import top.yxgu.hall.service.RedisPool;

@Component
public class ConfigManager {
	public static final String CONFIG_JSON_PREFIX = "config.";
	public static final String[] sendRoomConfig = 
				{"fish.json", 		"fish_boom.json", 		"fish_contain.json", 	"fish_group.json",
				 "fish_pos.json", 	"fish_standard.json", 	"gun.json", 			"gun_skin.json",
				 "item.json", 		"Lottery.json", 		"path.json", 			"room_type.json", 
				 "world_drop.json"
				};
	
	public static Map<Integer, ConfCv1> cv1Map;
	public static Map<Integer, ConfCv2> cv2Map;
	public static Map<Integer, ConfFish> fishMap;
	public static Map<Integer, ConfFishBoom> fishBoomMap;
	public static Map<Integer, ConfFishContain> fishContainMap;
	public static Map<Integer, ConfFishGroup> fishGroupMap;
	public static Map<Integer, ConfFishPos> fishPosMap;
	public static Map<Integer, ConfFishStandard> fishStandardMap;
	public static Map<Integer, ConfGun> gunMap;
	public static Map<Integer, ConfGunSkin> gunSkinMap;
	public static Map<Integer, ConfItem> itemMap;
	public static Map<Integer, ConfLanguage> languageMap;
	public static Map<Integer, ConfLottery> lotteryMap;
	public static Map<Integer, ConfPath> pathMap;
	public static Map<Integer, ConfRoomType> roomTypeMap;
	public static Map<Integer, ConfSv> svMap;
	public static Map<Integer, ConfWorldDrop> worldDropMap;
	
	private static String configDir;
	@Value("${config.dir}")
	public void setConfigDir(String v) {
		ConfigManager.configDir = v;
	}
	
	private static RedisPool redisPool;
	@Resource
	public void setRedisPool(RedisPool v) {
		ConfigManager.redisPool = v;
	}
	
	public static void loadJsonConfig() {
//		Resource res = new ClassPathResource(configDir);
		String dir = configDir;
		cv1Map = loadConfigFromFile(dir, "Cv1.json", new TypeReference<ArrayList<ConfCv1>>() {});
		cv2Map = loadConfigFromFile(dir, "Cv2.json", new TypeReference<ArrayList<ConfCv2>>() {});
		fishMap = loadConfigFromFile(dir, "fish.json", new TypeReference<ArrayList<ConfFish>>() {});
		fishBoomMap = loadConfigFromFile(dir, "fish_boom.json", new TypeReference<ArrayList<ConfFishBoom>>() {});
		fishContainMap = loadConfigFromFile(dir, "fish_contain.json", new TypeReference<ArrayList<ConfFishContain>>() {});
		fishGroupMap = loadConfigFromFile(dir, "fish_group.json", new TypeReference<ArrayList<ConfFishGroup>>() {});
		fishPosMap = loadConfigFromFile(dir, "fish_pos.json", new TypeReference<ArrayList<ConfFishPos>>() {});
		fishStandardMap = loadConfigFromFile(dir, "fish_standard.json", new TypeReference<ArrayList<ConfFishStandard>>() {});
		gunMap = loadConfigFromFile(dir, "gun.json", new TypeReference<ArrayList<ConfGun>>() {});
		gunSkinMap = loadConfigFromFile(dir, "gun_skin.json", new TypeReference<ArrayList<ConfGunSkin>>() {});
		itemMap = loadConfigFromFile(dir, "item.json", new TypeReference<ArrayList<ConfItem>>() {});
		languageMap = loadConfigFromFile(dir, "language.json", new TypeReference<ArrayList<ConfLanguage>>() {});
		lotteryMap = loadConfigFromFile(dir, "Lottery.json", new TypeReference<ArrayList<ConfLottery>>() {});
		pathMap = loadConfigFromFile(dir, "path.json", new TypeReference<ArrayList<ConfPath>>() {});
		roomTypeMap = loadConfigFromFile(dir, "room_type.json", new TypeReference<ArrayList<ConfRoomType>>() {});
		svMap = loadConfigFromFile(dir, "sv.json", new TypeReference<ArrayList<ConfSv>>() {});
		worldDropMap = loadConfigFromFile(dir, "world_drop.json", new TypeReference<ArrayList<ConfWorldDrop>>() {});
	}
	
	private static <T extends ConfData> Map<Integer, T> loadConfigFromFile(String dir, String fileName, TypeReference<ArrayList<T>> clazz) {
		Map<Integer, T> map = new HashMap<>();
		org.springframework.core.io.Resource res = new ClassPathResource(dir + fileName);
		try (InputStream read = res.getInputStream();
				Jedis jedis = redisPool.getResource(RedisPool.T_WRITE)){
			byte[] b = new byte[(int)res.contentLength()];
			read.read(b);
			String s = new String(b, "utf-8");
			ArrayList<T> list = JSON.parseObject(s, clazz);
			for (int i=0; i<list.size(); i++) {
				map.put(list.get(i).id, list.get(i));
			}
			jedis.set(CONFIG_JSON_PREFIX+fileName, s);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}
}
