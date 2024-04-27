package xhud;

import cn.nukkit.Player;
import cn.nukkit.network.protocol.SetDisplayObjectivePacket;
import cn.nukkit.network.protocol.SetScorePacket;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.scoreboard.IScoreboardLine;
import cn.nukkit.scoreboard.Scoreboard;
import cn.nukkit.scoreboard.ScoreboardLine;
import cn.nukkit.scoreboard.data.DisplaySlot;
import cn.nukkit.scoreboard.data.SortOrder;
import cn.nukkit.scoreboard.displayer.IScoreboardViewer;
import cn.nukkit.scoreboard.scorer.FakeScorer;
import cn.nukkit.utils.Config;
import me.onebone.economyapi.EconomyAPI;

public class Main extends PluginBase {
	public static Config config;

	public void onEnable() {
		saveDefaultConfig();
		config = getConfig();
		getServer().getScheduler().scheduleDelayedRepeatingTask(new Hud(this), 10, 10);
	}
}

class Hud extends Thread {
	private Main plugin;

	public Hud(Main plugin) {
		this.plugin = plugin;
	}

	@Override
	public void run() {
		for (Player player : plugin.getServer().getOnlinePlayers().values()) {
			String money;
			String hud = Main.config.getString("Message")
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

			hud = hud.replaceAll("<MONEY>", money);

			Scoreboard scoreboard = new Scoreboard("objectiveName", "SideBar", "dummy", SortOrder.ASCENDING);

			scoreboard.addViewer(player, DisplaySlot.SIDEBAR);

			FakeScorer scorer = new FakeScorer("Player1");
			IScoreboardLine line = new ScoreboardLine(scoreboard, scorer, plugin.getServer().getOnlinePlayers().size());
			scoreboard.addLine(line);

			line.setScore(plugin.getServer().getOnlinePlayers().size());
			scoreboard.updateScore(line);

			SetDisplayObjectivePacket objectivePacket = new SetDisplayObjectivePacket();
			objectivePacket.displaySlot = DisplaySlot.SIDEBAR;
			objectivePacket.objectiveName = "objectiveName";
			objectivePacket.displayName = "ABOBA";
			objectivePacket.criteriaName = "dummy";
			objectivePacket.sortOrder = SortOrder.ASCENDING;

			player.dataPacket(objectivePacket);
		}
	}
}
