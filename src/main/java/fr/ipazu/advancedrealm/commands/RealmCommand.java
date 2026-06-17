package fr.ipazu.advancedrealm.commands;


import fr.ipazu.advancedrealm.Main;
import fr.ipazu.advancedrealm.gui.WholeGUI;
import fr.ipazu.advancedrealm.realm.Realm;
import fr.ipazu.advancedrealm.realm.RealmConfig;
import fr.ipazu.advancedrealm.realm.RealmPlayer;
import fr.ipazu.advancedrealm.realm.RealmRank;
import fr.ipazu.advancedrealm.utils.Config;
import fr.ipazu.advancedrealm.utils.ConfigFiles;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import java.util.ArrayList;

public class RealmCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String arg, String[] args) {
        YamlConfiguration config = Config.ASPECT.getConfig();
        if (!(sender instanceof Player)) {
            sender.sendMessage("The command is only executable by a player !");
            return false;
        }
        Player player = (Player) sender;
        RealmPlayer rp = RealmPlayer.getPlayer(player.getUniqueId().toString());
        if (cmd.getName().equalsIgnoreCase("realm")) {
            if (!player.hasPermission("realm")) {
                player.sendMessage("§cYou don't have the permission to do this.");
                return false;
            }
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("invite")) {
                    if (!player.hasPermission("realm.invite")) {
                        player.sendMessage(Config.pushColor(config.getString("messages.commandsnoperm")));
                        return false;
                    }
                    if (Bukkit.getPlayer(args[1]) != null) {
                        Player visedplayer = Bukkit.getPlayer(args[1]);
                        RealmPlayer vised = RealmPlayer.getPlayer(visedplayer.getUniqueId().toString());
                        if (player.getName().equalsIgnoreCase(args[1])) {
                            player.sendMessage(Config.pushColor(config.getString("messages.realmcommands.invite.selfinvite")));
                            return false;
                        }

                        if (rp.getAllRealm().size() > 1) {
                            new WholeGUI().openAllInviteGUI(player, visedplayer);
                        } else if (rp.getOwned() != null) {
                            Realm owned = rp.getOwned();
                            if (owned.getRealmMembers().size() >= rp.getOwned().getLevel().getMaxplayer()) {
                                player.sendMessage(Config.getStringWithReplacementPlayer(config.getString("messages.realmcommands.invite.realmfull"),owned,vised));
                                player.closeInventory();
                                return false;
                            }
                            if (owned.getRealmMembers().contains(vised)) {
                                player.sendMessage(Config.getStringWithReplacementPlayer(config.getString("messages.realmcommands.invite.alreadyinrealm"),owned,vised));
                                return false;
                            }
                            if (vised.realmwaiting.contains(owned)) {
                                player.sendMessage(Config.getStringWithReplacementPlayer(config.getString("messages.realmcommands.invite.alreadyinvited"),owned,vised));
                                return false;
                            }
                            if (rp.getOwned().getBanned().contains(vised)) {
                                player.sendMessage(Config.getStringWithReplacementPlayer(config.getString("messages.realmcommands.invite.banned"),owned,vised));
                            }
                            if (rp.getRankByRealm(rp.getAllRealm().get(0)) == RealmRank.OWNER || rp.getRankByRealm(rp.getAllRealm().get(0)) == RealmRank.MANAGER) {
                                vised.addWaiting(rp.getOwned());
                                visedplayer.sendMessage("§e§l" + player.getName() + " §ainvited you to join §b§l" + rp.getName() + "'s Realm.");
                                ComponentBuilder cb = new ComponentBuilder("");
                                cb.append("§6Click Here").event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7click").create())).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/realm accept " + rp.getOwned().getOwner().getName()));
                                cb.append("§a to join! You have §d120 seconds §ato accept");
                                visedplayer.spigot().sendMessage(cb.create());
                                player.sendMessage(Config.getStringWithReplacementPlayer(config.getString("messages.realmcommands.invite.msgsent"),owned,vised));
                                Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                                    vised.removeWaiting(rp.getOwned());
                                }, 20 * 120);
                            }
                        } else {
                            player.sendMessage("§cYou don't have a realm where you can invite people");
                        }
                    } else {
                        player.sendMessage(Config.pushColor(config.getString("messages.realmcommands.invite.alreadyinvited")));
                    }
                } else if (args[0].equalsIgnoreCase("accept")) {
                    if (!player.hasPermission("realm.accept")) {
                        player.sendMessage(Config.pushColor(config.getString("messages.commandsnoperm")));
                        return false;
                    }
                    for (Realm r : rp.realmwaiting) {
                        if (r.getOwner().getName().equalsIgnoreCase(args[1])) {
                            rp.removeWaiting(r);
                            r.addPlayer(rp);
                            r.promote(rp, RealmRank.MEMBER);
                            for (RealmPlayer s : r.getRealmMembers()) {
                                if (Bukkit.getPlayer(s.getName()) != null)
                                    Bukkit.getPlayer(s.getName()).sendMessage("§b§l" + player.getName() + " §esuccefully joined §l§e" + r.getOwner().getName() + "'s Realm.");
                            }
                            return true;
                        } else {
                            player.sendMessage("§cYou have no invite pending from " + args[1]);
                            return false;
                        }
                    }
                } else if (args[0].equalsIgnoreCase("join")) {
                    if (RealmPlayer.getPlayerFromName(args[0]) != null) {
                        RealmPlayer vised = RealmPlayer.getPlayerFromName(args[0]);
                        if (!player.isOp()) {
                            return false;
                        }
                        if (vised.getOwned() == null) {
                            player.sendMessage("§cThis realm doesn't exist");
                            return false;
                        }
                        vised.getOwned().addPlayer(rp);
                        vised.getOwned().promote(rp, RealmRank.MANAGER);
                        for (RealmPlayer s : vised.getOwned().getRealmMembers()) {
                            if (Bukkit.getPlayer(s.getName()) != null)
                                Bukkit.getPlayer(s.getName()).sendMessage(player.getName() + "§a joined §b§l" + vised.getName() + "'s §aRealm with admin permission.");
                        }
                    }
                } else if (args[0].equalsIgnoreCase("kick")) {
                    if (!player.hasPermission("realm.kick")) {
                        player.sendMessage("§cYou don't have the permission to do this.");
                        return false;
                    }
                    if (RealmPlayer.getPlayerFromName(args[1]) != null) {
                        if (player.getName().equalsIgnoreCase(args[1])) {
                            player.sendMessage("§cYou cannot kick yourself !");
                            return false;
                        }
                        RealmPlayer vised = RealmPlayer.getPlayerFromName(args[1]);
                        if (rp.getAllRealm().size() > 1 || rp.getOwned() == null && rp.getAllRealm().size() >= 1) {
                            new WholeGUI().openAllKickGUI(player, vised);
                        } else if (rp.getOwned() != null) {
                            if (!rp.getOwned().getRealmMembers().contains(vised)) {
                                player.sendMessage("§cThis player is not in your Realm !");
                                return false;
                            }
                            rp.getOwned().kickPlayer(vised);
                            player.sendMessage("§aYou have successfully kicked that player from the Realm.");
                            if (!rp.getOwned().getPrivacy()) {
                                if (Bukkit.getPlayer(vised.getName()) != null) {
                                    Player p = Bukkit.getPlayer(vised.getName());
                                    if (rp.getOwned().getCuboid().containsLocation(p.getLocation())) {
                                        p.teleport(ConfigFiles.getSpawn());
                                    }
                                }
                            }
                            for (RealmPlayer s : rp.getOwned().getRealmMembers()) {
                                if (Bukkit.getPlayer(s.getName()) != null)
                                    Bukkit.getPlayer(s.getName()).sendMessage("§e§l" + vised.getName() + "§c has been kicked from §b§l" + rp.getName() + "'s §cRealm.");
                            }
                            if (Bukkit.getPlayer(vised.getUniqueId()) != null) {
                                Bukkit.getPlayer(vised.getUniqueId()).sendMessage("§aYou have been kicked from §b§l" + rp.getName() + "'s §cRealm.");
                            }

                        } else {
                            player.sendMessage("§cYou don't have a realm where you can kick people");
                        }
                    } else {
                        player.sendMessage("§cThe specified player doesn't exist.");
                    }
                } else if (args[0].equalsIgnoreCase("ban")) {
                    if (!player.hasPermission("realm.ban")) {
                        player.sendMessage("§cYou don't have the permission to do this.");
                        return false;
                    }
                    if (RealmPlayer.getPlayerFromName(args[1]) != null) {
                        if (player.getName().equalsIgnoreCase(args[1])) {
                            player.sendMessage("§cYou cannot ban yourself !");
                            return false;
                        }
                        RealmPlayer vised = RealmPlayer.getPlayerFromName(args[1]);
                        if (rp.getAllRealm().size() > 1 || rp.getOwned() == null && rp.getAllRealm().size() >= 1) {
                            new WholeGUI().openAllBanGUI(player, vised);
                        } else if (rp.getOwned() != null) {
                            if (rp.getOwned().getBanned().contains(vised)) {
                                player.sendMessage("§cThis player is already banned from your Realm !");
                                return false;
                            }
                            rp.getOwned().banPlayer(vised);
                            player.sendMessage("§aYou have successfully banned that player from the Realm.");
                            if (Bukkit.getPlayer(vised.getName()) != null) {
                                Player p = Bukkit.getPlayer(vised.getName());
                                if (rp.getOwned().getCuboid().containsLocation(p.getLocation())) {
                                    p.teleport(ConfigFiles.getSpawn());
                                }
                            }
                            for (RealmPlayer s : rp.getOwned().getRealmMembers()) {
                                if (Bukkit.getPlayer(s.getName()) != null)
                                    Bukkit.getPlayer(s.getName()).sendMessage("§e§l" + vised.getName() + "§c has been banned from §b§l" + rp.getName() + "'s §cRealm.");
                            }
                            if (Bukkit.getPlayer(vised.getName()) != null) {
                                Bukkit.getPlayer(vised.getName()).sendMessage("§cYou have been banned from §b§l" + rp.getName() + "'s §cRealm.");
                            }
                        } else {
                            player.sendMessage("§cYou don't have a realm where you can ban people");
                        }
                    } else {
                        player.sendMessage("§cThe specified player doesn't exist.");
                    }
                } else if (args[0].equalsIgnoreCase("leave")) {
                    if (!player.hasPermission("realm.leave")) {
                        player.sendMessage("§cYou don't have the permission to do this.");
                        return false;
                    }
                    for (Realm r : rp.getAllRealm()) {
                        if (r.getOwner().getName().equalsIgnoreCase(args[1])) {
                            if (r.getOwner().getName().equalsIgnoreCase(player.getName())) {
                                player.sendMessage("§cFailed to leave the Realm, you are the owner.");
                                return false;
                            }
                            player.sendMessage("§aSuccessfully left §e§l" + r.getOwner().getName() + "'s §aRealm.");
                            for (RealmPlayer s : r.getRealmMembers()) {
                                if (Bukkit.getPlayer(s.getName()) != null)
                                    Bukkit.getPlayer(s.getName()).sendMessage("§e§l" + player.getName() + " §chas left §b§l" + r.getOwner().getName() + "'s §cRealm.");
                            }
                            r.kickPlayer(rp);
                            return true;
                        }
                    }
                    player.sendMessage("§cYou are not in " + args[1] + "'s Realm.");
                } else if (args[0].equalsIgnoreCase("vote")) {
                    if (!player.hasPermission("realm.vote")) {
                        player.sendMessage("§cYou don't have the permission to do this.");
                        return false;
                    }
                    if (RealmPlayer.getPlayerFromName(args[1].toLowerCase()) != null) {
                        RealmPlayer vised = RealmPlayer.getPlayerFromName(args[1].toLowerCase());
                        if (vised.getOwned() == null) {
                            player.sendMessage("§cThis realm doesn't exist");
                            return false;
                        }
                        if (rp.getOwned() != null && vised.getOwned() == rp.getOwned() && !Config.CONFIG.getConfig().getBoolean("vote.ownedvote")) {
                            player.sendMessage("§cYou cannot vote for you realm.");
                            return false;
                        }
                        if (!Config.CONFIG.getConfig().getBoolean("vote.multiplevote") && rp.voteduuid.contains(vised.getUniqueId())) {
                            player.sendMessage("§cYou have already voted for this realm");
                            return false;
                        }
                        if (getNextMilis(rp) <= System.currentTimeMillis()) {
                            vised.getOwned().addVote();
                            new RealmConfig().updateLastVote(rp);
                            player.sendMessage("§aYou have successfully voted for §e" + vised.getName() + "'s§a realm !");
                            if (!Config.CONFIG.getConfig().getBoolean("vote.multiplevote")) {
                                rp.addRealmVoted(vised.getOwned());
                                new RealmConfig().updateMultipleVote(rp);
                            }
                            return true;
                        } else
                            player.sendMessage(getNextVoteString(rp));
                    } else
                        player.sendMessage("§cThis realm doesn't exist");
                    return false;
                }
                else
                    player.sendMessage("§cWrong command, type </realm help> to have a list of the commands");
            }


            else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("top")) {
                    if (!player.hasPermission("realm.top")) {
                        player.sendMessage("§cYou don't have the permission to do this.");
                        return false;
                    }
                    new WholeGUI().openTopGUI(player);
                }
                else if (args[0].equalsIgnoreCase("help")) {
                    player.sendMessage("                      §eRealms commands:" +
                            "\n§2/claim: §aClaim a new realm" +
                            "\n§2/unclaim: §aUnclaim your owned realm(delete)" +
                            "\n§2/visit §o<player>: §r§aVisit someone's realm" +
                            "\n§2/home: §aGo to your realms" +
                            "\n§2/realm: §aOpen realm gui" +
                            "\n§2/realm invite §o<player>: §r§aInvite someone to a realm" +
                            "\n§2/realm leave §o<player>: §r§aLeave a realm" +
                            "\n§2/realm kick §o<player>: §r§aKick someone from a realm" +
                            "\n§2/realm ban §o<player>: §r§aBan someone from a realm" +
                            "\n§2/realm top: §aOpen realm top gui" +
                            "\n§2/realm vote §o<player>: §r§aVote for someone's realm"
                    );
                }
                else
                    player.sendMessage("§cWrong command, type </realm help> to have a list of the commands");
            }
            else if (args.length == 0) {
                if (rp.getOwned() == null && rp.getAllRealm().size() == 0) {
                    player.sendMessage("§cYou haven't claimed a realm: /claim");
                    return false;
                }
                if (rp.getOwned() != null && rp.getAllRealm().size() <= 1) {
                    new WholeGUI().openRealmGui(player, rp.getAllRealm().get(0), false);
                } else
                    new WholeGUI().openAllRealmGUI(player);
            }
            else
                player.sendMessage("§cWrong command, type </realm help> to have a list of the commands");

        }
        return true;
    }

    public long getNextMilis(RealmPlayer rp) {
        long lastmilis = rp.getLastvote();
        //for seven hours
        lastmilis += ConfigFiles.getCooldownValue();
        return lastmilis;
    }

    public String getNextVoteString(RealmPlayer rp) {
        String nextvotestring = "§eYou already have voted, you can vote again in ";
        long milis = getNextMilis(rp) - System.currentTimeMillis();
        long days = milis / 86400000;
        milis = milis % 86400000;
        if (days != 0)
            nextvotestring += "§6" + days + " days ";
        long hours = milis / 3600000;
        milis = milis % 3600000;
        if (hours != 0)
            nextvotestring += "§6" + hours + " hours ";
        long minutes = milis / 60000;
        milis = milis % 60000;
        if (minutes != 0)
            nextvotestring += "§6" + minutes + " minutes ";
        long seconds = milis / 1000;
        if (seconds != 0)
            nextvotestring += "§6" + seconds + " seconds.";
        return nextvotestring;
    }
    private void useless()
    {
        ArrayList<String> strs = new ArrayList<>();
        strs.forEach(s -> Main.getInstance().getLogger().info(s));
    }
}

