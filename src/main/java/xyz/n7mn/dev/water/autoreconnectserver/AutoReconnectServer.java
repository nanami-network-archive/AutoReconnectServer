package xyz.n7mn.dev.water.autoreconnectserver;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.*;

public final class AutoReconnectServer extends Plugin {

    private Timer timer = new Timer();
    @Override
    public void onEnable() {
        // Plugin startup logic

        TimerTask task = new TimerTask() {

            List<ProxiedPlayer> playerList = new ArrayList<>();

            @Override
            public void run() {
                TextComponent component = new TextComponent();
                Collection<ProxiedPlayer> players = getProxy().getPlayers();

                try {
                    ServerInfo tct = getProxy().getServerInfo("tct");
                    tct.ping((result, error) -> {
                        if (error.getMessage() != null){
                            for (ProxiedPlayer player : players){
                                component.setText(ChatColor.RED + "現在 TCT鯖 は 停止中 です。 しばらくお待ち下さい。\n(自動接続されます)");
                                player.sendMessage(component);
                                playerList.add(player);
                            }
                        } else {
                            for (ProxiedPlayer player : players){

                                if (playerList.contains(player) && !player.getServer().getInfo().getName().equals("tct")){
                                    component.setText(ChatColor.GREEN + "自動接続しています...");
                                    player.sendMessage(component);
                                    player.connect(tct);
                                    playerList.remove(player);
                                    return;
                                }

                                if (!player.getServer().getInfo().getName().equals("tct")){
                                    TextComponent component1 = new TextComponent();
                                    component1.setText(ChatColor.GREEN + "現在 TCT鯖(1.8以降) は 起動しています。\n");

                                    TextComponent component2 = new TextComponent();
                                    component2.setText(ChatColor.YELLOW + "" + ChatColor.UNDERLINE +"TCT鯖へ入室");
                                    component2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/server tct"));
                                    component1.addExtra(component2);

                                    player.sendMessage(component1);
                                }
                            }
                        }
                    });
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        };

        timer.schedule(task, 0L, 5000L);
        getProxy().getPluginManager().registerListener(this, new EventListner());

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        timer.cancel();
    }
}
