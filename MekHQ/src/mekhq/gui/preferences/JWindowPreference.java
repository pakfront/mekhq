/*
 * Copyright (c) 2019 The MegaMek Team. All rights reserved.
 *
 * This file is part of MekHQ.
 *
 * MekHQ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MekHQ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MekHQ.  If not, see <http://www.gnu.org/licenses/>.
 */

package mekhq.gui.preferences;

import mekhq.preferences.PreferenceElement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.lang.ref.WeakReference;

public class JWindowPreference extends PreferenceElement implements WindowStateListener, ComponentListener {
    private final WeakReference<Window> weakRef;
    private int width;
    private int height;
    private int screenX;
    private int screenY;
    private boolean isMaximized;

    public JWindowPreference(Window window) {
        super (window.getName());

        try {
            this.width = window.getWidth();
            this.height = window.getHeight();
            this.screenX = window.getLocationOnScreen().x;
            this.screenY = window.getLocationOnScreen().y;

            if (window instanceof JFrame) {
                this.isMaximized = (((JFrame)window).getState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH;
            } else {
                this.isMaximized = false;
            }
        } catch (Exception ignored) {
        }

        this.weakRef = new WeakReference<>(window);
        window.addWindowStateListener(this);
        window.addComponentListener(this);
    }

    @Override
    public void windowStateChanged(WindowEvent e) {
        this.isMaximized = (e.getNewState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH;
    }

    @Override
    public void componentResized(ComponentEvent e) {
        this.width = e.getComponent().getWidth();
        this.height = e.getComponent().getHeight();
    }

    @Override
    public void componentMoved(ComponentEvent e) {
        this.screenX = e.getComponent().getLocationOnScreen().x;
        this.screenY = e.getComponent().getLocationOnScreen().y;
    }

    @Override
    protected String getValue() {
        return String.format("%d|%d|%d|%d|%s", this.width, this.height, this.screenX, this.screenY, this.isMaximized);
    }

    @Override
    protected void initialize(String value) {
        assert value != null && value.trim().length() > 0;

        Window element = weakRef.get();
        if (element != null) {
            String[] parts = value.split("\\|", -1);

            element.setSize(
                    Integer.parseInt(parts[0]),
                    Integer.parseInt(parts[1]));

            element.setLocation(
                    Integer.parseInt(parts[2]),
                    Integer.parseInt(parts[3]));

            if (Boolean.parseBoolean(parts[4])) {
                if (element instanceof JFrame) {
                    ((JFrame)element).setExtendedState(((JFrame)element).getExtendedState() | Frame.MAXIMIZED_BOTH);
                }
            }
        }
    }

    @Override
    protected void dispose() {
        Window element = weakRef.get();
        if (element != null) {
            element.removeWindowStateListener(this);
            element.removeComponentListener(this);
            weakRef.clear();
        }
    }

    @Override
    public void componentShown(ComponentEvent e) {
    }

    @Override
    public void componentHidden(ComponentEvent e) {
    }
}
