package me.tim.ui.alt;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import me.tim.Statics;
import me.tim.ui.notify.Notification;
import me.tim.util.SessionUtil;
import me.tim.util.common.FileUtil;
import me.tim.util.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class AltManager extends GuiScreen {
    private final GuiScreen parent;
    private final ArrayList<Alt> alts;
    private final JsonUtility jsonUtility;
    private MSLoginThread msLoginThread;

    public AltManager(GuiScreen parent) {
        this.parent = parent;
        this.alts = new ArrayList<>();
        this.jsonUtility = new JsonUtility(this, "alts.json");
        this.jsonUtility.load();
    }

    @Override
    public void initGui() {
        int index = 0;
        for (Alt alt : this.alts) {
            AltButton button = new AltButton(index, height / 6 + index * 25, width, alt);
            this.buttonList.add(button);
            this.buttonList.add(new RemoveButton(button));
            index += 2;
        }
        this.buttonList.add(new AddButton(index + 1, this.width, this.height / 6 + (index + 1) * 50, 32, 32));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        Gui.drawRect(0, 0, this.width, this.height, new Color(20, 20, 20).getRGB());
        if (this.msLoginThread != null) {
            Alt alt = this.msLoginThread.alt;
            for (GuiButton guiButton : this.buttonList) {
                if (guiButton instanceof AltButton && ((AltButton) guiButton).getAlt().equals(alt)) {
                    ((AltButton) guiButton).setLoggedIn(!this.msLoginThread.isAlive());
                }
            }
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button instanceof RemoveButton) {
            RemoveButton removeButton = (RemoveButton) button;
            if (removeButton.altButton != null && removeButton.altButton.getAlt() != null) {
                this.removeAlt(removeButton.altButton.getAlt());
            }
        }

        if (button instanceof AddButton) {
            this.mc.displayGuiScreen(new AddAltScreen(this));
        }

        if (button instanceof AltButton) {
            AltButton altButton = (AltButton) button;
            if (altButton.getAlt() == null) return;

            switch (altButton.getAlt().getAltType()) {
                case CRACKED:
                    Session s = SessionUtil.createCrackedSession(altButton.getAlt().getUser());
                    Statics.getZeus().notificationRenderer.sendNotification(new Notification("Login", "Login successful (" + altButton.getAlt().getUser() + ")!", Notification.NotificationType.SUCCESS));
                    Statics.setSession(s);
                    altButton.setLoggedIn(true);
                    break;
                case MICROSOFT:
                    this.msLoginThread = new MSLoginThread(altButton.getAlt());
                    this.msLoginThread.start();
                    break;
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public void addAlt(Alt alt) {
        this.alts.add(alt);
        this.jsonUtility.save();
    }

    public void removeAlt(Alt alt) {
        this.buttonList.removeIf(guiButton -> guiButton instanceof AltButton && ((AltButton) guiButton).getAlt().equals(alt));
        this.alts.remove(alt);
        this.jsonUtility.save();
    }

    public boolean contains(String user) {
        for (Alt alt : this.alts) {
            if (alt.getUser().equals(user)) return true;
        }
        return false;
    }

    private static final class JsonUtility {
        private final Gson gson;
        private final File jsonFile;
        private final AltManager altManager;
        
        public JsonUtility(AltManager altManager, String fileName) {
            this.altManager = altManager;

            this.gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .create();
            this.jsonFile = new File(FileUtil.getDataDir().getAbsolutePath(), fileName);
        }
        
        public void load() {
            if (!this.jsonFile.exists()) return;

            try (JsonReader reader = new JsonReader(new FileReader(this.jsonFile))) {
                JsonObject object = this.gson.fromJson(reader, JsonObject.class);
                if (object == null || object.entrySet().isEmpty()) return;

                for (Map.Entry<String, JsonElement> stringJsonElementEntry : object.entrySet()) {
                    if (!this.altManager.contains(stringJsonElementEntry.getKey())) {
                        this.altManager.alts.add(new Alt(stringJsonElementEntry.getKey(), stringJsonElementEntry.getValue().getAsString()));
                    }
                }
            } catch (IOException e) {
                System.err.println("Failed to load alt-list!");
                e.printStackTrace();
            }
        }

        private void save() {
            try {
                if (!this.jsonFile.exists()) {
                    this.jsonFile.createNewFile();
                }

                FileWriter writer = new FileWriter(this.jsonFile);
                JsonObject jsonObject = new JsonObject();
                for (Alt alt : this.altManager.alts) {
                    jsonObject.addProperty(alt.getUser(), alt.getPassword());
                }
                this.gson.toJson(jsonObject, writer);
                writer.flush();
                writer.close();
            } catch (IOException e) {
                System.err.println("Failed to save alt-list!");
                e.printStackTrace();
            }
        }
    }
    
    private static final class AddAltScreen extends GuiScreen {
        private final AltManager parent;
        public GuiTextField user, password;

        public AddAltScreen(AltManager parent) {
            this.parent = parent;
        }

        @Override
        public void initGui() {
            Keyboard.enableRepeatEvents(true);

            this.user = new GuiTextField(0, Statics.getMinecraft().fontRendererObj, this.width / 2 - 50, this.height / 2 - 70, 100, 15);
            this.user.setText("User");
            this.password = new GuiTextField(1, Statics.getMinecraft().fontRendererObj, this.width / 2 - 50, this.height / 2 - 40, 100, 15);
            this.password.setText("Password");

            this.buttonList.add(new GuiButton(2, this.width / 2 - 25, this.height / 2 - 10, 50, 15, "Submit"));
        }

        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            if (this.user.getVisible()) {
                this.user.drawTextBox();
            }

            if (this.password.getVisible()) {
                this.password.drawTextBox();
            }
            super.drawScreen(mouseX, mouseY, partialTicks);
        }

        @Override
        protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
            if (this.user.getVisible()) {
                this.user.mouseClicked(mouseX, mouseY, mouseButton);
            }

            if (this.password.getVisible()) {
                this.password.mouseClicked(mouseX, mouseY, mouseButton);
            }
            super.mouseClicked(mouseX, mouseY, mouseButton);
        }

        @Override
        protected void keyTyped(char typedChar, int keyCode) throws IOException {
            if (this.user.getVisible()) {
                this.user.textboxKeyTyped(typedChar, keyCode);
            }

            if (this.password.getVisible()) {
                this.password.textboxKeyTyped(typedChar, keyCode);
            }
            super.keyTyped(typedChar, keyCode);
        }

        @Override
        protected void actionPerformed(GuiButton button) throws IOException {
            if (button.id == 2) {
                this.parent.addAlt(new Alt(this.user.getText(), this.password.getText()));
                this.mc.displayGuiScreen(this.parent);
            }
        }

        @Override
        public void onGuiClosed() {
            super.onGuiClosed();
            Keyboard.enableRepeatEvents(false);
        }
    }

    private static final class MSLoginThread extends Thread {
        private final Alt alt;

        public MSLoginThread(Alt alt) {
            this.alt = alt;
        }

        @Override
        public void run() {
            if (alt.getAltType().equals(Alt.AltType.MICROSOFT)) {
                Session s = SessionUtil.createMSSession(alt.getUser(), alt.getPassword());
                if (s == null) {
                    Statics.getZeus().notificationRenderer.sendNotification(new Notification("Login", "Failed to log in to " + alt.getUser() + "!", Notification.NotificationType.FAILURE));
                } else {
                    Statics.getZeus().notificationRenderer.sendNotification(new Notification("Login", "Login successful (" + alt.getUser() + ")!", Notification.NotificationType.SUCCESS));
                    Statics.setSession(s);
                }
            }
            super.run();
        }
    }

    private static final class Alt {
        private String user, password;
        private final AltType altType;

        public Alt(String user, String password, AltType altType) {
            this.user = user;
            this.password = password;
            this.altType = altType;
        }

        public Alt(String user, String password) {
            this(user, password, password.isEmpty() ? AltType.CRACKED : AltType.MICROSOFT);
        }

        public Alt(String user) {
            this(user, "", AltType.CRACKED);
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public AltType getAltType() {
            return altType;
        }

        public enum AltType { CRACKED, MICROSOFT }
    }

    private static final class AltButton extends GuiButton {
        private static final ResourceLocation msLogo = new ResourceLocation("zeus/icon/microsoft.png");

        private final Alt alt;
        private boolean loggedIn;

        public AltButton(int buttonId, int y, int screenW, Alt alt) {
            super(buttonId, screenW / 4, y, screenW / 2, 50, alt.getUser());
            this.alt = alt;
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY) {
            if (!this.visible) return;
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;

            Gui.drawRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, this.loggedIn ? new Color(55, 205, 55).getRGB() : new Color(35, 35, 35).getRGB());
            RenderUtil.drawHead(this.getSkin(this.displayString), this.xPosition + 5, this.yPosition + 5, this.height - 10, this.height - 10);

            Statics.getFontRenderer().drawString(this.displayString, this.xPosition + this.height + 5, this.yPosition + this.height / 2 - Statics.getFontRenderer().FONT_HEIGHT / 2, -1);

            if (!this.hovered && this.alt.getAltType().equals(Alt.AltType.MICROSOFT)) {
                GlStateManager.pushMatrix();
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GlStateManager.color(1, 1, 1, 1);

                ITextureObject textureObject = Statics.getMinecraft().getTextureManager().getTexture(msLogo);
                if (textureObject == null) {
                    GlStateManager.disableBlend();
                    GlStateManager.popMatrix();
                    return;
                }

                GlStateManager.bindTexture(textureObject.getGlTextureId());
                Gui.drawScaledCustomSizeModalRect(this.xPosition + this.width - 42, this.yPosition + 10, 0, 0, 512, 512, 30, 30, 512, 512);
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }
        }

        public ResourceLocation getSkin(String name) {
            //TODO
            return DefaultPlayerSkin.getDefaultSkinLegacy();
        }

        public int getHeight() {
            return height;
        }

        public boolean isHovered() {
            return hovered;
        }

        public Alt getAlt() {
            return alt;
        }

        public void setLoggedIn(boolean loggedIn) {
            this.loggedIn = loggedIn;
        }

        public boolean isLoggedIn() {
            return loggedIn;
        }
    }

    private static final class AddButton extends GuiButton {
        public AddButton(int buttonId, int screenW, int y, int width, int height) {
            super(buttonId, screenW / 2 - width / 2, y, width, height, "");
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY) {
            if (!this.visible) return;
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;

            int thickness = 6;
            if (this.hovered) {
                thickness = 4;
            }

            Gui.drawRect(this.xPosition + this.width / 2 - thickness / 2, this.yPosition, this.xPosition + this.width / 2 + thickness / 2, this.yPosition + this.height, -1);
            Gui.drawRect(this.xPosition, this.yPosition + this.height / 2 - thickness / 2, this.xPosition + this.width, this.yPosition + this.height / 2 + thickness / 2, -1);
        }
    }

    private static final class RemoveButton extends GuiButton {
        private final AltButton altButton;

        public RemoveButton(AltButton altButton) {
            super(altButton.id + 1, altButton.xPosition + altButton.getButtonWidth() - 42, altButton.yPosition + 10, 30, 30, "");
            this.altButton = altButton;
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY) {
            this.visible = altButton.isHovered();
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;

            if (!this.visible) return;

            int thickness = 6;
            if (this.hovered) {
                thickness = 4;
            }

            Gui.drawRect(this.xPosition, this.yPosition + this.height / 2 - thickness / 2, this.xPosition + this.width, this.yPosition + this.height / 2 + thickness / 2, -1);
        }
    }
}
