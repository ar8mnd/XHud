package xhud;

import cn.nukkit.Player;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.scoreboard.Scoreboard;
import cn.nukkit.scoreboard.data.DisplaySlot;
import cn.nukkit.scoreboard.data.SortOrder;
import cn.nukkit.utils.Config;
import me.onebone.economyapi.EconomyAPI;

import java.util.*;
import java.util.stream.Collectors;

public class Main extends PluginBase {
	public static Config config;

	public void onEnable() {
		saveDefaultConfig();
		config = getConfig();
		getServer().getScheduler().scheduleDelayedRepeatingTask(new Hud(this), 10, 10);
	}
}

class Hud extends Thread {
	private final Main plugin;

	public Hud(Main plugin) {
		this.plugin = plugin;
	}

	private List<String> lst = new ArrayList<>();

	@Override
	public void run() {
		for (Player player : plugin.getServer().getOnlinePlayers().values()) {
			String money;
			String text = Main.config.getString("Message")
					.replaceAll("<NAME>", player.getName())
					.replaceAll("<WORLD>", player.getLevel().getName())
					.replaceAll("<X>", Integer.toString((int) player.x))
					.replaceAll("<Y>", Integer.toString((int) player.y))
					.replaceAll("<Z>", Integer.toString((int) player.z))
					.replaceAll("<N>", "\n")
					.replaceAll("<PLAYERS>", Integer.toString(plugin.getServer().getOnlinePlayers().size()))
					.replaceAll("<MAXPLAYERS>", Integer.toString(plugin.getServer().getMaxPlayers()))
					.replaceAll("<PING>", Integer.toString(player.getPing()))
					.replaceAll("<TPS>", Float.toString(plugin.getServer().getTicksPerSecond()));
			try {
				Class.forName("me.onebone.economyapi.EconomyAPI");
				money = Double.toString(EconomyAPI.getInstance().myMoney(player));
			} catch (Exception e) {
				money = "null";
			}

			StringTokenizer tokenizer = new StringTokenizer(text, "*");

			int tokenCount = tokenizer.countTokens();
			String[] stringArray = new String[tokenCount];

			for (int i = 0; i < tokenCount; i++) {
				stringArray[i] = tokenizer.nextToken();
			}

			Scoreboard scoreboard = new Scoreboard("sidebar", stringArray[0], "dummy", SortOrder.ASCENDING);

			List<String> sliceList = Arrays.stream(stringArray)
					.skip(1)
					.limit(stringArray.length)
					.collect(Collectors.toList());

			scoreboard.setLines(sliceList);
			scoreboard.addViewer(player, DisplaySlot.SIDEBAR);
		}
	}
}
