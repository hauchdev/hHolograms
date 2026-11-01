package dev.hauchdev.hholograms.command;

import dev.hauchdev.hholograms.bootstrap.Bootstrap;
import dev.hauchdev.hholograms.config.PluginConfig;
import dev.hauchdev.hholograms.config.PluginMessages;
import dev.hauchdev.hholograms.manager.HologramManager;
import dev.hauchdev.hholograms.manager.UpdateManager;
import dev.hauchdev.hholograms.model.Hologram;
import dev.hauchdev.hholograms.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public final class HHologramsCommand implements CommandExecutor, TabCompleter {
    private final Plugin plugin;
    private final PluginConfig config;
    private final PluginMessages messages;
    private final HologramManager holograms;
    private final UpdateManager updateManager;

    public HHologramsCommand(Plugin plugin, PluginConfig config, PluginMessages messages,
                             HologramManager holograms, UpdateManager updateManager) {
        this.plugin = plugin;
        this.config = config;
        this.messages = messages;
        this.holograms = holograms;
        this.updateManager = updateManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) { sendHelp(sender); return true; }
        String sub = args[0].toLowerCase(Locale.ROOT);
        try {
            switch (sub) {
                case "help", "?" -> sendHelp(sender);
                case "create" -> cmdCreate(sender, args);
                case "remove", "delete" -> cmdRemove(sender, args);
                case "list" -> cmdList(sender);
                case "info" -> cmdInfo(sender, args);
                case "move" -> cmdMove(sender, args);
                case "tp", "teleport" -> cmdTeleport(sender, args);
                case "duplicate", "copy" -> cmdDuplicate(sender, args);
                case "edit" -> cmdEdit(sender, args);
                case "page" -> cmdPage(sender, args);
                case "spacing" -> cmdSpacing(sender, args);
                case "billboard" -> cmdBillboard(sender, args);
                case "range", "viewrange" -> cmdRange(sender, args);
                case "background", "bg" -> cmdBackground(sender, args);
                case "type" -> cmdType(sender, args);
                case "content" -> cmdContent(sender, args);
                case "scale" -> cmdScale(sender, args);
                case "align", "alignment" -> cmdAlign(sender, args);
                case "brightness" -> cmdBrightness(sender, args);
                case "linewidth", "width" -> cmdLineWidth(sender, args);
                case "translate" -> cmdTranslate(sender, args);
                case "rotate" -> cmdRotate(sender, args);
                case "condition" -> cmdCondition(sender, args);
                case "click", "onclick" -> cmdClick(sender, args);
                case "animation", "anim" -> cmdAnimation(sender, args);
                case "pause" -> cmdPause(sender, args, true);
                case "resume", "unpause" -> cmdPause(sender, args, false);
                case "reload" -> cmdReload(sender);
                case "status", "diag", "diagnostics" -> cmdStatus(sender);
                default -> sender.sendMessage(ChatColor.RED + "Unknown subcommand. Use /hh help");
            }
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Error: " + e.getMessage());
        }
        return true;
    }

    private boolean perm(CommandSender s, String node) {
        return s.hasPermission("hholograms.admin") || s.hasPermission("hholograms." + node);
    }

    private void noPerm(CommandSender s) { s.sendMessage(ChatColor.RED + "No permission."); }

    private void usage(CommandSender s, String u) { s.sendMessage(ChatColor.YELLOW + "Usage: /hh " + u); }

    private boolean validName(String name) {
        return name != null && name.matches("[A-Za-z0-9_-]{1,32}");
    }

    private String join(String[] args, int start) {
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < args.length; i++) {
            if (i > start) sb.append(' ');
            sb.append(args[i]);
        }
        return sb.toString();
    }

    private Location parseLocation(String w, String x, String y, String z, String yaw, String pitch) {
        try {
            org.bukkit.World world = Bukkit.getWorld(w);
            if (world == null) return null;
            double dx = Double.parseDouble(x);
            double dy = Double.parseDouble(y);
            double dz = Double.parseDouble(z);
            float fy = yaw == null ? 0f : Float.parseFloat(yaw);
            float fp = pitch == null ? 0f : Float.parseFloat(pitch);
            return new Location(world, dx, dy, dz, fy, fp);
        } catch (Exception e) { return null; }
    }

    private String fmtLoc(Location l) {
        return l.getWorld().getName() + " " + String.format("%.2f, %.2f, %.2f", l.getX(), l.getY(), l.getZ());
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== hHolograms ===");
        String[] lines = {
            "/hh create <name> [world x y z]",
            "/hh remove <name>",
            "/hh list",
            "/hh info <name>",
            "/hh move <name> [world x y z]",
            "/hh tp <name>",
            "/hh duplicate <source> <newName>",
            "/hh edit <name> add <text>",
            "/hh edit <name> set <index> <text>",
            "/hh edit <name> insert <index> <text>",
            "/hh edit <name> remove <index>",
            "/hh edit <name> clear",
            "/hh edit <name> click <line> <command...|clear>",
            "/hh page <name> <next|prev|set|add|remove> [page]",
            "/hh spacing <name> <value>",
            "/hh billboard <name> <center|vertical|horizontal|fixed>",
            "/hh range <name> <blocks>",
            "/hh background <name> <transparent|hex>",
            "/hh type <name> <text|item|block|head>",
            "/hh content <name> <material/block>",
            "/hh scale <name> <factor>",
            "/hh align <name> <left|center|right>",
            "/hh brightness <name> <0-15|-1>",
            "/hh linewidth <name> <1-4096>",
            "/hh translate <name> <x> <y> <z>",
            "/hh rotate <name> <yaw> <pitch>",
            "/hh condition <name> <expr|clear>",
            "/hh click <name> <command|clear>",
            "/hh animation <name> <none|scroll|typewriter|rainbow|pulse|wave|bounce|glitch|fade|slide|shake> [speed]",
            "/hh pause <name> / /hh resume <name>",
            "/hh reload",
            "/hh status"
        };
        for (String l : lines) sender.sendMessage(ChatColor.GRAY + l);
    }

    private void cmdCreate(CommandSender sender, String[] args) {
        if (!perm(sender, "create")) { noPerm(sender); return; }
        if (args.length < 2) { usage(sender, "create <name>"); return; }
        String name = args[1];
        if (!validName(name)) { sender.sendMessage(ChatColor.RED + "Invalid name."); return; }
        if (holograms.exists(name)) { sender.sendMessage(ChatColor.RED + "Already exists."); return; }
        Location loc;
        if (sender instanceof Player p && args.length <= 2) loc = p.getLocation();
        else if (args.length >= 6) loc = parseLocation(args[2], args[3], args[4], args[5], args.length>6?args[6]:null, args.length>7?args[7]:null);
        else { sender.sendMessage(ChatColor.RED + "Console: create <name> <world> <x> <y> <z>"); return; }
        if (loc == null || loc.getWorld() == null) { sender.sendMessage(ChatColor.RED + "Invalid location."); return; }
        if (holograms.create(name, loc) == null) { sender.sendMessage(ChatColor.RED + "Failed."); return; }
        sender.sendMessage(ChatColor.GREEN + "Created '" + name + "'.");
    }

    private void cmdRemove(CommandSender sender, String[] args) {
        if (!perm(sender, "remove")) { noPerm(sender); return; }
        if (args.length < 2) { usage(sender, "remove <name>"); return; }
        if (holograms.delete(args[1])) sender.sendMessage(ChatColor.GREEN + "Removed.");
        else sender.sendMessage(ChatColor.RED + "Not found.");
    }

    private void cmdList(CommandSender sender) {
        if (!perm(sender, "list")) { noPerm(sender); return; }
        List<Hologram> all = new ArrayList<>(holograms.all());
        if (all.isEmpty()) { sender.sendMessage(ChatColor.GRAY + "No holograms."); return; }
        sender.sendMessage(ChatColor.GOLD + "Holograms (" + all.size() + "):");
        for (Hologram h : all) {
            sender.sendMessage(ChatColor.YELLOW + "- " + h.name() + ChatColor.GRAY + " (" + h.lines().size() + " lines)");
        }
    }

    private void cmdInfo(CommandSender sender, String[] args) {
        if (!perm(sender, "info")) { noPerm(sender); return; }
        if (args.length < 2) { usage(sender, "info <name>"); return; }
        Hologram h = holograms.get(args[1]);
        if (h == null) { sender.sendMessage(ChatColor.RED + "Not found."); return; }
        sender.sendMessage(ChatColor.GOLD + "Hologram: " + h.name());
        sender.sendMessage(ChatColor.GRAY + "Location: " + fmtLoc(h.location()));
        sender.sendMessage(ChatColor.GRAY + "Lines: " + h.lines().size() + " | Spacing: " + h.lineSpacing() + " | Width: " + h.lineWidth());
        sender.sendMessage(ChatColor.GRAY + "Page: " + (h.currentPage() + 1) + "/" + h.pageCount());
        sender.sendMessage(ChatColor.GRAY + "Billboard: " + h.billboard() + " | Range: " + h.viewRange());
        sender.sendMessage(ChatColor.GRAY + "Background: " + h.backgroundColor() + " | Shadow: " + h.shadowed() + " | SeeThrough: " + h.seeThrough());
        sender.sendMessage(ChatColor.GRAY + "Type: " + h.type() + " | Content: " + h.content());
        sender.sendMessage(ChatColor.GRAY + "Animation: " + h.animation() + " (speed " + h.animationSpeed() + ", paused=" + h.paused() + ")");
        for (int i = 0; i < h.lines().size(); i++) {
            dev.hauchdev.hholograms.model.HologramLine hl = h.lines().get(i);
            sender.sendMessage(ChatColor.YELLOW + "  " + i + ": " + hl.text() + ChatColor.GRAY + " (scale=" + hl.scale() + ", anim=" + hl.animation() + ")");
        }
    }

    private void cmdMove(CommandSender sender, String[] args) {
        if (!perm(sender, "move")) { noPerm(sender); return; }
        if (args.length < 2) { usage(sender, "move <name>"); return; }
        Hologram h = holograms.get(args[1]);
        if (h == null) { sender.sendMessage(ChatColor.RED + "Not found."); return; }
        Location loc;
        if (sender instanceof Player p && args.length <= 2) loc = p.getLocation();
        else if (args.length >= 6) loc = parseLocation(args[2], args[3], args[4], args[5], args.length>6?args[6]:null, args.length>7?args[7]:null);
        else { sender.sendMessage(ChatColor.RED + "Usage: move <name> [world x y z]"); return; }
        if (loc == null || loc.getWorld() == null) { sender.sendMessage(ChatColor.RED + "Invalid location."); return; }
        holograms.move(args[1], loc);
        sender.sendMessage(ChatColor.GREEN + "Moved.");
    }

    private void cmdTeleport(CommandSender sender, String[] args) {
        if (!(sender instanceof Player p)) { sender.sendMessage("Players only."); return; }
        if (!perm(sender, "teleport")) { noPerm(sender); return; }
        if (args.length < 2) { usage(sender, "tp <name>"); return; }
        Hologram h = holograms.get(args[1]);
        if (h == null) { sender.sendMessage(ChatColor.RED + "Not found."); return; }
        p.teleport(h.location());
        sender.sendMessage(ChatColor.GREEN + "Teleported.");
    }

    private void cmdDuplicate(CommandSender sender, String[] args) {
        if (!perm(sender, "duplicate")) { noPerm(sender); return; }
        if (args.length < 3) { usage(sender, "duplicate <source> <newName>"); return; }
        Hologram h = holograms.duplicate(args[1], args[2], null);
        if (h == null) { sender.sendMessage(ChatColor.RED + "Failed."); return; }
        sender.sendMessage(ChatColor.GREEN + "Duplicated.");
    }

    private void cmdEdit(CommandSender sender, String[] args) {
        if (!perm(sender, "edit")) { noPerm(sender); return; }
        if (args.length < 3) { usage(sender, "edit <name> <add|set|insert|remove|clear|swap>"); return; }
        String name = args[1];
        Hologram h = holograms.get(name);
        if (h == null) { sender.sendMessage(ChatColor.RED + "Not found."); return; }
        String op = args[2].toLowerCase(Locale.ROOT);
        boolean trusted = sender.hasPermission("hholograms.trustedformatting");
        switch (op) {
            case "add" -> {
                if (args.length < 4) { usage(sender, "edit <name> add <text>"); return; }
                h.lines().add(new dev.hauchdev.hholograms.model.HologramLine(TextUtil.sanitize(join(args, 3), trusted)));
            }
            case "set" -> {
                if (args.length < 5) { usage(sender, "edit <name> set <index> <text>"); return; }
                int idx = parseInt(args[3]);
                if (idx < 0 || idx >= h.lines().size()) { sender.sendMessage(ChatColor.RED + "Bad index."); return; }
                h.lines().get(idx).setText(TextUtil.sanitize(join(args, 4), trusted));
            }
            case "insert" -> {
                if (args.length < 5) { usage(sender, "edit <name> insert <index> <text>"); return; }
                int idx = parseInt(args[3]);
                if (idx < 0 || idx > h.lines().size()) { sender.sendMessage(ChatColor.RED + "Bad index."); return; }
                h.lines().add(idx, new dev.hauchdev.hholograms.model.HologramLine(TextUtil.sanitize(join(args, 4), trusted)));
            }
            case "scale" -> {
                if (args.length < 5) { usage(sender, "edit <name> scale <index> <value>"); return; }
                int idx = parseInt(args[3]);
                if (idx < 0 || idx >= h.lines().size()) { sender.sendMessage(ChatColor.RED + "Bad index."); return; }
                Float scale = finiteFloat(args[4]);
                if (scale == null || scale <= 0 || scale > 16) { sender.sendMessage(ChatColor.RED + "Line scale must be a finite number greater than 0 and at most 16."); return; }
                h.lines().get(idx).setScale(scale);
            }
            case "animation" -> {
                if (args.length < 5) { usage(sender, "edit <name> animation <index> <anim> [speed]"); return; }
                int idx = parseInt(args[3]);
                if (idx < 0 || idx >= h.lines().size()) { sender.sendMessage(ChatColor.RED + "Bad index."); return; }
                String animation = args[4].toLowerCase(Locale.ROOT);
                if (!Arrays.asList("none", "scroll", "typewriter", "rainbow", "pulse", "wave", "bounce", "glitch", "fade", "slide", "shake").contains(animation)) {
                    sender.sendMessage(ChatColor.RED + "Unknown animation."); return;
                }
                Integer animationSpeed = null;
                if (args.length >= 6) {
                    int speed = parseInt(args[5]);
                    if (speed < 1 || speed > 100) { sender.sendMessage(ChatColor.RED + "Animation speed must be an integer from 1 to 100."); return; }
                    animationSpeed = speed;
                }
                h.lines().get(idx).setAnimation(animation);
                if (animationSpeed != null) h.lines().get(idx).setAnimationSpeed(animationSpeed);
            }
            case "click" -> {
                if (args.length < 5) { usage(sender, "edit <name> click <index> <command...|clear>"); return; }
                int idx = parseInt(args[3]);
                if (idx < 0 || idx >= h.lines().size()) { sender.sendMessage(ChatColor.RED + "Bad index."); return; }
                h.lines().get(idx).setOnClickCommand(args[4].equalsIgnoreCase("clear") ? "" : join(args, 4));
            }
            case "pause" -> {
                if (args.length < 4) { usage(sender, "edit <name> pause <index>"); return; }
                int idx = parseInt(args[3]);
                if (idx < 0 || idx >= h.lines().size()) { sender.sendMessage(ChatColor.RED + "Bad index."); return; }
                h.lines().get(idx).setPaused(true);
            }
            case "resume" -> {
                if (args.length < 4) { usage(sender, "edit <name> resume <index>"); return; }
                int idx = parseInt(args[3]);
                if (idx < 0 || idx >= h.lines().size()) { sender.sendMessage(ChatColor.RED + "Bad index."); return; }
                h.lines().get(idx).setPaused(false);
            }
            case "remove" -> {
                if (args.length < 4) { usage(sender, "edit <name> remove <index>"); return; }
                int idx = parseInt(args[3]);
                if (idx < 0 || idx >= h.lines().size()) { sender.sendMessage(ChatColor.RED + "Bad index."); return; }
                h.lines().remove(idx);
            }
            case "clear" -> h.lines().clear();
            case "swap" -> {
                if (args.length < 5) { usage(sender, "edit <name> swap <i> <j>"); return; }
                int a = parseInt(args[3]); int b = parseInt(args[4]);
                if (a < 0 || b < 0 || a >= h.lines().size() || b >= h.lines().size()) { sender.sendMessage(ChatColor.RED + "Bad index."); return; }
                dev.hauchdev.hholograms.model.HologramLine tmp = h.lines().get(a);
                h.lines().set(a, h.lines().get(b));
                h.lines().set(b, tmp);
            }
            default -> { sender.sendMessage(ChatColor.RED + "Unknown edit op."); return; }
        }
        holograms.renderer().render(h);
        holograms.save();
        sender.sendMessage(ChatColor.GREEN + "Updated.");
    }

    private void cmdPage(CommandSender sender, String[] args) {
        if (args.length < 3) { usage(sender, "page <name> <next|prev|set|add|remove> [page]"); return; }
        Hologram h = holograms.get(args[1]);
        if (h == null) { sender.sendMessage(ChatColor.RED + "Not found."); return; }
        String operation = args[2].toLowerCase(Locale.ROOT);
        switch (operation) {
            case "next", "prev" -> {
                if (!perm(sender, "interact")) { noPerm(sender); return; }
                int next = operation.equals("next")
                        ? (h.currentPage() + 1) % h.pageCount()
                        : (h.currentPage() - 1 + h.pageCount()) % h.pageCount();
                h.setCurrentPage(next);
            }
            case "set", "goto" -> {
                if (!perm(sender, "edit")) { noPerm(sender); return; }
                if (args.length < 4) { usage(sender, "page <name> set <1-" + h.pageCount() + ">"); return; }
                int page = parseInt(args[3]) - 1;
                if (!h.setCurrentPage(page)) { sender.sendMessage(ChatColor.RED + "Page must be from 1 to " + h.pageCount() + "."); return; }
            }
            case "add" -> {
                if (!perm(sender, "edit")) { noPerm(sender); return; }
                h.addPage();
            }
            case "remove" -> {
                if (!perm(sender, "edit")) { noPerm(sender); return; }
                if (!h.removeCurrentPage()) { sender.sendMessage(ChatColor.RED + "A hologram must keep at least one page."); return; }
            }
            default -> { sender.sendMessage(ChatColor.RED + "Use next, prev, set, add, or remove."); return; }
        }
        holograms.renderer().render(h);
        holograms.save();
        sender.sendMessage(ChatColor.GREEN + "Showing page " + (h.currentPage() + 1) + "/" + h.pageCount() + ".");
    }

    private void cmdSpacing(CommandSender sender, String[] args) {
        if (!perm(sender, "edit")) { noPerm(sender); return; }
        if (args.length < 3) { usage(sender, "spacing <name> <value>"); return; }
        Hologram h = holograms.get(args[1]);
        if (h == null) { sender.sendMessage(ChatColor.RED + "Not found."); return; }
        Double spacing = finiteDouble(args[2]);
        if (spacing == null || spacing < 0 || spacing > 10) { sender.sendMessage(ChatColor.RED + "Spacing must be a finite number from 0 to 10."); return; }
        h.setLineSpacing(spacing);
        holograms.renderer().render(h);
        holograms.save();
        sender.sendMessage(ChatColor.GREEN + "Spacing set.");
    }

    private void cmdBillboard(CommandSender sender, String[] args) {
        if (!perm(sender, "edit")) { noPerm(sender); return; }
        if (args.length < 3) { usage(sender, "billboard <name> <center|vertical|horizontal|fixed>"); return; }
        Hologram h = holograms.get(args[1]);
        if (h == null) { sender.sendMessage(ChatColor.RED + "Not found."); return; }
        String billboard = args[2].toUpperCase(Locale.ROOT);
        if (!Arrays.asList("CENTER", "VERTICAL", "HORIZONTAL", "FIXED").contains(billboard)) { sender.sendMessage(ChatColor.RED + "Billboard must be center, vertical, horizontal, or fixed."); return; }
        h.setBillboard(billboard);
        holograms.renderer().render(h);
        holograms.save();
        sender.sendMessage(ChatColor.GREEN + "Billboard set.");
    }

    private void cmdRange(CommandSender sender, String[] args) {
        if (!perm(sender, "edit")) { noPerm(sender); return; }
        if (args.length < 3) { usage(sender, "range <name> <blocks>"); return; }
        Hologram h = holograms.get(args[1]);
        if (h == null) { sender.sendMessage(ChatColor.RED + "Not found."); return; }
        int range = parseInt(args[2]);
        if (range < 1 || range > 128) { sender.sendMessage(ChatColor.RED + "View range must be an integer from 1 to 128."); return; }
        h.setViewRange(range);
        holograms.renderer().render(h);
        holograms.save();
        sender.sendMessage(ChatColor.GREEN + "Range set.");
    }

    private void cmdBackground(CommandSender sender, String[] args) {
        if (!perm(sender, "edit")) { noPerm(sender); return; }
        if (args.length < 3) { usage(sender, "background <name> <transparent|#RRGGBB|#AARRGGBB>"); return; }
        Hologram h = holograms.get(args[1]);
        if (h == null) { sender.sendMessage(ChatColor.RED + "Not found."); return; }
        String bg = args[2];
        if (bg.equalsIgnoreCase("remove") || bg.equalsIgnoreCase("none")) bg = "transparent";
        if (!bg.equalsIgnoreCase("transparent") && !bg.matches("#?(?:[0-9a-fA-F]{6}|[0-9a-fA-F]{8})")) {
            sender.sendMessage(ChatColor.RED + "Use transparent or a valid #RRGGBB/#AARRGGBB color.");
            return;
        }
        h.setBackgroundColor(bg);
        holograms.renderer().render(h);
        holograms.save();
        sender.sendMessage(ChatColor.GREEN + "Background set.");
    }

    private void cmdType(CommandSender sender, String[] args) {
        if (!perm(sender, "edit")) { noPerm(sender); return; }
        if (args.length < 3) { usage(sender, "type <name> <text|item|block|head>"); return; }
        Hologram h = holograms.get(args[1]);
        if (h == null) { sender.sendMessage(ChatColor.RED + "Not found."); return; }
        String type = args[2].toUpperCase(Locale.ROOT);
        if (!Arrays.asList("TEXT", "ITEM", "BLOCK", "HEAD").contains(type)) { sender.sendMessage(ChatColor.RED + "Type must be text, item, block, or head."); return; }
        h.setType(type);
        holograms.renderer().render(h);
        holograms.save();
        sender.sendMessage(ChatColor.GREEN + "Type set.");
    }

    private void cmdContent(CommandSender sender, String[] args) {
        if (!perm(sender, "edit")) { noPerm(sender); return; }
        if (args.length < 3) { usage(sender, "content <name> <material/block>"); return; }
        Hologram h = holograms.get(args[1]);
        if (h == null) { sender.sendMessage(ChatColor.RED + "Not found."); return; }
        h.setContent(join(args, 2));
        holograms.renderer().render(h);
        holograms.save();
        sender.sendMessage(ChatColor.GREEN + "Content set.");
    }

    private void cmdBrightness(CommandSender sender, String[] args) {
        if (!perm(sender, "edit")) { noPerm(sender); return; }
        if (args.length < 3) { usage(sender, "brightness <name> <0-15|-1>"); return; }
        Hologram h = holograms.get(args[1]);
        if (h == null) { sender.sendMessage(ChatColor.RED + "Not found."); return; }
        int brightness = parseInt(args[2]);
        if (brightness < -1 || brightness > 15) { sender.sendMessage(ChatColor.RED + "Brightness must be an integer from -1 to 15."); return; }
        h.setBrightness(brightness);
        holograms.renderer().render(h);
        holograms.save();
        sender.sendMessage(ChatColor.GREEN + "Brightness set.");
    }

    private void cmdLineWidth(CommandSender sender, String[] args) {
        if (!perm(sender, "edit")) { noPerm(sender); return; }
        if (args.length < 3) { usage(sender, "linewidth <name> <1-4096>"); return; }
        Hologram h = holograms.get(args[1]);
        if (h == null) { sender.sendMessage(ChatColor.RED + "Not found."); return; }
        int width = parseInt(args[2]);
        if (width < 1 || width > 4096) { sender.sendMessage(ChatColor.RED + "Line width must be an integer from 1 to 4096."); return; }
        h.setLineWidth(width);
        holograms.renderer().render(h);
        holograms.save();
        sender.sendMessage(ChatColor.GREEN + "Line width set.");
    }

    private void cmdTranslate(CommandSender sender, String[] args) {
        if (!perm(sender, "edit")) { noPerm(sender); return; }
        if (args.length < 5) { usage(sender, "translate <name> <x> <y> <z>"); return; }
        Hologram h = holograms.get(args[1]);
        if (h == null) { sender.sendMessage(ChatColor.RED + "Not found."); return; }
        Double x = finiteDouble(args[2]), y = finiteDouble(args[3]), z = finiteDouble(args[4]);
        if (x == null || y == null || z == null) { sender.sendMessage(ChatColor.RED + "Translation values must be finite numbers."); return; }
        h.setTranslationX(x);
        h.setTranslationY(y);
        h.setTranslationZ(z);
        holograms.renderer().render(h);
        holograms.save();
        sender.sendMessage(ChatColor.GREEN + "Translation set.");
    }

    private void cmdRotate(CommandSender sender, String[] args) {
        if (!perm(sender, "edit")) { noPerm(sender); return; }
        if (args.length < 4) { usage(sender, "rotate <name> <yaw> <pitch>"); return; }
        Hologram h = holograms.get(args[1]);
        if (h == null) { sender.sendMessage(ChatColor.RED + "Not found."); return; }
        Float yaw = finiteFloat(args[2]), pitch = finiteFloat(args[3]);
        if (yaw == null || pitch == null) { sender.sendMessage(ChatColor.RED + "Rotation values must be finite numbers."); return; }
        h.setRotationYaw(yaw);
        h.setRotationPitch(pitch);
        if (!h.billboard().equalsIgnoreCase("FIXED")) {
            h.setBillboard("FIXED");
            sender.sendMessage(ChatColor.GRAY + "Billboard set to FIXED to apply rotation.");
        }
        holograms.renderer().render(h);
        holograms.save();
        sender.sendMessage(ChatColor.GREEN + "Rotation set to yaw=" + h.rotationYaw() + " pitch=" + h.rotationPitch() + ".");
    }

    private void cmdCondition(CommandSender sender, String[] args) {
        if (!perm(sender, "edit")) { noPerm(sender); return; }
        if (args.length < 2) { usage(sender, "condition <name> <expr...|clear>"); return; }
        Hologram h = holograms.get(args[1]);
        if (h == null) { sender.sendMessage(ChatColor.RED + "Not found."); return; }
        if (args.length < 3 || args[2].equalsIgnoreCase("clear")) h.setVisibilityCondition("");
        else h.setVisibilityCondition(join(args, 2));
        holograms.save();
        sender.sendMessage(ChatColor.GREEN + "Condition set.");
    }

    private void cmdClick(CommandSender sender, String[] args) {
        if (!perm(sender, "edit")) { noPerm(sender); return; }
        if (args.length < 2) { usage(sender, "click <name> <command...|clear>"); return; }
        Hologram h = holograms.get(args[1]);
        if (h == null) { sender.sendMessage(ChatColor.RED + "Not found."); return; }
        if (args.length < 3 || args[2].equalsIgnoreCase("clear")) h.setOnClickCommand("");
        else h.setOnClickCommand(join(args, 2));
        holograms.renderer().render(h);
        holograms.save();
        sender.sendMessage(ChatColor.GREEN + "Click command set.");
    }

    private void cmdScale(CommandSender sender, String[] args) {
        if (!perm(sender, "edit")) { noPerm(sender); return; }
        if (args.length < 3) { usage(sender, "scale <name> <factor>"); return; }
        Hologram h = holograms.get(args[1]);
        if (h == null) { sender.sendMessage(ChatColor.RED + "Not found."); return; }
        Float scale = finiteFloat(args[2]);
        if (scale == null || scale <= 0 || scale > 16) { sender.sendMessage(ChatColor.RED + "Scale must be a finite number greater than 0 and at most 16."); return; }
        h.setScale(scale);
        holograms.renderer().render(h);
        holograms.save();
        sender.sendMessage(ChatColor.GREEN + "Scale set.");
    }

    private void cmdAlign(CommandSender sender, String[] args) {
        if (!perm(sender, "edit")) { noPerm(sender); return; }
        if (args.length < 3) { usage(sender, "align <name> <left|center|right>"); return; }
        Hologram h = holograms.get(args[1]);
        if (h == null) { sender.sendMessage(ChatColor.RED + "Not found."); return; }
        String alignment = args[2].toUpperCase(Locale.ROOT);
        if (!Arrays.asList("LEFT", "CENTER", "RIGHT").contains(alignment)) { sender.sendMessage(ChatColor.RED + "Alignment must be left, center, or right."); return; }
        h.setAlignment(alignment);
        holograms.renderer().render(h);
        holograms.save();
        sender.sendMessage(ChatColor.GREEN + "Alignment set.");
    }

    private void cmdAnimation(CommandSender sender, String[] args) {
        if (!perm(sender, "edit")) { noPerm(sender); return; }
        if (args.length < 3) { usage(sender, "animation <name> <none|scroll|typewriter|rainbow|pulse> [speed]"); return; }
        Hologram h = holograms.get(args[1]);
        if (h == null) { sender.sendMessage(ChatColor.RED + "Not found."); return; }
        String animation = args[2].toLowerCase(Locale.ROOT);
        if (!Arrays.asList("none", "scroll", "typewriter", "rainbow", "pulse", "wave", "bounce", "glitch", "fade", "slide", "shake").contains(animation)) {
            sender.sendMessage(ChatColor.RED + "Unknown animation."); return;
        }
        Integer animationSpeed = null;
        if (args.length >= 4) {
            int speed = parseInt(args[3]);
            if (speed < 1 || speed > 100) { sender.sendMessage(ChatColor.RED + "Animation speed must be an integer from 1 to 100."); return; }
            animationSpeed = speed;
        }
        h.setAnimation(animation);
        if (animationSpeed != null) h.setAnimationSpeed(animationSpeed);
        holograms.renderer().render(h);
        holograms.save();
        sender.sendMessage(ChatColor.GREEN + "Animation set.");
    }

    private void cmdPause(CommandSender sender, String[] args, boolean pause) {
        if (!perm(sender, "edit")) { noPerm(sender); return; }
        if (args.length < 2) { usage(sender, (pause?"pause":"resume") + " <name>"); return; }
        Hologram h = holograms.get(args[1]);
        if (h == null) { sender.sendMessage(ChatColor.RED + "Not found."); return; }
        h.setPaused(pause);
        holograms.save();
        sender.sendMessage(ChatColor.GREEN + (pause ? "Paused." : "Resumed."));
    }

    private void cmdReload(CommandSender sender) {
        if (!perm(sender, "reload")) { noPerm(sender); return; }
        Bootstrap.reload();
        sender.sendMessage(ChatColor.GREEN + "Reloaded.");
    }

    private void cmdStatus(CommandSender sender) {
        if (!perm(sender, "status")) { noPerm(sender); return; }
        sender.sendMessage(ChatColor.GOLD + "hHolograms status");
        sender.sendMessage(ChatColor.GRAY + "Plugin: " + plugin.getDescription().getVersion());
        sender.sendMessage(ChatColor.GRAY + "Server: " + Bukkit.getName() + " " + Bukkit.getMinecraftVersion());
        sender.sendMessage(ChatColor.GRAY + "Java: " + System.getProperty("java.version"));
        sender.sendMessage(ChatColor.GRAY + "Holograms: " + holograms.all().size());
        sender.sendMessage(ChatColor.GRAY + "Display entities: " + holograms.all().stream().mapToInt(h -> h.entities().size()).sum());
        sender.sendMessage(ChatColor.GRAY + "PlaceholderAPI: " + (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null));
        sender.sendMessage(ChatColor.GRAY + "Refresh ticks: " + config.refreshIntervalTicks());
    }

    private int parseInt(String s) {
        try { return Integer.parseInt(s); } catch (NumberFormatException e) { return -1; }
    }

    private Double finiteDouble(String value) {
        try {
            double parsed = Double.parseDouble(value);
            return Double.isFinite(parsed) ? parsed : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Float finiteFloat(String value) {
        try {
            float parsed = Float.parseFloat(value);
            return Float.isFinite(parsed) ? parsed : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> out = new ArrayList<>();
        List<String> subs = Arrays.asList("help","create","remove","list","info","move","tp","duplicate","edit","page","spacing","billboard","range","background","type","content","scale","align","animation","brightness","linewidth","translate","rotate","condition","click","pause","resume","reload","status");
        if (args.length == 1) {
            for (String s : subs) {
                if (hasCommandPermission(sender, s) && s.startsWith(args[0].toLowerCase(Locale.ROOT))) out.add(s);
            }
            return out;
        }
        String sub = args[0].toLowerCase(Locale.ROOT);
        if (sub.equals("page") && args.length == 2) {
            for (Hologram hologram : holograms.all()) if (hologram.name().toLowerCase(Locale.ROOT).startsWith(args[1].toLowerCase(Locale.ROOT))) out.add(hologram.name());
            return out;
        }
        if (sub.equals("page") && args.length == 3) {
            for (String option : Arrays.asList("next", "prev", "set", "add", "remove")) {
                boolean allowed = Arrays.asList("next", "prev").contains(option)
                        ? perm(sender, "interact") : perm(sender, "edit");
                if (allowed && option.startsWith(args[2].toLowerCase(Locale.ROOT))) out.add(option);
            }
            return out;
        }
        if (args.length == 2 && hasCommandPermission(sender, sub) && (sub.equals("remove")||sub.equals("info")||sub.equals("move")||sub.equals("tp")||sub.equals("duplicate")||sub.equals("edit")||sub.equals("spacing")||sub.equals("billboard")||sub.equals("range")||sub.equals("background")||sub.equals("type")||sub.equals("content")||sub.equals("scale")||sub.equals("align")||sub.equals("animation")||sub.equals("brightness")||sub.equals("linewidth")||sub.equals("width")||sub.equals("translate")||sub.equals("rotate")||sub.equals("condition")||sub.equals("click")||sub.equals("pause")||sub.equals("resume"))) {
            for (Hologram h : holograms.all()) if (h.name().toLowerCase(Locale.ROOT).startsWith(args[1].toLowerCase(Locale.ROOT))) out.add(h.name());
            return out;
        }
        Hologram h = args.length > 1 ? holograms.get(args[1]) : null;
        if (sub.equals("edit")) {
            if (args.length == 3) {
                for (String s : Arrays.asList("add","set","insert","remove","clear","swap","scale","animation","click","pause","resume")) if (s.startsWith(args[2].toLowerCase())) out.add(s);
                return out;
            }
            if (h != null && args.length == 4) {
                for (int i = 0; i < h.lines().size(); i++) if (String.valueOf(i).startsWith(args[3])) out.add(String.valueOf(i));
                return out;
            }
            if (args.length >= 4 && args[2].equalsIgnoreCase("animation")) {
                String last = args[args.length-1];
                for (String s : Arrays.asList("none","scroll","typewriter","rainbow","pulse","wave","bounce","glitch","fade","slide","shake")) if (s.startsWith(last.toLowerCase())) out.add(s);
                return out;
            }
        }
        if (sub.equals("billboard") && args.length == 3) {
            for (String s : Arrays.asList("CENTER","VERTICAL","HORIZONTAL","FIXED")) if (s.toLowerCase().startsWith(args[2].toLowerCase())) out.add(s);
            return out;
        }
        if (sub.equals("align") && args.length == 3) {
            for (String s : Arrays.asList("LEFT","CENTER","RIGHT")) if (s.toLowerCase().startsWith(args[2].toLowerCase())) out.add(s);
            return out;
        }
        if (sub.equals("type") && args.length == 3) {
            for (String s : Arrays.asList("TEXT","ITEM","BLOCK","HEAD")) if (s.toLowerCase().startsWith(args[2].toLowerCase())) out.add(s);
            return out;
        }
        if (sub.equals("background") && args.length == 3) {
            for (String s : Arrays.asList("transparent","remove","none")) if (s.startsWith(args[2].toLowerCase())) out.add(s);
            return out;
        }
        if ((sub.equals("animation")||sub.equals("anim")) && args.length == 3) {
            for (String s : Arrays.asList("none","scroll","typewriter","rainbow","pulse","wave","bounce","glitch","fade","slide","shake")) if (s.startsWith(args[2].toLowerCase())) out.add(s);
            return out;
        }
        if (sub.equals("create") && args.length == 3) {
            for (org.bukkit.World w : org.bukkit.Bukkit.getWorlds()) if (w.getName().toLowerCase().startsWith(args[2].toLowerCase())) out.add(w.getName());
            return out;
        }
        if (sub.equals("brightness") && args.length == 3) {
            for (String s : Arrays.asList("-1","0","1","2","3","4","5","6","7","8","9","10","11","12","13","14","15")) if (s.startsWith(args[2])) out.add(s);
            return out;
        }
        return Collections.emptyList();
    }

    private boolean hasCommandPermission(CommandSender sender, String subcommand) {
        if (subcommand.equals("help")) return true;
        String node = switch (subcommand.toLowerCase(Locale.ROOT)) {
            case "create" -> "create";
            case "remove", "delete" -> "remove";
            case "list" -> "list";
            case "info" -> "info";
            case "move" -> "move";
            case "tp", "teleport" -> "teleport";
            case "duplicate", "copy" -> "duplicate";
            case "edit", "spacing", "billboard", "range", "viewrange", "background", "bg", "type", "content", "scale", "align", "alignment", "brightness", "linewidth", "width", "translate", "rotate", "condition", "click", "onclick", "animation", "anim", "pause", "resume", "unpause" -> "edit";
            case "reload" -> "reload";
            case "status", "diag", "diagnostics" -> "status";
            default -> "list";
        };
        return perm(sender, node);
    }
}
