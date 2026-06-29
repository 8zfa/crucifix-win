package com.crucifix.client.core;

import com.crucifix.client.Crucifix;
import com.crucifix.client.events.PacketEvent;
import com.crucifix.client.events.SubscribeEvent;
import com.crucifix.client.modules.Module;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages chat commands
 */
public class CommandManager {
    private Map<String, Command> commands;
    
    public CommandManager() {
        this.commands = new HashMap<>();
        registerDefaultCommands();
    }
    
    private void registerDefaultCommands() {
        commands.put("bind", new BindCommand());
        commands.put("help", new HelpCommand());
    }
    
    @SubscribeEvent
    public void onPacket(PacketEvent event) {
        if (event.isCancelled()) return;
        
        // Check if it's a chat packet
        try {
            Object packet = event.getPacket();
            if (packet == null) return;
            
            String packetName = packet.getClass().getSimpleName();
            if (packetName.equals("C01PacketChatMessage")) {
                // Get message
                String message = (String) packet.getClass().getMethod("getMessage").invoke(packet);
                
                if (message.startsWith(".")) {
                    // It's a command
                    handleCommand(message.substring(1));
                    event.setCancelled(true);
                }
            }
        } catch (Exception e) {
            // Ignore
        }
    }
    
    private void handleCommand(String commandLine) {
        String[] parts = commandLine.split(" ");
        String commandName = parts[0].toLowerCase();
        
        Command command = commands.get(commandName);
        if (command != null) {
            command.execute(parts);
        } else {
            sendChatMessage("§cUnknown command: ." + commandName);
        }
    }
    
    private void sendChatMessage(String message) {
        Crucifix.sendChatMessageStatic(message);
    }
    
    public interface Command {
        void execute(String[] args);
    }
    
    private class BindCommand implements Command {
        @Override
        public void execute(String[] args) {
            if (args.length < 2) {
                sendChatMessage("§cUsage: .bind <module> <key>");
                return;
            }
            
            String moduleName = args[1];
            ModuleManager moduleManager = Crucifix.getInstance().getModuleManager();
            Module module = moduleManager.getModule(moduleName);
            
            if (module == null) {
                sendChatMessage("§cModule not found: " + moduleName);
                return;
            }
            
            if (args.length < 3 || args[2].equalsIgnoreCase("none")) {
                module.setKeyBind(0);
                sendChatMessage("§aUnbound " + moduleName);
            } else {
                String keyName = args[2].toUpperCase();
                int keyCode = getKeyCode(keyName);

                if (keyCode == -1) {
                    sendChatMessage("§cInvalid key: " + keyName);
                    return;
                }

                module.setKeyBind(keyCode);
                sendChatMessage("§aBound " + moduleName + " to " + keyName);
            }
        }
        
        private int getKeyCode(String keyName) {
            switch (keyName) {
                case "RSHIFT": return 157;
                case "LSHIFT": return 42;
                case "LCONTROL": return 29;
                case "RCONTROL": return 157;
                case "LALT": return 56;
                case "RALT": return 184;
                case "INSERT": return 210;
                case "DELETE": return 211;
                case "HOME": return 199;
                case "END": return 207;
                case "PAGEUP": return 201;
                case "PAGEDOWN": return 209;
                case "F1": return 59;
                case "F2": return 60;
                case "F3": return 61;
                case "F4": return 62;
                case "F5": return 63;
                case "F6": return 64;
                case "F7": return 65;
                case "F8": return 66;
                case "F9": return 67;
                case "F10": return 68;
                case "F11": return 87;
                case "F12": return 88;
                default:
                    // Try to get from character
                    if (keyName.length() == 1) {
                        char c = keyName.charAt(0);
                        if (c >= 'A' && c <= 'Z') {
                            return c - 'A' + 30; // A=30, B=31, etc.
                        }
                        if (c >= '0' && c <= '9') {
                            return c - '0' + 2; // 0=2, 1=3, etc.
                        }
                    }
                    return -1;
            }
        }
    }
    
    private class HelpCommand implements Command {
        @Override
        public void execute(String[] args) {
            sendChatMessage("§bAvailable commands:");
            sendChatMessage("§f.bind <module> <key> - Bind a module to a key");
            sendChatMessage("§f.bind <module> none - Unbind a module");
            sendChatMessage("§f.help - Show this help message");
        }
    }
}
