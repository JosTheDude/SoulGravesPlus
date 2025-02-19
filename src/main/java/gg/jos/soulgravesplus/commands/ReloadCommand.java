package gg.jos.soulgravesplus.commands;

import gg.jos.soulgravesplus.SoulGravesPlus;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand implements CommandExecutor {
    private final SoulGravesPlus soulGravesPlus;

    public ReloadCommand(SoulGravesPlus soulGravesPlus) {
        this.soulGravesPlus = soulGravesPlus;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if (args.length == 0 || !args[0].equalsIgnoreCase("reload")) {
            sender.sendMessage("§cUsage: /soulgravesplus reload");
            return true;
        }

        if (sender instanceof Player && !sender.hasPermission("soulgravesplus.admin")) {
            sender.sendMessage("§cYou do not have permission to use this command. §7(soulgravesplus.admin)");
            return true;
        }

        this.soulGravesPlus.reloadConfig();
        this.soulGravesPlus.updateConfig();

        sender.sendMessage("§aSoulGravesPlus configuration reloaded.");
        return true;
    }
}