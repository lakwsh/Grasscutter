package emu.grasscutter.game.gacha;

import emu.grasscutter.net.proto.GachaInfoOuterClass.GachaInfo;
import emu.grasscutter.net.proto.GachaUpInfoOuterClass.GachaUpInfo;
import emu.grasscutter.utils.Utils;

import static emu.grasscutter.Configuration.*;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.data.common.ItemParamData;

public class GachaBanner {
	private int gachaType;
	private int scheduleId;
	private String prefabPath;
	private String previewPrefabPath;
	private String titlePath;
	private int costItemId = 0;
	private int costItemAmount = 1;
	private int costItemId10 = 0;
	private int costItemAmount10 = 10;
	private int beginTime;
	private int endTime;
	private int sortId;
	private int[] rateUpItems4 = {};
	private int[] rateUpItems5 = {};
	private int[] fallbackItems3 = {11301, 11302, 11306, 12301, 12302, 12305, 13303, 14301, 14302, 14304, 15301, 15302, 15304};
	private int[] fallbackItems4Pool1 = {1001, 1006, 1014, 1015, 1020, 1021, 1023, 1024, 1025, 1027, 1031, 1032, 1034, 1036, 1039, 1043, 1044, 1045, 1048, 1050, 1053, 1055, 1056, 1064};
	private int[] fallbackItems4Pool2 = {11401, 11402, 11403, 11404, 11405, 11406, 11407, 11408, 11409, 11410, 11412, 11413, 11414, 11415, 12401, 12402, 12403, 12404, 12405, 12406, 12407, 12408, 12409, 12410, 12411, 12412, 12414, 12416, 13401, 13402, 13403, 13404, 13405, 13406, 13407, 13408, 13409, 13414, 13415, 13416, 14401, 14402, 14403, 14404, 14405, 14406, 14407, 14408, 14409, 14410, 14412, 14413, 14414, 14415, 15401, 15402, 15403, 15404, 15405, 15406, 15407, 15408, 15409, 15410, 15412, 15413, 15414, 15415, 15416};
	private int[] fallbackItems5Pool1 = {1002, 1003, 1016, 1022, 1026, 1029, 1030, 1033, 1035, 1037, 1038, 1041, 1042, 1046, 1047, 1049, 1051, 1052, 1054, 1057, 1058, 1063, 1066};
	private int[] fallbackItems5Pool2 = {11501, 11502, 11503, 11504, 11505, 11509, 11510, 12501, 12502, 12503, 12504, 12510, 13501, 13502, 13504, 13505, 13507, 13509, 14501, 14502, 14504, 14506, 14509, 15501, 15502, 15503, 15507, 15509};
	private boolean removeC6FromPool = false;
	private boolean autoStripRateUpFromFallback = true;
	private int[][] weights4 = {{1,510}, {8,510}, {10,10000}};
	private int[][] weights5 = {{1,75}, {73,150}, {90,10000}};
	private int[][] poolBalanceWeights4 = {{1,255}, {17,255}, {21,10455}};
	private int[][] poolBalanceWeights5 = {{1,30}, {147,150}, {181,10230}};
	private int eventChance4 = 50; // Chance to win a featured event item
	private int eventChance5 = 50; // Chance to win a featured event item
	private BannerType bannerType = BannerType.STANDARD;

	// Kinda wanna deprecate these but they're in people's configs
	private int[] rateUpItems1 = {};
	private int[] rateUpItems2 = {};
	private int eventChance = -1;
	private int costItem = 0;

	public int getGachaType() {
		return gachaType;
	}

	public BannerType getBannerType() {
		return bannerType;
	}

	public int getScheduleId() {
		return scheduleId;
	}

	public String getPrefabPath() {
		return prefabPath;
	}

	public String getPreviewPrefabPath() {
		return previewPrefabPath;
	}

	public String getTitlePath() {
		return titlePath;
	}

	public ItemParamData getCost(int numRolls) {
		return switch (numRolls) {
			case 10 -> new ItemParamData((costItemId10 > 0) ? costItemId10 : getCostItem(), costItemAmount10);
			default -> new ItemParamData(getCostItem(), costItemAmount * numRolls);
		};
	}

	public int getCostItem() {
		return (costItem > 0) ? costItem : costItemId;
	}

	public int getBeginTime() {
		return beginTime;
	}

	public int getEndTime() {
		return endTime;
	}

	public int getSortId() {
		return sortId;
	}

