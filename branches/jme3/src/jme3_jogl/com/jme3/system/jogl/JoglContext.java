package com.jme3.system.jogl;

import com.jme3.input.JoyInput;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.awt.AwtKeyInput;
import com.jme3.input.awt.AwtMouseInput;
import com.jme3.input.dummy.DummyKeyInput;
import com.jme3.input.dummy.DummyMouseInput;
import com.jme3.renderer.Renderer;
import com.jme3.renderer.jogl.JoglRenderer;
import com.jme3.renderer.lwjgl.LwjglRenderer;
import com.jme3.system.AppSettings;
import com.jme3.system.SystemListener;
import com.jme3.system.JmeContext;
import com.jme3.system.NanoTimer;
import com.jme3.system.Timer;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class JoglContext implements JmeContext {

    protected AtomicBoolean created = new AtomicBoolean(false);
    protected AppSettings settings = new AppSettings(true);
    protected JoglRenderer renderer;
    protected Timer timer;
    protected SystemListener listener;

    protected AwtKeyInput keyInput;
    protected AwtMouseInput mouseInput;

    public void setSystemListener(SystemListener listener){
        this.listener = listener;
    }

    public void setSettings(AppSettings settings) {
        this.settings.copyFrom(settings);
    }

    public AppSettings getSettings() {
        return settings;
    }

    public Renderer getRenderer() {
        return renderer;
    }

    public MouseInput getMouseInput() {
        return mouseInput;
    }

    public KeyInput getKeyInput() {
        return keyInput;
    }

    public JoyInput getJoyInput() {
        return null;
    }

    public Timer getTimer() {
        return timer;
    }

    public boolean isCreated() {
        return created.get();
    }

    public void create() {
        timer = new NanoTimer();
        created.set(true);
        // renderer initialization must happen in subclass.
    }

    protected void internalDestroy() {
        created.set(false);
        renderer = null;
        timer = null;
    }

}