	public int[] getRateUpItems4() {
		return (rateUpItems2.length > 0) ? rateUpItems2 : rateUpItems4;
	}
	public int[] getRateUpItems5() {
		return (rateUpItems1.length > 0) ? rateUpItems1 : rateUpItems5;
	}

	public int[] getFallbackItems3() {return fallbackItems3;}
	public int[] getFallbackItems4Pool1() {return fallbackItems4Pool1;}
	public int[] getFallbackItems4Pool2() {return fallbackItems4Pool2;}
	public int[] getFallbackItems5Pool1() {return fallbackItems5Pool1;}
	public int[] getFallbackItems5Pool2() {return fallbackItems5Pool2;}

	public boolean getRemoveC6FromPool() {return removeC6FromPool;}
	public boolean getAutoStripRateUpFromFallback() {return autoStripRateUpFromFallback;}


	public int getWeight(int rarity, int pity) {
		return switch(rarity) {
			case 4 -> Utils.lerp(pity, weights4);
			default -> Utils.lerp(pity, weights5);
		};
	}

	public int getPoolBalanceWeight(int rarity, int pity) {
		return switch(rarity) {
			case 4 -> Utils.lerp(pity, poolBalanceWeights4);
			default -> Utils.lerp(pity, poolBalanceWeights5);
		};
	}

	public int getEventChance(int rarity) {
		return switch(rarity) {
			case 4 -> eventChance4;
			default -> (eventChance > -1) ? eventChance : eventChance5;
		};
	}

	@Deprecated
	public GachaInfo toProto() {
		return toProto("");
	}

	public GachaInfo toProto(String sessionKey) {
		String record = "http" + (HTTP_ENCRYPTION.useInRouting ? "s" : "") + "://"
						+ lr(HTTP_INFO.accessAddress, HTTP_INFO.bindAddress) + ":"
						+ lr(HTTP_INFO.accessPort, HTTP_INFO.bindPort)
						+ "/gacha?s=" + sessionKey + "&gachaType=" + gachaType;
		String details = "http" + (HTTP_ENCRYPTION.useInRouting ? "s" : "") + "://"
						+ lr(HTTP_INFO.accessAddress, HTTP_INFO.bindAddress) + ":"
						+ lr(HTTP_INFO.accessPort, HTTP_INFO.bindPort)
						+ "/gacha/details?s=" + sessionKey + "&gachaType=" + gachaType;

		// Grasscutter.getLogger().info("record = " + record);
		ItemParamData costItem1 = this.getCost(1);
		ItemParamData costItem10 = this.getCost(10);
		GachaInfo.Builder info = GachaInfo.newBuilder()
				.setGachaType(this.getGachaType())
				.setScheduleId(this.getScheduleId())
				.setBeginTime(this.getBeginTime())
				.setEndTime(this.getEndTime())
				.setCostItemId(costItem1.getId())
	            .setCostItemNum(costItem1.getCount())
	            .setTenCostItemId(costItem10.getId())
	            .setTenCostItemNum(costItem10.getCount())
	            .setGachaPrefabPath(this.getPrefabPath())
	            .setGachaPreviewPrefabPath(this.getPreviewPrefabPath())
	            .setGachaProbUrl(details)
	            .setGachaProbUrlOversea(details)
	            .setGachaRecordUrl(record)
	            .setGachaRecordUrlOversea(record)
	            .setLeftGachaTimes(Integer.MAX_VALUE)
	            .setGachaTimesLimit(Integer.MAX_VALUE)
	            .setGachaSortId(this.getSortId());
		if (this.getTitlePath() != null) {
			info.setGachaTitlePath(this.getTitlePath());
		}

		if (this.getRateUpItems5().length > 0) {
			GachaUpInfo.Builder upInfo = GachaUpInfo.newBuilder().setItemParentType(1);

			for (int id : getRateUpItems5()) {
				upInfo.addItemIdList(id);
				info.addMainNameId(id);
			}

			info.addGachaUpInfoList(upInfo);
		}

		if (this.getRateUpItems4().length > 0) {
			GachaUpInfo.Builder upInfo = GachaUpInfo.newBuilder().setItemParentType(2);

			for (int id : getRateUpItems4()) {
				upInfo.addItemIdList(id);
				if (info.getSubNameIdCount() == 0)
					info.addSubNameId(id);
			}

			info.addGachaUpInfoList(upInfo);
		}

		return info.build();
	}

	public enum BannerType {
		STANDARD, EVENT, WEAPON;
	}
}
